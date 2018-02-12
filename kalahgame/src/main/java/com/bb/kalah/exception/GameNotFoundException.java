package com.bb.kalah.exception;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(String gameId) {
        super("Could not find game with id " + gameId);
    }
}
