package com.psl.cc.analytics.exception;

public class CCException extends Exception {

	private static final long serialVersionUID = -6682311883239832182L;

	public CCException() {

	}

	public CCException(String message) {
		super(message);

	}

	public CCException(Throwable cause) {
		super(cause);

	}

	public CCException(String message, Throwable cause) {
		super(message, cause);

	}

	public CCException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
