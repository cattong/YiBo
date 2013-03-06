package com.cattong.commons;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.Test;

import com.cattong.commons.util.Base64;
import com.cattong.commons.util.RsaUtil;

public class RsaUtilTest {

	@Test
	public void myTest() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		
		PrivateKey privateKey = keyPair.getPrivate();
		byte[] privateKeyBytes = privateKey.getEncoded();
		String privateKeyStr = new String(Base64.encodeBase64(privateKeyBytes));
		System.out.println("private key:" + privateKeyStr);
		
		PublicKey publicKey = keyPair.getPublic();
		byte[] publicKeyBytes = publicKey.getEncoded();
		String publicKeyStr = new String(Base64.encodeBase64(publicKeyBytes));
		System.out.println("public key:" + publicKeyStr);

		System.out.println("**************************");
		String plain = "我测试";
		String cipherText = RsaUtil.encrypt(plain, publicKey);
		System.out.println(cipherText);
		String dePlain = RsaUtil.decrypt(cipherText, privateKey);
		System.out.println(dePlain);
		
		System.out.println("**************************");
		String encrypted = RsaUtil.encryptWithPrivateKey("我靠我靠我靠",
				privateKeyStr.getBytes());
		System.out.println(encrypted);
		String decrypted = RsaUtil.decryptWithPublicKey(encrypted,
				publicKeyStr.getBytes());
		System.out.println(decrypted);
		System.out.println("**************************");
		
		String encrypted2 = RsaUtil.encryptWithPublicKey("我靠我靠我靠",
				publicKeyStr.getBytes());
		System.out.println(encrypted2);
		String decrypted2 = RsaUtil.decryptWithPrivateKey(encrypted2,
				privateKeyStr.getBytes());
		System.out.println(decrypted2);
	}
}
