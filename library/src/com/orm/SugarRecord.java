package com.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;
import com.orm.dsl.Ignore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.*;

import static com.orm.SugarApp.getSugarContext;

public class SugarRecord<T> {

    @Ignore
    private Context context;
    @Ignore
    private SugarApp application;
    @Ignore
    private Database database;
    @Ignore
    String tableName = getSqlName();

    protected Long id = null;

    public SugarRecord(Context context) {
        this.context = context;
        // this.application = (SugarApp) context.getApplicationContext();
        this.database = ((SugarApp) context.getApplicationContext()).getDatabase();
    }

    public SugarRecord(){
        this.context = SugarApp.getSugarContext();
        this.database = SugarApp.getSugarContext().getDatabase();
    }

    public void delete() {
        if (id != null)
        {
            SQLiteDatabase db = getSugarContext().getDatabase().getDB();
            db.delete(this.tableName, "Id=?", new String[]{getId().toString()});
            Log.i("Sugar", getClass().getSimpleName() + " deleted: " + id);
            id = null;
        }
    }

    public static <T extends SugarRecord<?>> void deleteAll(Class<T> type) {
        Database db = getSugarContext().getDatabase();
        SQLiteDatabase sqLiteDatabase = db.getDB();
        sqLiteDatabase.delete(getTableName(type), null, null);
    }

    public static <T extends SugarRecord<?>> void deleteAll(Class<T> type, String whereClause, String... whereArgs ) {
        Database db = getSugarContext().getDatabase();
        SQLiteDatabase sqLiteDatabase = db.getDB();
        sqLiteDatabase.delete(getTableName(type), whereClause, whereArgs);
    }

    public static <T extends SugarRecord<?>> void deleteById(Class<T> type, Long id) {
        Database db = getSugarContext().getDatabase();
        SQLiteDatabase sqLiteDatabase = db.getDB();
        sqLiteDatabase.delete(getTableName(type), "id=?", new String[]{String.valueOf(id)});
    }
    
    public void save() {
        SQLiteDatabase sqLiteDatabase = getSugarContext().getDatabase().getDB();
        save(sqLiteDatabase);
    }

    protected void save(SQLiteDatabase db) {
        
        List<Field> columns = getTableFields();
        ContentValues values = new ContentValues(columns.size());
        for (Field column : columns) {
            column.setAccessible(true);

            try {
                Class<?> columnType = column.getType();
                String columnName = StringUtil.toSQLName(column.getName());
                Object columnValue = column.get(this);
                
                if (SugarRecord.class.isAssignableFrom(columnType)) {
                    values.put(columnName, (columnValue != null) ? String.valueOf(((SugarRecord<?>) columnValue).id) : "0");
                } else {
                    if (!"id".equalsIgnoreCase(columnName)) {
                        if (columnValue == null) {
                            values.putNull(columnName);
                        }
                        else if (columnType.equals(Short.class) || columnType.equals(short.class)) {
                            values.put(columnName, (Short) columnValue);
                        } 
                        else if (columnType.equals(Integer.class) || columnType.equals(int.class)) {
                            values.put(columnName, (Integer) columnValue);
                        }
                        else if (columnType.equals(Long.class) || columnType.equals(long.class)) {
                            values.put(columnName, (Long) columnValue);
                        }
                        else if (columnType.equals(Float.class) || columnType.equals(float.class)) {
                            values.put(columnName, (Float) columnValue);
                        }
                        else if (columnType.equals(Double.class) || columnType.equals(double.class)) {
                            values.put(columnName, (Double) columnValue);
                        }
                        else if (columnType.equals(Boolean.class) || columnType.equals(boolean.class)) {
                            values.put(columnName, (Boolean) columnValue);
                        } 
                        else if (columnType.equals(Date.class)) {
                            values.put(columnName, columnValue != null ? ((Date) columnValue).getTime() : null);
                        }
                        else if (columnType.equals(Calendar.class)) {
                            values.put(columnName, columnValue != null ? ((Calendar) columnValue).getTimeInMillis() : null);
                        }
                        else if (columnType.equals(Uri.class)) {
                            values.put(columnName, columnValue.toString());
                        }
                        else if (columnType.equals(byte[].class)) {
                            values.put(columnName, (byte[]) columnValue);
                        }
                        else {
                            values.put(columnName, String.valueOf(columnValue));
                        }
                    }
                }

            } catch (IllegalAccessException e) {
                Log.e("Sugar", e.getMessage());
            }
        }

        if (id == null) {
            id = db.insertOrThrow(getSqlName(), null, values);
            Log.i("Sugar", getClass().getSimpleName() + " saved: " + id);
        } else {
            db.update(getSqlName(), values, "ID = ?", new String[]{String.valueOf(id)});
            Log.i("Sugar", getClass().getSimpleName() + " updated: " + id);
        }
    }

