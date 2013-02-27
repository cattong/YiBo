package net.dev123.commons.oauth;

public class OAuthAccessToken extends OAuthToken implements java.io.Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 450206950308198926L;

	private String username;
	private String userId;

	public OAuthAccessToken(String token, String tokenSecret) {
		super(token, tokenSecret);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		OAuthAccessToken that = (OAuthAccessToken) o;

		if (userId != null ? !userId.equals(that.userId) : that.userId != null)
			return false;
		if (username != null ? !username.equals(that.username) : that.username != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (username != null ? username.hashCode() : 0);
		result = 31 * result + (userId != null ? userId.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "AccessToken{"
				+ "token=\"" + getToken() + "\"" + ", "
				+ "tokenSecret=\"**********\"" + ", "
				+ "userId=\"" + userId + "\"" + ", "
				+ "username=\"" + username + "\""
				+ "}";
	}
}
