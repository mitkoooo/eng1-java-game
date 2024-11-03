package com.ateam.campusquest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainScreen implements Screen {

    private final Main parent;
    private OrthographicCamera camera;
    private Viewport viewport;

    private TiledMap campusMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMapTileLayer roadLayer;
    private TiledMapTileLayer backgroundLayer;
    private TiledMapTileLayer buildingLayer;
    private TiledMapTileLayer highlightLayer;
    private TiledMapTileLayer obstacleLayer;
    private TiledMapTile building;
    private Stage uiStage;
    private Skin skin;
    private Building[][] buildingGrid;



    private boolean buildMode = false;

    public MainScreen(Main main) {
        this.parent = main;
        camera = new OrthographicCamera();


        // Load the Tiled campus map
        campusMap = new TmxMapLoader().load("Map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(campusMap);
        camera.setToOrtho(false, 960, 640);
        backgroundLayer = (TiledMapTileLayer) campusMap.getLayers().get("BackgroundLayer");
        roadLayer = (TiledMapTileLayer) campusMap.getLayers().get("RoadLayer");
        obstacleLayer = (TiledMapTileLayer) campusMap.getLayers().get("ObstacleLayer");
        buildingLayer = (TiledMapTileLayer) campusMap.getLayers().get("BuildingLayer");
        highlightLayer = (TiledMapTileLayer) campusMap.getLayers().get("HighlightLayer");
        building = (campusMap.getTileSets().getTileSet("Building").getTile(4));
        buildingGrid = new Building[30][20];




        // toggle build mode by pressing B logic
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.B) {
                    buildMode = !buildMode;  // Toggle build mode
                    System.out.println("Build mode: " + (buildMode ? "ON" : "OFF"));
                    return true;
                }
                return false;
            }


            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (buildMode && button == Input.Buttons.LEFT) {
                    Vector3 worldCoords = new Vector3(screenX, screenY, 0);
                    camera.unproject(worldCoords);
                    int tileX = (int) (worldCoords.x / buildingLayer.getTileWidth());
                    int tileY = (int) (worldCoords.y / buildingLayer.getTileHeight());
                    if (isOutOfBounds(tileX, tileY)) {
                        return false;
                    }

                    // Attempt to place a 2x2 building
                    placeBuilding(tileX, tileY);

                    return true;
                }
                if (!buildMode && button == Input.Buttons.LEFT) {
                    Vector3 worldCoords = new Vector3(screenX, screenY, 0);
                    camera.unproject(worldCoords);
                    int tileX = (int) (worldCoords.x / buildingLayer.getTileWidth());
                    int tileY = (int) (worldCoords.y / buildingLayer.getTileHeight());
                    if (isOutOfBounds(tileX, tileY)) {
                        return false;
                    }

                    Building clickedBuilding = buildingGrid[tileX][tileY];
                    if (clickedBuilding == null){
                        return false;
                    }
                    // Whatever we want to do with buildings goes here
                    System.out.println(clickedBuilding.getX() + " " + clickedBuilding.getY());

                }

                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                if (buildMode) {
                    Vector3 worldCoords = new Vector3(screenX, screenY, 0);
                    camera.unproject(worldCoords);
                    int tileX = (int) (worldCoords.x / highlightLayer.getTileWidth());
                    int tileY = (int) (worldCoords.y / highlightLayer.getTileHeight());
                    if (isOutOfBounds(tileX, tileY)) {
                        return false;
                    }
                    System.out.println(tileX + " " + tileY);
                    highlightPlacement(tileX, tileY); // Highlight where the building will be placed
                }
                return false;
            }

        });


    }



    private boolean isOutOfBounds(int x, int y){
        // method to check if the tile is out of bounds
        // there are 30x20 tiles but index 0, 0 is the first tile so check bounds as 29 and 19
        return x >= 29 || y >= 19 || x < 0 || y < 0;
    }

    private void placeBuilding(int x, int y) {
        // Check if the area is clear (i.e., not on the road layer) for a 2x2 space
        if(!isNextToRoad(x, y)){
            System.out.println("Can not place away from road");
            return;
        }
        if (isAreaClear(x, y)) {
            // Place the single 2x2 building tile at the bottom-left cell of the 2x2 space
            clearHighlightLayer();
            buildingLayer.setCell(x, y, new TiledMapTileLayer.Cell().setTile(building));
            Building newBuilding = new Building(x, y);
            buildingGrid[x][y] = newBuilding;
            buildingGrid[x][y+1] = newBuilding;
            buildingGrid[x+1][y] = newBuilding;
            buildingGrid[x+1][y+1] = newBuilding;
            buildMode = false;

        } else {
            System.out.println("Cannot place building here, area is not clear!");
        }
    }

    private boolean isAreaClear(int x, int y) {
        // Check if clear of roads
        if (!(roadLayer.getCell(x, y) == null && roadLayer.getCell(x + 1, y) == null &&
            roadLayer.getCell(x, y + 1) == null && roadLayer.getCell(x + 1, y + 1) == null)){
            System.out.println("Road Blocking");
            return false;
        }

        // check if buildings are already there
        if (!(buildingGrid[x][y] == null && buildingGrid[x][y+1] == null &&
            buildingGrid[x+1][y] == null && buildingGrid[x+1][y+1] == null)){
            System.out.println("Building Blocking");
            return false;
        }
        // if clear of all return true
        return true;
    }

    private boolean isNextToRoad(int x, int y){
        // Buildings are 2x2 so checks if any of the four tiles are next to road
        return (roadLayer.getCell(x - 1, y) != null ||
            roadLayer.getCell(x - 1, y+1) != null ||
            roadLayer.getCell(x, y + 2) != null ||
            roadLayer.getCell(x + 1, y + 2) != null ||
            roadLayer.getCell(x + 2, y) != null ||
            roadLayer.getCell(x + 2, y + 1) != null ||
            roadLayer.getCell(x + 1, y - 1) != null ||
            roadLayer.getCell(x, y - 1) != null);
    }

    private void clearHighlightLayer() {
        for (int x = 0; x < highlightLayer.getWidth(); x++) {
            for (int y = 0; y < highlightLayer.getHeight(); y++) {
                highlightLayer.setCell(x, y, null); // Clear each cell
            }
        }
    }

    private void highlightPlacement(int x, int y) {
        // Clear previous highlights
        clearHighlightLayer();

        // Highlight the new position
        if(!isNextToRoad(x, y)){
            return;
        }
        if (isAreaClear(x, y)) {
            // Set highlight tiles (you would replace this with your actual highlight tile)
            highlightLayer.setCell(x, y, new TiledMapTileLayer.Cell().setTile(building));

        } else {
            System.out.println("Cannot highlight here, area is not clear!");
        }
    }


    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Set clear colour
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    @Override
    public void resize(int width, int height) {


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
        mapRenderer.dispose();
        campusMap.dispose();
        uiStage.dispose();
        skin.dispose();

    }
}
