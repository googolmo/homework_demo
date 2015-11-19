package im.amomo.homework.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/8/15.
 */
public class GsonHelper {
    public static Gson sInstance;

    public synchronized static Gson getGson() {
        if (sInstance == null) {
            sInstance = new GsonBuilder()
                    .serializeNulls()
                    .create();
        }
        return sInstance;
    }
}
