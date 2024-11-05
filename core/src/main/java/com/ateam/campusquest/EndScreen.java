package com.ateam.campusquest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class EndScreen implements Screen {
    private Main parent;
    private Stage stage;
    private Skin skin;


    public EndScreen(Main main, int buildingCounter, int progress) {
        parent = main;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        Label gameOverLabel = new Label("Timer Over", skin);
        gameOverLabel.setColor(Color.WHITE);
        gameOverLabel.setFontScale(3);

        Label buildingCountLabel = new Label("Congratulations, You built "+ buildingCounter + " buildings", skin);
        buildingCountLabel.setColor(Color.WHITE);
        buildingCountLabel.setFontScale(3);

        Label progressLabel = new Label("You got a progress score of " + progress, skin);
        progressLabel.setColor(Color.WHITE);
        progressLabel.setFontScale(3);


        TextButton returnButton = new TextButton("Return to Menu", skin);

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(Main.MENU, 0,0);
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(gameOverLabel).pad(20);
        table.row();
        table.add(buildingCountLabel).pad(20);
        table.row();
        table.add(progressLabel).pad(20);
        table.row();
        table.add(returnButton).pad(20);

        stage.addActor(table);


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(delta); // Update the stage
        stage.draw(); // Draw the stage
    }

    @Override
    public void resize(int i, int i1) {
        stage.getViewport().update( i, i1, true );
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
        stage.dispose();
    }
}

