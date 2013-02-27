package net.dev123.exception;

import net.dev123.commons.ServiceProvider;

public class LibRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -3104663229009876698L;
	//各平台原生的错误码
	protected int errorCode = ExceptionCode.UNKNOWN_EXCEPTION;
	protected String errorDescription;
	protected String requestPath;
	protected ServiceProvider serviceProvider;

	//对应于LibException的错误码
	protected int exceptionCode = ExceptionCode.UNKNOWN_EXCEPTION;

	public LibRuntimeException(int errorCode) {
		super(errorCode + "");
		this.errorCode = errorCode;
		//this.requestPath = "";
		this.errorDescription = "";
		//this.serviceProvider = serviceProvider.None;
	}
	
	public LibRuntimeException(int errorCode, String error) {
		super(errorCode + ": " + error);
		this.errorCode = errorCode;
		//this.requestPath = "";
		this.errorDescription = error;
		//this.serviceProvider = serviceProvider.None;
	}
	
	public LibRuntimeException(int errorCode, ServiceProvider serviceProvider) {
		super(errorCode + ": " + serviceProvider);
		this.errorCode = errorCode;
		this.requestPath = "";
		this.errorDescription = "";
		this.serviceProvider = serviceProvider;
	}

	public LibRuntimeException(int errorCode, String requestPath,
			String errorDescription, ServiceProvider serviceProvider) {
		super(errorDescription);
		this.errorCode = errorCode;
		this.requestPath = requestPath;
		this.errorDescription = errorDescription;
		this.serviceProvider = serviceProvider;
	}

	public LibRuntimeException(int errorCode, Exception cause, ServiceProvider serviceProvider) {
		super(cause);
		this.errorCode = errorCode;
		this.requestPath = "";
		this.errorDescription = cause.getMessage();
		this.serviceProvider = serviceProvider;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getStatusCode() {
		if (exceptionCode == ExceptionCode.UNKNOWN_EXCEPTION) {
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

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

}