    @SuppressWarnings("deprecation")
    public static <T extends SugarRecord<?>> void saveInTx(T... objects ) {

        SQLiteDatabase sqLiteDatabase = getSugarContext().getDatabase().getDB();

        try{
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.setLockingEnabled(false);
            for(T object: objects){
                object.save(sqLiteDatabase);
            }
            sqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){
            Log.i("Sugar", "Error in saving in transaction " + e.getMessage());
        }finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.setLockingEnabled(true);
        }
    }

    @SuppressWarnings("deprecation")
    public static <T extends SugarRecord<?>> void saveInTx(Collection<T> objects ) {

        SQLiteDatabase sqLiteDatabase = getSugarContext().getDatabase().getDB();

        try{
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.setLockingEnabled(false);
            for(T object: objects){
                object.save(sqLiteDatabase);
            }
            sqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){
            Log.i("Sugar", "Error in saving in transaction " + e.getMessage());
        }finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.setLockingEnabled(true);
        }

    }

    public static <T extends SugarRecord<?>> List<T> listAll(Class<T> type) {
        return find(type, null, null, null, null, null);
    }

    public static <T extends SugarRecord<?>> T findById(Class<T> type, Long id) {
        List<T> list = find( type, "id=?", new String[]{String.valueOf(id)}, null, null, "1");
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public static <T extends SugarRecord<?>> List<T> find(Class<T> type,
                                                       String whereClause, String... whereArgs) {
        return find(type, whereClause, whereArgs, null, null, null);
    }

    public static <T extends SugarRecord<?>> List<T> findWithQuery(Class<T> type, String query, String... arguments){

        Database db = getSugarContext().getDatabase();
        SQLiteDatabase sqLiteDatabase = db.getDB();
        T entity;
        List<T> toRet = new ArrayList<T>();
        Cursor c = sqLiteDatabase.rawQuery(query, arguments);

        try {
            while (c.moveToNext()) {
                entity = type.getDeclaredConstructor(Context.class).newInstance(getSugarContext());
                entity.inflate(c);
                toRet.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        return toRet;
    }

    public static void executeQuery(String query, String... arguments){
        getSugarContext().getDatabase().getDB().execSQL(query, arguments);
    }

    public static <T extends SugarRecord<?>> List<T> find(Class<T> type,
                                                       String whereClause, String[] whereArgs,
                                                       String groupBy, String orderBy, String limit) {
        Database db = getSugarContext().getDatabase();
        SQLiteDatabase sqLiteDatabase = db.getDB();
        T entity;
        List<T> toRet = new ArrayList<T>();
        Cursor c = sqLiteDatabase.query(getTableName(type), null,
                whereClause, whereArgs, groupBy, null, orderBy, limit);
        try {
            while (c.moveToNext()) {
                entity = type.getDeclaredConstructor(Context.class).newInstance(getSugarContext());
                entity.inflate(c);
                toRet.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        return toRet;
    }
    
    public static <T extends SugarRecord<?>> long count(Class<T> type){
        return count(type, null, new String[]{});
    }

    public static <T extends SugarRecord<?>> long count(Class<T> type, String query){
        return count(type, query, new String[]{});
    }

    public static <T extends SugarRecord<?>> long count(Class<T> type, String query, String... arguments){
         Database db = getSugarContext().getDatabase();
         SQLiteDatabase sqLiteDatabase = db.getDB();
         String table = getTableName(type);
         try {
             return DatabaseUtils.queryNumEntries(sqLiteDatabase, table, query, arguments);
         } catch (Exception e) {
             e.printStackTrace();
             return -1;
         } 
    }
    
    @SuppressWarnings("unchecked")
    protected void inflate(Cursor cursor) {
        Map<Field, Long> entities = new HashMap<Field, Long>();
        List<Field> columns = getTableFields();
        for (Field field : columns) {
            field.setAccessible(true);
            try {
                Class<?> fieldType = field.getType();
                String colName = StringUtil.toSQLName(field.getName());
                int index = cursor.getColumnIndex(colName);
                
                if (cursor.isNull(index)) {
                    field.set(this, null);
                } 
                else if(colName.equalsIgnoreCase("id")){
                    long cid = cursor.getLong(index);
                    field.set(this, Long.valueOf(cid));
                }
                else if (fieldType.equals(long.class)) {
                    field.set(this, cursor.getLong(index));
                } 
                else if (fieldType.equals(Long.class)) {
                    field.set(this, (Long) cursor.getLong(index));
                } 
                else if (fieldType.equals(String.class)) {
                    String val = cursor.getString(index);
                    field.set(this, val != null && val.equals("null") ? null : val);
                } 
                else if (fieldType.equals(double.class)) {
                    field.set(this, cursor.getDouble(index));
                } 
                else if (fieldType.equals(Double.class)) {
                    field.set(this, (Double) cursor.getDouble(index));
                } 
                else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
                    field.set(this, Boolean.parseBoolean(cursor.getString(index)) || cursor.getString(index).equals("1"));
                } 
                else if (fieldType.equals(byte[].class)) {
                    field.set(this, cursor.getBlob(index));
                } 
                else if (fieldType.equals(int.class)) {
                    field.set(this, cursor.getInt(index));
                } 
                else if (fieldType.equals(Integer.class)) {
                    field.set(this, (Integer) cursor.getInt(index));
                } 
                else if (fieldType.equals(float.class)) {
                    field.set(this, cursor.getFloat(index));
                } 
                else if (fieldType.equals(Float.class)) {
                    field.set(this, (Float) cursor.getFloat(index));
                } 
                else if (fieldType.equals(short.class)) {
                    field.set(this, cursor.getShort(index));
                } 
                else if (fieldType.equals(Short.class)) {
                    field.set(this, (Short) cursor.getShort(index));
                } 
                else if (fieldType.equals(Uri.class)) {
                    String uri = cursor.getString(index);
                    field.set(this, Uri.parse(uri));
                } 
                else if (fieldType.equals(Timestamp.class)) {
                    long l = cursor.getLong(index);
                    field.set(this, new Timestamp(l));
                } 
                else if (fieldType.equals(Date.class)) {
                    long l = cursor.getLong(index);
                    field.set(this, new Date(l));
                } 
                else if (fieldType.equals(Calendar.class)) {
                    long l = cursor.getLong(index);
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(l);
                    field.set(this, c);
                } 
                else if (Enum.class.isAssignableFrom(fieldType)) {
                    try {
                        Method valueOf = fieldType.getMethod("valueOf", String.class);
                        String strVal = cursor.getString(index);
                        Object enumVal = valueOf.invoke(fieldType, strVal);
                        field.set(this, enumVal);
                    } catch (Exception e) {
                        Log.e("Sugar", "Enum cannot be read from Sqlite3 database. Please check the type of field " + field.getName());
                    }
                } 
                else if (SugarRecord.class.isAssignableFrom(fieldType)) {
                    long id = cursor.getLong(index);
                    if (id > 0)
                        entities.put(field, id);
                    else
                        field.set(this, null);
                } 
                else
                    Log.e("Sugar", "Class cannot be read from Sqlite3 database. Please check the type of field " + field.getName() + "(" + fieldType.getName() + ")");
            } catch (IllegalArgumentException e) {
                Log.e("Sugar", "Field set error (IllegalArgumentException). Please check the field " + field.getName());
            } catch (IllegalAccessException e) {
                Log.e("Sugar", "Field set error (IllegalAccessException). Please check the field " + field.getName());
            }
        }

        for (Field f : entities.keySet()) {
            try {
                f.set(this, findById((Class<? extends SugarRecord<?>>) f.getType(), entities.get(f)));
            } catch (SQLiteException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
    }

    public List<Field> getTableFields() {
        List<Field> fieldList = SugarConfig.getFields(getClass());
        if(fieldList != null) return fieldList;

        Log.d("Sugar", "fetching properties for " + getClass().getSimpleName());
        List<Field> typeFields = new ArrayList<Field>();

        getAllFields(typeFields, getClass());

        List<Field> toStore = new ArrayList<Field>();
        for (Field field : typeFields) {
            if (!field.isAnnotationPresent(Ignore.class) && !Modifier.isStatic(field.getModifiers())) {
                toStore.add(field);
            }
        }

        SugarConfig.setFields(getClass(), toStore);
        return toStore;
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        Collections.addAll(fields, type.getDeclaredFields());

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public String getSqlName() {
        return getTableName(getClass());
    }


    public static String getTableName(Class<?> type) {
        return StringUtil.toSQLName(type.getSimpleName());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
