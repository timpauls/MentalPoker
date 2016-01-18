package de.fhwedel.coinflipping.ui.states;

import de.fhwedel.coinflipping.network.CoinFlippingClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tim on 18.01.16.
 */
public class ServerSelectionState extends UIMultipleSelectionState {

    @Override
    protected String getPrompt() {
        return "Choose a server to play against.";
    }

    @Override
    protected List<String> getOptions() {
        return Arrays.asList("localhost:6882", "geistigunbewaff.net:6882", "fluffels.de:50000", "Enter an IP address and port");
    }

    @Override
    protected UIState handleInputOption(int index) {
        if (index == getOptions().size()-1) {
            return new IPEntryState();
        }

        String serverAndPort = getOptions().get(index);
        String[] split = serverAndPort.split(":");
        try {
            new CoinFlippingClient(split[0], Integer.valueOf(split[1])).start();
        } catch (IOException e) {
            return new ErrorState();
        }
        return new ExitState();
    }
}
