package com.bb.kalah.exception;

public class GameFullException extends RuntimeException {

    public GameFullException(String gameId) {
        super("Game is already full : " + gameId);
    }
}
