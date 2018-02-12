package com.bb.kalah.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFound(GameNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Not Found");
        response.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({GameNotStartedException.class, GameWrongTurnException.class, IllegalMoveException.class})
    public ResponseEntity<ExceptionResponse> badRequest(BadRequestException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Bad Request");
        response.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnauthorizedPitAccessException.class})
    public ResponseEntity<ExceptionResponse> unauthorized(UnauthorizedPitAccessException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unauthorized");
        response.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({GameFullException.class})
    public ResponseEntity<ExceptionResponse> conflict(GameFullException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Conflict");
        response.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
