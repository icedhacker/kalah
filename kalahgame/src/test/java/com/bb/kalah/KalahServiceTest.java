package com.bb.kalah;

import com.bb.kalah.exception.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
public class KalahServiceTest {

    private KalahService kalahService;

    private static final String GAME_NAME_1 = "Test Game 1";

    private static final String PLAYER_1 = "Player 1";
    private static final String PLAYER_2 = "Player 2";

    private String testGameId1;

    @Before
    public void setup() {
        kalahService = new KalahService();
        testGameId1 = kalahService.createGame(GAME_NAME_1);
    }

    @Test
    public void shouldGetGameAfterCreatingNewGame() {
        KalahGame game = kalahService.getGame(testGameId1);
        assertThat(game).isNotNull();
        assertThat(game.getName()).isEqualTo(GAME_NAME_1);
        assertThat(game.getCurrentPlayer()).isNull();
        assertThat(game.getPlayer1()).isNull();
        assertThat(game.getPlayer2()).isNull();
    }

    @Test(expected = GameNotFoundException.class)
    public void shouldThrowNotFoundExceptionIfGameDoesntExist() {
        KalahGame game = kalahService.getGame("WRONG_UUID");
        fail();
    }

    @Test
    public void shouldAssignPlayer1AndSetToCurrentPlayer() {
        String playerId1 = kalahService.assignPlayer(testGameId1, PLAYER_1);
        KalahGame game = kalahService.getGame(testGameId1);
        assertThat(game.getPlayer1()).isNotNull();
        assertThat(game.getPlayer1().getName()).isEqualTo(PLAYER_1);
        assertThat(game.getPlayer1().getId()).isEqualTo(playerId1);
        assertThat(game.getCurrentPlayer().getId()).isEqualTo(playerId1);
    }

    @Test
    public void shouldAssignPlayer2IfPlayer1ExistsAndStartGame() {
        String playerId1 = kalahService.assignPlayer(testGameId1, PLAYER_1);
        String playerId2 = kalahService.assignPlayer(testGameId1, PLAYER_2);
        KalahGame game = kalahService.getGame(testGameId1);
        assertThat(game.getCurrentPlayer().getId()).isEqualTo(playerId1);
        assertThat(game.getPlayer1().getId()).isEqualTo(playerId1);
        assertThat(game.getPlayer2()).isNotNull();
        assertThat(game.getPlayer2().getId()).isEqualTo(playerId2);
    }

    @Test(expected = GameFullException.class)
    public void shouldThrowExceptionIfThirdPlayerIsAdded() {
        String playerId1 = kalahService.assignPlayer(testGameId1, PLAYER_1);
        String playerId2 = kalahService.assignPlayer(testGameId1, PLAYER_2);
        String playerId3 = kalahService.assignPlayer(testGameId1, "Player 3");
    }

    @Test(expected = GameWrongTurnException.class)
    public void shouldThrowWrongTurnExceptionIfPlayerNotCurrentPlayer() {
        String playerId1 = kalahService.assignPlayer(testGameId1, PLAYER_1);
        String playerId2 = kalahService.assignPlayer(testGameId1, PLAYER_2);
        kalahService.makeMove(testGameId1, playerId2, 8);
    }

    @Test(expected = GameNotStartedException.class)
    public void shouldThrowNotStartedExceptionIfTwoPlayersNotSet() {
        String playerId1 = kalahService.assignPlayer(testGameId1, PLAYER_1);
        kalahService.makeMove(testGameId1, playerId1, 0);
    }

    @Test(expected = UnauthorizedPitAccessException.class)
    public void shouldThrowUnauthPitAccessExceptionIfPlayerAccessesPitsOfOtherPlayer() {
        String playerId1 = kalahService.assignPlayer(testGameId1, PLAYER_1);
        String playerId2 = kalahService.assignPlayer(testGameId1, PLAYER_2);
        kalahService.makeMove(testGameId1, playerId1, 2);
        kalahService.makeMove(testGameId1, playerId2, 1);
    }

    @Test
    public void shouldMakeMoveIfValidAndReturnGameState() {
        String playerId1 = kalahService.assignPlayer(testGameId1, PLAYER_1);
        String playerId2 = kalahService.assignPlayer(testGameId1, PLAYER_2);

        kalahService.makeMove(testGameId1, playerId1, 0);
        KalahGame game = kalahService.getGame(testGameId1);
        assertThat(game.getBoard().getPlayerOneScore()).isEqualTo(1);
        assertThat(game.getBoard().getPlayerOneStoneCount()).isEqualTo(35);

        // Check whether the current player remains PLayer 1 when the move ends at Kalah / Store.
        assertThat(game.getCurrentPlayer().getId()).isEqualTo(playerId1);
    }

    @Test(expected = IllegalMoveException.class)
    public void shouldThrowIllegalMoveExceptioIfInvalidMove() {
        String playerId1 = kalahService.assignPlayer(testGameId1, PLAYER_1);
        String playerId2 = kalahService.assignPlayer(testGameId1, PLAYER_2);
        kalahService.makeMove(testGameId1, playerId1, 0);

        // Cannot play the same move with index 0 as the number of stones in that house is zero.
        kalahService.makeMove(testGameId1, playerId1, 0);
    }

    @Test
    public void shouldEndGameAndReturnGameStateIfEnd() {
        KalahGame game = endGameMove();
        assertThat(game.getBoard().getPlayerOneScore()).isEqualTo(64);
        assertThat(game.getBoard().getPlayerTwoScore()).isEqualTo(8);
        assertThat(game.getBoard().getPlayerOneStoneCount()).isEqualTo(0);
        assertThat(game.getBoard().getPlayerTwoStoneCount()).isEqualTo(0);
    }

    @Test(expected = GameNotFoundException.class)
    public void shouldRemoveGameFromMapIfEndGame() {
        KalahGame game = endGameMove();
        game = kalahService.getGame(testGameId1);
    }

    private KalahGame endGameMove() {
        String playerId1 = kalahService.assignPlayer(testGameId1, PLAYER_1);
        String playerId2 = kalahService.assignPlayer(testGameId1, PLAYER_2);

        kalahService.makeMove(testGameId1, playerId1, 0);
        kalahService.makeMove(testGameId1, playerId1, 1);
        kalahService.makeMove(testGameId1, playerId2, 7);
        kalahService.makeMove(testGameId1, playerId1, 0);
        kalahService.makeMove(testGameId1, playerId2, 8);
        kalahService.makeMove(testGameId1, playerId1, 2);
        kalahService.makeMove(testGameId1, playerId2, 8);
        kalahService.makeMove(testGameId1, playerId1, 1);
        kalahService.makeMove(testGameId1, playerId2, 12);
        kalahService.makeMove(testGameId1, playerId1, 3);
        kalahService.makeMove(testGameId1, playerId2, 8);
        kalahService.makeMove(testGameId1, playerId1, 2);
        kalahService.makeMove(testGameId1, playerId2, 7);
        kalahService.makeMove(testGameId1, playerId1, 1);
        kalahService.makeMove(testGameId1, playerId2, 12);
        kalahService.makeMove(testGameId1, playerId2, 9);
        kalahService.makeMove(testGameId1, playerId1, 0);
        kalahService.makeMove(testGameId1, playerId2, 8);
        kalahService.makeMove(testGameId1, playerId1, 2);
        kalahService.makeMove(testGameId1, playerId2, 11);
        kalahService.makeMove(testGameId1, playerId1, 1);
        return kalahService.makeMove(testGameId1, playerId2, 12);
    }
}
