package com.bb.kalah;

import com.bb.kalah.request.CreateGameRequest;
import com.bb.kalah.request.CreateGameResponse;
import com.bb.kalah.request.MoveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class KalahControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String GAME_NAME = "Test Game";
    private static final String PLAYER_NAME_1 = "Player 1";
    private static final String PLAYER_NAME_2 = "Player 2";
    private static final String PLAYER_NAME_3 = "Player 3";

    @Test
    public void shouldReturn200OnCreatingGame() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/game")
                .content(convertToJson(createGameRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldReturn404OnJoiningGameIfGameDoesntExist() throws Exception {
        mockMvc.perform(post("/api/v1/game/join/INVALID_UUID")
                .content(PLAYER_NAME_1)
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn200OnJoiningGameIfGameExists() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/game")
                .content(convertToJson(createGameRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        CreateGameResponse response = objectMapper.readValue(content, CreateGameResponse.class);
        assertThat(response.getGameId()).isNotNull();

        mockMvc.perform(post("/api/v1/game/join/" + response.getGameId())
                .content(PLAYER_NAME_1)
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn409OnJoiningGameIfGameFull() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/game")
                .content(convertToJson(createGameRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        CreateGameResponse response = objectMapper.readValue(content, CreateGameResponse.class);
        assertThat(response.getGameId()).isNotNull();

        mockMvc.perform(post("/api/v1/game/join/" + response.getGameId())
                .content(PLAYER_NAME_2)
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/game/join/" + response.getGameId())
                .content(PLAYER_NAME_3)
                .contentType(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldReturn400OnMakingMovesWhenGameNotStarted() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/game")
                .content(convertToJson(createGameRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        CreateGameResponse response = objectMapper.readValue(content, CreateGameResponse.class);
        assertThat(response.getGameId()).isNotNull();

        mockMvc.perform(post("/api/v1/game/move/" + response.getGameId())
                .content(convertToJson(createMoveRequest(response.getPlayerId(), 0)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400OnMakingIllegalMoves() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/game")
                .content(convertToJson(createGameRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        CreateGameResponse response = objectMapper.readValue(content, CreateGameResponse.class);
        assertThat(response.getGameId()).isNotNull();

        mockMvc.perform(post("/api/v1/game/join/" + response.getGameId())
                .content(PLAYER_NAME_2)
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/game/move/" + response.getGameId())
                .content(convertToJson(createMoveRequest(response.getPlayerId(), 6)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn200OnMakingLegalMoves() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/game")
                .content(convertToJson(createGameRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        CreateGameResponse response = objectMapper.readValue(content, CreateGameResponse.class);
        assertThat(response.getGameId()).isNotNull();

        mockMvc.perform(post("/api/v1/game/join/" + response.getGameId())
                .content(PLAYER_NAME_2)
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/game/move/" + response.getGameId())
                .content(convertToJson(createMoveRequest(response.getPlayerId(), 0)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String moveResponse = mvcResult.getResponse().getContentAsString();
        assertThat(moveResponse).contains("currentPlayer\":{\"name\":\"Player 1");
        assertThat(moveResponse).contains("playerOneScore\":1");
        assertThat(moveResponse).contains("playerTwoScore\":0");
        assertThat(moveResponse).contains("playerOneStoneCount\":35");
        assertThat(moveResponse).contains("playerTwoStoneCount\":36");
    }

    private String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    private CreateGameRequest createGameRequest() {
        CreateGameRequest request = new CreateGameRequest();
        request.setGameName(GAME_NAME);
        request.setPlayerName(PLAYER_NAME_1);
        return request;
    }

    private MoveRequest createMoveRequest(String playerId, int pitIndex) {
        MoveRequest request = new MoveRequest();
        request.setPlayerId(playerId);
        request.setPitIndex(pitIndex);
        return request;
    }
}
