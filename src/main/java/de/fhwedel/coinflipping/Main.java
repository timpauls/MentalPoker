package de.fhwedel.coinflipping;

import de.fhwedel.coinflipping.network.CoinFlippingClient;
import de.fhwedel.coinflipping.network.CoinFlippingServer;
import de.fhwedel.coinflipping.ui.UIStateMachine;
import de.fhwedel.coinflipping.util.Log;

import java.io.IOException;

/**
 * Created by tim on 17.12.15.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            // without parameters start in interactive mode
            new UIStateMachine().mainLoop();
        } else {
            // else start in normal mode
            String launchMode = null;
            if (args.length > 0) {
                launchMode = args[0];
            }

            if (launchMode != null) {
                if (launchMode.equals("--server")) {
                    Integer port = null;
                    if (args.length > 1) {
                        port = Integer.valueOf(args[1]);
                    }
                    launchServer(port);
                } else if (launchMode.equals("--client")) {
                    if (args.length < 3) {
                        Log.error("Insufficient arguments supplied. Usage: [jar] --client SERVER PORT");
                        System.exit(1);
                    }
                    launchClient(args[1], Integer.valueOf(args[2]));
                } else {
                    Log.error("Launched with illegal parameter! Quitting.");
                    System.exit(1);
                }
            } else {
                Log.error("Launched without parameters! Quitting.");
                System.exit(1);
            }
        }
    }

    private static void launchClient(String server, Integer port) {
        CoinFlippingClient coinFlippingClient = new CoinFlippingClient(server, port);
        try {
            coinFlippingClient.start();
        } catch (IOException e) {
            Log.error("Failed to start client!", e);
        }
    }

    private static void launchServer(Integer port) {
        CoinFlippingServer coinFlippingServer = new CoinFlippingServer(port);
        coinFlippingServer.start();
    }
}
