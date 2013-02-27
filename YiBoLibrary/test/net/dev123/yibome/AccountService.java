package net.dev123.yibome;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.commons.http.auth.OAuthAuthorizeHelper;
import net.dev123.commons.oauth.OAuthToken;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.mblog.sina.Sina;
import net.dev123.yibome.entity.Account;

import org.junit.BeforeClass;
import org.junit.Test;

public class AccountService {
	//28804f4de111539bb6da840ff8e0c0f2,iZOLwAmL4H9vmLPdKkVIN19gFq2MHEDG
	private static YiBoMe yibome;

	@BeforeClass
	public static void beforClass() {
		OAuthAuthorization auth = new OAuthAuthorization("28804f4de111539bb6da840ff8e0c0f2", "fCQqqoClUltPScql4UaDJVqpqTSQo4fD", ServiceProvider.YiBoMe);
		yibome = new YiBoMeImpl(auth);
	}

	@Test
	public void testSyncUpload() throws LibException {
		Account account = getAccount("raise007", "24097410", ServiceProvider.Sina);

		List<Account> accounts = new ArrayList<Account>();
		accounts.add(account);
		System.out.println(yibome.syncAccounts(accounts));
	}

	@Test
	public void testSyncMerge() throws LibException{

		List<Account> accounts = new ArrayList<Account>();

		Account accountDel = new Account();
		accountDel.setAppKey("123");
		accountDel.setAuthSecret("123456");
		accountDel.setAuthToken("123456789");
		accountDel.setAuthVersion(1);
		accountDel.setServiceProviderNo(1);
		accountDel.setState(Account.STATE_ADDED);
		accountDel.setUserId("TestUserId");
		accounts.add(accountDel);

		Account accountAdd = new Account();
		accountAdd.setAppKey("TestKey");
		accountAdd.setAuthSecret("TestSecret");
		accountAdd.setAuthToken("TestToken");
		accountAdd.setAuthVersion(1);
		accountAdd.setServiceProviderNo(1);
		accountAdd.setState(Account.STATE_ADDED);
		accountAdd.setUserId("TestUserId" + System.currentTimeMillis());
		accounts.add(accountAdd);

		System.out.println(yibome.syncAccounts(accounts));


	}

	@Test
	public void testSyncDownloadAll() throws LibException{
		List<Account> accounts = new ArrayList<Account>();
		System.out.println(yibome.syncAccounts(accounts));
	}


	private Account getAccount(String username, String password, ServiceProvider sp) throws LibException {
		OAuthConfiguration oauthConfig = OAuthConfigurationFactory.getOAuthConfiguration(sp);
		OAuthAuthorizeHelper authHelper = new OAuthAuthorizeHelper(sp);
		OAuthToken token = authHelper.retrieveOAuthAccessToken(username, password);
		Authorization auth = new OAuthAuthorization(token, sp);
		MicroBlog mBlog = new Sina(auth);
		User user = mBlog.verifyCredentials();
		Account account = new Account();
		account.setAppKey(oauthConfig.getOAuthConsumerKey());
		account.setAuthSecret(token.getTokenSecret());
		account.setAuthToken(token.getToken());
		account.setAuthVersion(1);
		account.setServiceProviderNo(ServiceProvider.Sina.getServiceProviderNo());
		account.setState(Account.STATE_ADDED);
		account.setUser(user);

		return account;
	}

}
