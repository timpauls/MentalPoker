package de.fhwedel.coinflipping.util;

import com.google.gson.Gson;

/**
 * Created by tim on 09.12.2015.
 *
 * Wrap static Gson instance, so I don't have to create a new one all the time.
 */
public class JsonUtil {
    public static Gson gson = new Gson();
}
