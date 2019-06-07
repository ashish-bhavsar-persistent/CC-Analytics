package com.psl.cc.analytics.exception;

public class CC_Exception extends RuntimeException {

	private static final long serialVersionUID = -6682311883239832182L;

	public CC_Exception() {

	}

	public CC_Exception(String message) {
		super(message);

	}

	public CC_Exception(Throwable cause) {
		super(cause);

	}

	public CC_Exception(String message, Throwable cause) {
		super(message, cause);

	}

	public CC_Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
