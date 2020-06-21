package com.daisa.tfg.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.daisa.tfg.Balas.Bala;
import com.daisa.tfg.Balas.BalaCirc;
import com.daisa.tfg.Balas.BalaPol;
import com.daisa.tfg.Balas.BalaRect;
import com.daisa.tfg.Constantes.ConstantesJuego;
import com.daisa.tfg.Principal.Juego;
import com.daisa.tfg.Util.Explosion;
import com.daisa.tfg.Util.Util;

import java.lang.reflect.Field;

/**
 * Clase abstracta que almacena los atributos del personaje excepto el hitbox
 */
public abstract class Personaje {

    private int vida;
    private float velocidad;
    private static float velocidadBalas;
    private Vector2 posicion;
    private EstadosPersonaje estado;
    private float stateTime;
    private int idPj;
    private int numeroSonidoDisparo, numeroSonidoGolpe, numeroSonidoCarga;
    private boolean carga1Sonado, carga2Sonado;

    private Array<Bala> balas;
    private static Array<Bala> balasRival;
    private TextureRegion aspectoActual;
    private Animation animacionDerecha, animacionIzquierda;
    private Array<TextureRegion> texturasDerecha = new Array<>(), texturasIzquierda = new Array<>();
    private TextureRegion aspectoBasico;
    private TextureRegion hudCorazones;

    private float relacionAspecto;
    private float anchoRelativoAspecto, altoRelativoAspecto;

    private int municion;
    private TextureRegion hubBalas;
    private long momentoUltimoDisparo, momentoUltimaRecarga;

    private Array<Explosion> arrayExplosiones = new Array<>();


    public Personaje(Array<String> rutaAnimaciones, int idPj, int vida, float velocidad) {
        this.vida = vida;
        this.velocidad = velocidad;
        velocidadBalas = this.velocidad * 3;
        this.idPj = idPj;
        inicializarAnimaciones(rutaAnimaciones);

        estado = EstadosPersonaje.QUIETO;

        relacionAspecto = (float) aspectoBasico.getRegionWidth() / aspectoBasico.getRegionHeight();

        posicion = new Vector2(0, 0);

        anchoRelativoAspecto = ConstantesJuego.PPU * 3 * relacionAspecto;
        altoRelativoAspecto = ConstantesJuego.PPU * 3;

        balas = new Array<>();
        balasRival = new Array<>();

        hudCorazones = new TextureRegion(new Sprite(new Texture(Gdx.files.internal("Vidas/corazon.png"))));
        hubBalas = new TextureRegion(new Sprite(new Texture(Gdx.files.internal("Balas/" + idPj + "/b1.png"))));

        municion = ConstantesJuego.MUNICION_MAXIMA;
        momentoUltimoDisparo = TimeUtils.millis();
        momentoUltimaRecarga = TimeUtils.millis();

        carga1Sonado = false;
        carga2Sonado = false;
    }

    //Getters & Setters
    public Array<Explosion> getArrayExplosiones() {
        return arrayExplosiones;
    }

    public long getMomentoUltimaRecarga() {
        return momentoUltimaRecarga;
    }

    public void setMomentoUltimaRecarga(long momentoUltimaRecarga) {
        this.momentoUltimaRecarga = momentoUltimaRecarga;
    }

    public int getMunicion() {
        return municion;
    }

