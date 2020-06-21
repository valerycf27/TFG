package com.daisa.tfg.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Clase que hereda de Personaje y le anade un circulo de hitbox
 */
public class PersonajeCirc extends Personaje {

    Circle circ;

    public PersonajeCirc(Array<String> rutaAnimaciones, int idPj, int vida, float velocidad) {
        super(rutaAnimaciones, idPj, vida, velocidad);
        circ = new Circle(getPosicion().x + getAnchoRelativoAspecto() / 2, getPosicion().y + getAltoRelativoAspecto() / 2, getAltoRelativoAspecto() / 2);
    }

    @Override
    public void mover(Vector2 direccion) {
        getPosicion().add(direccion.scl(getVelocidad()));
    }

    @Override
    public void recolocarHitbox() {
        circ.setPosition(getPosicion().x + getAnchoRelativoAspecto() / 2, getPosicion().y + getAltoRelativoAspecto() / 2);
    }
}
