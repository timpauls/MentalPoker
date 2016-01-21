package de.fhwedel.coinflipping.util;

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

            return (PrivateKey) keyStore.getKey("", password.toCharArray());
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            Log.error("Error reading key from file!", e);
        }
        return null;
    }
}
