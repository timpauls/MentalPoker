package de.fhwedel.coinflipping.ui.states;

import de.fhwedel.coinflipping.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by tim on 18.01.16.
 */
public abstract class UITextEntryState extends UIState {
    @Override
    public UIState handleUI() {
        try {
            System.out.println(getPrompt());
            System.out.println("");

            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            String line = buffer.readLine();

            System.out.println("");
            System.out.println("");

            return handleTextInput(line);

        } catch (IOException e) {
            Log.error("An error occurred: ", e);
        }

        return new ErrorState();
    }

    protected abstract UIState handleTextInput(String input);
}
