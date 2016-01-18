package de.fhwedel.coinflipping.ui.states;

import de.fhwedel.coinflipping.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by tim on 17.01.16.
 */
public abstract class UIMultipleSelectionState extends UIState {
    @Override
    public UIState handleUI() {
        try {
            System.out.println(getPrompt());
            System.out.println("");

            List<String> options = getOptions();

            Integer optionIndex = null;
            while (optionIndex == null) {

                for (int i = 0; i < options.size(); i++) {
                    System.out.printf("%d) %s\n", i+1, options.get(i));
                }

                BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
                String line = buffer.readLine();

                try {
                    optionIndex = Integer.valueOf(line) - 1;
                } catch (NumberFormatException e) {
                    System.out.printf("Enter a number between 1 and %d.\n", options.size());
                }
            }

            System.out.println("");
            System.out.println("");

            return handleInputOption(optionIndex);

        } catch (IOException e) {
            Log.error("An error occurred: ", e);
        }

        return new ErrorState();
    }

    protected abstract List<String> getOptions();
    protected abstract UIState handleInputOption(int index);
}
