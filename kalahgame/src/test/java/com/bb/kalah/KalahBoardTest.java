package com.bb.kalah;


import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KalahBoardTest {

    private static final int INITIAL_PITS_COUNT = 6;
    private static final int INITIAL_STONES_COUNT = 6;

    private KalahBoard board;

    @Before
    public void setup() {
        board = new KalahBoard(INITIAL_PITS_COUNT, INITIAL_STONES_COUNT);
    }

    @Test
    public void checkInitialization() {
        assertThat(board.getPitsPerPlayer()).isEqualTo(INITIAL_PITS_COUNT);
        assertThat(board.getStonesPerPit()).isEqualTo(INITIAL_STONES_COUNT);
        assertThat(board.getPlayerOneScore()).isEqualTo(0);
        assertThat(board.getPlayerTwoScore()).isEqualTo(0);
        assertThat(board.getPlayerOneStoneCount()).isEqualTo(36);
        assertThat(board.getPlayerTwoStoneCount()).isEqualTo(36);
    }

    @Test
    public void shouldReturnPlayAgainNextMoveWhenLastInKalah() {
        KalahGameMove nextMove = board.makeMove(0);
        assertThat(nextMove).isEqualTo(KalahGameMove.PLAY_AGAIN);
        assertThat(board.getPlayerOneStoneCount()).isEqualTo(35);
        assertThat(board.getPlayerTwoStoneCount()).isEqualTo(36);
        assertThat(board.getPlayerOneScore()).isEqualTo(1);
        assertThat(board.getPlayerTwoScore()).isEqualTo(0);
    }

    @Test
    public void shouldReturnContinueNextMoveWhenLastNotInKalah() {
        KalahGameMove nextMove = board.makeMove(1);
        assertThat(nextMove).isEqualTo(KalahGameMove.CONTINUE);
        assertThat(board.getPlayerOneStoneCount()).isEqualTo(34);
        assertThat(board.getPlayerTwoStoneCount()).isEqualTo(37);
        assertThat(board.getPlayerOneScore()).isEqualTo(1);
        assertThat(board.getPlayerTwoScore()).isEqualTo(0);
    }

    @Test
    public void shouldReturnIllegalMoveWhenPitIsKalah() {
        KalahGameMove nextMove = board.makeMove(6);
        assertThat(nextMove).isEqualTo(KalahGameMove.ILLEGAL);
    }

    @Test
    public void shouldReturnIllegalMoveWhenPitIsEmpty() {
        KalahGameMove nextMove = board.makeMove(0);
        nextMove = board.makeMove(0);
        assertThat(nextMove).isEqualTo(KalahGameMove.ILLEGAL);
    }

    @Test
    public void shouldReturnIllegalMoveWhenPitIsOutOfBounds() {
        KalahGameMove nextMove = board.makeMove(14);
        assertThat(nextMove).isEqualTo(KalahGameMove.ILLEGAL);
    }

    @Test
    public void shouldCaptureWhenLastPitIsEmpty() {
        board.makeMove(0);
        board.makeMove(1);
        board.makeMove(7);
        KalahGameMove nextMove = board.makeMove(0);
        assertThat(board.getPlayerOneScore()).isEqualTo(10);
        assertThat(board.getPlayerTwoScore()).isEqualTo(1);
        assertThat(board.getPlayerOneStoneCount()).isEqualTo(32);
        assertThat(board.getPlayerTwoStoneCount()).isEqualTo(29);
        assertThat(nextMove).isEqualTo(KalahGameMove.CAPTURE);
    }

    @Test
    public void shouldEndGameWhenStoneCountIsZero() {
        board.makeMove(0);
        board.makeMove(1);
        board.makeMove(7);
        board.makeMove(0);
        board.makeMove(8);
        board.makeMove(2);
        board.makeMove(8);
        board.makeMove(1);
        board.makeMove(12);
        board.makeMove(3);
        board.makeMove(8);
        board.makeMove(2);
        board.makeMove(7);
        board.makeMove(1);
        board.makeMove(12);
        board.makeMove(9);
        board.makeMove(0);
        board.makeMove(8);
        board.makeMove(2);
        board.makeMove(11);
        board.makeMove(1);
        KalahGameMove nextMove = board.makeMove(12);
        assertThat(board.getPlayerOneScore()).isEqualTo(41);
        assertThat(board.getPlayerTwoScore()).isEqualTo(8);
        assertThat(board.getPlayerOneStoneCount()).isEqualTo(23);
        assertThat(board.getPlayerTwoStoneCount()).isEqualTo(0);
        assertThat(nextMove).isEqualTo(KalahGameMove.PLAY_AGAIN);
    }
}
