package com.ateam.campusquest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.math.Vector3;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture map;
    private Texture buildingTexture; // Texture for buildings

    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Tile[][] grid;
    private static final int GRID_ROWS = 10; // Example grid size
    private static final int GRID_COLS = 10;
    private static final float TILE_SIZE = 64; // Size of each tile
    private static final float VIRTUAL_WIDTH = GRID_COLS * TILE_SIZE;
    private static final float VIRTUAL_HEIGHT = GRID_ROWS * TILE_SIZE;

    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        map = new Texture("map.png");
        buildingTexture = new Texture("test_tile.png"); // Load building texture

        camera = new OrthographicCamera();
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();

        // Set the camera to view the map area
        camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT); // Set to the size of the grid

        // Initialize the grid with empty tiles
        grid = new Tile[GRID_ROWS][GRID_COLS];
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                grid[row][col] = new Tile() {}; // Start with empty tiles
            }
        }

        // Initialize ShapeRenderer for debugging
        shapeRenderer = new ShapeRenderer();

        // Set up input handling
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Calculate the tile based on screen coordinates
                int[] tileCoordinates = getTileAtScreenCoordinates(screenX, screenY);
                int row = tileCoordinates[0];
                int col = tileCoordinates[1];

                // Only place building if the tile indices are valid
                if (row >= 0 && row < GRID_ROWS && col >= 0 && col < GRID_COLS) {
                    Building building = new Building(buildingTexture);
                    grid[row][col] = building; // Replace the empty tile with a building
                    System.out.println("Building placed at Row: " + row + " Col: " + col);
                } else {
                    System.out.println("Invalid tile selection: Row: " + row + " Col: " + col);
                }
                return true; // Consume the event
            }
        });
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Draw the map texture
        batch.draw(map, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT); // Draw the full map size

        // Draw buildings on the grid
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                Tile tile = grid[row][col];
                if (tile instanceof Building) {
                    Building building = (Building) tile;
                    // Calculate the building's position
                    float x = col * TILE_SIZE;
                    float y = row * TILE_SIZE;
                    // Draw the building using its texture
                    batch.draw(building.getTexture(), x, y);
                }
            }
        }

        batch.end();

        // Debugging: Draw grid lines
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1); // Red color for grid lines
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                float x = col * TILE_SIZE;
                float y = row * TILE_SIZE;
                shapeRenderer.rect(x, y, TILE_SIZE, TILE_SIZE); // Draw rectangle for each tile
            }
        }
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        buildingTexture.dispose(); // Dispose of the building texture
        shapeRenderer.dispose(); // Dispose of the shape renderer
    }

    private int[] getTileAtScreenCoordinates(int screenX, int screenY) {
        // Convert screen coordinates to world coordinates
        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));

        // Calculate column and row based on world coordinates
        int col = (int) (worldCoordinates.x / TILE_SIZE);
        int row = (int) (worldCoordinates.y / TILE_SIZE); // Direct calculation for top-left origin

        // Check bounds
        if (col < 0 || col >= GRID_COLS || row < 0 || row >= GRID_ROWS) {
            return new int[]{-1, -1}; // Return invalid indices if out of bounds
        }

        return new int[]{row, col}; // Return the valid indices
    }
}
