package com.cattong.oauth;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.http.auth.OAuth2AuthorizeHelper;
import com.cattong.commons.oauth.OAuth2.DisplayType;
import com.cattong.commons.oauth.OAuth2.GrantType;
import com.cattong.sns.TokenConfig;

public class OAuth2AuthorizeHelperTest {

	@Test
	public void testAuthorizationCode() {
		Authorization auth = new Authorization(Config.SP);
		try {
			OAuth2AuthorizeHelper oauthHelper = new OAuth2AuthorizeHelper();
			String authorzieUrl = oauthHelper.getAuthorizeUrl(auth, GrantType.AUTHORIZATION_CODE, DisplayType.PC);
			System.out.println(authorzieUrl);
			BareBonesBrowserLaunch.openURL(authorzieUrl);
			String code = null;
			while (null == code || code.trim().length() == 0) {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Please Enter Authorization Code : ");
				code = br.readLine();
			}
			
			auth = oauthHelper.retrieveAccessToken(auth, code, null);
		} catch (Exception e) {
			e.printStackTrace();
			auth = null;
		}
		
		assertNotNull(auth);
	}

	@Test
	public void testImplicitGrant() {
		Authorization auth = new Authorization(TokenConfig.currentProvider);
		try {
			OAuth2AuthorizeHelper oauthHelper = new OAuth2AuthorizeHelper();
			String authorzieUrl = oauthHelper.getAuthorizeUrl(auth, GrantType.IMPLICIT, DisplayType.PC);
			BareBonesBrowserLaunch.openURL(authorzieUrl);
			String url = null;
			while (null == url || url.trim().length() == 0) {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Please Enter implicit Callback : ");
				url = br.readLine();
			}
			
			auth = OAuth2AuthorizeHelper.retrieveAccessTokenFromFragment(url);
			
		} catch (Exception e) {
			e.printStackTrace();
			auth = null;
		}
		
		assertNotNull(auth);
	}

}
