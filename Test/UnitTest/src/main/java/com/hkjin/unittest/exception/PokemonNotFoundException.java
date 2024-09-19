package com.hkjin.unittest.exception;

public class PokemonNotFoundException extends RuntimeException{
	public PokemonNotFoundException () {
		super();
	}

	public PokemonNotFoundException (String message) {
		super(message);
	}

	public PokemonNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

	public PokemonNotFoundException (Throwable cause) {
		super(cause);
	}

	protected PokemonNotFoundException (String message, Throwable cause, boolean enableSuppression,
		boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
