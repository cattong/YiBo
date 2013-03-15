package com.cattong.oauth;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.http.auth.OAuth2AuthorizeHelper;
import com.cattong.commons.http.auth.OAuthAuthorizeHelper;
import com.cattong.commons.oauth.OAuth2.DisplayType;
import com.cattong.commons.oauth.OAuth2.GrantType;
import com.cattong.commons.oauth.config.OAuthConfig;

public class CustomAppKeyTest {
   
    
    @Test
	public void testOauth1CustomeAppKey() {
		Authorization auth = new Authorization(Config.SP);
		OAuthConfig oauthConfig = auth.getoAuthConfig();
		oauthConfig.setConsumerKey(Config.appkey);
		oauthConfig.setConsumerSecret(Config.appSecret);
		oauthConfig.setCallbackUrl(Config.callbackUrl);
		
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
	
	@Test
	public void testOauth2CustomeAppKey() {
		Authorization auth = new Authorization(Config.SP);
		OAuthConfig oauthConfig = auth.getoAuthConfig();
		oauthConfig.setConsumerKey(Config.appkey);
		oauthConfig.setConsumerSecret(Config.appSecret);
		oauthConfig.setCallbackUrl(Config.callbackUrl);
		
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
}
