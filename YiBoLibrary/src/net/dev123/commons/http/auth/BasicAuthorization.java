package net.dev123.commons.http.auth;

import net.dev123.commons.ServiceProvider;

public class BasicAuthorization extends Authorization{

	private static final long serialVersionUID = -8412901905988800468L;

	private String userName;
	private String password;

	public BasicAuthorization(String username, String password, ServiceProvider serviceProvider) {
		super(serviceProvider);
		this.userName = username;
		this.password = password;
		this.authVersion = AUTH_VERSION_BASIC;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getAuthToken() {
		return userName;
	}

	@Override
	public String getAuthSecret() {
		return password;
	}

	@Override
	public String toString() {
		return "BasicAuthorization{"
		 			+ "serviceProvider=\"" + serviceProvider + "\""
		 			+ ", username=\"" + userName + "\""
		 			+ ", password=\"**********\""
					+ "}";
	}
}
