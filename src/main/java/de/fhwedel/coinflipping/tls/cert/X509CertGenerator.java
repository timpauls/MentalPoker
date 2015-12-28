package de.fhwedel.coinflipping.tls.cert;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;

import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Class used to generate certificates using BC and java.security. Can create a
 * self-signed root certificate and can sign certificates using the root
 * certificate.
 * 
 * The generated certificates are saved inKeyStore files, to be used with the
 * Java Secure Socket Extension (JSSE).
 * 
 * Sources: http://www.bouncycastle.org/wiki/display/JA1/BC+Version+2+APIs
 * http:/
 * /stackoverflow.com/questions/29852290/self-signed-x509-certificate-with-
 * bouncy-castle-in-java
 * 
 * @author tevua
 * @version 2.2
 */
public class X509CertGenerator {

	/** the key pair which includes the private key used to sign certificates */
	private KeyPair pair;
	/** the issuer of the certificates (aka the ca) */
	private X500Name issuer;
	/** what the serial number start with/the last used serial number */
	private BigInteger serialNumber;
	/** true if a root exists */
	private boolean rootExists;

	/**
	 * Constructor. Either loadRoot() or createRoot() has to be called before
	 * any other method is called (otherwise stuff is not working).
	 * 
	 * @param is
	 *            the issuer of the certificate
	 * @param serialNumberStartsAt
	 *            what the serial numbers start with
	 */
	public X509CertGenerator(BigInteger serialNumberStartsAt) {
		// adds the Bouncy castle provider to java security
		Security.addProvider(new BouncyCastleProvider());
		this.rootExists = false;
		this.serialNumber = serialNumberStartsAt;
	}

	/**
	 * Creates the root certificate and stores it in two files. To do so, it
	 * will create a rsa key pair. If you don't want to create a RSA key pair,
	 * use the other method createRootGivenKey().
	 * 
	 * @param strength
	 *            the strength of the rsa key pair
	 * @param filename
	 *            the name of the files (they will be called <filename>.public
	 *            and <filename>.private) - both will contain the certificate,
	 *            but only the private one will contain the private key
	 * @param pw
	 *            the password used to secure both keystores stored in the two
	 *            files (it's the same for both)
	 * @param alias
	 *            the alias used to store the entries (the same alias will be
	 *            used for all entries) * @param isCA true if the certificate is
	 *            supposed to be a ca (can be used to sign certificates)
	 * @throws Exception
	 *             if something goes wrong with the keystores or with the
	 *             writing of the file
	 */
	public void createRoot(X500Name is, int strength, String filename, String pw, String alias, boolean isCA)
			throws Exception {
		this.rootExists = true;
		this.issuer = is;
		this.pair = generateRSAKeyPair(strength);
		X509Certificate cert = generateX509Certificate(this.issuer, (RSAPublicKey) this.pair.getPublic(), isCA);
		this.storePrivate(cert, this.pair.getPrivate(), alias, pw, filename);
		this.storePublic(cert, alias, pw, filename);

	}

	/**
	 * Creates a certificate from a given key pair and stores it in two
	 * corresponding files. The given key pair has to be a RSA key pair.
	 * 
	 * @param pubKey
	 *            the public key of the key pair
	 * @param privKey
	 *            the private Key of the key pair
	 * @param filename
	 *            the name of the files (they will be called <filename>.public
	 *            and <filename>.private) - both will contain the certificate,
	 *            but only the private one will contain the private key
	 * @param pw
	 *            the password used to secure both keystores stored in the two
	 *            files (it's the same for both)
	 * @param alias
	 *            the alias used to store the entries (the same alias will be
	 *            used for all entries) * @param isCA true if the certificate is
	 *            supposed to be a ca (can be used to sign certificates)
	 * @throws Exception
	 *             if something goes wrong with the keystores or with the
	 *             writing of the file
	 */
	public void createRootGivenKey(X500Name is, RSAPublicKey pubKey, RSAPrivateKey privKey, String filename, String pw,
			String alias, boolean isCA) throws Exception {
		this.rootExists = true;
		this.issuer = is;
		this.pair = new KeyPair(pubKey, privKey);
		X509Certificate cert = generateX509Certificate(this.issuer, (RSAPublicKey) this.pair.getPublic(), isCA);
		this.storePrivate(cert, this.pair.getPrivate(), alias, pw, filename);
		this.storePublic(cert, alias, pw, filename);
	}

