package com.cattong.commons;

public class LibException extends Exception {
	private static final long serialVersionUID = -783370970702574190L;

	protected int errorCode = LibResultCode.E_UNKNOWN_ERROR;

    private String errorDescr;
    
	public LibException(int errorCode, Throwable e) {
		super(e);
		this.errorCode = errorCode;
	}

	public LibException(int errorCode) {
		super(errorCode + "");
		this.errorCode = errorCode;
	}
	
	public LibException(int errorCode, String errorDescr) {
		super(errorCode + ":" + errorDescr);
		this.errorCode = errorCode;
		this.errorDescr = errorDescr;
	}
	
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDescr() {
		return errorDescr;
	}

	public void setErrorDescr(String errorDescr) {
		this.errorDescr = errorDescr;
	}

}
