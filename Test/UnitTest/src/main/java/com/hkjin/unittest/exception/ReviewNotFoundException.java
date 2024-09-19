package com.hkjin.unittest.exception;

public class ReviewNotFoundException extends RuntimeException {
	public ReviewNotFoundException () {
	}

	public ReviewNotFoundException (String message) {
		super(message);
	}

	public ReviewNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

	public ReviewNotFoundException (Throwable cause) {
		super(cause);
	}

	public ReviewNotFoundException (String message, Throwable cause, boolean enableSuppression,
		boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
