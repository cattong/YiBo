package com.cattong.oauth;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.http.auth.OAuthAuthorizeHelper;
import com.cattong.commons.util.EncryptUtil;
import com.cattong.weibo.Config;

public class OAuthAuthorizeHelperTest {

	@Test
	public void testXAuth() {
		Authorization auth = new Authorization(Config.currentProvider);
		try {
			OAuthAuthorizeHelper oauthHelper = new OAuthAuthorizeHelper();
			auth.setAccessToken(Config.userName);
			auth.setAccessSecret(Config.password);
			auth = oauthHelper.retrieveAccessToken(auth);
			System.out.println(EncryptUtil.desEncrypt(auth.getAccessToken(), Config.KEY_BYTES));
			System.out.println(EncryptUtil.desEncrypt(auth.getAccessSecret(), Config.KEY_BYTES));

		} catch (LibException e) {
			e.printStackTrace();
		}
		
		assertNotNull(auth);
	}

	@Test
	public void testOAuth() {
		Authorization auth = new Authorization(Config.currentProvider);
		try {
			OAuthAuthorizeHelper oauthHelper = new OAuthAuthorizeHelper();
			auth = oauthHelper.retrieveRequestToken(auth);
			String authorizeUrl = oauthHelper.getAuthorizeUrl(auth);
			BareBonesBrowserLaunch.openURL(authorizeUrl);
			String verifier = null;
			while (null == verifier || verifier.trim().length() == 0) {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Please Enter verifier : ");
				verifier = br.readLine();
			}
			
			auth = oauthHelper.retrieveAccessToken(auth, verifier.trim());
		} catch (Exception e) {
			e.printStackTrace();
			auth = null;
		}
		
		assertNotNull(auth);
	}

}
