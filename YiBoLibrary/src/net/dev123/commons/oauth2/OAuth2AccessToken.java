package net.dev123.commons.oauth2;

import java.util.Date;

public class OAuth2AccessToken implements java.io.Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 450206950308198926L;

	private String accessToken;
	private String tokenType;
	private Date expiresDate;
	private String refreshToken;
	private String scope;

	public OAuth2AccessToken(String accessToken, Date expiresDate) {
		this.accessToken = accessToken;
		this.expiresDate = expiresDate;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public Date getExpiresDate() {
		return expiresDate;
	}

	public void setExpiresDate(Date expiresDate) {
		this.expiresDate = expiresDate;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public boolean isExpired() {
		if (expiresDate == null) {
			return true;
		}
		if (expiresDate.before(new Date())) {
			return true;
		}
		return false;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scopes) {
		this.scope = scopes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+ ((accessToken == null) ? 0 : accessToken.hashCode());
		result = prime * result	+ ((expiresDate == null) ? 0 : expiresDate.hashCode());
		result = prime * result	+ ((refreshToken == null) ? 0 : refreshToken.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result	+ ((tokenType == null) ? 0 : tokenType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OAuth2AccessToken other = (OAuth2AccessToken) obj;
		if (accessToken == null) {
			if (other.accessToken != null)
				return false;
		} else if (!accessToken.equals(other.accessToken))
			return false;
		if (expiresDate == null) {
			if (other.expiresDate != null)
				return false;
		} else if (!expiresDate.equals(other.expiresDate))
			return false;
		if (refreshToken == null) {
			if (other.refreshToken != null)
				return false;
		} else if (!refreshToken.equals(other.refreshToken))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (tokenType == null) {
			if (other.tokenType != null)
				return false;
		} else if (!tokenType.equals(other.tokenType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OAuth2AccessToken {" +
				"accessToken=" + accessToken
				+ ", tokenType=" + tokenType
				+ ", expiresDate=" + expiresDate
				+ ", refreshToken=" + refreshToken
				+ ", scope=" + scope
				+ "}";
	}


}
