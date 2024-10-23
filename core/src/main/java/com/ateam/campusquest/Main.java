package com.ateam.campusquest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture map;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    @Override
    public void create() {
        batch = new SpriteBatch();
        map = new Texture("map.png");

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();  // Apply the viewport settings

        // Set the camera to the center of the world
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        // Update the camera
        camera.update();

        // Set the projection matrix for the SpriteBatch to match the camera's view
        batch.setProjectionMatrix(camera.combined);

        // Start drawing
        batch.begin();
        // Example: Draw the texture at the center of the virtual world
        batch.draw(map, VIRTUAL_WIDTH / 2 - map.getWidth() / 2, VIRTUAL_HEIGHT / 2 - map.getHeight() / 2);
        batch.end();
    }


    @Override
    public void resize(int width, int height) {
        // Adjust the viewport based on the new screen size
        viewport.update(width, height);
    }


    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        // Other dispose...
    }
}
