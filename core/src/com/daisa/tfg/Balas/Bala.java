package com.daisa.tfg.Balas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.daisa.tfg.Constantes.ConstantesJuego;
import com.daisa.tfg.Personajes.Personaje;
import com.daisa.tfg.Personajes.PersonajeCirc;
import com.daisa.tfg.Personajes.PersonajePol;

import java.lang.reflect.Field;

/**
 * Clase abstracta que contiene todos los atributos (menos el hitbox) de las balas
 */
public abstract class Bala {

    private Vector2 posicion;
    private TextureRegion aspecto;
    private float velocidad;
    private float relacionAspecto;
    private float anchoRelativoAspecto, altoRelativoAspecto;
    private static final Vector2 centro = new Vector2();
    private static final Vector2 vec1 = new Vector2();
    private static final Vector2 vec2 = new Vector2();
    private int tamanoBala;
    private int idPj;

    Animation animation;
    Array<TextureRegion> arrayTexturas;
    private float stateTime;

    /**
     * @param posicion posicion X e Y donde va a generarse la bala
     * @param velocidad velocidad a la que se mueve la bala
     * @param arrayTexturas texturas de la animacion de la bala
     * @param idPj id deljugador que ha generado la bala
     * @param tamanoBala tamano con el que se va a pintar la bala
     */
    public Bala(Vector2 posicion, float velocidad, Array<TextureRegion> arrayTexturas, int idPj, int tamanoBala) {
        this.posicion = posicion;
        this.velocidad = velocidad;
        this.arrayTexturas = arrayTexturas;
        this.idPj = idPj;

        relacionAspecto = (float) this.arrayTexturas.get(0).getRegionWidth() / this.arrayTexturas.get(0).getRegionHeight();
        animation = new Animation(0.2f, arrayTexturas);

        anchoRelativoAspecto = ConstantesJuego.PPU * relacionAspecto * tamanoBala;
        altoRelativoAspecto = ConstantesJuego.PPU * tamanoBala;

        this.tamanoBala = tamanoBala;
    }

