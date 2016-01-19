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
    private MessageListener mMessageListener;

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

    public void setMessageListener(MessageListener listener) {
        mMessageListener = listener;
    }

    public void messageListeners(String message) {
        if (mMessageListener != null) {
            mMessageListener.onNewMessage(message);
        }
    }

    abstract void receivedProtocolStep(Protocol protocol);

    public interface MessageListener {
        void onNewMessage (String message);
    }
}
