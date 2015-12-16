package de.fhwedel.coinflipping.network;

import de.fhwedel.coinflipping.handling.ClientProtocolHandler;
import de.fhwedel.coinflipping.handling.ServerProtocolHandler;
import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tim on 09.12.2015.
 */
public class CoinFlippingServer extends  Transmitter {
    private static final int SERVER_PORT = 6882;
    private static ServerSocket mServerSocket;

    public static void main(String[] args) throws IOException {
        Log.info("Starting server on port " + SERVER_PORT);

        try (
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            mServerSocket = serverSocket;
            mOut = out;
            mIn = in;

            Log.info("Accepted connection from: " + clientSocket.getInetAddress());

            performProtocol();
        } catch (IOException e) {
            Log.error("Exception caught when trying to listen on port " + SERVER_PORT + " or listening for a connection", e);
        }
    }

    private static void performProtocol() throws IOException {
        while (true) {
            // we expect a message and handle it
            Protocol protocolMessage = readAndLog();
            Protocol nextStep = ServerProtocolHandler.handleProtocolStep(protocolMessage);

            // if handling the response led to an error, it will be clear from our next step message
            if (nextStep.isValid()) {
                sendAndLog(nextStep);

                // ProtocolId 7 means the protocol is over, process the final result
                if (nextStep.getProtocolId() == 7) {
                    Protocol result = ServerProtocolHandler.determineWinner(nextStep);
                    Log.info(">>>> " + result.getStatusMessage());
                    System.exit(0);
                }
            } else {
                // send error message to client, print info and quit
                sendAndLog(nextStep);
                Log.error("Error in protocol: " + nextStep.getStatusMessage());
                Log.info("Closing socket.");
                mServerSocket.close();
                break;
            }
        }

        Log.info("Protocol has finished.");
    }
}
