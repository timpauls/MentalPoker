package de.fhwedel.coinflipping.ui.states;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tim on 17.01.16.
 */
public class EntryState extends UIMultipleSelectionState {
    @Override
    protected String getPrompt() {
        return "Welcome to coin flipping. Please choose a launch mode.";
    }

    @Override
    protected List<String> getOptions() {
        return Arrays.asList("Launch as client", "Launch as server");
    }

    @Override
    protected UIState handleInputOption(int index) {
        switch (index) {
            case 0:
                return new ServerSelectionState();
            case 1:
                return new PortEntryState();
        }

        return new ErrorState();
    }
}
