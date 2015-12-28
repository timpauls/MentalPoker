package de.fhwedel.coinflipping;

import de.fhwedel.coinflipping.network.CoinFlippingClient;
import de.fhwedel.coinflipping.network.CoinFlippingServer;
import de.fhwedel.coinflipping.tls.cert.X509CertGenerator;
import de.fhwedel.coinflipping.util.Log;
import org.bouncycastle.asn1.x500.X500Name;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by tim on 17.12.15.
 */
public class Main {
    public static void main(String[] args) {
        String launchMode = null;
        if (args.length > 0) {
            launchMode = args[0];
        }

        if (launchMode != null) {
            if (launchMode.equals("--gencert")) {
                generateCerts();
            } else if (launchMode.equals("--server")) {
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

    private static void generateCerts() {
        X509CertGenerator gen = new X509CertGenerator(new BigInteger("5"));
        try {
            gen.createRoot(
                    new X500Name("C=GERMANY,L=Wedel,O=FH Wedel, OU=ITS Project WS1516, CN=Mental Poker Root"),
                    2048,
                    "mentalpoker_root",
                    "secretsauce",
                    "rootalias",
                    true);

            gen.createCert(
                    2048,
                    "mentalpoker_tim",
                    "secretsauce",
                    "certalias",
                    new X500Name("C=Germany, L=Wedel, O=FH Wedel, OU=ITS Project WS1516, CN=Tim"),
                    false);
        } catch (Exception e) {
            Log.error("Error in certificate generation!", e);
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
