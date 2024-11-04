package com.ateam.campusquest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
    private Skin skin;
    private Building[][] buildingGrid;
    private SpriteBatch batch;
    private Texture buildbuttonTexture;
    private Texture exitbuttonTexture;
    private Table popupTable;
    private Texture buildingTexture1;
    private Texture buildingTexture2;
    private Texture buildingTexture3;
    private Texture buildingTexture4;
    private Stage stage;

    private ImageButton buildbutton;
    private ImageButton exitbutton;




    private boolean buildMode = false;
    private int buildingType;

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
        buildingGrid = new Building[30][20];
        buildingType = 1;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();


        buildbuttonTexture = new Texture(Gdx.files.internal("hammer_icon.png"));
        exitbuttonTexture = new Texture(Gdx.files.internal("back_button.png"));
        buildingTexture1 = new Texture(Gdx.files.internal("test_tile.png"));
        buildingTexture2 = new Texture(Gdx.files.internal("test_tile.png"));
        buildingTexture3 = new Texture(Gdx.files.internal("test_tile.png"));
        buildingTexture4 = new Texture(Gdx.files.internal("test_tile.png"));

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        popupTable = new Table();
        popupTable.setSize(300,500);
        popupTable.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("popup_background.png"))));
        popupTable.add(new Label("Build Menu", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).pad(10).colspan(2);
        popupTable.row();
        popupTable.add(new Label("Select Building:", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).pad(10).colspan(2);
        popupTable.row();
        addIconWithLabel(popupTable, buildingTexture1, "Accomodation Building");
        addIconWithLabel(popupTable, buildingTexture2, "Lecture Building");
        addIconWithLabel(popupTable, buildingTexture3, "Recreational Building");
        addIconWithLabel(popupTable, buildingTexture4, "Restaurant Building");

        popupTable.setVisible(false);
        popupTable.setPosition(Gdx.graphics.getWidth() / 2 - popupTable.getWidth() / 2, Gdx.graphics.getHeight() / 2 - popupTable.getHeight() / 2);


        TextureRegionDrawable drawable = new TextureRegionDrawable(buildbuttonTexture);
        buildbutton = new ImageButton(drawable);


        TextureRegionDrawable exitdrawable = new TextureRegionDrawable(exitbuttonTexture);
        exitbutton = new ImageButton(exitdrawable);



        table.add(buildbutton).expandX().left().size(45,45).pad(10);
        table.add(exitbutton).expandX().right().size(45,45).pad(10);

        stage.addActor(table);
        stage.addActor(popupTable);

        buildbutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                popupTable.setVisible(!popupTable.isVisible());
            }
        });
        exitbutton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(Main.MENU);
            }
        });

        // toggle build mode by pressing B logic
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.B) {
                    buildMode = true;  // Toggle build mode
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

    private TiledMapTile getBuildingTexture(int buildingType){
        if (buildingType == 1){
            return (campusMap.getTileSets().getTileSet("Building").getTile(4));
        }
        else if (buildingType == 2){
            // lecture building

            return (campusMap.getTileSets().getTileSet("Building").getTile(5));
        }
        else if (buildingType == 3){
            return (campusMap.getTileSets().getTileSet("Building").getTile(4));
        }
        else if (buildingType == 4){
            return(campusMap.getTileSets().getTileSet("Building").getTile(4));
        }
        return(campusMap.getTileSets().getTileSet("Building").getTile(4));

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
            Building newBuilding;
            buildingLayer.setCell(x, y, new TiledMapTileLayer.Cell().setTile(((getBuildingTexture(buildingType)))));
            if (buildingType ==  1) {
                 newBuilding = new AccomodationBuilding(x, y);

            }
            else if (buildingType == 2){
                 newBuilding = new LectureBuilding(x, y);

            }
            else if (buildingType == 3){
                 newBuilding = new CafeBuilding(x, y);

            }
            else if (buildingType == 4){
                newBuilding = new RecreationalBuilding(x, y);

            }
            else{
                System.out.println("ERROR No building type");
                return;
            }
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
            highlightLayer.setCell(x, y, new TiledMapTileLayer.Cell().setTile(getBuildingTexture(buildingType)));

        } else {
            System.out.println("Cannot highlight here, area is not clear!");
        }
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

    }
    private void addIconWithLabel(Table table, Texture iconTexture, String labelText) {
        TextureRegionDrawable iconDrawable = new TextureRegionDrawable(iconTexture);

        ImageButton iconButton = new ImageButton(iconDrawable);
        iconButton.setSize(50,50);

        iconButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                popupTable.setVisible(!popupTable.isVisible());
                if (labelText.equals("Accomodation Building")){
                    buildingType = 1;
                }
                else if (labelText.equals("Lecture Building")){
                    buildingType = 2;
                }
                else if (labelText.equals("Recreational Building")){
                    buildingType = 4;
                }
                else if (labelText.equals("Restaurant Building")){
                    buildingType = 3;
                }
                buildMode = true;
            }
         });

        table.add(iconButton).size(50,50).pad(10);
        table.add(new Label(labelText, new Label.LabelStyle(new BitmapFont(), Color.WHITE))).pad(10);
        table.row();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Set clear colour
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        popupTable.setPosition(Gdx.graphics.getWidth() / 2 - popupTable.getWidth() / 2, Gdx.graphics.getHeight() / 2 - popupTable.getHeight() / 2);

        buildbutton.setPosition(10, height  - 10);
        exitbutton.setPosition(10, height  - 10);

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
        skin.dispose();
        stage.dispose();

    }
}
