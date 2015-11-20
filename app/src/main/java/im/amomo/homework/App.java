package im.amomo.homework;

import android.app.Application;

import im.amomo.homework.database.DatabaseHelper;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/20/15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper.createInstance(this);
    }
}
