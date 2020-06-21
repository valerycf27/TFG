package com.daisa.tfg.Balas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Clase que hereda de Bala a la que le anade un poligono como hitbox
 */
public class BalaPol extends Bala {

    Polygon pol;
    float[] arrVertices;

    public BalaPol(Vector2 posicion, float velocidad, Array<TextureRegion> aspecto, int idPj, int tamanoBala) {
        super(posicion, velocidad, aspecto, idPj, tamanoBala);
        switch (idPj) {
            //Triangulo
            case 3:
                arrVertices = new float[]{posicion.x, posicion.y,
                        posicion.x + getAnchoRelativoAspecto() * 0.5f, posicion.y + getAltoRelativoAspecto(),
                        posicion.x + getAnchoRelativoAspecto(), posicion.y};
                break;
            //Rayo
            case 5:
                arrVertices = new float[]{posicion.x + getAnchoRelativoAspecto() * 0.25f, posicion.y,
                        posicion.x + getAnchoRelativoAspecto() * 0.7f, posicion.y,
                        posicion.x + getAnchoRelativoAspecto(), posicion.y + getAltoRelativoAspecto() * 0.55f,
                        posicion.x + getAnchoRelativoAspecto() * 0.6f, posicion.y + getAltoRelativoAspecto() * 0.55f,
                        posicion.x + getAnchoRelativoAspecto() * 0.8f, posicion.y + getAltoRelativoAspecto(),
                        posicion.x, posicion.y + getAltoRelativoAspecto() * 0.3f,
                        posicion.x + getAnchoRelativoAspecto() * 0.35f, posicion.y + getAltoRelativoAspecto() * 0.3f};
                break;
            //Estrella 4 puntas
            case 6:
            case 10:
                arrVertices = new float[]{posicion.x, posicion.y + getAltoRelativoAspecto() * 0.5f,
                        posicion.x + getAnchoRelativoAspecto() * 0.4f, posicion.y + getAltoRelativoAspecto() * 0.4f,
                        posicion.x + getAnchoRelativoAspecto() * 0.5f, posicion.y,
                        posicion.x + getAnchoRelativoAspecto() * 0.6f, posicion.y + getAltoRelativoAspecto() * 0.4f,
                        posicion.x + getAnchoRelativoAspecto(), posicion.y + getAltoRelativoAspecto() * 0.5f,
                        posicion.x + getAnchoRelativoAspecto() * 0.6f, posicion.y + getAltoRelativoAspecto() * 0.6f,
                        posicion.x + getAnchoRelativoAspecto() * 0.5f, posicion.y + getAltoRelativoAspecto(),
                        posicion.x + getAnchoRelativoAspecto() * 0.4f, posicion.y + getAltoRelativoAspecto() * 0.6f,};
                break;
            //Cruz tumbada
            case 8:
                arrVertices = new float[]{posicion.x + getAnchoRelativoAspecto() * 0.3f, posicion.y + getAltoRelativoAspecto() * 0.5f,
                        posicion.x, posicion.y + getAnchoRelativoAspecto()*0.2f,
                        posicion.x + getAnchoRelativoAspecto() * 0.2f, posicion.y,
                        posicion.x + getAnchoRelativoAspecto() * 0.5f, posicion.y + getAltoRelativoAspecto() * 0.3f,
                        posicion.x + getAnchoRelativoAspecto() * 0.8f, posicion.y,
                        posicion.x + getAnchoRelativoAspecto(), posicion.y + getAltoRelativoAspecto()*0.2f,
                        posicion.x + getAnchoRelativoAspecto() * 0.7f, posicion.y + getAltoRelativoAspecto() * 0.5f,
                        posicion.x + getAnchoRelativoAspecto(), posicion.y + getAltoRelativoAspecto()*0.8f,
                        posicion.x + getAnchoRelativoAspecto()*0.8f, posicion.y + getAltoRelativoAspecto(),
                        posicion.x + getAnchoRelativoAspecto() * 0.5f, posicion.y + getAltoRelativoAspecto() * 0.7f,
                        posicion.x + getAnchoRelativoAspecto() * 0.2f, posicion.y + getAltoRelativoAspecto(),
                        posicion.x, posicion.y + getAltoRelativoAspecto() * 0.8f,};
                break;
            //Rombo
            case 9:
                arrVertices = new float[]{posicion.x, posicion.y + getAltoRelativoAspecto() * 0.5f,
                        posicion.x + getAnchoRelativoAspecto() * 0.5f, posicion.y,
                        posicion.x + getAnchoRelativoAspecto(), posicion.y + getAltoRelativoAspecto() * 0.5f,
                        posicion.x + getAnchoRelativoAspecto() * 0.5f, posicion.y + getAltoRelativoAspecto()};
                break;
            default:
                Gdx.app.debug("DEBUG", "[ERROR] Caso de bala poligonal no contemplado");
                break;
        }

        pol = new Polygon(arrVertices);
        pol.setPosition(posicion.x, posicion.y);
    }
}
