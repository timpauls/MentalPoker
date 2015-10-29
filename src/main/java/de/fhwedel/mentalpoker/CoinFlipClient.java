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

    private String pickedEncryptedCoin;
    private String receivedTwiceEncryptedCoin;
    private String[] encryptedCoins;

    public CoinFlipClient(BigInteger p, BigInteger q, boolean isInitiator) {
        try {
            secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Fatal: could not instantiate SecureRandom with algorithm " + SECURE_RANDOM_ALGORITHM);
        }

        this.p = p;
        this.q = q;
        this.isInitiator = isInitiator;

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
        if (isInitiator) {
            receivedTwiceEncryptedCoin = encryptedCoin;
        }
        return decryptCoin(encryptedCoin, this.keyPair);
    }

    /**
     * Randomly picks one the provided encrypted coins and encrypts it with this clients key.
     * @param coins array of exactly two already encrypted coins
     * @return twice-encrypted coin
     */
    public String getTwiceEncryptedCoin(String[] coins) {
        this.encryptedCoins = coins;
        String coin = pickRandomCoin(coins);
        pickedEncryptedCoin = coin;
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
            return coin.equals(decryptCoin(decryptCoin(receivedTwiceEncryptedCoin, this.keyPair), otherKeyPair));
        } else {
            // Bob checks if the initial list of coins he received from Alice contained both a 'Heads' and a 'Tails' coin
            String coin1 = new String(Hex.decode(decryptCoin(encryptedCoins[0], otherKeyPair)));
            String coin2 = new String(Hex.decode(decryptCoin(encryptedCoins[1], otherKeyPair)));
            boolean listWasValid = (coin1.startsWith(HEADS) && coin2.startsWith(TAILS)) || (coin1.startsWith(TAILS) && coin2.startsWith(HEADS));

            // Bob uses Alice's key to decrypt the coin he initially picked and compares it with the result
            String coinResultingFromPick = decryptCoin(pickedEncryptedCoin, otherKeyPair);
            boolean resultCoinEqualsDecryptedCoin = coin.equals(coinResultingFromPick);

            return listWasValid && resultCoinEqualsDecryptedCoin;
        }
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }
}
