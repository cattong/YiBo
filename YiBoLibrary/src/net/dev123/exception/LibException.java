package net.dev123.exception;

public class LibException extends Exception {

	private static final long serialVersionUID = -783370970702574190L;

	protected int exceptionCode = ExceptionCode.UNKNOWN_EXCEPTION;;

	public LibException(int exceptionCode, String msg) {
		super(exceptionCode + ": " + msg);
		this.exceptionCode = exceptionCode;
	}

	public LibException(int exceptionCode, Throwable e) {
		super(e);
		this.exceptionCode = exceptionCode;
	}

	public LibException(int exceptionCode) {
		super(exceptionCode + "");
		this.exceptionCode = exceptionCode;
	}

	public int getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(int exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

}
