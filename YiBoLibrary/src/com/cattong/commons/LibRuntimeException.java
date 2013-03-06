package com.cattong.commons;


public class LibRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -3104663229009876698L;
	//各平台原生的错误码
	protected int errorCode = LibResultCode.E_UNKNOWN_ERROR;
	protected String errorDescr;
	protected String requestPath;
	protected ServiceProvider serviceProvider;

	//对应于LibException的错误码
	protected int exceptionCode = LibResultCode.E_UNKNOWN_ERROR;

	public LibRuntimeException(int errorCode) {
		super(errorCode + "");
		this.errorCode = errorCode;
		//this.requestPath = "";
		this.errorDescr = "";
		//this.serviceProvider = serviceProvider.None;
	}
	
	public LibRuntimeException(int errorCode, String errorDescr) {
		super(errorCode + ": " + errorDescr);
		this.errorCode = errorCode;
		//this.requestPath = "";
		this.errorDescr = errorDescr;
		//this.serviceProvider = serviceProvider.None;
	}
	
	public LibRuntimeException(int errorCode, ServiceProvider sp) {
		super(errorCode + ": " + sp);
		this.errorCode = errorCode;
		this.requestPath = "";
		this.errorDescr = "";
		this.serviceProvider = sp;
	}

	public LibRuntimeException(int errorCode, String requestPath,
			String errorDescr, ServiceProvider sp) {
		super(errorDescr);
		this.errorCode = errorCode;
		this.requestPath = requestPath;
		this.errorDescr = errorDescr;
		this.serviceProvider = sp;
	}

	public LibRuntimeException(int errorCode, Exception cause, ServiceProvider sp) {
		super(cause);
		this.errorCode = errorCode;
		this.requestPath = "";
		this.errorDescr = cause.getMessage();
		this.serviceProvider = sp;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getStatusCode() {
		if (exceptionCode == LibResultCode.E_UNKNOWN_ERROR) {
			exceptionCode = errorCode;
		}
		return exceptionCode;
	}

	public void setStatusCode(int statusCode) {
		this.exceptionCode = statusCode;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public String getErrorDescr() {
		return errorDescr;
	}

	public void setErrorDescr(String errorDescr) {
		this.errorDescr = errorDescr;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

}
