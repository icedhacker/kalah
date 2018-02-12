package com.bb.kalah.request;

import lombok.Data;

@Data
public class MoveRequest {
    private String playerId;

    private int pitIndex;
}