	/**
	 * Loads an existing keystore file that includes a certificate entry and a
	 * private key entry. The key pair constructed from those can afterwards be
	 * used to generate new certificates.
	 * 
	 * @param filename
	 *            the name of the keystore file (it should end with ".private")
	 * @param pw
	 *            the password of the keystore file
	 * @param alias
	 *            the alias which was used to store the certificate and
	 *            corresponding private key
	 * @throws KeyStoreException
	 *             when something goes wrong with the key store
	 * @throws NoSuchAlgorithmException
	 *             when you cannot get the private key
	 * @throws CertificateException
	 * @throws IOException
	 *             something goes wrong when reading the file
	 * @throws UnrecoverableKeyException
	 */
	public void loadRoot(String filename, String pw, String alias) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableKeyException {
		this.rootExists = true;

		File keystoreFile = new File(filename);
		char[] password = pw.toCharArray();

		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

		// Load the keystore contents
		FileInputStream in = new FileInputStream(keystoreFile);
		ks.load(in, password);
		in.close();

		java.security.cert.Certificate cert = ks.getCertificate(alias);
		if (cert == null) {
			throw new IllegalStateException("Alias is wrong. There is no certificate for given alias.");
		}

		// this.issuer = new X500Name(((X509Certificate)
		// cert).getIssuerX500Principal().getName());
		// CREATES AN X500 CA SUBJECT FOR ISSUER

		this.issuer = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();

		PublicKey pubKey = ks.getCertificate(alias).getPublicKey();
		PrivateKey privKey = (RSAPrivateKey) ks.getKey(alias, password);

		this.pair = new KeyPair(pubKey, privKey);

	}

	/**
	 * Creates a certificate (and also a new RSA key pair to be used with the
	 * certificate)
	 * 
	 * @param strength
	 *            the strength of the new key pair
	 * @param filename
	 *            the name of the file that should be used to store the
	 *            certificate (file will be called <filename>.private)
	 * @param pw
	 *            the password for the keystore
	 * @param alias
	 *            the alias used to store the entries in the key store
	 * @param subject
	 *            the subject of the certificate * @param isCA true if the
	 *            certificate is supposed to be a ca (can be used to sign
	 *            certificates)
	 * @throws Exception
	 *             if something goes wrong with the keystore or the file
	 *             operations
	 */
	public void createCert(int strength, String filename, String pw, String alias, X500Name subject, boolean isCA)
			throws Exception {
		KeyPair keyPair = generateRSAKeyPair(strength);
		X509Certificate cert = generateX509Certificate(subject, (RSAPublicKey) keyPair.getPublic(), isCA);
		this.storePrivate(cert, keyPair.getPrivate(), alias, pw, filename);

	}

	/**
	 * Creates and signs a certificate (from a given public key).
	 * 
	 * @param filename
	 *            the name of the file the certificate will be store (file will
	 *            be called <filename>.public)
	 * @param pw
	 *            the password of the keystore
	 * @param alias
	 *            the alias used to store the certificate entry in the key store
	 * @param subject
	 *            the subject of the certicate
	 * @param pubKey
	 *            the public key that is supposed to be signed
	 * @param isCA
	 *            true if the certificate is supposed to be a ca (can be used to
	 *            sign certificates)
	 * @throws Exception
	 *             if something goes wrong with the keystore or the file
	 *             operations
	 */
	public void createCertGivenKey(String filename, String pw, String alias, X500Name subject, RSAPublicKey pubKey,
			boolean isCA) throws Exception {
		X509Certificate cert = generateX509Certificate(subject, pubKey, isCA);
		this.storePublic(cert, alias, pw, filename);
	}

	/**
	 * Returns the current serial number (which is going to be the serial number
	 * for the next certificate created).
	 * 
	 * @return the current serial number.
	 */
	public BigInteger getSerialNumber() {
		return this.serialNumber;
	}

