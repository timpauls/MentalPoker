package de.fhwedel.coinflipping.ui.states;

import java.util.Collections;
import java.util.List;

/**
 * Created by tim on 18.01.16.
 */
public class ErrorState extends UIMultipleSelectionState {
    @Override
    protected String getPrompt() {
        return "An Error occurred.";
    }

    @Override
    protected List<String> getOptions() {
        return Collections.singletonList("Exit");
    }

    @Override
    protected UIState handleInputOption(int index) {
        return new ExitState();
    }
}
