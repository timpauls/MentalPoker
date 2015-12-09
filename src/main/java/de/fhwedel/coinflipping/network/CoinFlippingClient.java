package de.fhwedel.coinflipping.network;

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
//    private static final String TARGET_HOST = "95.91.224.194";
    private static final String TARGET_HOST = "2A02:8108:3140:FE4:F931:8BEC:9BEA:310F";
    private static final int TARGET_PORT = 50000;
//    private static final String TARGET_HOST = "localhost";
//    private static final int TARGET_PORT = 6882;

    public static void main(String[] args) throws IOException {

        try (
            Socket echoSocket = new Socket(TARGET_HOST, TARGET_PORT);
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String userInput;

            System.out.println("Established connection to: " + echoSocket.getInetAddress());

            // TODO: initiate protocol

            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("> " + in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + TARGET_HOST);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + TARGET_PORT);
            System.exit(1);
        }
    }
}
