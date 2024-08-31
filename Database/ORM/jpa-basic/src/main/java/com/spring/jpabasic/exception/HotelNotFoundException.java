package com.spring.jpabasic.exception;

public class HotelNotFoundException extends RuntimeException{
	public HotelNotFoundException () {
		super();
	}

	public HotelNotFoundException (String message) {
		super(message);
	}

	public HotelNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

	public HotelNotFoundException (Throwable cause) {
		super(cause);
	}

	protected HotelNotFoundException (String message, Throwable cause, boolean enableSuppression,
		boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
