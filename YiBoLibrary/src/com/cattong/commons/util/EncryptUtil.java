package com.cattong.commons.util;

import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * <p>
 * 说明：
 * 本工具类中的DES加密不能用于不同平台之间传输数据时加密，
 * 因Cipher在init时未完全明确指定相关参数，导致init时会使用到平台默认参数，
 * 而不同平台默认参数不同，故无法用于数据传输加密；
 * 同时，本工具类中DES加密也不适用于使用不同密钥并发调用的情况，
 * 密钥相同的情况下没有问题。
 * </p>
 *
 */
public class EncryptUtil {
	private static MessageDigest MD5_DIGEST = null;
	private static MessageDigest SHA_DIGEST = null;
	private static Cipher DES_CYPHER = null;

	static {
		try {
		     MD5_DIGEST = MessageDigest.getInstance("MD5");
		     SHA_DIGEST = MessageDigest.getInstance("SHA-1");
		     DES_CYPHER = Cipher.getInstance("DES/ECB/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
    /*
     * 摘要算法: MD5
     */
	public static synchronized String getMD5(String srcTxt) {
		if (srcTxt == null) {
			return null;
		}

		MD5_DIGEST.update(srcTxt.getBytes());

		byte[] byteRes = MD5_DIGEST.digest();

		return new String(Hex.encode(byteRes));
	}

	/*
	 * 摘要算法: SHA
	 */
	public static String getSHA(String srcTxt) {
		if (srcTxt == null) {
			return null;
		}

	    SHA_DIGEST.update(srcTxt.getBytes());

		byte[] byteRes = SHA_DIGEST.digest();

		return new String(Hex.encode(byteRes));

	}

	public static String desEncrypt(String plain, byte[] keyBytes) {

		if (plain == null) {
			return null;
		}

		if (keyBytes == null || keyBytes.length != 8) {
			throw new InvalidParameterException("DES key must be 8 bytes ");
		}

		String encrypted = plain;
		if (DES_CYPHER != null) {
			try {
				SecretKey key = new SecretKeySpec(keyBytes, "DES");
				DES_CYPHER.init(Cipher.ENCRYPT_MODE, key);
				encrypted = new String(Base64.encodeBase64(DES_CYPHER.doFinal(plain.getBytes())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return encrypted;
	}

	public static String desDecrypt(String encrypted, byte[] keyBytes) {

		if (encrypted == null) {
			return null;
		}

		if (keyBytes == null || keyBytes.length != 8) {
			throw new InvalidParameterException("DES key must be 8 bytes ");
		}

		String plain = encrypted;
		if (DES_CYPHER != null) {
			try {
				SecretKey key = new SecretKeySpec(keyBytes, "DES");
				DES_CYPHER.init(Cipher.DECRYPT_MODE, key);
				plain = new String(DES_CYPHER.doFinal(Base64.decodeBase64(encrypted.getBytes())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return plain;
	}

}
