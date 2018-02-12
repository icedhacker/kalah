package com.bb.kalah;

import com.bb.kalah.exception.GameFullException;
import com.bb.kalah.exception.GameNotFoundException;
import com.bb.kalah.exception.GameNotStartedException;
import com.bb.kalah.exception.GameWrongTurnException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KalahService {
    private Map<String, KalahGame> games = new ConcurrentHashMap<>();

    /**
     * Creates a new game and adds it to the in memory data store(map).
     * @param name name of the game to be created
     * @return id the new game's UUID
     */
    public String createGame(String name) {
        KalahGame game = new KalahGame(name);
        games.put(game.getGameId(), game);
        return game.getGameId();
    }

    /**
     * Assigns a player to the game
     * @param playerName name of the player who wants to join the game
     * @param gameId ID of the game that the player wants to join
     * @return id the player's ID
     * @throws GameNotFoundException if the game doesn't exist
     * @throws GameFullException if the game is already full
     */
    public String assignPlayer(String gameId, String playerName) {
        KalahGame game = getGame(gameId);

        if (game.getPlayer1() == null) {
            return game.setPlayer1(playerName);
        }
        if (game.getPlayer2() == null) {
            return game.setPlayer2(playerName);
        }
        throw new GameFullException(gameId);
    }

    /**
     * Makes the next move, and remove the game, if it was finished
     * @param gameId ID of the current game
     * @param playerId ID of the player making the move
     * @param pitIndex Index of pit to be picked
     * @return current state of the game
     * @throws GameNotStartedException if the game has not yet started
     * @throws GameWrongTurnException if it is the turn of the other player to move
     */
    public KalahGame makeMove(String gameId, String playerId, int pitIndex) {
        KalahGame game = getGame(gameId);

        if (!game.hasStarted()) {
            throw new GameNotStartedException(gameId);
        }

        if (!game.getCurrentPlayer().getId().equals(playerId)) {
            throw new GameWrongTurnException(gameId, playerId);
        }

        game.makeMove(pitIndex);
        if (game.hasGameEnded()) {
            game.endGame();
            games.remove(gameId);
        }
        return game;
    }

    /**
     *
     * @param gameId ID of the game
     * @return current state of the game
     * @throws GameNotFoundException if the game doesn't exist.
     */
    public KalahGame getGame(String gameId) {
        if (games.containsKey(gameId)) {
            return games.get(gameId);
        }
        throw new GameNotFoundException(gameId);
    }
}
