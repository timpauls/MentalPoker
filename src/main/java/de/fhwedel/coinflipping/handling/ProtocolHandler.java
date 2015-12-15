package de.fhwedel.coinflipping.handling;

import de.fhwedel.coinflipping.model.Protocol;

/**
 * Created by tim on 15.12.15.
 */
public abstract class ProtocolHandler {
    protected static Protocol error(String errorMessage) {
        return new Protocol.Builder()
                .setStatusId(Integer.MIN_VALUE)
                .setStatusMessage("Error: " + errorMessage).build();
    }
}
