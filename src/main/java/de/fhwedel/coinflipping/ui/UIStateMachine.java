package de.fhwedel.coinflipping.ui;

import de.fhwedel.coinflipping.ui.states.EntryState;
import de.fhwedel.coinflipping.ui.states.UIState;

/**
 * Created by tim on 17.01.16.
 */
public class UIStateMachine {

    UIState mCurrentState = new EntryState();

    public void mainLoop() {
        while (!mCurrentState.isExitState()) {
            mCurrentState = mCurrentState.handleUI();
        }
    }

}
