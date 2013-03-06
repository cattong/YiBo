package com.cattong.commons.oauth;

public class OAuthException extends Exception {

	/**
	 * For subclasses only.
	 */
	protected OAuthException() {
	}

	/**
	 * @param message
	 */
	public OAuthException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public OAuthException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OAuthException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
