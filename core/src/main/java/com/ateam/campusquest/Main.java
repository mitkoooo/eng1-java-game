package com.ateam.campusquest;

import com.badlogic.gdx.Game;


public class Main extends Game {
    private LoadingScreen loadingScreen;
    private PreferencesScreen preferencesScreen;
    private MenuScreen menuScreen;
    private MainScreen mainScreen;
    private EndScreen endScreen;
    private AppPreferences preferences;


    public final static int MENU = 0;
    public final static int PREFERENCES = 1;
    public final static int APPLICATION = 2;
    public final static int ENDGAME = 3;


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

    @Override
    public void create() {
        loadingScreen = new LoadingScreen(this);
        preferences = new AppPreferences();
        setScreen(loadingScreen);

    }

    public AppPreferences getPreferences() {
        return this.preferences;
    }


}