	/**
	 * Generates an RSA Key Pair.
	 * 
	 * @param strength
	 *            the strength of the newly created key pair
	 * @return the key pair
	 * @throws NoSuchAlgorithmException
	 *             if the key pair generator thinks there is no algorithm for
	 *             generating RSA stuff...
	 */
	private KeyPair generateRSAKeyPair(int strength) throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(strength);
		return keyPairGenerator.genKeyPair();
	}

	/**
	 * Create the public key info needed for the certificate generation.
	 * 
	 * @param modulus
	 *            the modulus of the key
	 * @param pubExponent
	 *            the public exponent
	 * @return the public key info
	 */
	private SubjectPublicKeyInfo getPublicKeyInfo(BigInteger modulus, BigInteger pubExponent) {
		try {
			return SubjectPublicKeyInfoFactory
					.createSubjectPublicKeyInfo(new RSAKeyParameters(false, modulus, pubExponent));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Generate a signed X509 certificate using BC.
	 * 
	 * @param subject
	 *            the subject of the certificate
	 * @param pubKey
	 *            the public key that is supposed to be signed
	 * @return the signed certificate
	 * @throws Exception
	 *             if something goes wrong.. obviously
	 */
	private X509Certificate generateX509Certificate(X500Name subject, RSAPublicKey pubKey, boolean isCA)
			throws Exception {
		if (!this.rootExists) {
			throw new IllegalStateException("Certificate to sign certificates is missing.");
		}
		ContentSigner sigGen;

		sigGen = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(this.pair.getPrivate());

		// RSAKeyParameters pub = (RSAKeyParameters) pair.getPublic();
		// RSAPublicKey pubKey = (RSAPublicKey) pair.getPublic();

		SubjectPublicKeyInfo subPubKeyInfo = getPublicKeyInfo(pubKey.getModulus(), pubKey.getPublicExponent());

		Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000); // yesterday
		Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000); // in
																							// a
																							// //
																							// year

		// X500Name("C=GERMANY,L=Wedel,O=FH Wedel, OU=ITS Project WS1516,
		// CN=Mental Poker")
		X509v3CertificateBuilder v3CertGen = new X509v3CertificateBuilder(this.issuer, this.serialNumber, startDate,
				endDate, subject, subPubKeyInfo);
		v3CertGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(isCA));

		X509CertificateHolder certHolder = v3CertGen.build(sigGen);

		// Extension extension = new Extension();
		// Need this extension to signify that this certificate is a CA and
		// can issue certificates. (Extension is marked as critical)
		// v3CertGen.addExtension( X509Extensions.BasicConstraints, true, new
		// BasicConstraints(
		// NUM_ALLOWED_INTERMEDIATE_CAS ) );

		// serial number is increased by one
		this.serialNumber = this.serialNumber.add(BigInteger.ONE);

		X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

		return cert;
	}

	/**
	 * Stores a certificate in a keystore file.
	 * 
	 * @param cert
	 *            the certificate
	 * @param alias
	 *            the alias used to store the certificate
	 * @param password
	 *            the password for the keystore
	 * @param keystoreFile
	 *            the keystore File
	 * @param ks
	 *            the keystore
	 * @throws Exception
	 *             thrown if file operation/keystore operation go wrong
	 */
	private void storeCert(X509Certificate cert, String alias, char[] password, File keystoreFile, KeyStore ks)
			throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {

		if (!keystoreFile.exists()) {
			keystoreFile.createNewFile();
			ks.load(null, null);
		} else {
			// Load the keystore contents
			FileInputStream in = new FileInputStream(keystoreFile);
			ks.load(in, password);
			in.close();
		}

		// Add the certificate
		ks.setCertificateEntry(alias, cert);

		// Save the new keystore contents
		FileOutputStream out = new FileOutputStream(keystoreFile);
		ks.store(out, password);
		out.close();
	}

	/**
	 * Stores the certificate in a file that can be distribueted publicly.
	 * 
	 * @param cert
	 *            the certificate
	 * @param alias
	 *            the alias used to store the certificate
	 * @param pw
	 *            the password for the keystore
	 * @param filename
	 *            the filename (file will be called <filename>.public)
	 * @throws Exception
	 *             thrown if file operations/keystore operation go wrong
	 */
	private void storePublic(X509Certificate cert, String alias, String pw, String filename)
			throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		File keystoreFile = new File(filename + ".public");
		char[] password = pw.toCharArray();
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		storeCert(cert, alias, password, keystoreFile, ks);

	}

	/**
	 * Stores the "private" certificate (which means the certificate itself and
	 * the private key) in a newly created file. File should not be distributed
	 * but kept private.
	 * 
	 * @param cert
	 *            the certificate
	 * @param privKey
	 *            the private Key
	 * @param alias
	 *            the alias which should be used to store the certificate in the
	 *            KeyStore
	 * @param pw
	 *            the password for the KeyStore
	 * @param filename
	 *            name of the file (the file will eventually be called
	 *            <filename>.private
	 * @throws Exception
	 *             thrown if file operations/keystore operation go wrong
	 */
	private void storePrivate(X509Certificate cert, PrivateKey privKey, String alias, String pw, String filename)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

		File keystoreFile = new File(filename + ".private");
		char[] password = pw.toCharArray();
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		storeCert(cert, alias, password, keystoreFile, ks);

		// now add the private key to the new keystore
		ks.load(null, password);

		PrivateKeyEntry entry = new PrivateKeyEntry(privKey, new java.security.cert.Certificate[] { cert });
		ks.setEntry(alias, entry, new KeyStore.PasswordProtection(password));

		FileOutputStream fos = new FileOutputStream(keystoreFile);
		ks.store(fos, password);
		fos.close();
	}
}