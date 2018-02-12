package com.bb.kalah.pit;

public class KalahHouse extends KalahPit {

    public KalahHouse(int stones) {
        super(stones);
    }

    public int pickStones() {
        int picked = this.getStones();
        this.setStones(0);
        return picked;
    }
}
