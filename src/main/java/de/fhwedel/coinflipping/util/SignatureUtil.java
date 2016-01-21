package de.fhwedel.coinflipping.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by tim on 21.01.16.
 */
public class SignatureUtil {

    public static PrivateKey readPrivateKeyFromFile(String fileName, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream(new File(fileName)), password.toCharArray());

            String alias = keyStore.aliases().nextElement();

            return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            Log.error("Error reading key from file!", e);
        }
        return null;
    }

    public static byte[] sign(String message, PrivateKey privateKey) {
        Security.addProvider(new BouncyCastleProvider());

        Signature signature = null;
        try {
            signature = Signature.getInstance("SHA256with" + privateKey.getAlgorithm(), "BC");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            return signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | NoSuchProviderException e) {
            Log.error("Error signing message", e);
        }
        return null;
    }
}
