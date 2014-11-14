package com.orm;

import com.orm.SugarContext;
import com.orm.util.ManifestHelper;

import android.app.Application;

public class SugarApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        String password = ManifestHelper.getPassword(this);
        SugarContext.init(this, password);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

}
