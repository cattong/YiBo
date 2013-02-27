package net.dev123.commons.oauth;

import java.io.Serializable;

public class OAuthConsumer implements Serializable {

    private static final long serialVersionUID = -2258581186977818580L;

    public final String callbackURL;
    public final String consumerKey;
    public final String consumerSecret;
    public final OAuthServiceProvider serviceProvider;
    private String signatureMethod;
    private OAuthParameterStyle parameterStyle;

    public OAuthConsumer(String callbackURL, String consumerKey,
            String consumerSecret, OAuthServiceProvider serviceProvider) {
        this.callbackURL = callbackURL;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.serviceProvider = serviceProvider;
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

	public OAuthServiceProvider getServiceProvider() {
		return serviceProvider;
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
