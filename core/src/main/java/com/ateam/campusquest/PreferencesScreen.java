package com.ateam.campusquest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PreferencesScreen implements Screen {
    private Stage stage;
    private Main parent;
    private Label titleLabel;
    private Label volumeMusicLabel;
    private Label volumeSoundLabel;

    /**
     * Constructor for preferences screen
     * @param main
     */
    public PreferencesScreen(Main main) {
        parent = main;
        stage = new Stage(new ScreenViewport()); // Initialise the stage with viewport to fit screen

    }

    // Called when the screen in made visible
    @Override
    public void show() {
        stage.clear(); // Clear any previous actors or UI components
        Gdx.input.setInputProcessor(stage);

        // Creating table to place UI components onto
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        // Creating sliders to adjust sound volume
        final Slider volumeMusicSlider = new Slider(0f,1f,0.1f,false,skin);
        volumeMusicSlider.setValue(parent.getPreferences().getMusicVolume());
        volumeMusicSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                parent.getPreferences().setMusicVolume(volumeMusicSlider.getValue());
                parent.updateMusicSettings();
                return false;
            }
        });

        final Slider volumeSoundSlider = new Slider(0f,1f,0.1f,false,skin);
        volumeSoundSlider.setValue(parent.getPreferences().getSoundVolume());
        volumeSoundSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                parent.getPreferences().setSoundVolume(volumeSoundSlider.getValue());
                return false;
            }
        });

        final TextButton backButton = new TextButton("Back", skin, "small");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                parent.changeScreen(Main.MENU,0, 0);
            }
        });


        // Creating and adding Labels,sliders and checkboxes to the table
        titleLabel = new Label( "Preferences", skin );
        volumeMusicLabel = new Label( "Music", skin );
        volumeSoundLabel = new Label( "Sound Effects", skin );

        table.add(titleLabel).colspan(2);
        table.row().pad(10,0,0,10);
        table.add(volumeMusicLabel).left();
        table.add(volumeMusicSlider);
        table.row().pad(10,0,0,10);
        table.add(volumeSoundLabel).left();
        table.add(volumeSoundSlider);
        table.row().pad(10,0,0,10);
        table.add(backButton).colspan(2);

    }

    // Render method is called every frame to update screen
    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0f,0f,0f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(),1/30f));
        stage.draw();
    }

    // Called everytime window size changes
    @Override
    public void resize(int i, int i1) {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    // Unused methods as implementing screen
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

