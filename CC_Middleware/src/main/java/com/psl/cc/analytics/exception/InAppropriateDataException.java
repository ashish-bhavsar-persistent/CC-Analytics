package com.psl.cc.analytics.exception;

public class InAppropriateDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InAppropriateDataException() {

	}

	public InAppropriateDataException(String message) {
		super(message);

	}

	public InAppropriateDataException(Throwable cause) {
		super(cause);

	}

	public InAppropriateDataException(String message, Throwable cause) {
		super(message, cause);

	}

	public InAppropriateDataException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}
}
