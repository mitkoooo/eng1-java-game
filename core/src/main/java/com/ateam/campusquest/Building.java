package com.ateam.campusquest;

import com.badlogic.gdx.graphics.Texture;

public class Building extends Tile {
    private final Texture texture;

    public Building(Texture texture){
        this.texture = texture;

    }

    public Texture getTexture(){
        return texture;
    }

}
