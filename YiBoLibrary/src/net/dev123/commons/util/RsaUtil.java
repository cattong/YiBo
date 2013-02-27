package net.dev123.commons.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import net.dev123.commons.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RsaUtil {
	private static final Logger logger = LoggerFactory.getLogger(RsaUtil.class.getSimpleName());
	private static final String CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	private static final String KEY_ALGORITHM = "RSA";

	public static String decryptWithPrivateKey(String encrypted,
			byte[] privateKeyBytes) {
		if (StringUtil.isEmpty(encrypted) || privateKeyBytes == null) {
			return encrypted;
		}
		try {
			return decrypt(encrypted, getPrivateKey(privateKeyBytes));
		} catch (Exception e) {
			if (Constants.DEBUG) {
				logger.error(e.getMessage(), e);
			}
			return encrypted;
		}
	}
	
	public static String decryptWithPublicKey(String encrypted,
			byte[] publicKeyBytes) {
		if (StringUtil.isEmpty(encrypted) || publicKeyBytes == null) {
			return encrypted;
		}
		try {
			return decrypt(encrypted, getPublicKey(publicKeyBytes));
		} catch (Exception e) {
			if (Constants.DEBUG) {
				logger.error(e.getMessage(), e);
			}
			return encrypted;
		}
	}

	public static String encryptWithPublicKey(String plain,
			byte[] publicKeyBytes) {
		if (StringUtil.isEmpty(plain) || publicKeyBytes == null) {
			return plain;
		}
		try {
			return encrypt(plain, getPublicKey(publicKeyBytes));
		} catch (Exception e) {
			if (Constants.DEBUG) {
				logger.error(e.getMessage(), e);
			}
			return plain;
		}
	}

	public static String encryptWithPrivateKey(String plain,
			byte[] privateKeyBytes) {
		if (StringUtil.isEmpty(plain) || privateKeyBytes == null) {
			return plain;
		}
		try {
			return encrypt(plain, getPrivateKey(privateKeyBytes));
		} catch (Exception e) {
			if (Constants.DEBUG) {
				logger.error(e.getMessage(), e);
			}
			return plain;
		}
	}

	private static PrivateKey getPrivateKey(byte[] privateKeyBytes) throws Exception {
		byte[] decodedKeyBytes = Base64.decodeBase64(privateKeyBytes);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(decodedKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		return keyFactory.generatePrivate(pkcs8KeySpec);
	}

	private static String decrypt(String encrypted, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, key);

		byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));
		return new String(plainBytes);
	}
	
	private static PublicKey getPublicKey(byte[] publicKeyBytes) throws Exception {
		byte[] decodedKeyBytes = Base64.decodeBase64(publicKeyBytes);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(
				decodedKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		return keyFactory.generatePublic(x509KeySpec);
	}

	private static String encrypt(String plain, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] encryptedBytes = cipher.doFinal(plain.getBytes());
		return new String(Base64.encodeBase64(encryptedBytes));
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
		String privateKey = new String(Base64.encodeBase64(privateKeyBytes));
		System.out.println(privateKey);
		byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
		String publicKey = new String(Base64.encodeBase64(publicKeyBytes));
		System.out.println(publicKey);

		String encrypted = RsaUtil.encryptWithPrivateKey("我靠我靠我靠",
				privateKey.getBytes());
		System.out.println(encrypted);
		String decrypted = RsaUtil.decryptWithPublicKey(encrypted,
				publicKey.getBytes());
		System.out.println(decrypted);

		String encrypted2 = RsaUtil.encryptWithPublicKey("我靠我靠我靠",
				publicKey.getBytes());
		System.out.println(encrypted2);
		String decrypted2 = RsaUtil.decryptWithPrivateKey(encrypted2,
				privateKey.getBytes());
		System.out.println(decrypted2);
	}

}
