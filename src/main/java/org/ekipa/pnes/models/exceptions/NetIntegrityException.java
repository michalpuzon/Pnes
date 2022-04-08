package org.ekipa.pnes.models.exceptions;

/**
 * Ogólny wyjątek spójności sieci.
 */

public class NetIntegrityException extends Exception {
    public NetIntegrityException() {
        super("Net integrity violation");
    }

    public NetIntegrityException(String message) {
        super(message);
    }
}
