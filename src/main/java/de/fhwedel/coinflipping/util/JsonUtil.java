package de.fhwedel.coinflipping.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by tim on 09.12.2015.
 *
 * Wrap static Gson instance, so I don't have to create a new one all the time.
 */
public class JsonUtil {
    private static Gson mGson = new Gson();

    /**
     * Tries to convert a JSON string to a Java Object. Returns null if that fails.
     * @param jsonString JSON
     * @param cls Class of Java Object
     * @param <T> Type of target object
     * @return Java Object of type T if no errors occur, otherwise null
     */
    public static <T> T fromJson(String jsonString, Class<T> cls) {
        try {
            return mGson.fromJson(jsonString, cls);
        } catch (JsonSyntaxException e) {
            Log.error("Error in received JSON: " + jsonString, e);
            return null;
        }
    }

    public static String toJson(Object object) {
        return mGson.toJson(object);
    }
}
