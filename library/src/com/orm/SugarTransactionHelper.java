package com.orm;

import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;

public class SugarTransactionHelper {

    public static void doInTansaction(SugarTransactionHelper.Callback callback) throws Throwable {
        SQLiteDatabase database = SugarContext.getSugarContext().getSugarDb().getDB();
        database.beginTransaction();

        try {
            Log.d(SugarTransactionHelper.class.getSimpleName(),
                    "Callback executing within transaction");
            callback.manipulateInTransaction();
            database.setTransactionSuccessful();
            Log.d(SugarTransactionHelper.class.getSimpleName(),
                    "Callback successfully executed within transaction");
        } catch (Throwable e) {
            Log.d(SugarTransactionHelper.class.getSimpleName(),
                    "Could execute callback within transaction", e);
            throw e;
        } finally {
            database.endTransaction();
        }

    }

    public static interface Callback {
        void manipulateInTransaction();
    }

}
