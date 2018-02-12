package com.bb.kalah;

import com.bb.kalah.exception.IllegalMoveException;
import com.bb.kalah.exception.UnauthorizedPitAccessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
final class KalahGame {
    private final String gameId;
    private final String name;
    private final KalahGameType gameType;

    private KalahPlayer player1;
    private KalahPlayer player2;
    private KalahPlayer currentPlayer;
    private KalahBoard board;

    private String result = "In Progress";

    private static final int INITIAL_PITS_COUNT = 6;
    private static final int INITIAL_STONES_COUNT = 6;

    public KalahGame(String name) {
        this(name, INITIAL_PITS_COUNT, INITIAL_STONES_COUNT);
    }

    public KalahGame(String name, int initialPitsCount, int initialStonesCount) {
        this.gameId = UUID.randomUUID().toString();
        this.name = name;
        this.gameType = KalahGameType.MULTI_PLAYER;
        this.board = new KalahBoard(initialPitsCount, initialStonesCount);
    }

    public String setPlayer1(String playerName) {
        player1 = new KalahPlayer(playerName);
        currentPlayer = player1;
        return player1.getId();
    }

    public String setPlayer2(String playerName) {
        player2 = new KalahPlayer(playerName);
        return player2.getId();
    }

    public boolean hasStarted() {
        return (player1 != null && player2 != null);
    }

    /**
     * @param pitNum index of the pit to pick stones
     * @throws UnauthorizedPitAccessException If the current player doesn't own the pitNum.
     * @throws IllegalMoveException           if an illegal move is made by the player.
     */
    public void makeMove(int pitNum) {
        if (!isPitOwnedByCurrentPlayer(pitNum)) {
            throw new UnauthorizedPitAccessException(gameId, currentPlayer.getId(), pitNum);
        }

        KalahGameMove nextMove = board.makeMove(pitNum);
        switch (nextMove) {
            case CAPTURE:
            case CONTINUE:
                changePlayer();
                break;
            case PLAY_AGAIN:
                break;
            case ILLEGAL:
                throw new IllegalMoveException(gameId, currentPlayer.getId());
        }
    }

    /**
     * @return true if the game has ended else false.
     */
    public boolean hasGameEnded() {
        return (board.getPlayerOneStoneCount() == 0 || board.getPlayerTwoStoneCount() == 0);
    }

    /**
     * End the game
     *
     * @return result of the game. Winner or Draw.
     */
    public String endGame() {
        board.endGame();
        setGameResult();
        return result;
    }

    private void setGameResult() {
        int player1Score = board.getPlayerOneScore();
        int player2Score = board.getPlayerTwoScore();
        if (player1Score > player2Score) {
            result = player1.getName() + " Wins";
        } else if (player2Score > player1Score) {
            result = player2.getName() + " Wins";
        } else {
            result = "Draw";
        }
    }

    /**
     * Switches the current player in the Game.
     */
    private void changePlayer() {
        if (isPlayer1Turn()) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    private boolean isPitOwnedByCurrentPlayer(int pitNum) {
        if (isPlayer1Turn()) {
            if (pitNum > board.getPitsPerPlayer()) {
                return false;
            }
        } else {
            if (pitNum < board.getPitsPerPlayer() + 1) {
                return false;
            }
        }
        return true;
    }

    private boolean isPlayer1Turn() {
        return currentPlayer == null || currentPlayer == player1;
    }
}
