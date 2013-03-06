package com.cattong.commons.oauth;

import java.io.Serializable;

public class OAuthConsumer implements Serializable {

    private static final long serialVersionUID = -2258581186977818580L;

    public final String callbackURL;
    public final String consumerKey;
    public final String consumerSecret;
    private String signatureMethod;
    private OAuthParameterStyle parameterStyle;

    public OAuthConsumer(String callbackURL, String consumerKey,
            String consumerSecret) {
        this.callbackURL = callbackURL;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

	public String getCallbackURL() {
		return callbackURL;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getSignatureMethod() {
		return signatureMethod;
	}

	public void setSignatureMethod(String signatureMethod) {
		this.signatureMethod = signatureMethod;
	}

	public OAuthParameterStyle getParameterStyle() {
		return parameterStyle;
	}

	public void setParameterStyle(OAuthParameterStyle parameterStyle) {
		this.parameterStyle = parameterStyle;
	}

}
