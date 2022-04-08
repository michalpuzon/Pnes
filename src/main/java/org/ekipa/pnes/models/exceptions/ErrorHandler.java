package org.ekipa.pnes.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ExceptionResponse> handleErrors(HttpStatusCodeException throwable) {
        ExceptionResponse response = ExceptionResponse.builder().error(throwable.getStatusText()).status(throwable.getStatusCode()).message(throwable.getMessage()).build();
        return ResponseEntity.status(409).body(response);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionResponse> handleThrowable(Throwable throwable) {
        return handleErrors(new HttpClientErrorException(HttpStatus.CONFLICT, throwable.getMessage()));
    }

}
