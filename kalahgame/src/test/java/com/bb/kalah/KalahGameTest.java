package com.bb.kalah;

import com.bb.kalah.exception.IllegalMoveException;
import com.bb.kalah.exception.UnauthorizedPitAccessException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KalahGameTest {

    private KalahGame game;

    private static final String TEST_GAME_NAME = "Test Game";
    private static final String PLAYER_1 = "Player 1";
    private static final String PLAYER_2 = "Player 2";

    @Before
    public void setup() {
        game = new KalahGame(TEST_GAME_NAME);
    }

    @Test
    public void checkInitialization() {
        assertThat(game).isNotNull();
        assertThat(game.getPlayer1()).isNull();
        assertThat(game.getCurrentPlayer()).isNull();
    }

    @Test
    public void shouldStartGameAfter2Players() {
        String playerId1 = game.setPlayer1(PLAYER_1);
        assertThat(game.hasStarted()).isFalse();
        String playerId2 = game.setPlayer2(PLAYER_2);
        assertThat(game.hasStarted()).isTrue();
        assertThat(game.getCurrentPlayer().getId()).isEqualTo(playerId1);
    }

    @Test(expected = UnauthorizedPitAccessException.class)
    public void shouldThrowUnauthorizedPitExceptionAccessIfNotOwner() {
        String playerId1 = game.setPlayer1(PLAYER_1);
        String playerId2 = game.setPlayer2(PLAYER_2);
        assertThat(game.getCurrentPlayer().getId()).isEqualTo(playerId1);
        game.makeMove(10);
    }

    @Test(expected = IllegalMoveException.class)
    public void shouldThrowIllegalMoveExceptionIfMoveIllegal() {
        String playerId1 = game.setPlayer1(PLAYER_1);
        String playerId2 = game.setPlayer2(PLAYER_2);
        assertThat(game.getCurrentPlayer().getId()).isEqualTo(playerId1);
        game.makeMove(6);
    }

    public void shouldEndGameIfEnd() {
        String playerId1 = game.setPlayer1(PLAYER_1);
        String playerId2 = game.setPlayer2(PLAYER_2);
        game.makeMove(0);
        game.makeMove(1);
        game.makeMove(7);
        game.makeMove(0);
        game.makeMove(8);
        game.makeMove(2);
        game.makeMove(8);
        game.makeMove(1);
        game.makeMove(12);
        game.makeMove(3);
        game.makeMove(8);
        game.makeMove(2);
        game.makeMove(7);
        game.makeMove(1);
        game.makeMove(12);
        game.makeMove(9);
        game.makeMove(0);
        game.makeMove(8);
        game.makeMove(2);
        game.makeMove(11);
        game.makeMove(1);
        game.makeMove(12);
        assertThat(game.hasGameEnded()).isTrue();
        game.endGame();
        assertThat(game.getBoard().getPlayerOneScore()).isEqualTo(41);
        assertThat(game.getBoard().getPlayerTwoScore()).isEqualTo(8);
        assertThat(game.getBoard().getPlayerOneStoneCount()).isEqualTo(23);
        assertThat(game.getBoard().getPlayerTwoStoneCount()).isEqualTo(0);
        assertThat(game.getResult()).isEqualTo(PLAYER_1 + " Wins");
    }
}
