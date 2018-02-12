package com.bb.kalah;

import com.bb.kalah.pit.KalahHouse;
import com.bb.kalah.pit.KalahPit;
import com.bb.kalah.pit.KalahStore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class KalahBoard {
    @Getter
    private int pitsPerPlayer;

    @Getter
    private int stonesPerPit;

    private final int playerOneStore;
    private final int playerTwoStore;

    private List<KalahPit> pits;

    KalahBoard(int initialPitsCount, int initialStoneCount) {
        this.pits = new ArrayList<>((initialPitsCount + 1) * 2);
        pitsPerPlayer = initialPitsCount;
        stonesPerPit = initialStoneCount;
        playerOneStore = initialPitsCount;
        playerTwoStore = (initialPitsCount * 2) + 1;
        createPlayerPits(initialPitsCount, initialStoneCount);
        createPlayerPits(initialPitsCount, initialStoneCount);
    }

    private void createPlayerPits(int initialPitsCount, int initialStoneCount) {
        IntStream.range(0, initialPitsCount).forEach(
                i -> pits.add(new KalahHouse(initialStoneCount))
        );
        pits.add(new KalahStore(0));
    }

    /**
     * Performs all the tasks related to a specific player move :
     * 1. Validate the Move.
     * 2. Pick All the stones from the selected pit.
     * 3. Sows the stones into each of the following pits.
     * 4. Checks for the edge cases and takes action.
     * 5. Return a GameMove to help decide on the next move / GameState.
     *
     * @param pitNum the index of the pit to pick stones.
     * @return the GameMove for deciding next move.
     */
    KalahGameMove makeMove(int pitNum) {
        try {
            KalahHouse pit = (KalahHouse) validate(pitNum);

            // Pick all the stones in the selected pit.
            int stones = pit.pickStones();

            KalahGameMove nextMove = KalahGameMove.CONTINUE;
            boolean isOppositionPit = false;
            int index = pitNum;
            while (stones > 0) {
                stones--;
                if (index == playerTwoStore) {
                    index = 0;
                } else {
                    index++;
                }
                KalahPit currentPit = pits.get(index);

                if (currentPit.isStore()) {
                    if (!isOppositionPit) {
                        // Stone is added to the players kalah / store.
                        currentPit.sowStone();
                        // Set next move to play again if this is the last stone.
                        if (stones == 0) {
                            nextMove = KalahGameMove.PLAY_AGAIN;
                        }
                    } else {
                        // No stones are added to the opponents score.
                        stones++;
                    }
                    isOppositionPit = !isOppositionPit;
                } else {
                    // Set the next move to Capture opposite pit stones if this is the last stone.
                    if (!isOppositionPit && stones == 0 && currentPit.isEmpty()) {
                        nextMove = KalahGameMove.CAPTURE;
                    }
                    currentPit.sowStone();
                }
            }
            if (nextMove == KalahGameMove.CAPTURE) {
                capture(index);
            }
            return nextMove;
        } catch (RuntimeException e) {
            log.info("Illegal Move exception : " + e.getMessage());
            return KalahGameMove.ILLEGAL;
        }
    }

    /**
     * @return count The total number of stones in all the houses for player 1.
     */
    public int getPlayerOneStoneCount() {
        int count = 0;
        for (int i = 0; i < pitsPerPlayer; i++) {
            count += pits.get(i).getStones();
        }
        return count;
    }

    /**
     * @return count The total number of stones in all the houses for player 2.
     */
    public int getPlayerTwoStoneCount() {
        int count = 0;
        for (int i = pitsPerPlayer + 1; i <= pitsPerPlayer * 2; i++) {
            count += pits.get(i).getStones();
        }
        return count;
    }

    /**
     * Ends the game by collecting all the stones in the pits and collecting it in the respective kalah / store.
     */
    public void endGame() {
        IntStream.range(0, pitsPerPlayer).forEach(
                i -> ((KalahStore) pits.get(pitsPerPlayer)).addStones(((KalahHouse) pits.get(i)).pickStones())
        );

        IntStream.range(pitsPerPlayer + 1, (pitsPerPlayer * 2) + 1).forEach(
                i -> ((KalahStore) pits.get((pitsPerPlayer * 2) + 1)).addStones(((KalahHouse) pits.get(i)).pickStones())
        );
    }

    /**
     * @return count of stones in the Player 1 Kalah / Store.
     */
    public int getPlayerOneScore() {
        return pits.get(playerOneStore).getStones();
    }

    /**
     * @return count of stones in the Player 2 Kalah / Store.
     */
    public int getPlayerTwoScore() {
        return pits.get(playerTwoStore).getStones();
    }

    /**
     * Captures the last stone to be added to the player's empty pit and all the
     * stones in the opposite pit. Adds all these stones to the player's score.
     *
     * @param pitNum the index of the current player's pit.
     */
    private void capture(int pitNum) {
        int oppPit = getOppositePit(pitNum);

        KalahHouse currentPit = (KalahHouse) pits.get(pitNum);
        KalahHouse oppositePit = (KalahHouse) pits.get(oppPit);
        int stones = currentPit.pickStones() + oppositePit.pickStones();

        int storeIndex = playerOneStore;
        if (pitNum > pitsPerPlayer) {
            storeIndex = playerTwoStore;
        }
        KalahStore store = (KalahStore) pits.get(storeIndex);
        store.addStones(stones);
    }

    private int getOppositePit(int pitNum) {
        return (pitsPerPlayer * 2) - pitNum;
    }

    // TODO: Create custom exceptions here.
    private KalahPit validate(int pitNum) {
        if (pitNum < 0 || pitNum >= pits.size()) {
            throw new RuntimeException("Pit is out of range.");
        }

        KalahPit pit = pits.get(pitNum);
        if (pit.isStore()) {
            throw new RuntimeException("Pit is a Kalah / Store.");
        }

        if (pit.isEmpty()) {
            throw new RuntimeException("Pit is empty.");
        }

        return pit;
    }
}
