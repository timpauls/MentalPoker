package de.fhwedel.coinflipping.network;

import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.util.JsonUtil;
import de.fhwedel.coinflipping.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by tim on 15.12.15.
 */
public abstract class Transmitter {
    protected static PrintWriter mOut;
    protected static BufferedReader mIn;

    protected static void sendAndLogString(String message) {
        mOut.println(message);
        Log.info("> " + message);
    }

    protected static void sendAndLog(Protocol protocol) {
        sendAndLogString(JsonUtil.toJson(protocol));
    }

    protected static String readAndLogString() throws IOException {
        String message = mIn.readLine();
        Log.info("< " + message);
        return message;
    }

    protected static Protocol readAndLog() throws IOException {
        String message = readAndLogString();
        return JsonUtil.fromJson(message, Protocol.class);
    }
}
