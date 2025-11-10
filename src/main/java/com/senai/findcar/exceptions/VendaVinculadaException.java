package com.senai.findcar.exceptions;

public class VendaVinculadaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public VendaVinculadaException(String message) {
        super(message);
    }
}
