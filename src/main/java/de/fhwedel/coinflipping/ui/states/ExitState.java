package de.fhwedel.coinflipping.ui.states;

import java.util.List;

/**
 * Created by tim on 18.01.16.
 */
public class ExitState extends UIMultipleSelectionState {

    @Override
    protected String getPrompt() {
        return null;
    }

    @Override
    protected List<String> getOptions() {
        return null;
    }

    @Override
    protected UIMultipleSelectionState handleInputOption(int index) {
        return null;
    }

    @Override
    public boolean isExitState() {
        return true;
    }
}
