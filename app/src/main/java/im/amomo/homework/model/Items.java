package im.amomo.homework.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.squareup.sqlbrite.BriteDatabase;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import im.amomo.homework.BuildConfig;
import rx.Observable;
import rx.functions.Func1;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/8/15.
 */
public class Items {
    private static final String TAG = Items.class.getSimpleName();

    @StringDef({TABLES.HOT, TABLES.TOP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TABLE {}

    public interface TABLES {
        String HOT = "hot";
        String TOP = "top";
    }

    public interface Columns {
        String ID = "items_id";
        String ITEM_ID = "item_id";
    }

    public static final String SQL_CREATE_HOT = "CREATE TABLE IF NOT EXISTS `" + TABLES.HOT + "` (`" +
            Columns.ID + "` INTEGER PRIMARY KEY AUTOINCREMENT, `" +
            Columns.ITEM_ID + "` INTEGER)";

    public static final String SQL_CREATE_TOP = "CREATE TABLE IF NOT EXISTS `" + TABLES.TOP + "` (`" +
            Columns.ID + "` INTEGER PRIMARY KEY AUTOINCREMENT, `" +
            Columns.ITEM_ID + "` INTEGER)";

    public List<Item> data;

    public Items() {

    }

    @Override
    public String toString() {
        return "Items{" +
                "data=" + data +
                '}';
    }

    @UiThread
    public static Observable<List<Item>> query(@NonNull BriteDatabase db, @NonNull @TABLE String table) {
        return db.createQuery(table,
                "select * from " + table + "," + Item.TABLE + " where " + Columns.ITEM_ID + " = " + Item.Columns.ID)
                .mapToList(new Func1<Cursor, Item>() {
                    @Override
                    public Item call(Cursor cursor) {
                        return new Item(cursor);
                    }
                });
    }

    @WorkerThread
    public static void clear(@NonNull BriteDatabase db, @NonNull @TABLE String table) {
        db.delete(table, null);
    }

    @WorkerThread
    public static void save(@NonNull BriteDatabase db, @NonNull @TABLE String table, List<Item> list) {
        long startTime = SystemClock.elapsedRealtime();
        BriteDatabase.Transaction transaction = db.newTransaction();
        for (Item item: list) {
            db.insert(Item.TABLE, item.getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
            ContentValues cv = new ContentValues();
            cv.put(Columns.ITEM_ID, item.id);
            db.insert(table, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
        transaction.markSuccessful();
        transaction.end();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "save item list cost " + (SystemClock.elapsedRealtime() - startTime));
        }

    }

    /**
     * trim item table. Delete item which both no in HOT table and TOP table;
     * @param db {@link BriteDatabase} object
     */
    @WorkerThread
    public static void trimDatabase(@NonNull BriteDatabase db) {
        int count = db.delete(Item.TABLE,
                String.format("%1$s not in (select %2$s from %3$s) and %1$s not in (select %2$s from %4$s)",
                        Item.Columns.ID, Columns.ITEM_ID, TABLES.HOT, TABLES.TOP));

        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("trim item table %1$s rows affect", count));
        }

    }
}
