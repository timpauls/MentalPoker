package de.fhwedel.coinflipping.handling;

import de.fhwedel.coinflipping.model.AvailableVersion;
import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.model.ProtocolNegotiation;

/**
 * Created by tim on 09.12.2015.
 */
public class ClientProtocolHandler {

    public static Protocol initiateProtocol() {
        return new Protocol.Builder()
                .setProtocolId(0)
                .setStatusId(0)
                .setStatusMessage(Protocol.STATUS_OK)
                .setProtocolNegotiation(new ProtocolNegotiation(new AvailableVersion("1.0")))
                .build();
    }

    public static Protocol handleProtocolStep(Protocol protocol) {
        Protocol response;

        if (protocol == null) {
            // if the supplied protocol step is null an error must have occurred
            response = error("Received null or unparsable protocol!");
        } else if (!protocol.isValid()) {
            response = error("Received invalid protocol saying: '" + protocol.getStatusMessage() + "'");
        } else {
            Integer protocolId = protocol.getProtocolId();
            if (protocolId % 2 == 0) {
                response = error("Invalid protocol ID. Client expects odd IDs only!");
            } else {
                switch (protocolId) {
                    case 1:
                        response = error("Not yet implemented!");
                        break;
                    case 3:
                        response = error("Not yet implemented!");
                        break;
                    case 5:
                        response = error("Not yet implemented!");
                        break;
                    case 7:
                        response = error("Not yet implemented!");
                        break;
                    default:
                        response = error("Invalid protocol ID. Client expects odd IDs <= 7 only!");
                        break;
                }
            }
        }

        return response;
    }

    private static Protocol error(String errorMessage) {
        return new Protocol.Builder()
                .setStatusId(Integer.MIN_VALUE)
                .setStatusMessage("Error:" + errorMessage).build();
    }
}
