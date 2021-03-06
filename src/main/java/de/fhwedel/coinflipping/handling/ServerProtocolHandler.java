package de.fhwedel.coinflipping.handling;

import de.fhwedel.coinflipping.config.ServerConfig;
import de.fhwedel.coinflipping.model.*;
import de.fhwedel.coinflipping.util.CryptoUtil;
import de.fhwedel.coinflipping.util.StringUtil;
import org.bouncycastle.jcajce.provider.asymmetric.sra.SRADecryptionKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
                        response = handleStep2(protocol);
                        break;
                    case 4:
                        response = handleStep4(protocol);
                        break;
                    case 6:
                        response = handleStep6(protocol);
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

    private static Protocol handleStep2(Protocol protocol) {
        KeyNegotiation keyNegotiation = protocol.getKeyNegotiation();

        if (keyNegotiation == null) {
            return error("Received keyNegotiation is null or not parsable!");
        }

        List<AvailableSids> availableSids = keyNegotiation.getAvailableSids();
        if (availableSids == null || availableSids.size() == 0) {
            return error("Received availableSids is null or empty!");
        }

        ArrayList<Integer> sids = new ArrayList<>();
        sids.addAll(Arrays.asList(ServerConfig.SUPPORTED_SIDS));
        sids.retainAll(availableSids.get(0).getSids());

        Collections.sort(sids);

        availableSids.add(new AvailableSids(ServerConfig.SUPPORTED_SIDS));
        Integer sidId = sids.get(sids.size() - 1);
        keyNegotiation.setSid(sidId);

        Sid sid = Sid.findById(sidId);

        try {
            mCryptoUtil = new CryptoUtil(sid);
            SRADecryptionKeySpec keySpec = mCryptoUtil.getKeySpec();
            keyNegotiation.setP(keySpec.getP());
            keyNegotiation.setQ(keySpec.getQ());
        } catch (Exception e) {
            return error("Something went wrong during key generation!");
        }

        protocol.setProtocolId(3);

        return protocol;
    }

    private static Protocol handleStep4(Protocol protocol) {
        Payload payload = protocol.getPayload();

        if (payload == null) {
            return error("Payload is null or not unparsable.");
        }

        List<String> initialCoin = payload.getInitialCoin();
        if (initialCoin == null || initialCoin.size() != 2) {
            return error("InitalCoin is null or does not contain exactly two entries!");
        }

        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            int randomPlainIndex = secureRandom.nextInt(2);
            payload.setDesiredCoin(payload.getInitialCoin().get(randomPlainIndex));

            List<String> encryptedCoin = payload.getEncryptedCoin();
            if (encryptedCoin == null || encryptedCoin.size() != 2) {
                return error("EncryptedCoin is null or does not contain exactly two entries!");
            }

            int randomEncryptedIndex = secureRandom.nextInt(2);
            String chosenEncryptedCoin = encryptedCoin.get(randomEncryptedIndex);
            String chosenDoubleEncryptedCoin = mCryptoUtil.useEngine(true, chosenEncryptedCoin, null, false, true, true);
            payload.setEnChosenCoin(chosenDoubleEncryptedCoin);
        } catch (NoSuchAlgorithmException e) {
            return error("Something went wrong when picking a coin.");
        }

        protocol.setProtocolId(5);
        return protocol;
    }

    private static Protocol handleStep6(Protocol protocol) {
        Payload payload = protocol.getPayload();
        if (payload == null) {
            return error("Received payload is null or unparsable!");
        }

        String deChosenCoin = payload.getDeChosenCoin();
        if (StringUtil.isEmpty(deChosenCoin)) {
            return error("DeChosenCoin is null or empty!");
        }

        SRADecryptionKeySpec keySpec = mCryptoUtil.getKeySpec();
        if (keySpec != null) {
            payload.setKeyB(keySpec.getE(), keySpec.getD());
        } else {
            return error("Something went wrong during key extraction.");
        }

        protocol.setProtocolId(7);
        return protocol;
    }

    public static Protocol determineWinner(Protocol protocol) {
        Payload payload = protocol.getPayload();
        String plainCoin = mCryptoUtil.useEngine(false, payload.getDeChosenCoin(), null, true, true, false);
        if (plainCoin == null) {
            return error("Something went wrong during coin decryption.");
        }

        String winner;
        if (plainCoin.equals(payload.getDesiredCoin())) {
            winner = "Server WINS!";
        } else {
            winner = "Server LOSES!";
        }

        protocol.setStatusMessage("Winning coin is: " + plainCoin + " (server desired coin " + payload.getDesiredCoin() + "). " + winner);
        protocol.setProtocolId(8);

        return protocol;
    }
}
