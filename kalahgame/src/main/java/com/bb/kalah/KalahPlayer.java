package com.bb.kalah;

import lombok.Getter;

import java.util.UUID;

@Getter
public class KalahPlayer {
    private String name;

    private String id;

    KalahPlayer(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
}
