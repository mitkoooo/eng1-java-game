package com.ateam.campusquest;

import com.badlogic.gdx.Screen;

public class LoadingScreen implements Screen{
    private Main parent;

    public LoadingScreen(Main main) {
        parent = main;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        parent.changeScreen(Main.MENU,0,0);

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}


