package net.dev123.oauth;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.dev123.commons.http.auth.OAuth2AuthorizeHelper;
import net.dev123.commons.oauth2.OAuth2.GrantType;
import net.dev123.commons.oauth2.OAuth2AccessToken;
import net.dev123.sns.TokenConfig;

import org.junit.Test;

public class OAuth2AuthorizeHelperTest {

	@Test
	public void testAuthorizationCode() {
		OAuth2AccessToken accessToken = null;
		try {
			OAuth2AuthorizeHelper oauthHelper = new OAuth2AuthorizeHelper(TokenConfig.currentProvider);
			String authorzieUrl = oauthHelper.getAuthrizationUrl(GrantType.AUTHORIZATION_CODE, null);
			System.out.println(authorzieUrl);
			BareBonesBrowserLaunch.openURL(authorzieUrl);
			String code = null;
			while (null == code || code.trim().length() == 0) {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Please Enter Authorization Code : ");
				code = br.readLine();
			}
			accessToken = oauthHelper.getAccessTokenByAuthorizationCode(code, null);
			//accessToken = oauthHelper.getAccessTokenByRefreshToken(accessToken.getRefreshToken(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotNull(accessToken);
	}

	@Test
	public void testImplicitGrant() {
		OAuth2AccessToken accessToken = null;
		try {
			OAuth2AuthorizeHelper oauthHelper = new OAuth2AuthorizeHelper(TokenConfig.currentProvider);
			String authorzieUrl = oauthHelper.getAuthrizationUrl(GrantType.IMPLICIT, null);
			BareBonesBrowserLaunch.openURL(authorzieUrl);
			String url = null;
			while (null == url || url.trim().length() == 0) {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Please Enter implicit Callback : ");
				url = br.readLine();
			}
			accessToken = OAuth2AuthorizeHelper.retrieveAccessTokenFromFragment(url);
			System.out.println(accessToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotNull(accessToken);
	}

}
