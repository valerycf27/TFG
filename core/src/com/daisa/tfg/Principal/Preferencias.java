package com.daisa.tfg.Principal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Preferencias {

    private static final String PREF_MUSIC_VOLUME = "volumen";
    private static final String PREF_MUSIC_ENABLED = "musica.activada";
    private static final String PREF_SOUND_ENABLED = "sonido.activado";
    private static final String PREF_SOUND_VOL = "sonido";
    private static final String PREFS_NAME = "preferencias";

    Preferences preferencias;

    protected Preferences getPrefs() {
        if (preferencias == null)
            preferencias = Gdx.app.getPreferences(PREFS_NAME);
        return preferencias;
    }

    public boolean sonidosActivados() {
        return getPrefs().getBoolean(PREF_SOUND_ENABLED, true);
    }

    public void activarSonidos(boolean soundEffectsEnabled) {
        getPrefs().putBoolean(PREF_SOUND_ENABLED, soundEffectsEnabled);
        getPrefs().flush();
    }

    public float volumenSonidos() {
        return getPrefs().getFloat(PREF_SOUND_VOL, 0.5f);
    }

    public void cambiarVolumenSonidos(float volume) {
        Gdx.app.debug("DEBUG", "Volumen de los sonidos al cambiarlos: " + volume);
        getPrefs().putFloat(PREF_SOUND_VOL, volume);
        getPrefs().flush();
    }

    public boolean musicaActivada() {
        return getPrefs().getBoolean(PREF_MUSIC_ENABLED, true);
    }

    public void activarMusica(boolean musicEnabled) {
        getPrefs().putBoolean(PREF_MUSIC_ENABLED, musicEnabled);
        getPrefs().flush();
    }

    public float volumenMusica() {
        return getPrefs().getFloat(PREF_MUSIC_VOLUME, 0.5f);
    }

    public void cambiarVolumenMusica(float volume) {
        Gdx.app.debug("DEBUG", "Volumen de la musica al cambiarla: " + volume);
        getPrefs().putFloat(PREF_MUSIC_VOLUME, volume);
        getPrefs().flush();
    }
}
