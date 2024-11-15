package com.ateam.campusquest;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainScreen implements Screen {

    private final Main parent;
    private final OrthographicCamera camera;

    private final Music buildingSoundEffect;
    private final AppPreferences preferences;

    // Layers for tiled map
    private final TiledMap campusMap;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final TiledMapTileLayer roadLayer;
    private final TiledMapTileLayer buildingLayer;
    private final TiledMapTileLayer highlightLayer;
    private final TiledMapTileLayer obstacleLayer;
    private final TiledMapTileLayer busLayer;

    // Other assets
    private final Skin skin;
    private final Building[][] buildingGrid;
    private final SpriteBatch batch;
    private final Table popupTable;

    private final Stage stage;

    private final ImageButton buildButton;
    private final ImageButton exitButton;

    // UI Elements
    private final Label timerLabel;
    private final Label counterLabel;
    private final Label hintLabel;

    // Timer Variables
    private final float countdownTime = 300; // 300 seconds
    private float elapsedTime = 0;
    private boolean isRunning = false;

    // Building mode variables
    private boolean buildMode = false;
    private boolean suspendedBuilding = false;
    private int buildingType;
    private int buildingCounter = 0;

    private int progress = 0;

    /**
     *  Constructor initialises the main game screen
     */
    public MainScreen(Main main) {
        startTimer();
        this.parent = main;
        camera = new OrthographicCamera();
        preferences = new AppPreferences();
        buildingSoundEffect = Gdx.audio.newMusic(Gdx.files.internal("buildingSoundEffect.mp3"));
        updateSoundSettings();

        // Load the Tiled campus map
        campusMap = new TmxMapLoader().load("Map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(campusMap);
        camera.setToOrtho(false, 960, 640);
        roadLayer = (TiledMapTileLayer) campusMap.getLayers().get("RoadLayer");
        obstacleLayer = (TiledMapTileLayer) campusMap.getLayers().get("ObstacleLayer");
        buildingLayer = (TiledMapTileLayer) campusMap.getLayers().get("BuildingLayer");
        highlightLayer = (TiledMapTileLayer) campusMap.getLayers().get("HighlightLayer");
        busLayer = (TiledMapTileLayer) campusMap.getLayers().get("BusStop");
        buildingGrid = new Building[30][20];
        buildingType = 1;

        stage = new Stage(new FitViewport(1920, 1080));
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));


        // Load assets
        Texture buildbuttonTexture = new Texture(Gdx.files.internal("hammer_icon.png"));
        Texture exitbuttonTexture = new Texture(Gdx.files.internal("back_button.png"));
        Texture buildingTexture1 = new Texture(Gdx.files.internal("accommodationBuilding.png"));
        Texture buildingTexture2 = new Texture(Gdx.files.internal("LectureHall.png"));
        Texture buildingTexture3 = new Texture(Gdx.files.internal("pub.png"));
        Texture buildingTexture4 = new Texture(Gdx.files.internal("cafe.png"));
        Texture resumebuttonTexture = new Texture(Gdx.files.internal("resume_button.png"));
        Texture progressBarTexture = new Texture(Gdx.files.internal("progress_bar.png"));
        Texture progressKnobTexture = new Texture(Gdx.files.internal("progress_knob.png"));

        //Building Counter
        counterLabel = new Label("Buildings: " + buildingCounter, new Label.LabelStyle(new BitmapFont(), Color.WHITE ));
        counterLabel.setFontScale(2);

        // Setup clock
        timerLabel = new Label(formatTime(countdownTime), new Label.LabelStyle(new BitmapFont(), Color.WHITE ));
        timerLabel.setFontScale(3);

        // Button to resume/pause timer
        TextureRegionDrawable resumeDrawable = new TextureRegionDrawable(resumebuttonTexture);
        ImageButton resumeButton = new ImageButton(resumeDrawable);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isRunning){
                    pauseTimer();
                } else {
                    resumeTimer();
                }            }
        });

        // Create a ProgressBar
        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = new TextureRegionDrawable(progressBarTexture);
        progressBarStyle.knob = new TextureRegionDrawable(progressKnobTexture);
        ProgressBar progressBar = new ProgressBar(0, 100, 1, false, progressBarStyle);
        progressBar.setValue(progress); // Set initial value

        Label progressLabel = new Label("Student Satisfaction:", skin);
        progressLabel.setColor(Color.BLACK);
        progressLabel.setFontScale(2);



        // Setup table for buttons and labels
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        hintLabel = new Label("Press B to exit build mode", new Label.LabelStyle(new BitmapFont(), Color.WHITE ));
        hintLabel.setFontScale(3);
        hintLabel.setVisible(false);

        // Set up popup table for building selection
        popupTable = new Table();
        popupTable.setSize(300, 500);
        popupTable.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("popup_background.png"))));
        Label buildMenuLabel = new Label("Build Menu", new Label.LabelStyle(new BitmapFont(), Color.WHITE ));
        buildMenuLabel.setFontScale(2);
        popupTable.add(buildMenuLabel).pad(10).colspan(2);
        popupTable.row();
        popupTable.add(new Label("Select Building:", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).pad(10).colspan(2);
        popupTable.row();
        addIconWithLabel(popupTable, buildingTexture1, "Accommodation Building");
        addIconWithLabel(popupTable, buildingTexture2, "Lecture Building");
        addIconWithLabel(popupTable, buildingTexture3, "Recreational Building");
        addIconWithLabel(popupTable, buildingTexture4, "Restaurant Building");

        popupTable.setVisible(false); // Build menu is hidden until building button pressed
        popupTable.setPosition((float) Gdx.graphics.getWidth() / 2 - popupTable.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2 - popupTable.getHeight() / 2);

        // Set up building and exit buttons
        TextureRegionDrawable drawable = new TextureRegionDrawable(buildbuttonTexture);
        buildButton = new ImageButton(drawable);

        TextureRegionDrawable exitDrawable = new TextureRegionDrawable(exitbuttonTexture);
        exitButton = new ImageButton(exitDrawable);

        // Set up layout
        table.add(buildButton).width(75).height(75).expandX().left().padLeft(20).top();
        table.add(timerLabel).width(150).padRight(20).padTop(20).top();
        table.add(resumeButton).width(75).height(75).padTop(10).top();
        // table.add(progressLabel).width(350).pad(20); NOT USED IN THIS IMPLEMENTATION, SAVED FOR FUTURE USE
        //table.add(progressBar).width(300).height(30).center().pad(20);       NOT USED IN THIS IMPLEMENTATION, SAVED FOR FUTURE USE
        table.add(counterLabel).padLeft(30).padTop(25).top();
        table.add(exitButton).width(150).height(75).expandX().right().padRight(20).padTop(10).top();
        table.row();
        table.add(hintLabel).center().colspan(2);
        //table.add(increaseButton).size(100,50);   NOT USED IN THIS IMPLEMENTATION, SAVED FOR FUTURE USE

        stage.addActor(table);
        stage.addActor(popupTable);


        // Creating listeners for buttons
        buildButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                popupTable.setVisible(!popupTable.isVisible());
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(Main.MENU, 0,0);
            }
        });

        // Input Multiplexer to handle both stage and custom inputs
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.B) {
                    buildMode = !buildMode;  // Toggle build mode
                    clearHighlightLayer();
                    // System.out.println("Build mode toggled: " + buildMode);
                    return true;
                }
                return false;
            }

            /**
             *
             * @param screenX X coordinate of mouse on screen
             * @param screenY Y coordinate of mouse on screen
             */
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
                camera.unproject(worldCoordinates);
                int tileX = (int) (worldCoordinates.x / buildingLayer.getTileWidth());
                int tileY = (int) (worldCoordinates.y / buildingLayer.getTileHeight());

                if (isOutOfBounds(tileX, tileY)) {
                    return false;
                }

                if (buildMode && button == Input.Buttons.LEFT) {
                    placeBuilding(tileX, tileY);
                    return true;
                } else if (!buildMode && button == Input.Buttons.LEFT) {
                    Building clickedBuilding = buildingGrid[tileX][tileY];
                    if (clickedBuilding != null) {
                        // Clicked on building logic goes here
                        System.out.println(clickedBuilding.getX() + " " + clickedBuilding.getY());
                    }
                    return false;
                }
                return false;
            }

            /**
             * Handles mouse movement
             *
             * @param screenX X coordinate of mouse screen position
             * @param screenY Y coordinate of mouse screen position
             */
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                if (buildMode) {
                    Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
                    camera.unproject(worldCoordinates);
                    int tileX = (int) (worldCoordinates.x / highlightLayer.getTileWidth());
                    int tileY = (int) (worldCoordinates.y / highlightLayer.getTileHeight());

                    if (!isOutOfBounds(tileX, tileY)) {
                        highlightPlacement(tileX, tileY);
                    }
                    return false;
                }
                return false;
            }
        });
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer); // Set the input processor to the multiplexer
    }

    /**
     * Checks if found tile is out of bounds
     * @param x x coordinate of tile
     * @param y y coordinate of tile
     */
    private boolean isOutOfBounds(int x, int y){
        // method to check if the tile is out of bounds
        // there are 30x20 tiles but index 0, 0 is the first tile so check bounds as 29 and 19
        return x >= 29 || y >= 19 || x < 0 || y < 0;
    }

    /**
     *
     * @param buildingType currently selected building type
     * @return The texture to display for currently selected building
     */
    private TiledMapTile getBuildingTexture(int buildingType){
        if (buildingType == 1){
            return (campusMap.getTileSets().getTileSet("accommodationBuilding").getTile(6));
        }
        else if (buildingType == 2){
            // lecture building

            return (campusMap.getTileSets().getTileSet("LectureBuilding").getTile(4));
        }
        else if (buildingType == 3){
            return (campusMap.getTileSets().getTileSet("cafeBuilding").getTile(7));
        }
        else if (buildingType == 4){
            return(campusMap.getTileSets().getTileSet("pubBuilding").getTile(5));
        }
        return(campusMap.getTileSets().getTileSet("Building").getTile(3));

    }


    /**
     * Places building on Map
     * @param x x coordinate of tile to place building
     * @param y y coordinate of tile to place building
     **/
    private void placeBuilding(int x, int y) {
        // Check if the area is clear (i.e., not on the road layer) for a 2x2 space


        if(isAwayFromRoad(x, y)){
            System.out.println("Can not place away from road");
            return;
        }
        if(suspendedBuilding){
            System.out.println("Can not place when timer paused");
            return;
        }
        if (isAreaClear(x, y)) {
            // Place the single 2x2 building tile at the bottom-left cell of the 2x2 space
            clearHighlightLayer();
            Building newBuilding;

            buildingLayer.setCell(x, y, new TiledMapTileLayer.Cell().setTile(((getBuildingTexture(buildingType)))));
            if (buildingType ==  1) {
                 newBuilding = new AccommodationBuilding(x, y);

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
            hintLabel.setVisible(false);
            buildingCounter += 1;
            progress += 1;
            if(preferences.isMusicEnabled()){
                buildingSoundEffect.play();
            }
            counterLabel.setText("Buildings: " + buildingCounter);

        } else {
            System.out.println("Cannot place building here, area is not clear!");
        }
    }

    /**
     * Confirms specified tile does not contain any roads, buildings or obstacles
     * @param x x coordinate of tile to check
     * @param y y coordinate of tile to check
     **/
    private boolean isAreaClear(int x, int y) {
        // Check if clear of roads
        if (!(roadLayer.getCell(x, y) == null && roadLayer.getCell(x + 1, y) == null &&
            roadLayer.getCell(x, y + 1) == null && roadLayer.getCell(x + 1, y + 1) == null)){
            System.out.println("Road Blocking");
            return false;
        }

        if (!(obstacleLayer.getCell(x, y) == null && obstacleLayer.getCell(x + 1, y) == null &&
                obstacleLayer.getCell(x, y + 1) == null && obstacleLayer.getCell(x + 1, y + 1) == null)){
            System.out.println("Obstacle Blocking");
            return false;
        }

        // check if buildings are already there
        if (!(buildingGrid[x][y] == null && buildingGrid[x][y+1] == null &&
            buildingGrid[x+1][y] == null && buildingGrid[x+1][y+1] == null)){
            System.out.println("Building Blocking");
            return false;
        }



        if (!(busLayer.getCell(x, y) == null && busLayer.getCell(x + 1, y) == null &&
            busLayer.getCell(x, y + 1) == null && busLayer.getCell(x + 1, y + 1) == null)){
            System.out.println("Bus Stop Blocking");
            return false;
        }


        // if clear of all return true
        return true;
    }


    /**
     * Checks if tile is not adjacent to road
     * @param x x coordinate of tile to check
     * @param y y coordinate of tile to check
     **/
    private boolean isAwayFromRoad(int x, int y){
        // Buildings are 2x2 so checks if any of the four tiles are next to road
        return (roadLayer.getCell(x - 1, y) == null &&
                roadLayer.getCell(x - 1, y + 1) == null &&
                roadLayer.getCell(x, y + 2) == null &&
                roadLayer.getCell(x + 1, y + 2) == null &&
                roadLayer.getCell(x + 2, y) == null &&
                roadLayer.getCell(x + 2, y + 1) == null &&
                roadLayer.getCell(x + 1, y - 1) == null &&
                roadLayer.getCell(x, y - 1) == null);
    }

    /**
     * Clears all highlights on screen
     **/
    private void clearHighlightLayer()
{
        for (int x = 0; x < highlightLayer.getWidth(); x++) {
            for (int y = 0; y < highlightLayer.getHeight(); y++) {
                highlightLayer.setCell(x, y, null); // Clear each cell
            }
        }
    }


    /**
     * When placing a building checks if region is appropriate for building
     * if so highlights building there
     * @param x x coordinate of tile to check
     * @param y y coordinate of tile to check
     **/
    private void highlightPlacement(int x, int y) {
        // Clear previous highlights
        clearHighlightLayer();

        // Highlight the new position
        if(isAwayFromRoad(x, y)){
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
    }

    // Add building icons with corresponding labels to the popup table
    private void addIconWithLabel(Table table, Texture iconTexture, String labelText) {
        TextureRegionDrawable iconDrawable = new TextureRegionDrawable(iconTexture);

        ImageButton iconButton = new ImageButton(iconDrawable);
        iconButton.setSize(50,50);

        iconButton.addListener(new ClickListener(){
            /**
             * This method takes in which button is clicked and assigns buildingType the
             * corresponding building to be placed.

             */
            @Override
            public void clicked(InputEvent event, float x, float y) {
                popupTable.setVisible(!popupTable.isVisible());
                switch (labelText) {
                    case "Accommodation Building":
                        buildingType = 1;
                        break;
                    case "Lecture Building":
                        buildingType = 2;
                        break;
                    case "Recreational Building":
                        buildingType = 4;
                        break;
                    case "Restaurant Building":
                        buildingType = 3;
                        break;
                }
                buildMode = true;
                hintLabel.setVisible(true);
            }
         });

        table.add(iconButton).size(50,50).pad(10);
        table.add(new Label(labelText, new Label.LabelStyle(new BitmapFont(), Color.WHITE))).pad(10);
        table.row();
    }


    // Render method to update screen and time every frame
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Set clear colour
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        if (isRunning){
            elapsedTime += delta;
            if (elapsedTime >= countdownTime){
                isRunning = false;
                parent.changeScreen(Main.ENDGAME, buildingCounter, progress);
            } else {
                timerLabel.setText(formatTime(countdownTime- elapsedTime));
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }

    // Starts Timer
    private void startTimer(){
        elapsedTime = 0;
        isRunning = true;
        suspendedBuilding = false;
    }

    // Pause countdown timer
    private void pauseTimer(){
        isRunning = false;
        suspendedBuilding = true;
    }

    // Resume the countdown timer
    private void resumeTimer(){
        isRunning = true;
        suspendedBuilding = false;
    }

    // Method to format the time used in the countdown timer
    private String formatTime(float time){
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void updateSoundSettings(){
        buildingSoundEffect.setVolume(preferences.getSoundVolume());
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        popupTable.setPosition(
            ((float) 1920 / 2) - (popupTable.getWidth() / 2),  // X position
            ((float) 1080 / 2) - (popupTable.getHeight() / 2)  // Y position
        );

        buildButton.setPosition(10, 1080 - 10);
        exitButton.setPosition(10, 1080 - 10);
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
