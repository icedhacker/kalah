package com.bb.kalah.exception;

public class GameWrongTurnException extends BadRequestException {

    public GameWrongTurnException(String gameId, String playerId) {
        super(String.format("Game %s, Not %s's turn", gameId, playerId));
    }
}
