package net.dev123.yibome.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.Constants;
import net.dev123.commons.util.DateTimeUtil;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.RsaUtil;
import net.dev123.yibome.entity.Account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountJSONConverter {

	public static JSONArray toJSONArray(List<? extends Account> accounts) 
	    throws JSONException {
		JSONArray jsonArray = new JSONArray();
		if (accounts == null || accounts.size() == 0) {
			return jsonArray;
		}

		for (Account account : accounts) {
			jsonArray.put(toJSON(account));
		}
		return jsonArray;
	}

	public static List<Account> toAccountList(JSONArray jsonArray) 
	    throws JSONException, ParseException {
		List<Account> accountList = new ArrayList<Account>();
		int length = jsonArray.length();
		for (int i = 0; i < length; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			accountList.add(toAccount(jsonObject));
		}
		return accountList;
	}

	public static JSONObject toJSON(Account account) throws JSONException {
		if (account == null) {
			return null;
		}
		JSONObject json = new JSONObject();
		json.put("user_id", account.getUserId());
		if (account.getUser() != null) {
			json.put("user", UserJSONConverter.toJSON(account.getUser()));
		}
		json.put("service_provider", account.getServiceProviderNo());
		
		json.put("auth_version", account.getAuthVersion());
		json.put("access_token", account.getAuthToken());
		String encryptedSecret = 
			RsaUtil.encryptWithPublicKey(account.getAuthSecret(), Constants.PUBLIC_KEY);
		json.put("token_secret", encryptedSecret);
				
		json.put("app_key", account.getAppKey());
		encryptedSecret = 
			RsaUtil.encryptWithPublicKey(account.getAppSecret(), Constants.PUBLIC_KEY);
		json.put("app_secret", encryptedSecret);
		
		json.put("state", account.getState());
		DateFormat dateFormat = DateTimeUtil.getGMTDateFormat();
		String createdTimeString = account.getCreatedAt() == null ?
			null : dateFormat.format(account.getCreatedAt());
		json.put("created_at", createdTimeString);
		json.put("is_default", account.isDefault());
		json.put("rest_proxy_url", account.getRestProxyUrl());
		json.put("search_proxy_url", account.getSearchProxyUrl());
		String expiresTimeString = account.getTokenExpiresAt() == null ?
				null : dateFormat.format(account.getTokenExpiresAt());
		json.put("token_expires_at", expiresTimeString);
		json.put("token_scopes", account.getTokenScopes());
		return json;
	}

	public static Account toAccount(JSONObject json) throws JSONException,
		ParseException {
		if (json == null) {
			return null;
		}
		Account account = new Account();
		account.setAccountId(0L);
		
		account.setAuthToken(ParseUtil.getRawString("access_token", json));
		String secretString = ParseUtil.getRawString("token_secret", json);
		String plainSecret =
			RsaUtil.decryptWithPublicKey(secretString, Constants.PUBLIC_KEY);
		account.setAuthSecret(plainSecret);
		
		account.setAppKey(ParseUtil.getRawString("app_key", json));
		secretString = ParseUtil.getRawString("app_secret", json);
		plainSecret = 
			RsaUtil.decryptWithPublicKey(secretString, Constants.PUBLIC_KEY);
		account.setAppSecret(plainSecret);
						
		account.setAuthVersion(ParseUtil.getInt("auth_version", json));
		account.setServiceProviderNo(ParseUtil.getInt("service_provider", json));
		if (json.isNull("state")) {
			account.setState(Account.STATE_SYNCED);
		} else {
			account.setState(ParseUtil.getInt("state", json));
		}
		account.setUserId(ParseUtil.getRawString("user_id", json));
		if (!json.isNull("user")) {
			account.setUser(UserJSONConverter.toUser(json.getJSONObject("user")));
		}
		account.setCreatedAt(ParseUtil.getDate("created_at", json));
		account.setDefault(ParseUtil.getBoolean("is_default", json));
		account.setRestProxyUrl(ParseUtil.getRawString("rest_proxy_url", json));
		account.setSearchProxyUrl(ParseUtil.getRawString("search_proxy_url", json));
		account.setTokenExpiresAt(ParseUtil.getDate("token_expires_at", json));
		account.setTokenScopes(ParseUtil.getRawString("token_scopes", json));
		return account;
	}
}