    //Getters
    public Vector2 getPosicion() {
        return posicion;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public float getAnchoRelativoAspecto() {
        return anchoRelativoAspecto;
    }

    public float getAltoRelativoAspecto() {
        return altoRelativoAspecto;
    }

    public int getTamanoBala() {
        return tamanoBala;
    }

    public int getIdPj() {
        return idPj;
    }

    /**
     * Pinta la bala on el tamano escalado
     * @param batch objeto para pintar en pantalla
     */
    public void pintar(SpriteBatch batch) {
        batch.draw(aspecto, posicion.x, posicion.y, anchoRelativoAspecto, altoRelativoAspecto);
    }

    /**
     * Actualiza la textura que hay que pintar
     * @param delta deltaTime del juego
     */
    public void actualizarFrame(float delta) {
        stateTime += delta;
        aspecto = (TextureRegion) animation.getKeyFrame(stateTime, true);
    }

    /**
     * @return porcentaje de la posicion en pantalla
     */
    public float posicionRelativaPantallaEnvio() {
        return posicion.x * 100 / ConstantesJuego.ANCHO_PANTALLA;
    }

    /**
     * Obtiene el rectangulo de la bala
     * @param bala bala de la que se quiere obtener el hitbox
     * @return devuelve el rectanguo o null si ha habido algun fallo
     */
    public Rectangle obtenerRectanguloBala(Bala bala) {
        return obtenerRectangulo(bala);
    }

    /**
     * Obtiene el rectangulo del objeto
     * @param object objeto de la que se quiere obtener el hitbox
     * @return devuelve el rectanguo o null si ha habido algun fallo
     */
    public Rectangle obtenerRectangulo(Object object) {
        try {
            Field field = object.getClass().getDeclaredField("rect");
            field.setAccessible(true);
            return (Rectangle) field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Gdx.app.debug("DEBUG", "[ERROR Rectangulo] " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene el poligono de la bala
     * @param bala bala de la que se quiere obtener el hitbox
     * @return devuelve el poligono o null si ha habido algun fallo
     */
    public Polygon obtenerPoligonoBala(Bala bala) {
        return obtenerPoligono(bala);
    }

    /**
     * Obtiene el poligono del personaje
     * @param personaje personaje del que se quiere obtener el hitbox
     * @return devuelve el poligono o null si ha habido algun fallo
     */
    Polygon obtenerPoligonoPersonaje(Personaje personaje) {
        return obtenerPoligono(personaje);
    }

    /**
     * Obtiene el poligono del objeto
     * @param object objeto del que se quiere obtener el hitbox
     * @return devuelve el poligono o null si ha habido algun fallo
     */
    protected Polygon obtenerPoligono(Object object) {
        Field field;
        try {
            field = object.getClass().getDeclaredField("pol");
            field.setAccessible(true);
            return (Polygon) field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Gdx.app.debug("DEBUG", "[ERROR Poligono] " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene el circulo de la bala
     * @param bala bala de la que se quiere obtener el hitbox
     * @return devuelve el circulo o null si ha habido algun fallo
     */
    public Circle obtenerCirculoBala(Bala bala) {
        return obtenerCirculo(bala);
    }

    /**
     * Obtiene el circulo del personaje
     * @param personaje personaje del que se quiere obtener el hitbox
     * @return devuelve el circulo o null si ha habido algun fallo
     */
    Circle obtenerCirculoPersonaje(Personaje personaje) {
        return obtenerCirculo(personaje);
    }

    /**
     * Obtiene el circulo del objeto
     * @param object objeto del que se quiere obtener el hitbox
     * @return devuelve el circulo o null si ha habido algun fallo
     */
    Circle obtenerCirculo(Object object) {
        try {
            Field field = object.getClass().getDeclaredField("circ");
            field.setAccessible(true);
            return (Circle) field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Gdx.app.debug("DEBUG", "[ERROR Circulo]" + e.getMessage());
        }
        return null;
    }

    /**
     * Comprueba si las balas han chocado
     * @param bala bala del jugador
     * @param balaRival bala del rival
     * @return true si han chocado, false si no.
     * Se devuele true por defecto para evitar fallos de balas que no se chocan
     */
    public boolean comprobarColisiones(Bala bala, Bala balaRival) {
        if (bala instanceof BalaRect) {
            Rectangle rect = obtenerRectanguloBala(bala);
            if (balaRival instanceof BalaRect) {
                Rectangle rectRival = obtenerRectanguloBala(balaRival);
                if (rect != null && rectRival != null)
                    return rect.overlaps(rectRival);

            } else if (balaRival instanceof BalaCirc) {
                Circle circRival = obtenerCirculoBala(balaRival);
                if (rect != null && circRival != null)
                    return Intersector.overlaps(circRival, rect);

            } else {
                Polygon polRival = obtenerPoligonoBala(balaRival);
                if (rect != null && polRival != null)
                    return chocaRectanguloPoligono(rect, polRival, polRival.getVertices());
            }

        } else if (bala instanceof BalaCirc) {
            Circle circ = obtenerCirculoBala(bala);
            if (balaRival instanceof BalaRect) {
                Rectangle rectRival = obtenerRectanguloBala(balaRival);
                if (circ != null && rectRival != null)
                    return Intersector.overlaps(circ, rectRival);

            } else if (balaRival instanceof BalaCirc) {
                Circle circRival = obtenerCirculoBala(balaRival);
                if (circ != null && circRival != null)
                    return circ.overlaps(circRival);

            } else {
                Polygon polRival = obtenerPoligonoBala(balaRival);
                if (circ != null && polRival != null)
                    return chocaCirculoPoligono(circ, polRival);
            }

        } else if (bala instanceof BalaPol) {
            Polygon pol = obtenerPoligonoBala(bala);
            if (balaRival instanceof BalaRect) {
                Rectangle rectRival = obtenerRectanguloBala(balaRival);
                if (pol != null && rectRival != null)
                    return chocaRectanguloPoligono(rectRival, pol, pol.getVertices());

            } else if (balaRival instanceof BalaCirc) {
                Circle circRival = obtenerCirculoBala(balaRival);
                if (pol != null && circRival != null)
                    return chocaCirculoPoligono(circRival, pol);

            } else {
                Polygon polRival = obtenerPoligonoBala(balaRival);
                if (pol != null && polRival != null)
                    return Intersector.overlapConvexPolygons(pol, polRival);
            }
        }
        return true;
    }
    /**
     * Comprueba si las balas han chocado con el personaje
     * @param bala bala del jugador
     * @param personaje personaje
     * @return true si han chocado, false si no.
     * Se devuele true por defecto para evitar fallos de balas que no se chocan
     */
    public boolean comprobarColisiones(Bala bala, Personaje personaje) {

        if (bala instanceof BalaRect) {
            Rectangle rect = obtenerRectanguloBala(bala);
            if (personaje instanceof PersonajeCirc) {
                Circle circPersonaje = obtenerCirculoPersonaje(personaje);
                if (rect != null && circPersonaje != null)
                    return Intersector.overlaps(circPersonaje, rect);
            } else if (personaje instanceof PersonajePol) {
                Polygon polPersonaje = obtenerPoligonoPersonaje(personaje);
                if (rect != null && polPersonaje != null) {
                    Gdx.app.debug("DEBUG", "Se comprueba colision balaRect personajePol");
                    return chocaRectanguloPoligono(rect, polPersonaje, polPersonaje.getVertices());
                }
            }

        } else if (bala instanceof BalaCirc) {
            Circle circ = obtenerCirculoBala(bala);
            if (personaje instanceof PersonajeCirc) {
                Circle circPersonaje = obtenerCirculoPersonaje(personaje);
                if (circ != null && circPersonaje != null)
                    return circ.overlaps(circPersonaje);
            } else if (personaje instanceof PersonajePol) {
                Polygon polPersonaje = obtenerPoligonoPersonaje(personaje);
                if (circ != null && polPersonaje != null)
                    return chocaCirculoPoligono(circ, polPersonaje);
            }

        } else if (bala instanceof BalaPol) {
            Polygon pol = obtenerPoligonoBala(bala);
            if (personaje instanceof PersonajeCirc) {
                Circle circPersonaje = obtenerCirculoPersonaje(personaje);
                if (pol != null && circPersonaje != null)
                    return chocaCirculoPoligono(circPersonaje, pol);

            } else if (personaje instanceof PersonajePol) {
                Polygon polPersonaje = obtenerPoligonoPersonaje(personaje);
                if (pol != null && polPersonaje != null) {
                    Gdx.app.debug("DEBUG", "Se comprueba colision balaPol personajePol");
                    for (int i = 0; i < polPersonaje.getTransformedVertices().length; i += 2) {
                        if (pol.contains(polPersonaje.getTransformedVertices()[i], polPersonaje.getTransformedVertices()[i + 1])) {
                            Gdx.app.debug("DEBUG", "balaPol dentro de personajePol");
                            return true;
                        }
                    }
                    return Intersector.overlapConvexPolygons(pol, polPersonaje);
                }
            }
        }
        return true;
    }

    /**
     * Comprueba si el rectangulo y elpoligono se han tocado
     * @param rect rectangulo
     * @param polRival poligono con el que se comprueba si se toca
     * @param vertices vertices del poligono con que se comprueba si se toca
     * @return true si se han tocado o el rectangulo esta dentro del poligono, false si no
     */
    boolean chocaRectanguloPoligono(Rectangle rect, Polygon polRival, float[] vertices) {
        Polygon rPoly = new Polygon(vertices);
        rPoly.setPosition(rect.x, rect.y);

        if (polRival.contains(rect.x, rect.y))
            return true;
        return Intersector.overlapConvexPolygons(rPoly, polRival);
    }

    /**
     * Comprueba si el circulo y el poligono se han tocado
     * @param circ circulo
     * @param pol poligono con el que se comprueba si se toca
     * @return true si se han tocado o el poligono esta dentro del circulo, false si no
     */
    boolean chocaCirculoPoligono(Circle circ, Polygon pol) {
        centro.set(circ.x, circ.y);
        float[] vertices = pol.getVertices();
        float radioCuadrado = (float) Math.pow(circ.radius, 2d);
        if (Intersector.intersectSegmentCircle(vec1.set(vertices[vertices.length - 2], vertices[vertices.length - 1]), vec2.set(vertices[0], vertices[1]), centro, radioCuadrado))
            return true;
        for (int i = 2; i < vertices.length; i += 2)
            if (Intersector.intersectSegmentCircle(vec1.set(vertices[i - 2], vertices[i - 1]), vec2.set(vertices[i], vertices[i + 1]), centro, radioCuadrado))
                return true;
        return pol.contains(circ.x, circ.y);
    }


}
