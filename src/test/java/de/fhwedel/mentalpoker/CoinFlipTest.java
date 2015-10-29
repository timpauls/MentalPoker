package de.fhwedel.mentalpoker;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tim on 27.10.2015.
 */
public class CoinFlipTest {

    private static final BigInteger P = new BigInteger("f75e80839b9b9379f1cf1128f321639757dba514642c206bbbd99f9a4846208b3e93fbbe5e0527cc59b1d4b929d9555853004c7c8b30ee6a213c3d1bb7415d03", 16);
    private static final BigInteger Q = new BigInteger("b892d9ebdbfc37e397256dd8a5d3123534d1f03726284743ddc6be3a709edb696fc40c7d902ed804c6eee730eee3d5b20bf6bd8d87a296813c87d3b3cc9d7947", 16);

    @Test
    public void testCheatingClientsWrongResultCoin() throws Exception {
        CoinFlipClient alice = new CoinFlipClient(P, Q, true);
        CoinFlipClient bob = new CoinFlipClient(alice.getP(), alice.getQ(), false);

        String[] encryptedCoins = alice.getEncryptedCoins();

        String twiceEncryptedCoin = bob.getTwiceEncryptedCoin(encryptedCoins);

        String bobEncryptedCoin = alice.decryptCoin(twiceEncryptedCoin);

        // Bob guesses a random string
        String resultCoin = Hex.toHexString("Tailsasdasdasdasdasd".getBytes());

        assertThat(alice.isRandomStringCorrect(resultCoin)).isFalse();
        assertThat(alice.checkCalculations(resultCoin, bob.getKeyPair())).isFalse();
        assertThat(bob.checkCalculations(resultCoin, alice.getKeyPair())).isFalse();
    }

    @Test
    public void testHonestCoinFlipClients() throws Exception {
        CoinFlipClient alice = new CoinFlipClient(P, Q, true);
        CoinFlipClient bob = new CoinFlipClient(alice.getP(), alice.getQ(), false);

        String[] encryptedCoins = alice.getEncryptedCoins();

        String twiceEncryptedCoin = bob.getTwiceEncryptedCoin(encryptedCoins);

        String bobEncryptedCoin = alice.decryptCoin(twiceEncryptedCoin);

        String resultCoin = bob.decryptCoin(bobEncryptedCoin);

        assertThat(alice.isRandomStringCorrect(resultCoin)).isTrue();
        assertThat(alice.checkCalculations(resultCoin, bob.getKeyPair())).isTrue();
        assertThat(bob.checkCalculations(resultCoin, alice.getKeyPair())).isTrue();
    }
}
