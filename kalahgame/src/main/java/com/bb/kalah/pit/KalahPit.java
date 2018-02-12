package com.bb.kalah.pit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KalahPit {
    private int stones;

    public void sowStone() {
        this.stones++;
    }

    public boolean isEmpty() {
        return this.stones == 0;
    }

    protected void setStones(int stones) {
        this.stones = stones;
    }

    public boolean isStore() {
        return this instanceof KalahStore;
    }
}
