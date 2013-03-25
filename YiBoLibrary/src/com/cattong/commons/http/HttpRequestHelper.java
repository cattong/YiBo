package com.cattong.commons.http;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.cattong.commons.Constants;
import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.config.HttpConfig;
import com.cattong.commons.http.config.HttpConfigFactory;
import com.cattong.commons.oauth.OAuthException;
import com.cattong.commons.util.StringUtil;

public class HttpRequestHelper {

	private static ClientConnectionManager connectionManager;
	private static HttpHost globalProxy;
	private static UsernamePasswordCredentials globalProxyCredentials; //代理的用户认证信息

	private static Hashtable<ServiceProvider, HttpClient> spHttpClients;

	static {
		spHttpClients = new Hashtable<ServiceProvider, HttpClient>();
	}

	private static HttpClient getHttpClient(ServiceProvider sp) {
		HttpConfig conf = HttpConfigFactory.getHttpConfiguration(sp);
		HttpClient httpClient = spHttpClients.get(sp);
		if (httpClient == null) {
			httpClient = createHttpClient(conf);
			spHttpClients.put(sp, httpClient);
		}

		return httpClient;
	}

	private static synchronized HttpClient createHttpClient(HttpConfig config) {
		if (config == null) {
			return null;
		}

		if (connectionManager == null) {
			connectionManager = createConnectionManager();
		}

		HttpParams httpParams = new BasicHttpParams();

		if (config.getHttpConnectionTimeout() > 0) {
			httpParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				config.getHttpConnectionTimeout());
		}
		if (config.getHttpReadTimeout() > 0) {
			httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, config.getHttpReadTimeout());
		}
		// 设置cookie策略
	    httpParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
				
		// 设置http.protocol.expect-continue参数为false，即不使用Expect:100-Continue握手，
		// 因为如果服务器不支持HTTP 1.1，则会导致HTTP 417错误。
		HttpProtocolParams.setUseExpectContinue(httpParams, false);
		// 设置User-Agent
		HttpProtocolParams.setUserAgent(httpParams, config.getUserAgent());
		// 设置HTTP版本为 HTTP 1.1
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

		DefaultHttpClient httpClient = new LibHttpClient(connectionManager, httpParams);

		updateProxySetting(config, httpClient);

		if (config.isUseGzip()) {
			httpClient.addRequestInterceptor(new GzipRequestInterceptor());
			httpClient.addResponseInterceptor(new GzipResponseInterceptor());
		}
		if (config.getHttpRetryCount() > 0) {
			HttpRequestRetryHandler retryHandler =
				new DefaultHttpRequestRetryHandler(config.getHttpRetryCount(), true);
			httpClient.setHttpRequestRetryHandler(retryHandler);
		}

		return httpClient;
	}

	private static void updateProxySetting(HttpConfig config, DefaultHttpClient httpClient) {
		if (config == null || httpClient == null){
			return;
		}

		HttpHost[] proxyChain = null;

		if (globalProxy != null) {
			//全局代理设置
			proxyChain = new HttpHost[] {globalProxy};
			if (globalProxyCredentials != null){
				AuthScope globalProxyAuthScope = new AuthScope(globalProxy.getHostName(), globalProxy.getPort());
				httpClient.getCredentialsProvider().setCredentials(globalProxyAuthScope, globalProxyCredentials);
			}
		}

		if (config.isUseProxy() && StringUtil.isNotEmpty(config.getHttpProxyHost())){
			HttpHost spProxy = new HttpHost(config.getHttpProxyHost(), config.getHttpProxyPort());
			if (globalProxy != null){
				proxyChain = new HttpHost[] {globalProxy, spProxy};
			} else {
				proxyChain = new HttpHost[] {spProxy};
			}
			if (StringUtil.isNotEmpty(config.getHttpProxyUser())){
				AuthScope spProxyAuthScope = new AuthScope(spProxy.getHostName(), spProxy.getPort());
				UsernamePasswordCredentials spProxyCredentials = new UsernamePasswordCredentials(
						config.getHttpProxyUser(), config.getHttpProxyPassword());
				httpClient.getCredentialsProvider().setCredentials(spProxyAuthScope, spProxyCredentials);
			}
		}

		httpClient.setRoutePlanner(new LibHttpRoutePlanner(connectionManager.getSchemeRegistry(), proxyChain));
	}

	private static synchronized ClientConnectionManager createConnectionManager() {
		if (connectionManager != null) {
			return connectionManager;
		}

		// Create and initialize scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		Scheme httpScheme = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
		//Scheme httpsScheme = new Scheme("https", SSLSocketFactory.getSocketFactory(), 443);
		Scheme httpsScheme = new Scheme("https", LibSSLSocketFactory.getSocketFactory(), 443);
		schemeRegistry.register(httpScheme);
		schemeRegistry.register(httpsScheme);

		HttpParams managerParams = new BasicHttpParams();
		//设置连接池允许的最大连接数
		ConnManagerParams.setMaxTotalConnections(managerParams, Constants.CONNECTION_POOL_SIZE);
		//设置连接池连接超时时间
		ConnManagerParams.setTimeout(managerParams, Constants.CONNECTION_TIME_OUT);
		connectionManager = new ThreadSafeClientConnManager(managerParams, schemeRegistry);

		return connectionManager;
	}

	public static <T> T execute(HttpRequestWrapper httpRequestWrapper,
			ResponseHandler<T> responseHandler)	throws LibException {
		T result = null;

		try {
			HttpRequest httpRequest = HttpRequestBuilder.newHttpRequest(httpRequestWrapper);
			
			if (Logger.level <= Logger.VERBOSE) {
				//Logger.verbose("HttpRequestMessage {}:{}", httpRequest.getRequestLine().getMethod(), httpRequest.getRequestLine().getUri());
				Logger.verbose("HttpRequestMessage {}:{}", httpRequestWrapper.getMethod(), httpRequestWrapper.getUrl());
				Logger.verbose("HttpRequestMessage params:{}", httpRequestWrapper.getParameters());
			}
			
			ServiceProvider serviceProvider = ServiceProvider.None;
			if (httpRequestWrapper.getAuth() != null) {
				serviceProvider = httpRequestWrapper.getAuth().getServiceProvider();
			}

			// Create a local instance of cookie store
			CookieStore cookieStore = new BasicCookieStore();
			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            HttpClient httpClient = getHttpClient(serviceProvider);
            result = httpClient.execute((HttpUriRequest) httpRequest, responseHandler, localContext);
		} catch (SocketTimeoutException e) {
			throw new LibException(LibResultCode.NET_SOCKET_TIME_OUT, e);
		} catch (HttpResponseException e) {
			throw new LibException(e.getStatusCode(), e);
		} catch (ClientProtocolException e) {
			throw new LibException(LibResultCode.NET_CLIENT_PROTOCOL_EXCEPTION, e);
		} catch (IOException e) {
			throw new LibException(LibResultCode.NET_I_O_EXCEPTION, e);
		} catch (OAuthException e) {
			throw new LibException(LibResultCode.OAUTH_EXCEPTION, e);
		} catch (URISyntaxException e) {
			throw new LibException(LibResultCode.URI_SYNTAX_ERROR, e);
		} catch (LibRuntimeException e) {
			throw new LibException(e.getStatusCode(), e.getErrorDescr());
		}

		return result;
	}

	public static <T> T execute(HttpRequest httpRequest,
			ResponseHandler<T> responseHandler)	throws LibException {
		T result = null;
		if (httpRequest == null) {
			return result;
		}

		try {
			
			ServiceProvider serviceProvider = ServiceProvider.None;

			// Create a local instance of cookie store
			CookieStore cookieStore = new BasicCookieStore();
			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            HttpClient httpClient = getHttpClient(serviceProvider);
            result = httpClient.execute((HttpUriRequest) httpRequest, responseHandler, localContext);
		} catch (SocketTimeoutException e) {
			throw new LibException(LibResultCode.NET_SOCKET_TIME_OUT, e);
		} catch (HttpResponseException e) {
			throw new LibException(e.getStatusCode(), e);
		} catch (ClientProtocolException e) {
			throw new LibException(LibResultCode.NET_CLIENT_PROTOCOL_EXCEPTION, e);
		} catch (IOException e) {
			throw new LibException(LibResultCode.NET_I_O_EXCEPTION, e);
		} catch (LibRuntimeException e) {
			throw new LibException(e.getStatusCode(), e.getErrorDescr());
		}

		return result;
	}
	
	/*
	 * 提现由外部处理的ResponseHandler;
	 */
	public static <T> T getContent(String url, ResponseHandler<T> responseHandler) throws LibException {
        T t = null;
		if (StringUtil.isEmpty(url) || responseHandler == null) {
        	return t;
        }

		try {
			HttpGet urlGet = new HttpGet(url);
			HttpClient httpClient = getHttpClient(ServiceProvider.None);
			t = httpClient.execute(urlGet, responseHandler);
		} catch (SocketTimeoutException e) {
			throw new LibException(LibResultCode.NET_SOCKET_TIME_OUT, e);
		} catch (ClientProtocolException e) {
			throw new LibException(LibResultCode.NET_CLIENT_PROTOCOL_EXCEPTION, e);
		} catch (IOException e) {
			throw new LibException(LibResultCode.NET_I_O_EXCEPTION, e);
		} catch (LibRuntimeException e) {
			throw new LibException(e.getStatusCode());
		} catch (NullPointerException e) {
			//什么都不做，部分图片获取时重定向过程中出现空指针异常
		}
		
		return t;
	}
	
	public static byte[] getContentBytes(String url) throws LibException {
		Logger.verbose("Download url content : {} " , url);
		byte[] byteArray = null;
        if (StringUtil.isEmpty(url)) {
        	return byteArray;
        }

		try {
			HttpGet urlGet = new HttpGet(url);
			HttpClient httpClient = getHttpClient(ServiceProvider.None);
			byteArray = httpClient.execute(urlGet, new ByteArrayResponseHandler());
		} catch (SocketTimeoutException e) {
			throw new LibException(LibResultCode.NET_SOCKET_TIME_OUT, e);
		} catch (ClientProtocolException e) {
			throw new LibException(LibResultCode.NET_CLIENT_PROTOCOL_EXCEPTION, e);
		} catch (IOException e) {
			throw new LibException(LibResultCode.NET_I_O_EXCEPTION, e);
		} catch (LibRuntimeException e) {
			throw new LibException(e.getStatusCode());
		} catch (NullPointerException e) {
			//什么都不做，部分图片获取时重定向过程中出现空指针异常
		}

		return byteArray;
	}
	
	/*
	 * 直接写入文件
	 */
	public static File getBitmapFile(String url, File destFile) throws LibException {
		Logger.verbose("Download Image : {} " , url);
		File bitmapFile = null;
        if (StringUtil.isEmpty(url)) {
        	return null;
        }

		try {
			HttpGet urlGet = new HttpGet(url);
			HttpClient httpClient = getHttpClient(ServiceProvider.None);
			bitmapFile = httpClient.execute(urlGet, new FileResponseHandler(destFile));
		} catch (SocketTimeoutException e) {
			throw new LibException(LibResultCode.NET_SOCKET_TIME_OUT, e);
		} catch (ClientProtocolException e) {
			throw new LibException(LibResultCode.NET_CLIENT_PROTOCOL_EXCEPTION, e);
		} catch (IOException e) {
			throw new LibException(LibResultCode.NET_I_O_EXCEPTION, e);
		} catch (LibRuntimeException e) {
			throw new LibException(e.getStatusCode());
		} catch (NullPointerException e) {
			//什么都不做，部分图片获取时重定向过程中出现空指针异常
		}

		return bitmapFile;
	}

	public static String getRedirectUrl(String url) throws LibException {
		if (StringUtil.isEmpty(url)) {
        	return null;
        }
		String redirect = null;
		try {
			HttpGet urlGet = new HttpGet(url);

			// Set HANDLE_REDIRECTS to false
			urlGet.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
			// Create a local instance of cookie store
			CookieStore cookieStore = new BasicCookieStore();
			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			HttpResponse response = getHttpClient(ServiceProvider.None).execute(urlGet, localContext);
			if (response.containsHeader("location")) {
				redirect = response.getFirstHeader("location").getValue();
			}
			urlGet.abort();
		} catch (SocketTimeoutException e) {
			throw new LibException(LibResultCode.NET_SOCKET_TIME_OUT, e);
		} catch (ClientProtocolException e) {
			throw new LibException(LibResultCode.NET_CLIENT_PROTOCOL_EXCEPTION, e);
		} catch (IOException e) {
			throw new LibException(LibResultCode.NET_I_O_EXCEPTION, e);
		} catch (LibRuntimeException e) {
			throw new LibException(e.getStatusCode());
		} catch (NullPointerException e) {
			//什么都不做，部分图片获取时重定向过程中出现空指针异常
		}

		return redirect;
	}

	public static synchronized void setGlobalProxy(String hostname, int port,
		String username, String password) {
		globalProxy = null;
		globalProxyCredentials = null;
		if (StringUtil.isNotEmpty(hostname) && port > 0) {
			globalProxy = new HttpHost(hostname, port);
			if (StringUtil.isNotEmpty(username)) {
				globalProxyCredentials = new UsernamePasswordCredentials(username, password);
			}
		}

		for (ServiceProvider sp : spHttpClients.keySet()) {
			HttpConfig config = HttpConfigFactory.getHttpConfiguration(sp);
			DefaultHttpClient httpClient = (DefaultHttpClient) spHttpClients.get(sp);
			updateProxySetting(config, httpClient);
		}
	}

	/**
	 * 关闭连接池，清理helper资源
	 */
	public static synchronized void shutdown() {

		if (connectionManager != null) {
			connectionManager.shutdown();
			connectionManager = null;
		}

		spHttpClients.clear();

		globalProxy = null;
		globalProxyCredentials = null;
	}

	public static void evictConnections() {
		if (connectionManager == null) {
			return;
		}
		synchronized (HttpRequestHelper.class) {
			Logger.verbose("Connections Eviction");
			// Close expired connections
			connectionManager.closeExpiredConnections();
			// Optionally, close connections
			// that have been idle longer than 30 second
			connectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
		}
	}

}
