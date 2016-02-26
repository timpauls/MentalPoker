package de.fhwedel.coinflipping;

import de.fhwedel.coinflipping.config.GeneralConfig;
import de.fhwedel.coinflipping.network.CoinFlippingClient;
import de.fhwedel.coinflipping.network.CoinFlippingServer;
import de.fhwedel.coinflipping.ui.UIStateMachine;
import de.fhwedel.coinflipping.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by tim on 17.12.15.
 */
public class Main {
    public static void main(String[] args) {
        Map<String, List<String>> argsMap = parseArgs(args);

        // re-enable md5 hashes (disabled in newest java8 release)
        // this is dirty, the broker should not use md5-hash signatures in his certificates!
        java.security.Security.setProperty("jdk.tls.disabledAlgorithms", "SSLv3, DH keySize < 768");
        java.security.Security.setProperty("jdk.certpath.disabledAlgorithms", "MD2, RSA keySize < 1024");

        if (argsMap.containsKey("--log")) {
            Log.IS_DEBUG = true;
        }

        if (argsMap.containsKey("--broker")) {
            List<String> brokerValues = argsMap.get("--broker");
            if (brokerValues.size() < 2) {
                Log.error("Insufficient arguments supplied. Usage: [jar] [...] --broker HOST PORT [...]");
                System.exit(1);
            }
            GeneralConfig.setBrokerBaseUrl(brokerValues.get(0) + ":" + brokerValues.get(1));
        }

        if (argsMap.keySet().size() == 0 || argsMap.containsKey("--interactive")) {
            // without parameters start in interactive mode
            new UIStateMachine().mainLoop();
        } else {
            if (argsMap.keySet().contains("--server")) {
                List<String> serverValues = argsMap.get("--server");
                if (serverValues.size() < 2) {
                    Log.error("Insufficient arguments supplied. Usage: [jar] --server NAME PORT [--log]");
                    System.exit(1);
                }

                launchServer(serverValues.get(0), Integer.valueOf(serverValues.get(1)));
            } else if (argsMap.keySet().contains("--client")) {
                List<String> clientValues = argsMap.get("--client");
                if (clientValues.size() < 2) {
                    Log.error("Insufficient arguments supplied. Usage: [jar] --client HOST PORT [--log]");
                    System.exit(1);
                }

                launchClient(clientValues.get(0), Integer.valueOf(clientValues.get(1)));
            } else {
                System.out.println(usage());
                System.exit(1);
            }
        }
    }

    private static Map<String, List<String>> parseArgs(String[] args) {
        Map<String, List<String>> result = new HashMap<>();
        String key = null;
        List<String> values = new LinkedList<>();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                if (key != null) {
                    result.put(key, values);
                    values = new LinkedList<>();
                }
                key = arg;
            } else {
                values.add(arg);
            }
        }
        if (key != null) {
            result.put(key, values);
        }

        return result;
    }

    private static String usage() {
        return "Usage:\n" +
                "  java -jar [filename.jar] [parameters]\n\n" +

                "  Parameters:\n\n" +

                "  Interactive mode:\tNO PARAMETERS OR --interactive [--log] [--broker HOST PORT]\n" +
                "  Server mode:\t\t--server NAME PORT [--log] [--broker HOST PORT]\n" +
                "  Client mode:\t\t--client HOST PORT [--log] [--broker HOST PORT]";
    }

    private static void launchClient(String server, Integer port) {
        CoinFlippingClient coinFlippingClient = new CoinFlippingClient(server, port);
        try {
            coinFlippingClient.start();
        } catch (IOException e) {
            Log.error("Failed to start client!", e);
        }
    }

    private static void launchServer(String name, Integer port) {
        CoinFlippingServer coinFlippingServer = new CoinFlippingServer(name, port);
        coinFlippingServer.start();
    }
}
