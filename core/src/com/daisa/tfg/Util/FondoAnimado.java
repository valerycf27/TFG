package com.daisa.tfg.Util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.daisa.tfg.Constantes.ConstantesJuego;

public class FondoAnimado {

    public static final int VELOCIDAD_POR_DEFECTO = 200;

    TextureRegion aspecto;
    float y1, y2;
    int velocidad;
    float aspectoRelativo;
    float altoRelativo;

    public FondoAnimado (String rutaFondo) {
        aspecto = new TextureRegion(new Sprite(new Texture(rutaFondo)));

        velocidad = VELOCIDAD_POR_DEFECTO;
        aspectoRelativo = (float) ConstantesJuego.ANCHO_PANTALLA / aspecto.getRegionWidth();
        altoRelativo = aspecto.getRegionHeight() * aspectoRelativo;

        y1 = 0;
        y2 = altoRelativo;
    }

    /**
     * Mueve el fondo
     * @param deltaTime deltaTime del juego
     */
    public void actualizar (float deltaTime) {
        y1 -= velocidad * deltaTime;
        y2 -= velocidad * deltaTime;

        if (y1 + altoRelativo <= 0)
            y1 = y2 + altoRelativo;

        if (y2 + altoRelativo <= 0)
            y2 = y1 + altoRelativo;
    }

    /**
     * Se pintan los dos fondos para dar una sensacion de continuidad
     * @param batch objeto para pintar
     */
    public void pintar(SpriteBatch batch){
        batch.draw(aspecto, 0, y1, ConstantesJuego.ANCHO_PANTALLA, altoRelativo);
        batch.draw(aspecto, 0, y2, ConstantesJuego.ANCHO_PANTALLA, altoRelativo);
    }
}
