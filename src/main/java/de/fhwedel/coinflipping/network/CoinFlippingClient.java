package de.fhwedel.coinflipping.network;

import de.fhwedel.coinflipping.handling.ClientProtocolHandler;
import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.util.JsonUtil;
import de.fhwedel.coinflipping.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by tim on 09.12.2015.
 */
public class CoinFlippingClient {
    // Merv
//    private static final String TARGET_HOST = "fluffels.de";
//    private static final int TARGET_PORT = 50000;

    // Konstantin
    private static final String TARGET_HOST = "54.77.97.90";
    private static final int TARGET_PORT = 4444;

    // Localhost
//    private static final String TARGET_HOST = "localhost";
//    private static final int TARGET_PORT = 6882;

    private static Socket mSocket;
    private static PrintWriter mOut;
    private static BufferedReader mIn;

    public static void main(String[] args) throws IOException {

        try (
            Socket socket = new Socket(TARGET_HOST, TARGET_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            mSocket = socket;
            mOut = out;
            mIn = in;

            Log.info("Established connection to: " + socket.getInetAddress());

            performProtocol();
        } catch (UnknownHostException e) {
            Log.error("Don't know about host " + TARGET_HOST, e);
            System.exit(1);
        } catch (IOException e) {
            Log.error("Couldn't get I/O for the connection to " + TARGET_PORT);
            System.exit(1);
        }
    }

    private static void sendAndLogString(String message) {
        mOut.println(message);
        Log.info("> " + message);
    }

    private static void sendAndLog(Protocol protocol) {
        sendAndLogString(JsonUtil.toJson(protocol));
    }

    private static String readAndLogString() throws IOException {
        String message = mIn.readLine();
        Log.info("< " + message);
        return message;
    }

    private static Protocol readAndLog() throws IOException {
        String message = readAndLogString();
        return JsonUtil.fromJson(message, Protocol.class);
    }

    private static void performProtocol() throws IOException {
        // we initiate the protocol
        Protocol protocol = ClientProtocolHandler.initiateProtocol();
        sendAndLog(protocol);

        while (true) {
            // we expect a response and handle it
            Protocol protocolResponse = readAndLog();
            Protocol nextStep = ClientProtocolHandler.handleProtocolStep(protocolResponse);

            // if handling the response led to an error, it will be clear from our next step message
            if (nextStep.isValid()) {
                // ProtocolId 8 means the protocol is over, the message contains the winner
                if (nextStep.getProtocolId() == 8) {
                    Log.info(">>>> " + nextStep.getStatusMessage());
                    System.exit(0);
                } else {
                    sendAndLog(nextStep);
                }
            } else {
                Log.error("Error in protocol: " + nextStep.getStatusMessage());
                Log.info("Closing socket.");
                mSocket.close();
                break;
            }
        }

        Log.info("Protocol has finished.");
    }
}
