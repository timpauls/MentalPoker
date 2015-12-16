package de.fhwedel.coinflipping.util;

import de.fhwedel.coinflipping.model.Sid;
import org.bouncycastle.jcajce.provider.asymmetric.sra.SRADecryptionKeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.sra.SRAKeyGenParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by tim on 10.12.15.
 */
public class CryptoUtil {

    private KeyPair mKeyPair;
    private Sid mSid;
    private Cipher mEngine;
    private Cipher mNoPaddingEngine;


    public CryptoUtil(Sid sid) {
        init(sid);
        mKeyPair = createNewKeyPair();
    }

    public CryptoUtil(Sid sid, BigInteger p, BigInteger q) {
        init(sid);
        mKeyPair = createKeyPair(p, q);
    }

    private void init(Sid sid) {
        this.mSid = sid;
        Security.addProvider(new BouncyCastleProvider());

        try {
            mEngine = Cipher.getInstance("SRA/NONE/OAEPWITH" + mSid.getAlgorithm() + "ANDMGF1PADDING", BouncyCastleProvider.PROVIDER_NAME);
            mNoPaddingEngine = Cipher.getInstance("SRA", BouncyCastleProvider.PROVIDER_NAME);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            throw new IllegalArgumentException("Could not create CryptoUtil! Invalid params. ", e);
        }
    }

    private KeyPair createNewKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("SRA", BouncyCastleProvider.PROVIDER_NAME);
            generator.initialize(mSid.getModulusSize());
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            Log.error("Error creating key pair!");
            return null;
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

    public String useEngine(boolean encrypt, String input, Key key, boolean usePadding, boolean isHexInput, boolean isHexOutput) {
        byte[] inBytes;
        byte[] outBytes;
        int cipherMode;
        Cipher engine;

        if (isHexInput) {
            inBytes = Hex.decode(input);
        } else {
            inBytes = input.getBytes();
        }

        if (encrypt) {
            cipherMode = Cipher.ENCRYPT_MODE;

            if (key == null) {
                key = mKeyPair.getPublic();
            }
        } else {
            cipherMode = Cipher.DECRYPT_MODE;

            if (key == null) {
                key = mKeyPair.getPrivate();
            }
        }

        if (usePadding) {
            engine = mEngine;
        } else {
            engine = mNoPaddingEngine;
        }

        try {
            engine.init(cipherMode, key);
            outBytes = engine.doFinal(inBytes);

            if (isHexOutput) {
                return Hex.toHexString(outBytes);
            } else {
                return new String(outBytes);
            }
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            return null;
        }
    }

    public SRADecryptionKeySpec getKeySpec(PrivateKey privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("SRA", BouncyCastleProvider.PROVIDER_NAME);
            return keyFactory.getKeySpec(privateKey, SRADecryptionKeySpec.class);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            return null;
        }
    }

    public SRADecryptionKeySpec getKeySpec() {
        return getKeySpec(mKeyPair.getPrivate());
    }

    public PrivateKey getTheirPrivateKey(BigInteger theirE, BigInteger theirD) {
        SRADecryptionKeySpec myKeySpec = getKeySpec();
        SRADecryptionKeySpec theirKeySpec = new SRADecryptionKeySpec(myKeySpec.getP(), myKeySpec.getQ(), theirD, theirE);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("SRA");
            return keyFactory.generatePrivate(theirKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            return null;
        }
    }
}
