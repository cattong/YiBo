package com.cattong.commons.http;

import java.net.InetAddress;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.routing.RouteInfo.LayerType;
import org.apache.http.conn.routing.RouteInfo.TunnelType;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HttpContext;

class LibHttpRoutePlanner implements HttpRoutePlanner{
	/** The scheme registry. */
	private SchemeRegistry schemeRegistry;
    private HttpHost[] proxyChain;

    /**
     * Creates a new default route planner.
     *
     * @param schreg    the scheme registry
     */
    public LibHttpRoutePlanner(SchemeRegistry schemeRegistry, HttpHost[] proxyChain) {
        if (schemeRegistry == null) {
            throw new IllegalArgumentException("SchemeRegistry must not be null.");
        }
        this.schemeRegistry = schemeRegistry;
        this.proxyChain = proxyChain;
    }

    public HttpRoute determineRoute(HttpHost target,
                                    HttpRequest request,
                                    HttpContext context)
        throws HttpException {

        if (request == null) {
            throw new IllegalStateException("Request must not be null.");
        }

        // If we have a forced route, we can do without a target.
        HttpRoute route =
            ConnRouteParams.getForcedRoute(request.getParams());
        if (route != null) {
            return route;
        }

        // If we get here, there is no forced route.
        // So we need a target to compute a route.

        if (target == null) {
            throw new IllegalStateException("Target host must not be null.");
        }

        final InetAddress local =
            ConnRouteParams.getLocalAddress(request.getParams());

        final Scheme schm = schemeRegistry.getScheme(target.getSchemeName());
        // as it is typically used for TLS/SSL, we assume that
        // a layered scheme implies a secure connection
        final boolean secure = schm.isLayered();

        if (proxyChain == null || proxyChain.length == 0) {
            route = new HttpRoute(target, local, secure);
        } else {
            TunnelType tunnelType = secure ? TunnelType.TUNNELLED : TunnelType.PLAIN;
            LayerType layereType = secure ? LayerType.LAYERED : LayerType.PLAIN;
        	return new HttpRoute(target, null, proxyChain, secure, tunnelType, layereType);
        }
        return route;
    }

}
