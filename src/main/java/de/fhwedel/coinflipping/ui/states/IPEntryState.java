package de.fhwedel.coinflipping.ui.states;

import de.fhwedel.coinflipping.network.CoinFlippingClient;
import de.fhwedel.coinflipping.network.Transmitter;

import java.io.IOException;

/**
 * Created by tim on 18.01.16.
 */
public class IPEntryState extends UITextEntryState implements Transmitter.MessageListener {

    @Override
    protected String getPrompt() {
        return "Enter a server IP and port in the format <IP>:<port>.";
    }

    @Override
    protected UIState handleTextInput(String input) {
        String[] split = input.split(":");
        try {
            CoinFlippingClient coinFlippingClient = new CoinFlippingClient(split[0], Integer.valueOf(split[1]));
            coinFlippingClient.setMessageListener(this);
            coinFlippingClient.start();
        } catch (IOException e) {
            return new ErrorState();
        }
        return new ExitState();
    }

    @Override
    public void onNewMessage(String message) {
        System.out.println(message);
    }
}
