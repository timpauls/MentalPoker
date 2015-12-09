package de.fhwedel.coinflipping.network;

import de.fhwedel.coinflipping.model.AvailableVersion;
import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.model.ProtocolNegotiation;
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

            // TODO: initiate protocol
            performProtocol();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + TARGET_HOST);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + TARGET_PORT);
            System.exit(1);
        }
    }

    private static void sendAndLog(String message) {
        mOut.println(message);
        System.out.println(">" + message);
    }

    private static String readAndLog() throws IOException {
        String message = mIn.readLine();
        System.out.println("< " + message);
        return message;
    }

    private static void performProtocol() throws IOException {
        Protocol protocol = new Protocol.Builder()
                .setProtocolId(0)
                .setStatusId(0)
                .setStatusMessage(Protocol.STATUS_OK)
                .setProtocolNegotiation(new ProtocolNegotiation(new AvailableVersion("1.0")))
                .build();

        String x = JsonUtil.toJson(protocol);
        sendAndLog(x);

        String response = readAndLog();
        Protocol protocolResponse = JsonUtil.fromJson(response, Protocol.class);
        if (protocolResponse != null && protocolResponse.getStatusId() != 0) {
            System.out.println("Error in protocol! Closing socket.");
            mSocket.close();
        }
    }
}
