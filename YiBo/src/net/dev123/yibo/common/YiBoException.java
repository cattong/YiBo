package net.dev123.yibo.common;

import net.dev123.exception.ExceptionCode;


public final class YiBoException extends Exception {

	/**手机端异常代号**/
	public static final int NET_HTTPS_UNDER_CMWAP      = 3000;      //CMWAP下不支持HTTPS
	public static final int AUTH_TOKEN_IS_NULL         = 3001;      //认证token或secret为空
	public static final int ACCOUNT_IS_EXIST           = 3002;      //帐号已经存在;
	public static final int TOKEN_MISMATCH             = 3003;      //Token不匹配

	private static final long serialVersionUID = -2623309261327598087L;

	/** 异常状态码 */
	private int statusCode = ExceptionCode.UNKNOWN_EXCEPTION;

	public YiBoException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public YiBoException(int statusCode, String msg) {
		super(msg);
		this.statusCode = statusCode;
	}

	public YiBoException(int statusCode, Exception cause) {
		super(cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	@Override
	public String toString() {
		String s = getClass().getName();
		String message = (getLocalizedMessage() != null) ? getLocalizedMessage() : "";
        message = s + ": statusCode=" + this.statusCode + " " + message;
        return message;
	}
}
