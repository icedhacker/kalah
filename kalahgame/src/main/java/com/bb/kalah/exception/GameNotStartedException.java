package com.bb.kalah.exception;

public class GameNotStartedException extends BadRequestException {

    public GameNotStartedException(String gameId) {
        super("Game has not yet started : " + gameId);
    }
}
