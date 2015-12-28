package de.fhwedel.coinflipping.network;

import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.tls.network.TLSNetwork;
import de.fhwedel.coinflipping.tls.network.TLSNetworkGame;
import de.fhwedel.coinflipping.util.JsonUtil;
import de.fhwedel.coinflipping.util.Log;

/**
 * Created by tim on 15.12.15.
 */
public abstract class Transmitter implements TLSNetworkGame {
    protected TLSNetwork tlsNetwork;

    protected void sendAndLogString(String message) {
        tlsNetwork.send(message);
        Log.info("> " + message);
    }

    protected void sendAndLog(Protocol protocol) {
        sendAndLogString(JsonUtil.toJson(protocol));
    }

    @Override
    public void receivedMessage(String message) {
        Log.info("< " + message);
        receivedProtocolStep(JsonUtil.fromJson(message, Protocol.class));
    }

    protected void finish() {
        tlsNetwork.stop();
        Log.info("Protocol has finished.");
    }

    abstract void receivedProtocolStep(Protocol protocol);
}
