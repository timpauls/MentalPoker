package de.fhwedel.coinflipping.handling;

import de.fhwedel.coinflipping.config.ClientConfig;
import de.fhwedel.coinflipping.model.*;
import de.fhwedel.coinflipping.util.CryptoUtil;
import de.fhwedel.coinflipping.util.SignatureUtil;
import de.fhwedel.coinflipping.util.StringUtil;
import org.bouncycastle.jcajce.provider.asymmetric.sra.SRADecryptionKeySpec;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.List;

/**
 * Created by tim on 09.12.2015.
 *
 * TODO: convert error handling the form:
 *      if (errorCondition) {
 *          return error ("Error")
 *       }
 *
 *       non error code
 */
public class ClientProtocolHandler extends ProtocolHandler {

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
                        response = handleStep7(protocol);
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
                String encryptedCoin0 = mCryptoUtil.useEngine(true, ClientConfig.INITIAL_COINS[0], null, true, false, true);
                String encryptedCoin1 = mCryptoUtil.useEngine(true, ClientConfig.INITIAL_COINS[1], null, true, false, true);

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
            String decryptedCoin = mCryptoUtil.useEngine(false, payload.getEnChosenCoin(), null, false, true, true);
            if (decryptedCoin != null) {
                protocol.setProtocolId(6);
                payload.setDeChosenCoin(decryptedCoin);

                SRADecryptionKeySpec keySpec = mCryptoUtil.getKeySpec();
                if (keySpec != null) {
                    payload.setKeyA(keySpec.getE(), keySpec.getD());
                } else {
                    return error("Something went wrong during key extraction.");
                }

                String toBeSigned =     payload.getDesiredCoin()
                                    +   payload.getEnChosenCoin()
                                    +   Hex.toHexString(keySpec.getE().toByteArray())
                                    +   Hex.toHexString(keySpec.getD().toByteArray());

                PrivateKey privateKey = SignatureUtil.readPrivateKeyFromFile("ssl-certs/client", "fhwedel");
                if (privateKey != null) {
                    byte[] sign = SignatureUtil.sign(toBeSigned, privateKey);
                    if (sign != null) {
                        String signature = Hex.toHexString(sign);
                        payload.setSignatureA(signature);
                    } else {
                        return error("Something went wrong during signing.");
                    }
                } else {
                    return error("Something went wrong when trying to read private key from file.");
                }

                return protocol;
            } else {
                return error("Something went wrong during coin decryption.");
            }
        } else {
            return error("Received payload is null or unparsable!");
        }
    }

    private static Protocol handleStep7(Protocol protocol) {
        Payload payload = protocol.getPayload();
        if (payload != null) {
            List<BigInteger> theirKey = payload.getKeyB();
            PrivateKey theirPrivateKey = mCryptoUtil.getTheirPrivateKey(theirKey.get(0), theirKey.get(1));
            if (theirPrivateKey != null) {
                String coin = mCryptoUtil.useEngine(false, payload.getDeChosenCoin(), theirPrivateKey, true, true, false);

                if (coin != null) {
                    String winner;
                    if (coin.equals(payload.getDesiredCoin())) {
                        winner = "You LOSE!";
                    } else {
                        winner = "You WIN!";
                    }

                    protocol.setStatusMessage("Winning coin is: " + coin + " (server desired coin " + payload.getDesiredCoin() + "). " + winner);
                    protocol.setProtocolId(8);
                    return protocol;
                } else {
                    return error("Something went wrong when decrypting the coin!");
                }
            } else {
                return error("Something went wrong when retrieving their key!");
            }
        } else {
            return error("Received payload is null or unparsable!");
        }
    }
}
