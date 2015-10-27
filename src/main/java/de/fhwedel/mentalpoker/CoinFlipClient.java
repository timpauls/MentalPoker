package de.fhwedel.mentalpoker;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.engines.SRAEngine;
import org.bouncycastle.crypto.generators.SRAKeyPairGenerator;
import org.bouncycastle.crypto.params.SRAKeyGenerationParameters;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by tim on 26.10.2015.
 */
public class CoinFlipClient {

    private static final BigInteger P = new BigInteger("f75e80839b9b9379f1cf1128f321639757dba514642c206bbbd99f9a4846208b3e93fbbe5e0527cc59b1d4b929d9555853004c7c8b30ee6a213c3d1bb7415d03", 16);
    private static final BigInteger Q = new BigInteger("b892d9ebdbfc37e397256dd8a5d3123534d1f03726284743ddc6be3a709edb696fc40c7d902ed804c6eee730eee3d5b20bf6bd8d87a296813c87d3b3cc9d7947", 16);
    private static final int CERTAINTY = 5;
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String HEADS = "Heads";
    private static final String TAILS = "Tails";

    private final boolean isInitiator;
    private final SecureRandom secureRandom;
    private final String encryptedHeads;
    private final String encryptedTails;
    private final SRAEngine sraEngine;
    private final AsymmetricCipherKeyPair keyPair;
    private final BigInteger p;
    private final BigInteger q;
    private final String paddedHeads;
    private final String paddedTails;

    public CoinFlipClient(BigInteger p, BigInteger q) {
        try {
            secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Fatal: could not instantiate SecureRandom with algorithm " + SECURE_RANDOM_ALGORITHM);
        }

        if (null == p || null == q) {
            isInitiator = true;
            this.p = P;
            this.q = Q;
        } else {
            this.p = p;
            this.q = q;
            isInitiator = false;
        }

        keyPair = generateKeyPair(this.p, this.q);
        sraEngine = new SRAEngine();

        paddedHeads = Hex.toHexString((HEADS + createRandomString()).getBytes());
        paddedTails = Hex.toHexString((TAILS + createRandomString()).getBytes());

        encryptedHeads = encryptCoin(paddedHeads, keyPair);
        encryptedTails = encryptCoin(paddedTails, keyPair);
    }

    private AsymmetricCipherKeyPair generateKeyPair(BigInteger p, BigInteger q) {
        SRAKeyGenerationParameters keyGenerationParameters = new SRAKeyGenerationParameters(p, q, secureRandom, CERTAINTY);
        SRAKeyPairGenerator sraKeyPairGenerator = new SRAKeyPairGenerator();
        sraKeyPairGenerator.init(keyGenerationParameters);
        return sraKeyPairGenerator.generateKeyPair();
    }

    /**
     * Creates a secure random string. See http://stackoverflow.com/a/41156 for details
     * @return random string
     */
    private String createRandomString() {
        return new BigInteger(100, secureRandom).toString(32);
    }

    /**
     * Returns the encrypted coin messages in random order.
     * @return array containing the encrypted heads and tails messages in random order
     */
    public String[] getEncryptedCoins() {
        String[] result = new String[2];
        if(secureRandom.nextBoolean()) {
            result[0] = encryptedHeads;
            result[1] = encryptedTails;
        } else {
            result[0] = encryptedTails;
            result[1] = encryptedHeads;
        }
        return result;
    }

    private String pickRandomCoin(String[] coins) {
        if(secureRandom.nextBoolean()) {
            return coins[0];
        } else {
            return coins[1];
        }
    }

    private String encryptCoin(String coin, AsymmetricCipherKeyPair keyPair) {
        sraEngine.init(true, keyPair.getPublic());
        byte[] coinBytes = Hex.decode(coin);
        return Hex.toHexString(sraEngine.processBlock(coinBytes, 0, coinBytes.length));
    }

    private String decryptCoin(String encryptedCoin, AsymmetricCipherKeyPair keyPair) {
        sraEngine.init(false, keyPair.getPrivate());
        byte[] encryptedCoinBytes = Hex.decode(encryptedCoin);
        return Hex.toHexString(sraEngine.processBlock(encryptedCoinBytes, 0, encryptedCoinBytes.length));
    }

    public String decryptCoin(String encryptedCoin) {
        return decryptCoin(encryptedCoin, this.keyPair);
    }

    /**
     * Randomly picks one the provided encrypted coins and encrypts it with this clients key.
     * @param coins array of exactly two already encrypted coins
     * @return twice-encrypted coin
     */
    public String getTwiceEncryptedCoin(String[] coins) {
        String coin = pickRandomCoin(coins);
        return encryptCoin(coin, this.keyPair);
    }

    public boolean isRandomStringCorrect(String coin) {
        return coin.equals(paddedHeads) || coin.equals(paddedTails);
    }

    public AsymmetricCipherKeyPair getKeyPair() {
        return keyPair;
    }

    /**
     * Checks the algorithms calculations based on the knowledge of both key pairs involved.
     * @param coin the chosen result coin
     * @param otherKeyPair the other participant's key pair
     * @return true if all calculations were correct, false if there were errors
     */
    public boolean checkCalculations(String coin, AsymmetricCipherKeyPair otherKeyPair) {
        if (isInitiator) {
            String encryptedCoin = encryptCoin(coin, this.keyPair);
            String twiceEncryptedCoin = encryptCoin(encryptedCoin, otherKeyPair);
            String othersEncryptedCoin = decryptCoin(twiceEncryptedCoin, this.keyPair);
            String decryptedCoin = decryptCoin(othersEncryptedCoin, otherKeyPair);
            return coin.equals(decryptedCoin);
        } else {
            String encryptedCoin = encryptCoin(coin, otherKeyPair);
            String twiceEncryptedCoin = encryptCoin(encryptedCoin, this.keyPair);
            String myEncryptedCoin = decryptCoin(twiceEncryptedCoin, otherKeyPair);
            String decryptedCoin = decryptCoin(myEncryptedCoin, this.keyPair);
            return coin.equals(decryptedCoin);
        }
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }
}
