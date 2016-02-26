package de.fhwedel.coinflipping.ui.states;

import de.fhwedel.coinflipping.config.GeneralConfig;
import de.fhwedel.coinflipping.network.CoinFlippingClient;
import de.fhwedel.coinflipping.network.Transmitter;
import gr.planetz.impl.HttpPingingService;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tim on 18.01.16.
 */
public class ServerSelectionState extends UIMultipleSelectionState implements Transmitter.MessageListener {

    @Override
    protected String getPrompt() {
        return "Choose a server to play against.";
    }

    @Override
    protected List<String> getOptions() {
        try {
            List<String> options = new ArrayList<>();
            options.add("Localhost - localhost:6882");

            HttpPingingService pingingService = new HttpPingingService(GeneralConfig.getBrokerPlayerUrl(), "", "", "ssl-certs/tipa_keystore.jks", "secret");
            Map<String, String> players = pingingService.getPlayersDirectlyOverHttpGetRequest();
            for (String name : players.keySet()) {
                options.add(name + " - " + players.get(name));
            }

            options.add("Enter an IP address and port");
            return options;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected UIState handleInputOption(int index) {
        if (index == getOptions().size()-1) {
            return new IPEntryState();
        }

        String serverAndPort = getOptions().get(index).split(" - ")[1];
        String[] split = serverAndPort.split(":");
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
