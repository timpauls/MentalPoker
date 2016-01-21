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
            String launchMode = args[0];

            if (launchMode != null) {
                switch (launchMode) {
                    case "--server":
                        Integer port = null;
                        if (args.length > 1) {
                            port = Integer.valueOf(args[1]);

                            if (args.length > 2 && args[2].equals("--log")) {
                                Log.IS_DEBUG = true;
                            }
                            launchServer(port);
                        }
                        break;
                    case "--client":
                        if (args.length < 3) {
                            Log.error("Insufficient arguments supplied. Usage: [jar] --client SERVER PORT");
                            System.exit(1);
                        }
                        if (args.length > 3 && args[3].equals("--log")) {
                            Log.IS_DEBUG = true;
                        }

                        launchClient(args[1], Integer.valueOf(args[2]));
                        break;
                    case "--interactive":
                        if (args.length > 1 && args[1].equals("--log")) {
                            Log.IS_DEBUG = true;
                        }
                        new UIStateMachine().mainLoop();
                        break;
                    default:
                        System.out.println(usage());
                        System.exit(1);
                }
            } else {
                Log.error("Launched without parameters! Quitting.");
                System.exit(1);
            }
        }
    }

    private static String usage() {
        return "Usage:\n" +
                "  java -jar [filename.jar] [parameters]\n\n" +

                "  Parameters:\n\n" +

                "  Interactive mode:\tno parameters OR --interactive [--log]\n" +
                "  Server mode:\t\t--server PORT [--log]\n" +
                "  Client mode:\t\t--client HOST POST [--log]";
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
