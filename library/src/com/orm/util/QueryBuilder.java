package com.orm.util;

import com.orm.SugarRecord;

public class QueryBuilder {

    public static String getColumnType(Class<?> type) {
        if ((type.equals(Boolean.class)) ||
                (type.equals(Boolean.TYPE)) ||
                (type.equals(Byte.class)) ||
                (type.equals(Byte.TYPE)) ||
                (type.equals(Short.class)) ||
                (type.equals(Short.TYPE)) ||
                (type.equals(Integer.class)) ||
                (type.equals(Integer.TYPE)) ||
                (type.equals(Long.class)) ||
                (type.equals(Long.TYPE)) ||
                (type.equals(Character.TYPE)) ||
                (type.equals(Character.class)) ||
                ((!type.isPrimitive()) && (SugarRecord.class.isAssignableFrom(type))))  {
            return "INTEGER";
        }

        if ((type.equals(java.util.Date.class)) ||
                (type.equals(java.sql.Date.class)) ||
                (type.equals(java.util.Calendar.class))) {
            return "INTEGER NULL";
        }

        if (type.getName().equals("[B")) {
            return "BLOB";
        }

        if ((type.equals(Double.class)) || (type.equals(Double.TYPE)) || (type.equals(Float.class)) ||
                (type.equals(Float.TYPE))) {
            return "FLOAT";
        }

        if (type.getName().equals(byte[].class)) {
            return "BLOB";
        }

        if ((type.equals(String.class)) || 
                (type.equals(android.net.Uri.class))) {
            return "TEXT";
        }

        return "";
    }

}
