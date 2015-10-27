package de.fhwedel.mentalpoker;

import org.junit.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tim on 27.10.2015.
 */
public class CoinFlipTest {

    @Test
    public void testCoinFlipClients() throws Exception {
        CoinFlipClient alice = new CoinFlipClient(null, null);
        CoinFlipClient bob = new CoinFlipClient(alice.getP(), alice.getQ());

        String[] encryptedCoins = alice.getEncryptedCoins();

        String twiceEncryptedCoin = bob.getTwiceEncryptedCoin(encryptedCoins);

        String bobEncryptedCoin = alice.decryptCoin(twiceEncryptedCoin);

        String resultCoin = bob.decryptCoin(bobEncryptedCoin);

        assertThat(alice.isRandomStringCorrect(resultCoin)).isTrue();
        assertThat(alice.checkCalculations(resultCoin, bob.getKeyPair()));
        assertThat(bob.checkCalculations(resultCoin, alice.getKeyPair()));
    }
}
