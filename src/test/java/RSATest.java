import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tim on 21.10.2015.
 */
public class RSATest {
    private static final int KEY_SIZE = 1024;

    private static final String PLAIN_TEXT = "A quick movement of the enemy will jeopardize six gunboats";

    @Before
    public void setUp() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
    }


    @After
    public void tearDown() throws Exception {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
    }

    @Test
    public void testRSA() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        generator.initialize(KEY_SIZE);

        KeyPair keyPair = generator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        RSAEngine rsaEngine = new RSAEngine();

        System.out.println("Plain: " + PLAIN_TEXT);

        rsaEngine.init(true, PublicKeyFactory.createKey(publicKey.getEncoded()));
        byte[] cipher = rsaEngine.processBlock(PLAIN_TEXT.getBytes(), 0, PLAIN_TEXT.getBytes().length);
        System.out.println("Cipher: " + Hex.toHexString(cipher));

        rsaEngine.init(false, PrivateKeyFactory.createKey(privateKey.getEncoded()));
        byte[] decrypted = rsaEngine.processBlock(cipher, 0, cipher.length);
        System.out.println("Decrypted: " + new String(decrypted));

        assertThat(new String(decrypted)).isEqualTo(PLAIN_TEXT);
    }

    @Test
    public void testRSA2() throws Exception {
        RSAKeyPairGenerator rsaKeyPairGenerator = new RSAKeyPairGenerator();
        rsaKeyPairGenerator.init(new RSAKeyGenerationParameters(new BigInteger("7"), SecureRandom.getInstance("SHA1PRNG"), KEY_SIZE, 5));
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = rsaKeyPairGenerator.generateKeyPair();

        RSAEngine rsaEngine = new RSAEngine();

        System.out.println("Plain: " + PLAIN_TEXT);

        rsaEngine.init(true, asymmetricCipherKeyPair.getPublic());
        byte[] cipher = rsaEngine.processBlock(PLAIN_TEXT.getBytes(), 0, PLAIN_TEXT.getBytes().length);
        System.out.println("Cipher: " + Hex.toHexString(cipher));

        rsaEngine.init(false, asymmetricCipherKeyPair.getPrivate());
        byte[] decrypted = rsaEngine.processBlock(cipher, 0, cipher.length);
        System.out.println("Decrypted: " + new String(decrypted));

        assertThat(new String(decrypted)).isEqualTo(PLAIN_TEXT);
    }
}
