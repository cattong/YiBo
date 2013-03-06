package com.cattong.commons.oauth;

public class OAuth2 {

	public static final String VERSION_2_0 = "2.0";

	/** The encoding used to represent characters as bytes. */
	public static final String ENCODING = "UTF-8";

	/** The MIME type for a sequence of OAuth parameters. */
	public static final String FORM_ENCODED = "application/x-www-form-urlencoded";

	public static final String RESPONSE_TYPE = "response_type";
	public static final String GRANT_TYPE = "grant_type";
	public static final String DISPLAY_TYPE = "display";
	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String REDIRECT_URI = "redirect_uri";
	public static final String CODE = "code";
	public static final String SCOPE = "scope";
	public static final String STATE = "state";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String EXPIRES_IN = "expires_in";
	public static final String ERROR = "error";
	public static final String ERROR_DESCRIPTION = "error_description";
	public static final String ERROR_URI = "error_uri";

	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";

	public static final String ASSERTION_TYPE = "assertion_type";
	public static final String ASSERTION = "assertion";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String TOKEN_TYPE = "token_type";

	public enum ResponseType {
		CODE("code"),
		TOKEN("token"),
		CODE_AND_TOKEN("code_and_token");

		private String typeValue;

		private ResponseType(String typeValue) {
			this.typeValue = typeValue;
		}

		public String getTypeValue() {
			return typeValue;
		}
	}

	public enum GrantType {
		AUTHORIZATION_CODE("authorization_code"),
		RESOURCE_OWNER_PASSWORD_CREDENTIALS("password"),		
		IMPLICIT(""),		
		REFRESH_TOKEN("refresh_token");

		private String typeValue;
		private GrantType(String typeValue) {
			this.typeValue = typeValue;
		}

		public String getTypeValue() {
			return typeValue;
		}
	}
	
	public enum DisplayType {
		MOBILE("mobile"),
		PC("");
		
		private String typeValue;
		private DisplayType(String typeValue) {
			this.typeValue = typeValue;
		}

		public String getTypeValue() {
			return typeValue;
		}
	}
}
