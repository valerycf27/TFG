package com.daisa.tfg.Personajes;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Clase que hereda de Personaje y le anade un poligono de hitbox
 */
public class PersonajePol extends Personaje {
    Polygon pol;
    float[] arrVertices;

    public PersonajePol(Array<String> rutaAnimaciones, int idPj, int vida, float velocidad) {
        super(rutaAnimaciones, idPj, vida, velocidad);

        getVertices();
        pol = new Polygon(arrVertices);
        pol.setPosition(getPosicion().x, getPosicion().y);
    }

    @Override
    public void mover(Vector2 direccion) {
        getPosicion().add(direccion.scl(getVelocidad()));
    }

    @Override
    public void recolocarHitbox() {
        getVertices();
        pol.setVertices(arrVertices);
    }

    /**
     * Obtiene la posicion actual de los vertices del poligono
     */
    private void getVertices() {
        switch (getIdPj()) {
            case 1:
            case 2:
            case 3:
                arrVertices = new float[]{getPosicion().x, getPosicion().y,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.5f, getPosicion().y + getAltoRelativoAspecto(),
                        getPosicion().x + getAnchoRelativoAspecto(), getPosicion().y};
                break;
            case 7:
                arrVertices = new float[]{getPosicion().x, getPosicion().y + getAltoRelativoAspecto() * 0.6f,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.15f, getPosicion().y,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.85f, getPosicion().y,
                        getPosicion().x + getAnchoRelativoAspecto(), getPosicion().y + getAltoRelativoAspecto() * 0.6f,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.65f, getPosicion().y + getAltoRelativoAspecto(),
                        getPosicion().x + getAnchoRelativoAspecto() * 0.5f, getPosicion().y + getAltoRelativoAspecto() * 0.8f,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.35f, getPosicion().y + getAltoRelativoAspecto()};
                break;
            case 8:
                arrVertices = new float[]{getPosicion().x, getPosicion().y + getAltoRelativoAspecto() * 0.5f,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.2f, getPosicion().y,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.8f, getPosicion().y,
                        getPosicion().x + getAnchoRelativoAspecto(), getPosicion().y + getAltoRelativoAspecto() * 0.5f,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.8f, getPosicion().y+ getAltoRelativoAspecto(),
                        getPosicion().x + getAnchoRelativoAspecto() * 0.2f, getPosicion().y+ getAltoRelativoAspecto()};
                break;
            case 9:
                arrVertices = new float[]{getPosicion().x, getPosicion().y + getAltoRelativoAspecto() * 0.5f,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.1f, getPosicion().y,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.9f, getPosicion().y,
                        getPosicion().x + getAnchoRelativoAspecto(), getPosicion().y + getAltoRelativoAspecto() * 0.5f,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.9f, getPosicion().y+ getAltoRelativoAspecto(),
                        getPosicion().x + getAnchoRelativoAspecto() * 0.1f, getPosicion().y+ getAltoRelativoAspecto()};
                break;
            case 10:
                arrVertices = new float[]{getPosicion().x, getPosicion().y + getAltoRelativoAspecto() * 0.5f,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.38f, getPosicion().y,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.5f, getPosicion().y + getAltoRelativoAspecto() * 0.22f,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.62f, getPosicion().y,
                        getPosicion().x + getAnchoRelativoAspecto(), getPosicion().y + getAltoRelativoAspecto() * 0.5f,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.62f, getPosicion().y + getAltoRelativoAspecto(),
                        getPosicion().x + getAnchoRelativoAspecto() * 0.5f, getPosicion().y + getAltoRelativoAspecto() * 0.78f,
                        getPosicion().x + getAnchoRelativoAspecto() * 0.38f, getPosicion().y + getAltoRelativoAspecto()};
                break;
            default:
                break;
        }
    }
}
