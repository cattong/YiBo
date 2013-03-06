package com.cattong.commons.oauth;

/**
 * Where to place OAuth parameters in an HTTP message. The alternatives are
 * summarized in <a href="http://oauth.net/documentation/spec">OAuth Core</a>
 * under <a href="http://oauth.net/core/1.0a#consumer_req_param">Consumer
 * Request Parameters</a>.
 */
public enum OAuthParameterStyle {
    /**
     * Send parameters whose names begin with "oauth_" in an HTTP header, and
     * other parameters (whose names don't begin with "oauth_") in either the
     * message body or URL query string. The header formats are specified by
     * OAuth Core under <a href="http://oauth.net/core/1.0a#auth_header">OAuth
     * HTTP Authorization Scheme</a>.
     */
    AUTHORIZATION_HEADER,

    /** Send all parameters in the query string part of the URL. */
    QUERY_STRING;
}
