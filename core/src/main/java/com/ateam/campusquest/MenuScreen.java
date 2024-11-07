package com.ateam.campusquest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {
    private final Stage stage;
    private Main parent;

    /**
     * Constructor initialises the menu screen, the first proper screen
     * that the user will see, here they can start game, go to preferences
     * screen or quit
     * @param main
     */
    public MenuScreen(Main main) {
        parent = main;
        stage = new Stage(new ScreenViewport());


    }

    // Called when the screen is visible to the user
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); // Setting input processor

        // Creating table to organise UI elements
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        
        Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        // Creating and adding buttons to table for menu options
        TextButton newGame = new TextButton("New Game", skin);
        TextButton preferences = new TextButton("Preferences", skin);
        TextButton exit = new TextButton("Exit", skin);

        table.add(newGame).fillX().uniformX();
        table.row().pad(10,0,10,0);
        table.add(preferences).fillX().uniformX();
        table.row();
        table.add(exit).fillX().uniformX();

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(Main.APPLICATION,0,0);
            }
        });
        preferences.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(Main.PREFERENCES,0,0);
            }
        });

    }

    // Called every frame to update screen
    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    // Called every time window is resized
    @Override
    public void resize(int i, int i1) {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }


    // Unused methods due as implementing screen
    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    // Dispose of resources no longer needed
    @Override
    public void dispose() {
        stage.dispose();

    }
}
