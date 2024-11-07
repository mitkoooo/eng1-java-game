package com.ateam.campusquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;


public class Main extends Game {
    private LoadingScreen loadingScreen;
    private PreferencesScreen preferencesScreen;
    private MenuScreen menuScreen;
    private MainScreen mainScreen;
    private EndScreen endScreen;
    private AppPreferences preferences;
    private Music backgroundMusic;

    // Constants to represent screen type for reference
    public final static int MENU = 0;
    public final static int PREFERENCES = 1;
    public final static int APPLICATION = 2;
    public final static int ENDGAME = 3;


    /**
     * Method to change screen to appropriate screen type
     *
     * @param screen
     * @param buildingCounter   Number of buildings placed in game (used in end game screen)
     * @param progress      The student satisfaction progress (used in end game screen)
     */
    public void changeScreen(int screen, int buildingCounter, int progress){
        switch(screen){
            case MENU:
                if(menuScreen == null) menuScreen = new MenuScreen(this);
                this.setScreen(new MenuScreen(this));
                break;
            case PREFERENCES:
                if(preferencesScreen == null) preferencesScreen = new PreferencesScreen(this);
                this.setScreen(new PreferencesScreen(this));
                break;
            case APPLICATION:
                if(mainScreen == null) mainScreen = new MainScreen(this);
                this.setScreen(new MainScreen(this));
                break;
            case ENDGAME:
                if(endScreen == null) endScreen = new EndScreen(this,buildingCounter, progress);
                this.setScreen(new EndScreen(this, buildingCounter, progress));
                break;
        }
    }

    /**
     * Initialises the loading screen and sets to initial screen
     */
    @Override
    public void create() {
        loadingScreen = new LoadingScreen(this);
        preferences = new AppPreferences();
        setScreen(loadingScreen);
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundMusic.mp3"));
        updateMusicSettings();

        backgroundMusic.setLooping(true);
        if(preferences.isMusicEnabled()){
            backgroundMusic.play();
        }
    }

    /**
     * Get method to access app preferences
     *
     * @return app preferences object
     */
    public AppPreferences getPreferences() {
        return this.preferences;
    }

    public void updateMusicSettings(){
        backgroundMusic.setVolume(preferences.getMusicVolume());
        if(preferences.isMusicEnabled()){
            backgroundMusic.play();
        } else{
            backgroundMusic.pause();
        }
    }

    public void dispose(){
        backgroundMusic.dispose();
    }


}


