package de.fhwedel.coinflipping.network;

import de.fhwedel.coinflipping.handling.ClientProtocolHandler;
import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.util.JsonUtil;

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
//    private static final String TARGET_HOST = "87.106.43.47";
//    private static final int TARGET_PORT = 50000;
    private static final String TARGET_HOST = "localhost";
    private static final int TARGET_PORT = 6882;

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

            System.out.println("Established connection to: " + socket.getInetAddress());
            
            performProtocol();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + TARGET_HOST);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + TARGET_PORT);
            System.exit(1);
        }
    }

    private static void sendAndLogString(String message) {
        mOut.println(message);
        System.out.println("> " + message);
    }

    private static void sendAndLog(Protocol protocol) {
        sendAndLogString(JsonUtil.toJson(protocol));
    }

    private static String readAndLogString() throws IOException {
        String message = mIn.readLine();
        System.out.println("< " + message);
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
                sendAndLog(nextStep);
            } else {
                System.out.println("Error in protocol: " + nextStep.getStatusMessage());
                System.out.println("Closing socket.");
                mSocket.close();
                break;
            }
        }

        System.out.println("Protocol has finished.");
    }
}
