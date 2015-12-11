package de.fhwedel.coinflipping.handling;

import de.fhwedel.coinflipping.config.ClientConfig;
import de.fhwedel.coinflipping.model.*;
import de.fhwedel.coinflipping.util.CryptoUtil;
import de.fhwedel.coinflipping.util.StringUtil;
import org.bouncycastle.jcajce.provider.asymmetric.sra.SRADecryptionKeySpec;

import java.math.BigInteger;

/**
 * Created by tim on 09.12.2015.
 */
public class ClientProtocolHandler {

    private static CryptoUtil mCryptoUtil;

    public static Protocol initiateProtocol() {
        return new Protocol.Builder()
                .setProtocolId(0)
                .setStatusId(Protocol.STATUS_ID_OK)
                .setStatusMessage(Protocol.STATUS_OK)
                .setProtocolNegotiation(new ProtocolNegotiation(new AvailableVersion(ClientConfig.SUPPORTED_PROTOCOL_VERSIONS)))
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
                        response = handleStep1(protocol);
                        break;
                    case 3:
                        response = handleStep3(protocol);
                        break;
                    case 5:
                        response = handleStep5(protocol);
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

    private static Protocol handleStep1(Protocol protocol) {
        String version = protocol.getProtocolNegotiation().getVersion();
        if (!StringUtil.isEmpty(version)) {
            protocol.setProtocolId(2);
            protocol.setKeyNegotiation(new KeyNegotiation(null, null, null, new AvailableSids(ClientConfig.SUPPORTED_SIDS)));
            return protocol;
        } else {
            return error("Received protocol version is null or empty!");
        }
    }

    private static Protocol handleStep3(Protocol protocol) {
        if (protocol.getKeyNegotiation() != null) {
            BigInteger p = protocol.getKeyNegotiation().getP();
            BigInteger q = protocol.getKeyNegotiation().getQ();
            Integer sidId = protocol.getKeyNegotiation().getSid();
            Sid sid = Sid.findById(sidId);

            try {
                mCryptoUtil = new CryptoUtil(sid, p, q);
                String encryptedCoin0 = mCryptoUtil.encryptString(ClientConfig.INITIAL_COINS[0]);
                String encryptedCoin1 = mCryptoUtil.encryptString(ClientConfig.INITIAL_COINS[1]);

                protocol.setProtocolId(4);
                Payload payload = new Payload.Builder()
                        .setInitialCoin(ClientConfig.INITIAL_COINS)
                        .setEncryptedCoin(encryptedCoin0, encryptedCoin1) // TODO: shuffle encrypted coins
                        .build();

                protocol.setPayload(payload);
                return protocol;
            } catch (Exception e) {
                return error("Something went wrong during coin encryption!");
            }
        } else {
            return error("Received key negotiation is null or unparsable!");
        }
    }

    private static Protocol handleStep5(Protocol protocol) {
        Payload payload = protocol.getPayload();
        if (payload != null) {
            String decryptedCoin = mCryptoUtil.decryptString(payload.getEnChosenCoin());
            if (decryptedCoin != null) {
                protocol.setProtocolId(6);
                payload.setDeChosenCoin(decryptedCoin);

                SRADecryptionKeySpec keySpec = mCryptoUtil.getKeySpec();
                if (keySpec != null) {
                    payload.setKeyA(keySpec.getE(), keySpec.getD());
                } else {
                    return error("Something went wrong during key extraction.");
                }

                return protocol;
            } else {
                return error("Something went wrong during coin decryption.");
            }
        } else {
            return error("Received payload is null or unparsable!");
        }
    }

    private static Protocol error(String errorMessage) {
        return new Protocol.Builder()
                .setStatusId(Integer.MIN_VALUE)
                .setStatusMessage("Error: " + errorMessage).build();
    }
}
