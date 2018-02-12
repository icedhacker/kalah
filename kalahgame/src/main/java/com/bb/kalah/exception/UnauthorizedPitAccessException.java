package com.bb.kalah.exception;

public class UnauthorizedPitAccessException extends RuntimeException {
    public UnauthorizedPitAccessException(String gameId, String playerId, int pitIndex) {
        super(String.format("Player %s doesn't own pit %d in game %s", playerId, pitIndex, gameId));
    }
}
