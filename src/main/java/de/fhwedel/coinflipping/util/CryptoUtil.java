package de.fhwedel.coinflipping.util;

import de.fhwedel.coinflipping.model.Sid;
import org.bouncycastle.jcajce.provider.asymmetric.sra.SRAKeyGenParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;

/**
 * Created by tim on 10.12.15.
 */
public class CryptoUtil {

    private KeyPair mKeyPair;
    private Sid mSid;
    private Cipher mEngine;

    public CryptoUtil(Sid sid, BigInteger p, BigInteger q) {
        this.mSid = sid;
        Security.addProvider(new BouncyCastleProvider());

        try {
            mEngine = Cipher.getInstance("SRA/NONE/OAEPWITH" + mSid.getAlgorithm() + "ANDMGF1PADDING", BouncyCastleProvider.PROVIDER_NAME);
            mKeyPair = createKeyPair(p, q);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            throw new IllegalArgumentException("Could not create CryptoUtil! Invalid params. ", e);
        }
    }

    private KeyPair createKeyPair(BigInteger p, BigInteger q) {
        SRAKeyGenParameterSpec sraKeyGenParameterSpec = new SRAKeyGenParameterSpec(mSid.getModulusSize(), p, q);

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("SRA", BouncyCastleProvider.PROVIDER_NAME);
            generator.initialize(sraKeyGenParameterSpec);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            Log.error("Error creating key pair!");
            return null;
        }
    }

    public String encryptString(String plain) {
        try {
            mEngine.init(Cipher.ENCRYPT_MODE, mKeyPair.getPublic());
            byte[] cipherBytes = mEngine.doFinal(plain.getBytes());
            return Hex.encode(cipherBytes).toString();
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            return null;
        }
    }
}
