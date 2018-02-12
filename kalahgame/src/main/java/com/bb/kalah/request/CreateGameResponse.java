package com.bb.kalah.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateGameResponse {
    private String gameId;

    private String playerId;
}
