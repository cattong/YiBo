package net.dev123.oauth;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.dev123.commons.http.auth.OAuthAuthorizeHelper;
import net.dev123.commons.oauth.OAuthAccessToken;
import net.dev123.commons.oauth.OAuthRequestToken;
import net.dev123.commons.util.EncryptUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.Config;

import org.junit.Test;

public class OAuthAuthorizeHelperTest {

	@Test
	public void testXAuth() {
		OAuthAccessToken accessToken = null;
		try {
			OAuthAuthorizeHelper oauthHelper = new OAuthAuthorizeHelper(Config.currentProvider);
			accessToken = oauthHelper.retrieveOAuthAccessToken(Config.userName, Config.password);
			System.out.println(EncryptUtil.desEncrypt(accessToken.getToken(), Config.KEY_BYTES));
			System.out.println(EncryptUtil.desEncrypt(accessToken.getTokenSecret(), Config.KEY_BYTES));

		} catch (LibException e) {
			e.printStackTrace();
		}
		assertNotNull(accessToken);
	}

	@Test
	public void testOAuth() {
		OAuthAccessToken accessToken = null;
		try {
			OAuthAuthorizeHelper oauthHelper = new OAuthAuthorizeHelper(Config.currentProvider);
			OAuthRequestToken requestToken = oauthHelper.retrieveOAuthRequestToken();
			BareBonesBrowserLaunch.openURL(requestToken.getAuthorizationURL());
			String verifier = null;
			while (null == verifier || verifier.trim().length() == 0) {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Please Enter verifier : ");
				verifier = br.readLine();
			}
			accessToken = oauthHelper.retrieveOAuthAccessToken(requestToken, verifier.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotNull(accessToken);
	}

}
