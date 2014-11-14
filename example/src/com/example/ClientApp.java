package com.example;

import net.sqlcipher.database.SQLiteDatabase;

import com.orm.SugarContext;

import android.app.Application;

public class ClientApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this, "password");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

}
