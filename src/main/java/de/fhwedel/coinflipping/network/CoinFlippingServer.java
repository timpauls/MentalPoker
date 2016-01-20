package de.fhwedel.coinflipping.network;

import de.fhwedel.coinflipping.handling.ServerProtocolHandler;
import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.tls.network.OwnTrustManager;
import de.fhwedel.coinflipping.tls.network.TLSNetwork;
import de.fhwedel.coinflipping.util.Log;

/**
 * Created by tim on 09.12.2015.
 */
public class CoinFlippingServer extends Transmitter {
    private static final int SERVER_PORT = 6882;
    private Integer mPort;

    // TODO: parametrize port
    public CoinFlippingServer(Integer port) {
        mPort = port;
        tlsNetwork = new TLSNetwork(TLSNetwork.SERVER, this);
    }

    public void start() {
        Integer serverPort;
        if (mPort != null) {
            serverPort = mPort;
        } else {
            serverPort = SERVER_PORT;
        }
        tlsNetwork.start(
                serverPort,
                "ssl-certs/root", "fhwedel",
                "ssl-certs/server", "fhwedel",
                OwnTrustManager.ALWAYS,
                null,
                true);

        Log.info("Starting secure server on port: " + serverPort);
        messageListeners("Starting secure server on port: " + serverPort);
    }

    @Override
    void receivedProtocolStep(Protocol protocol) {
        messageListeners(protocol);

        Protocol nextStep = ServerProtocolHandler.handleProtocolStep(protocol);

        messageListeners(nextStep);

        // if handling the response led to an error, it will be clear from our next step message
        if (nextStep.isValid()) {
            sendAndLog(nextStep);

            // ProtocolId 7 means the protocol is over, process the final result
            if (nextStep.getProtocolId() == 7) {
                Protocol result = ServerProtocolHandler.determineWinner(nextStep);
                messageListeners(result);
                Log.info(">>>> " + result.getStatusMessage());
                finish();
                // restart
                start();
            }
        } else {
            // send error message to client, print info and quit
            sendAndLog(nextStep);
            Log.error("Error in protocol: " + nextStep.getStatusMessage());
            finish();
            // restart
            start();
        }
    }

    private void messageListeners(Protocol protocol) {
        String message = null;
        switch (protocol.getProtocolId()) {
            case 0:
                message = "Received protocol initialization.";
                break;
            case 1:
                message = "Protocol version is " + protocol.getProtocolNegotiation().getVersion() + ".";
                break;
            case 2:
                message = "Received key negotiation initialization";
                break;
            case 3:
                message = "Key negotiation complete.";
                break;
            case 4:
                message = "Received coins "+ protocol.getPayload().getInitialCoin().toString();
                break;
            case 5:
                message = "Chose coin " + protocol.getPayload().getDesiredCoin();
                break;
            case 6:
                message = "Receiving client key";
                break;
            case 7:
                message = "Sending key to client.";
                break;
            case 8:
                message = protocol.getStatusMessage();
                break;
        }
        if (message != null) {
            messageListeners(message);
        }
    }
}
