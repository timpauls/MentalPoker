package de.fhwedel.coinflipping.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tim on 09.12.2015.
 */
public class CoinFlippingServer {
    private static final int SERVER_PORT = 6882;

    public static void main(String[] args) throws IOException {

        try (
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine, outputLine;

            System.out.println("Accept connection from: " + clientSocket.getInetAddress());
            System.out.println("> Hello there.");

            out.println("Hello there.");

            while ((inputLine = in.readLine()) != null) {
                System.out.println("< " + inputLine);
                out.println("Good point.");
                System.out.println("> Good point.");
                // TODO: implement
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + SERVER_PORT + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
