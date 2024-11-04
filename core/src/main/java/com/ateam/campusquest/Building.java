package com.ateam.campusquest;

import com.badlogic.gdx.graphics.Texture;

abstract class Building {
    private int x, y; // Position of the building (bottom-left tile)

    public Building(int x, int y) {
        this.x = x;
        this.y = y;

    }

    public int getX() { return x; }
    public int getY() { return y; }

}
