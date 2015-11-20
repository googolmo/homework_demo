package im.amomo.homework.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import im.amomo.homework.model.Item;
import im.amomo.homework.model.Items;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/20/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;
    private static final int VERSION = 1;

    private static final String DB_NAME = "homework.db";

    public synchronized static void createInstance(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
    }

    public static DatabaseHelper getInstance() {
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Item.SQL_CREATE);
        db.execSQL(Items.SQL_CREATE_HOT);
        db.execSQL(Items.SQL_CREATE_TOP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
