package com.bb.kalah.exception;

public class IllegalMoveException extends BadRequestException {

    public IllegalMoveException(String gameId, String playerId) {
        super(String.format("Illegal Move by player %s in game %s ", playerId, gameId));
    }
}
