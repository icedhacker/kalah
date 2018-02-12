package com.bb.kalah;

import com.bb.kalah.request.CreateGameRequest;
import com.bb.kalah.request.CreateGameResponse;
import com.bb.kalah.request.MoveRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game")
public class KalahController {
    private KalahService kalahService;

    public KalahController(KalahService kalahService) {
        this.kalahService = kalahService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public CreateGameResponse createGame(@RequestBody CreateGameRequest createGameRequest) {
        String gameId = kalahService.createGame(createGameRequest.getGameName());

        String playerId = kalahService.assignPlayer(gameId, createGameRequest.getPlayerName());

        return CreateGameResponse.builder().gameId(gameId).playerId(playerId).build();
    }

    @RequestMapping(value = "/join/{gameId}", method = RequestMethod.POST)
    public String joinGame(@PathVariable("gameId") String gameId, @RequestBody String playerName) {
        return kalahService.assignPlayer(gameId, playerName);
    }

    @RequestMapping(value = "/move/{gameId}", method = RequestMethod.POST)
    public KalahGame makeMove(@PathVariable("gameId") String gameId, @RequestBody MoveRequest moveRequest) {
        return kalahService.makeMove(gameId, moveRequest.getPlayerId(), moveRequest.getPitIndex());
    }

    @RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
    public KalahGame getGame(@PathVariable("gameId") String gameId) {
        return kalahService.getGame(gameId);
    }
}
