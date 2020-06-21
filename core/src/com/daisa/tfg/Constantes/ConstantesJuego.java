package com.daisa.tfg.Constantes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class ConstantesJuego {

    public static int ALTO_PANTALLA = Gdx.graphics.getHeight();
    public static int ANCHO_PANTALLA = Gdx.graphics.getWidth();

    public static float ALTO_UNIDADES = 20;
    public static float PPU = ALTO_PANTALLA / ALTO_UNIDADES;
    public static float ANCHO_UNIDADES = ANCHO_PANTALLA / PPU;

    public static int MUNICION_MAXIMA = 10;
    public static int TIEMPO_REGENERACION_BALAS_MILIS = 3000;
    public static int CADENCIA_DISPAROS_MILIS = 1000;

    public static String NOMBRE_JSON_SKIN = "skin/glassy-ui.json";
    public static String NOMBRE_ATLAS_SKIN = "skin/glassy-ui.atlas";

    public static String SONIDO_PULSAR_BOTON = "Musica/Sonidos/pulsar boton/pulsar_boton.wav";
    public static String SONIDO_TRANSICION = "Musica/Sonidos/transicion/transicion1.wav";
    public static String SONIDO_FIN_PARTIDA = "Musica/Sonidos/fin partida/finPartida.wav";
    public static Array<String> ARRAY_SONIDOS_DISPARO = new Array<>(new String[]{"Musica/Sonidos/disparo/disparo1.mp3", "Musica/Sonidos/disparo/disparo2.wav", "Musica/Sonidos/disparo/disparo3.wav"});
    public static Array<String> ARRAY_SONIDOS_GOLPE = new Array<>(new String[]{"Musica/Sonidos/golpe/golpe1.mp3", "Musica/Sonidos/golpe/golpe2.wav", "Musica/Sonidos/golpe/golpe3.wav", "Musica/Sonidos/golpe/golpe4.wav", "Musica/Sonidos/golpe/golpe5.wav"});
    public static Array<String> ARRAY_SONIDOS_CARGA = new Array<>(new String[]{"Musica/Sonidos/cargar bala/carga_bala1.wav", "Musica/Sonidos/cargar bala/carga_bala2.wav"});

    public static String MUSICA_MENU = "Musica/musica_menus.mp3";
    public static String MUSICA_JUEGO = "Musica/musica_juego.mp3";

    public static String FUENTE = "Fuentes/fuente.fnt";

    public static String IMAGEN_LOGO = "Logo/logo.png";
    public static String IMAGEN_EXCLAMACION = "Signos/signoExclamacion.png";

    public static String SEPARADOR_DATOS = ":";
}
