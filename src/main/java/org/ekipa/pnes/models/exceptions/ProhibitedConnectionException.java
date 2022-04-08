package org.ekipa.pnes.models.exceptions;

/**
 * Wyjątek wyrzucany przy próbie wykonania nieprawidłowego połączenia między elementami sieci.
 */
public class ProhibitedConnectionException extends NetIntegrityException {
    public ProhibitedConnectionException(String message) {
        super(message);
    }
}
