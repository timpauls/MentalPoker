package de.fhwedel.coinflipping.config;

/**
 * Created by tim on 10.12.2015.
 */
public class GeneralConfig {
    private static String mBrokerBaseUrl = "52.35.76.130:8443";

    public static String getBrokerBaseUrl() {
        return mBrokerBaseUrl;
    }

    public static void setBrokerBaseUrl(String mBrokerBaseUrl) {
        GeneralConfig.mBrokerBaseUrl = mBrokerBaseUrl;
    }

    public static String getBrokerJoinUrl() {
        return "https://" + mBrokerBaseUrl + "/broker/1.0/join";
    }

    public static String getBrokerPlayerUrl() {
        return "https://" + mBrokerBaseUrl + "/broker/1.0/players";
    }
}
