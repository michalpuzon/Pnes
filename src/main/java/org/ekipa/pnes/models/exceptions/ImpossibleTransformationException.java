package org.ekipa.pnes.models.exceptions;

/**
 * Wyjątek wyrzucany przy nieprawidłowej transformacji.
 */
public class ImpossibleTransformationException extends Exception{
    public ImpossibleTransformationException(String message) {
        super(message);
    }

}
