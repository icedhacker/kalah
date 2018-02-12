package com.bb.kalah.pit;

public class KalahStore extends KalahPit {

    public KalahStore(int stones) {
        super(stones);
    }

    public void addStones(int count) {
        this.setStones(this.getStones() + count);
    }
}
