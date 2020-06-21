package com.daisa.tfg.Balas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Clase que hereda de Bala a la que le anade un circulo como hitbox
 */
public class BalaCirc extends Bala{

    Circle circ;

    public BalaCirc(Vector2 posicion, float velocidad, Array<TextureRegion> aspecto, int idPj, int tamanoBala) {
        super(posicion, velocidad, aspecto, idPj, tamanoBala);
        circ= new Circle(posicion.x + getAnchoRelativoAspecto()/2, posicion.y + getAltoRelativoAspecto()/2, getAltoRelativoAspecto()/2);
    }
}
