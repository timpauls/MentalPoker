package de.fhwedel.coinflipping.ui.states;

import de.fhwedel.coinflipping.network.CoinFlippingServer;
import de.fhwedel.coinflipping.network.Transmitter;
import de.fhwedel.coinflipping.util.StringUtil;

/**
 * Created by tim on 18.01.16.
 */
public class PortEntryState extends UITextEntryState implements Transmitter.MessageListener {

    private static final int DEFAULT_PORT = 6882;

    @Override
    protected String getPrompt() {
        return "Enter server port number (default 6882).";
    }

    @Override
    protected UIState handleTextInput(String input) {
        Integer port;
        if (StringUtil.isEmpty(input)) {
            port = DEFAULT_PORT;
        } else {
            try {
                port = Integer.valueOf(input);
            } catch (NumberFormatException e) {
                return new ErrorState();
            }
        }

        CoinFlippingServer coinFlippingServer = new CoinFlippingServer(port);
        coinFlippingServer.setMessageListener(this);
        coinFlippingServer.start();
        return new ExitState();
    }

    @Override
    public void onNewMessage(String message) {
        System.out.println(message);
    }
}
