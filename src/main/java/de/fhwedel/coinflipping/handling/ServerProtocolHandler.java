package de.fhwedel.coinflipping.handling;

import de.fhwedel.coinflipping.config.ServerConfig;
import de.fhwedel.coinflipping.model.AvailableVersion;
import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.model.ProtocolNegotiation;
import de.fhwedel.coinflipping.util.CryptoUtil;

import java.util.*;

/**
 * Created by tim on 15.12.2015.
 */
public class ServerProtocolHandler extends ProtocolHandler {

    private static CryptoUtil mCryptoUtil;

    public static Protocol handleProtocolStep(Protocol protocol) {
        Protocol response;

        if (protocol == null) {
            // if the supplied protocol step is null an error must have occurred
            response = error("Received null or unparsable protocol!");
        } else if (!protocol.isValid()) {
            response = error("Received invalid protocol saying: '" + protocol.getStatusMessage() + "'");
        } else {
            Integer protocolId = protocol.getProtocolId();
            if (protocolId % 2 != 0) {
                response = error("Invalid protocol ID. Server expects even IDs only!");
            } else {
                switch (protocolId) {
                    case 0:
                        response = handleStep0(protocol);
                        break;
                    case 2:
                        response = error("Not yet implemented!");
                        break;
                    case 4:
                        response = error("Not yet implemented!");
                        break;
                    case 6:
                        response = error("Not yet implemented!");
                        break;
                    default:
                        response = error("Invalid protocol ID. Server expects even IDs <= 6 only!");
                        break;
                }
            }
        }

        return response;
    }

    private static Protocol handleStep0(Protocol protocol) {
        ProtocolNegotiation protocolNegotiation = protocol.getProtocolNegotiation();
        if (protocolNegotiation == null) {
            return error("Received protocolNegotiation is null or not parsable!");
        }

        List<AvailableVersion> availableVersions = protocolNegotiation.getAvailableVersions();
        if (availableVersions == null || availableVersions.size() != 1) {
            return error("Received availableVersions is null or does not contain exactly one element!");
        }

        ArrayList<String> versions = new ArrayList<>();
        versions.addAll(Arrays.asList(ServerConfig.SUPPORTED_PROTOCOL_VERSIONS));
        versions.retainAll(availableVersions.get(0).getVersions());

        Collections.sort(versions, new Comparator<String>() {
            @Override
            public int compare(String one, String other) {
                try {
                    return Float.valueOf(one).compareTo(Float.valueOf(other));
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });

        availableVersions.add(new AvailableVersion(ServerConfig.SUPPORTED_PROTOCOL_VERSIONS));
        protocolNegotiation.setVersion(versions.get(versions.size()-1));
        protocol.setProtocolId(1);

        return protocol;
    }
}
