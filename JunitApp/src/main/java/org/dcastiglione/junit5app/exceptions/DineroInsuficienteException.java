package org.dcastiglione.junit5app.exceptions;

public class DineroInsuficienteException extends RuntimeException {

    public DineroInsuficienteException(String message) {
        super(message);
    }
}
