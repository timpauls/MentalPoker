package de.fhwedel.coinflipping.network;

import de.fhwedel.coinflipping.handling.ClientProtocolHandler;
import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.tls.network.OwnTrustManager;
import de.fhwedel.coinflipping.tls.network.TLSNetwork;
import de.fhwedel.coinflipping.util.Log;

import java.io.IOException;

/**
 * Created by tim on 09.12.2015.
 */
public class CoinFlippingClient extends Transmitter {
    // Merv
//    private static final String TARGET_HOST = "fluffels.de";
//    private static final int TARGET_PORT = 50000;

    // Konstantin
//    private static final String TARGET_HOST = "54.77.97.90";
//    private static final int TARGET_PORT = 4444;

    // Localhost
//    private static final String TARGET_HOST = "localhost";
//    private static final String TARGET_HOST = "geistigunbewaff.net";
//    private static final int TARGET_PORT = 6884;

    private final String mServer;
    private final Integer mPort;

    public CoinFlippingClient(String server, Integer port) {
        this.mServer = server;
        this.mPort = port;
        tlsNetwork = new TLSNetwork(TLSNetwork.CLIENT, this);
    }

    public void start() throws IOException {
        tlsNetwork.connect(
                mServer,
                mPort,
                "ssl-certs/root", "fhwedel",
                "ssl-certs/client", "fhwedel",
                OwnTrustManager.ALWAYS,
                null,
                true);

        Log.info("Established secure connection to: " + tlsNetwork.getInetAddress());

        // we initiate the protocol
        Protocol protocol = ClientProtocolHandler.initiateProtocol();
        sendAndLog(protocol);

        // the rest happens in receivedProtocolStep when the mServer responds
    }

    @Override
    void receivedProtocolStep(Protocol protocol) {
        // we received a response and handle it
        Protocol nextStep = ClientProtocolHandler.handleProtocolStep(protocol);

        // if handling the response led to an error, it will be clear from our next step message
        if (nextStep.isValid()) {
            // ProtocolId 8 means the protocol is over, the message contains the winner
            if (nextStep.getProtocolId() == 8) {
                Log.info(">>>> " + nextStep.getStatusMessage());
                finish();
            } else {
                sendAndLog(nextStep);
            }
        } else {
            // send error message to mServer, print info and quit
            sendAndLog(nextStep);
            Log.error("Error in protocol: " + nextStep.getStatusMessage());
            finish();
        }
    }
}
