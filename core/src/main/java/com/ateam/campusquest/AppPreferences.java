package com.ateam.campusquest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class AppPreferences {
    private static final String PREF_MUSIC_VOLUME = "volume";
    private static final String PREF_MUSIC_ENABLED = "music.enabled";
    private static final String PREF_SOUND_ENABLED = "sound.enabled";
    private static final String PREF_SOUND_VOL = "sound";
    private static final String PREFS_NAME  = "b2dtut";

    protected Preferences getPrefs(){
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    public float getMusicVolume(){
        return getPrefs().getFloat(PREF_MUSIC_VOLUME, 0.5f);
    }

    public void setMusicVolume(float volume){
        getPrefs().putFloat(PREF_MUSIC_VOLUME, volume);
        getPrefs().flush();
    }

    public boolean isMusicEnabled(){
        return getPrefs().getBoolean(PREF_MUSIC_ENABLED, true);
    }

    public void setMusicEnabled(boolean enabled){
        getPrefs().putBoolean(PREF_MUSIC_ENABLED, isMusicEnabled());
        getPrefs().flush();
    }
    public boolean isSoundEnabled(){
        return getPrefs().getBoolean(PREF_SOUND_ENABLED, true);
    }
    public void setSoundEnabled(boolean enabled){
        getPrefs().putBoolean(PREF_SOUND_ENABLED, isSoundEnabled());
        getPrefs().flush();
    }
    public float getSoundVolume(){
        return getPrefs().getFloat(PREF_SOUND_VOL, 0.5f);
    }
    public void setSoundVolume(float volume){
        getPrefs().putFloat(PREF_SOUND_VOL, volume);
        getPrefs().flush();
    }
}


