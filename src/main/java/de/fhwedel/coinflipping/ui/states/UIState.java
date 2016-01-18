package de.fhwedel.coinflipping.ui.states;

/**
 * Created by tim on 18.01.16.
 */
public abstract class UIState {
    public abstract UIState handleUI();
    protected abstract String getPrompt();

    public boolean isExitState() {
        return false;
    }
}