    public void setMunicion(int municion) {
        this.municion = municion;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public Vector2 getPosicion() {
        return posicion;
    }

    public void setEstado(EstadosPersonaje estado) {
        this.estado = estado;
    }

    public int getIdPj() {
        return idPj;
    }

    public Array<Bala> getBalas() {
        return balas;
    }

    public static Array<Bala> getBalasRival() {
        return balasRival;
    }

    public float getAnchoRelativoAspecto() {
        return anchoRelativoAspecto;
    }

    public float getAltoRelativoAspecto() {
        return altoRelativoAspecto;
    }

    public void setNumeroSonidoDisparo(int numeroSonidoDisparo) {
        this.numeroSonidoDisparo = numeroSonidoDisparo;
    }

    public int getNumeroSonidoGolpe() {
        return numeroSonidoGolpe;
    }

    public void setNumeroSonidoGolpe(int numeroSonidoGolpe) {
        this.numeroSonidoGolpe = numeroSonidoGolpe;
    }

    public int getNumeroSonidoCarga() {
        return numeroSonidoCarga;
    }

    public void setNumeroSonidoCarga(int numeroSonidoCarga) {
        this.numeroSonidoCarga = numeroSonidoCarga;
    }

    public boolean isCarga1Sonado() {
        return carga1Sonado;
    }

    public void setCarga1Sonado(boolean carga1Sonado) {
        this.carga1Sonado = carga1Sonado;
    }

    public boolean isCarga2Sonado() {
        return carga2Sonado;
    }

    public void setCarga2Sonado(boolean carga2Sonado) {
        this.carga2Sonado = carga2Sonado;
    }

    /**
     * anade las texturas a las animaciones
     * @param rutaAnimaciones ruta de las texturas del personaje
     */
    private void inicializarAnimaciones(Array<String> rutaAnimaciones) {

        aspectoBasico = new Sprite(new Texture(Gdx.files.internal(rutaAnimaciones.get(0))));

        for (int i = 1; i < 6; i++) {
            if (rutaAnimaciones.get(i).contains("D"))
                texturasDerecha.add(new Sprite(new Texture(Gdx.files.internal(rutaAnimaciones.get(i)))));
            else
                texturasIzquierda.add(new Sprite(new Texture(Gdx.files.internal(rutaAnimaciones.get(i)))));
        }

        animacionDerecha = new Animation(0.75f, texturasDerecha);
        animacionIzquierda = new Animation(0.75f, texturasIzquierda);
    }

    /**
     * Metodo abstracto que mueve la textura del personaje
     * @param direccion
     */
    public abstract void mover(Vector2 direccion);

    /**
     * Mueve a la derecha la textura del personaje
     * Estado pasa a DERECHA
     */
    public void moverDerecha() {
        estado = EstadosPersonaje.DERECHA;
        mover(new Vector2(1, 0));
    }

    /**
     * Mueve a la izquierda la textura del personaje
     * Estado pasa a IZQUIERDA
     */
    public void moverIzquierda() {
        estado = EstadosPersonaje.IZQUIERDA;
        mover(new Vector2(-1, 0));
    }

    /**
     * Mueve hacia arriba la textura del personaje
     * Estado pasa a QUIETO
     */
    public void moverArriba() {
        estado = EstadosPersonaje.QUIETO;
        mover(new Vector2(0, 1));
    }

    /**
     * Mueve hacia abajo la textura del personaje
     * Estado pasa a QUIETO
     */
    public void moverAbajo() {
        estado = EstadosPersonaje.QUIETO;
        mover(new Vector2(0, -1));
    }

    /**
     * Pinta al personaje escalado
     * @param batch objeto para pintar
     */
    public void pintar(SpriteBatch batch) {
        batch.draw(aspectoActual, posicion.x, posicion.y, anchoRelativoAspecto, altoRelativoAspecto);
    }

    /**
     * <p>Pinta las vidas (en corazones) en la parte derecha de la pantalla</p>
     * <p>Pinta la municion en la parte izquierda de la pantalla</p>
     * @param batch
     */
    public void dibujarHud(SpriteBatch batch) {
        for (int i = 0; i < vida; i++) {
            batch.draw(hudCorazones, ConstantesJuego.PPU / 2, ConstantesJuego.PPU * ((3 * i + 1) / 2f), ConstantesJuego.PPU, ConstantesJuego.PPU);
        }

        float relacionAspecto = (float) hubBalas.getRegionWidth() / hubBalas.getRegionHeight();
        for (int i = 0; i < municion; i++) {
            batch.draw(hubBalas, ConstantesJuego.ANCHO_PANTALLA - ConstantesJuego.PPU * 3 / 2, ConstantesJuego.PPU * ((3 * i + 1) / 2f), ConstantesJuego.PPU * relacionAspecto, ConstantesJuego.PPU);
        }
    }

    /**
     * Comprueba si se puede disparar:
     *      -Si se puede, se genera una bala con el id y la posicion del personaje
     * @param tamanoBala tamano de la bala al disparar
     * @param juego objeto usado para reproducir sonidos en la funcion
     */
    public void disparar(int tamanoBala, Juego juego) {
        if (puedeDisparar(tamanoBala) && TimeUtils.millis() - momentoUltimoDisparo >= ConstantesJuego.CADENCIA_DISPAROS_MILIS) {
            juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.ARRAY_SONIDOS_DISPARO.get(numeroSonidoDisparo), Sound.class));
            momentoUltimoDisparo = TimeUtils.millis();
            municion -= tamanoBala;
            Vector2 posicionBala = new Vector2(posicion.x + anchoRelativoAspecto / 2f, posicion.y + altoRelativoAspecto);
            Bala bala = tipoBala(this.idPj, posicionBala, tamanoBala);
            balas.add(bala);
        }
    }

    /**
     * Comprueba al disparar una bala, su tamano no supere la municion restante
     * @param tamanoBala tamano de la bala al disparar
     * @return si hay municion suficiente para disparar o no
     */
    protected boolean puedeDisparar(int tamanoBala) {
        return municion >= tamanoBala;
    }

    /**
     * Se anade la bala recibida por BlueTooth al array de las balas rivales
     * @param balaX posicion X de la bala
     * @param idPjRival id del personaje que disparo la bala
     * @param tamanoBala tamano de la bala al recibirla
     */
    public static void anadirBalaRival(float balaX, int idPjRival, int tamanoBala) {
        Vector2 posicionRelativaRecibido = new Vector2(ConstantesJuego.ANCHO_PANTALLA * balaX / 100, ConstantesJuego.ALTO_PANTALLA);
        Bala balaRival = tipoBala(idPjRival, posicionRelativaRecibido, tamanoBala);
        balasRival.add(balaRival);
    }

    /**
     * Genera una clase de bala diferente dependiendo del personaje que le haya disparado
     * @param idPj id del personaje que le ha disparado
     * @param posicionRelativaRecibido posicion X e Y donde se generara la bala
     * @param tamanoBala tamano de la bala que se ha disparado'
     * @return retorna una bala de un rectangular, circular o poligonal
     */
    private static Bala tipoBala(int idPj, Vector2 posicionRelativaRecibido, int tamanoBala) {
        Bala bala;
        Array<TextureRegion> arrayTexturas = Util.ficherosEnDirectorio(Gdx.files.internal("Balas/" + idPj));
        switch (idPj) {
            //Balas con forma rectangular
            case 1:
            case 4:
            case 7:
                bala = new BalaRect(posicionRelativaRecibido, velocidadBalas, arrayTexturas, idPj, tamanoBala);
                break;
            //Balas con forma circular
            case 2:
                bala = new BalaCirc(posicionRelativaRecibido, velocidadBalas, arrayTexturas, idPj, tamanoBala);
                break;
            //Balas con forma poligonal
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
                bala = new BalaPol(posicionRelativaRecibido, velocidadBalas, arrayTexturas, idPj, tamanoBala);
                break;
            default:
                arrayTexturas = Util.ficherosEnDirectorio(Gdx.files.internal("Balas/1"));
                bala = new BalaRect(posicionRelativaRecibido, velocidadBalas, arrayTexturas, idPj, tamanoBala);
                break;
        }
        return bala;
    }

    /**
     * Metodo que se encarga de actualizar la textura que hay que pintar en cada frame
     * @param delta deltaTime
     */
    public void actualizarFrame(float delta) {
        stateTime += delta;
        switch (estado) {
            case DERECHA:
                aspectoActual = (TextureRegion) animacionDerecha.getKeyFrame(stateTime, false);
                break;
            case IZQUIERDA:
                aspectoActual = (TextureRegion) animacionIzquierda.getKeyFrame(stateTime, false);
                break;
            default:
                aspectoActual = aspectoBasico;
                break;
        }
    }

    /**
     * Metodo abstracto que se encarga de recolocar el hitbox del personaje despues de moverse
     */
    public abstract void recolocarHitbox();

    public void dispose() {
        estado = EstadosPersonaje.MUERTO;
        balas.clear();
        balasRival.clear();
        texturasDerecha.clear();
        texturasIzquierda.clear();
    }


    /**
     * Mueve las balas de su array
     * @param balas array de balas a mover
     */
    public void moverBalas(Array<Bala> balas) {
        for (Bala bala : balas) {
            switch (bala.getIdPj()) {
                case 1:
                case 4:
                case 7:
                    bala.getPosicion().add(new Vector2(0, 1).scl(bala.getVelocidad()));
                    Rectangle rect = bala.obtenerRectanguloBala(bala);
                    rect.setPosition(bala.getPosicion());
                    break;

                case 2:
                    bala.getPosicion().add(new Vector2(0, 1).scl(bala.getVelocidad()));
                    Circle circ = bala.obtenerCirculoBala(bala);
                    circ.setPosition(bala.getPosicion().x + bala.getAnchoRelativoAspecto() / 2, bala.getPosicion().y + bala.getAltoRelativoAspecto() / 2);
                    break;
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                    bala.getPosicion().add(new Vector2(0, 1).scl(bala.getVelocidad()));
                    Polygon p = bala.obtenerPoligonoBala(bala);
                    for (int i = 0; i < p.getVertices().length; i++) {
                        if (i % 2 != 0) {
                            p.getVertices()[i] += bala.getVelocidad();
                        }
                    }
                    p.setPosition(bala.getPosicion().x, bala.getPosicion().y);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Mueve las balas rival de su array
     * @param balasRival array de balas las rival a mover
     */
    public void moverBalasRival(Array<Bala> balasRival) {
        for (Bala bala : balasRival) {
            switch (bala.getIdPj()) {
                case 1:
                case 4:
                case 7:
                    bala.getPosicion().add(new Vector2(0, -1).scl(bala.getVelocidad()));
                    Rectangle rect = bala.obtenerRectanguloBala(bala);
                    rect.setPosition(bala.getPosicion());
                    break;

                case 2:
                    bala.getPosicion().add(new Vector2(0, -1).scl(bala.getVelocidad()));
                    Circle circ = bala.obtenerCirculoBala(bala);
                    circ.setPosition(bala.getPosicion().x + bala.getAnchoRelativoAspecto() / 2, bala.getPosicion().y + bala.getAltoRelativoAspecto() / 2);
                    break;
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                    bala.getPosicion().add(new Vector2(0, -1).scl(bala.getVelocidad()));
                    Polygon p = bala.obtenerPoligonoBala(bala);
                    for (int i = 0; i < p.getVertices().length; i++) {
                        if (i % 2 != 0) {
                            p.getVertices()[i] -= bala.getVelocidad();
                        }
                    }
                    p.setPosition(bala.getPosicion().x, bala.getPosicion().y);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Obtiene el poligono del personaje
     * @param personaje personaje del que se quiere obtener el poligono
     * @return poligono del personaje o null si ha habido algun error
     */
    public Polygon obtenerPoligono(Personaje personaje) {
        Field field;
        try {
            field = personaje.getClass().getDeclaredField("pol");
            field.setAccessible(true);
            return (Polygon) field.get(personaje);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Gdx.app.debug("DEBUG", "[ERROR Poligono] " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene el circulo del personaje
     * @param personaje personaje del que se quiere obtener el circulo
     * @return circulo del personaje o null si ha habido algun error
     */
    public Circle obtenerCirculo(Personaje personaje) {
        try {
            Field field = personaje.getClass().getDeclaredField("circ");
            field.setAccessible(true);
            return (Circle) field.get(personaje);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Gdx.app.debug("DEBUG", "[ERROR Circulo]" + e.getMessage());
        }
        return null;
    }

    /**
     * Estados del movimiento del personaje
     */
    public enum EstadosPersonaje {
        DERECHA, IZQUIERDA, QUIETO, MUERTO
    }
}
