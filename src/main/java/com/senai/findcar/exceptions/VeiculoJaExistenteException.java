package com.senai.findcar.exceptions;

public class VeiculoJaExistenteException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public VeiculoJaExistenteException(String message) {
		super(message);
	}
}
