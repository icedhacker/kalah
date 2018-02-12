package com.bb.kalah.request;

import lombok.Data;

@Data
public class CreateGameRequest {
    private String gameName;

    private String playerName;
}
