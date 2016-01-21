package de.fhwedel.coinflipping.ui.states;

import de.fhwedel.coinflipping.util.StringUtil;

/**
 * Created by tim on 21.01.16.
 */
public class ServerNameEntryState extends UITextEntryState {
    @Override
    protected UIState handleTextInput(String input) {
        if (StringUtil.isEmpty(input)) {
            return new ServerNameEntryState();
        }
        return new PortEntryState(input);
    }

    @Override
    protected String getPrompt() {
        return "Enter server name:";
    }
}
