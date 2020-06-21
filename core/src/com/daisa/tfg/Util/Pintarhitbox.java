package com.daisa.tfg.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.daisa.tfg.Balas.Bala;
import com.daisa.tfg.Balas.BalaPol;
import com.daisa.tfg.Balas.BalaRect;
import com.daisa.tfg.Personajes.Personaje;
import com.daisa.tfg.Personajes.PersonajeCirc;

public class Pintarhitbox {

    private static ShapeRenderer shapeRenderer = new ShapeRenderer();

    /**
     * Pinta los hitboxs de todos los objetos
     * @param personaje
     */
    public static void pintarHitboxes(Personaje personaje){
        Gdx.gl.glLineWidth(5f);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.setColor(Color.BLACK);

        for (Bala bala : Personaje.getBalasRival()) {
            if (bala instanceof BalaRect) {
                Rectangle r = bala.obtenerRectanguloBala(bala);
                shapeRenderer.rect(r.x, r.y, r.width, r.height);
            } else if (bala instanceof BalaPol) {
                Polygon p = bala.obtenerPoligonoBala(bala);
                shapeRenderer.polygon(p.getVertices());
            } else {
                Circle c = bala.obtenerCirculoBala(bala);
                shapeRenderer.circle(c.x, c.y, c.radius);
            }
        }

        for (Bala bala : personaje.getBalas()) {
            if (bala instanceof BalaRect) {
                Rectangle r = bala.obtenerRectanguloBala(bala);
                shapeRenderer.rect(r.x, r.y, r.width, r.height);
            } else if (bala instanceof BalaPol) {
                Polygon p = bala.obtenerPoligonoBala(bala);
                shapeRenderer.polygon(p.getVertices());
            } else {
                Circle c = bala.obtenerCirculoBala(bala);
                shapeRenderer.circle(c.x, c.y, c.radius);
            }
        }

        if (personaje instanceof PersonajeCirc) {
            Circle circ = personaje.obtenerCirculo(personaje);
            shapeRenderer.circle(circ.x, circ.y, circ.radius);
        } else {
            Polygon p = personaje.obtenerPoligono(personaje);
            shapeRenderer.polygon(p.getVertices());
        }

        shapeRenderer.end();
    }
}
