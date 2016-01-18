package de.fhwedel.coinflipping.ui.states;

import de.fhwedel.coinflipping.network.CoinFlippingClient;

import java.io.IOException;

/**
 * Created by tim on 18.01.16.
 */
public class IPEntryState extends UITextEntryState {

    @Override
    protected String getPrompt() {
        return "Enter a server IP and port in the format <IP>:<port>.";
    }

    @Override
    protected UIState handleTextInput(String input) {
        String[] split = input.split(":");
        try {
            new CoinFlippingClient(split[0], Integer.valueOf(split[1])).start();
        } catch (IOException e) {
            return new ErrorState();
        }
        return new ExitState();
    }
}
