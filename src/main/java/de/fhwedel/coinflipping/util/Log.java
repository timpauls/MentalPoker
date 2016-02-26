package de.fhwedel.coinflipping.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tim on 10.12.2015.
 */
public class Log {
    public static boolean IS_DEBUG = false;

    public static final String LOG_TAG = "CoinFlipping";

    private static final Logger mLog = Logger.getLogger(LOG_TAG);

    public static void info(String message) {
        if (IS_DEBUG) {
            mLog.log(Level.INFO, message);
        }
    }

    public static void warning(String message) {
        if (IS_DEBUG) {
            mLog.log(Level.WARNING, message);
        }
    }

    public static void error(String message) {
        mLog.log(Level.SEVERE, message);
    }

    public static void error(String message, Throwable throwable) {
        mLog.log(Level.SEVERE, message, throwable);
    }
}
