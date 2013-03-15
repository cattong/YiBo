package com.cattong.commons.http.config;


/**
 * Configuration
 *
 * @version
 * @author cattong.com
 * @time 2010-8-1 上午10:50:18
 */
public interface HttpConfig extends java.io.Serializable {

	boolean isUseGzip();

	boolean isUseProxy();

	String getUserAgent();

	// methods for HttpClientConfiguration

	String getHttpProxyHost();

	String getHttpProxyUser();

	String getHttpProxyPassword();

	int getHttpProxyPort();

	int getHttpConnectionTimeout();

	int getHttpReadTimeout();

	int getHttpRetryCount();

	int getHttpRetryIntervalSeconds();

	String getClientVersion();

	String getClientURL();

}