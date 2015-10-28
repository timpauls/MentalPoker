package de.fhwedel.mentalpoker;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tim on 27.10.2015.
 */
public class CoinFlipTest {


    @Test
    public void testCheatingClientsWrongResultCoin() throws Exception {
        CoinFlipClient alice = new CoinFlipClient(null, null);
        CoinFlipClient bob = new CoinFlipClient(alice.getP(), alice.getQ());

        String[] encryptedCoins = alice.getEncryptedCoins();

        String twiceEncryptedCoin = bob.getTwiceEncryptedCoin(encryptedCoins);

        String bobEncryptedCoin = alice.decryptCoin(twiceEncryptedCoin);

        String resultCoin = Hex.toHexString("Tailsasdasdasdasdasd".getBytes());

        assertThat(alice.isRandomStringCorrect(resultCoin)).isFalse();
        assertThat(alice.checkCalculations(resultCoin, bob.getKeyPair())).isFalse();
        assertThat(bob.checkCalculations(resultCoin, alice.getKeyPair())).isFalse();
    }

    @Test
    public void testHonestCoinFlipClients() throws Exception {
        CoinFlipClient alice = new CoinFlipClient(null, null);
        CoinFlipClient bob = new CoinFlipClient(alice.getP(), alice.getQ());

        String[] encryptedCoins = alice.getEncryptedCoins();

        String twiceEncryptedCoin = bob.getTwiceEncryptedCoin(encryptedCoins);

        String bobEncryptedCoin = alice.decryptCoin(twiceEncryptedCoin);

        String resultCoin = bob.decryptCoin(bobEncryptedCoin);

        assertThat(alice.isRandomStringCorrect(resultCoin)).isTrue();
        assertThat(alice.checkCalculations(resultCoin, bob.getKeyPair())).isTrue();
        assertThat(bob.checkCalculations(resultCoin, alice.getKeyPair())).isTrue();
    }
}
