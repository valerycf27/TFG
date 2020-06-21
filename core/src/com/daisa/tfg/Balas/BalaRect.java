package com.daisa.tfg.Balas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Clase que hereda de Bala a la que le anade un rectangulo como hitbox
 */
public class BalaRect extends Bala {

    Rectangle rect;

    public BalaRect(Vector2 posicion, float velocidad, Array<TextureRegion> aspecto, int idPj, int tamanoBala) {
        super(posicion, velocidad, aspecto, idPj, tamanoBala);
        rect = new Rectangle(posicion.x, posicion.y, getAnchoRelativoAspecto(), getAltoRelativoAspecto());
    }
}
