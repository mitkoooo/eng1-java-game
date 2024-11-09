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
    private Main parent; // Referring to main class for screen changes
    private Stage stage;
    private Skin skin;

    /**
     * Constructor to initialise end screen
     *
     * @param main The main class used for screen changes
     * @param buildingCounter The number of buildings placed during game
     * @param progress The student satisfaction level achieved during game
     */
    public EndScreen(Main main, int buildingCounter, int progress) {
        parent = main;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json")); // Loading skin from assets

        // Label for "Game Over" message
        Label gameOverLabel = new Label("Timer Over", skin);
        gameOverLabel.setColor(Color.WHITE);
        gameOverLabel.setFontScale(3);

        // Label to display buildingCounter
        Label buildingCountLabel = new Label("Congratulations, You built "+ buildingCounter + " buildings", skin);
        buildingCountLabel.setColor(Color.WHITE);
        buildingCountLabel.setFontScale(3);

        // Label to display student satisfaction progress
        Label progressLabel = new Label("You got a progress score of " + progress, skin);
        progressLabel.setColor(Color.WHITE);
        progressLabel.setFontScale(3);

        // Create button and listener to return to main menu
        TextButton returnButton = new TextButton("Return to Menu", skin);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(Main.MENU, 0,0);
            }
        });

        // Creating table and adding Labels and Buttons to it
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

    /**
     * This method is called when screen is shown. It sets input processor to the stage
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Called continuously to render screen, updating and drawing screen
     * @param delta Time since last render
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(delta); // Update the stage
        stage.draw(); // Draw the stage
    }

    /**
     * Called when screen is resized
     * Updated viewport to match screen dimensions
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update( width, height, true );
    }

    /**
     * Empty methods as implementing Screen
     */
    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    /**
     * Called to dispose of resources used that are no longer needed
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}

