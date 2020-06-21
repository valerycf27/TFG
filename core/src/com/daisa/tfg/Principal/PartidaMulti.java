package com.daisa.tfg.Principal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.daisa.tfg.Balas.Bala;
import com.daisa.tfg.Constantes.ConstantesJuego;
import com.daisa.tfg.Personajes.Personaje;
import com.daisa.tfg.Personajes.PersonajeCirc;
import com.daisa.tfg.Personajes.PersonajePol;
import com.daisa.tfg.Screens.ElegirPersonajeScreen;
import com.daisa.tfg.Util.Explosion;
import com.daisa.tfg.Util.FondoAnimado;

import java.util.Random;

/**
 * Clase que se encarga de la gestion de la partida
 */
public class PartidaMulti implements Screen, InputProcessor {

    Juego juego;
    Personaje personaje;
    float accelX, accelY;
    int duracionPulsacion;

    OrthographicCamera camera;
    FondoAnimado fondoAnimado;
    Random random;

    public PartidaMulti(Juego juego, Array<String> rutaTexturas, int idPJ) {
        this.juego = juego;

        camera = new OrthographicCamera(ConstantesJuego.ANCHO_UNIDADES, ConstantesJuego.ALTO_UNIDADES);
        camera.position.set(ConstantesJuego.ANCHO_UNIDADES / 2, ConstantesJuego.ALTO_UNIDADES / 2, 0);
        camera.update();

        Gdx.input.setInputProcessor(this);
        instanciarPersonaje(idPJ, rutaTexturas);
        duracionPulsacion = 0;

        fondoAnimado = new FondoAnimado("Fondos/fondoAnimado.jpg");

        sonidosAleatorios();
    }

    /**
     * Metodo que genera unos sonidos diferentes en cada partida
     */
    private void sonidosAleatorios() {
        random = new Random();
        personaje.setNumeroSonidoDisparo(random.nextInt(ConstantesJuego.ARRAY_SONIDOS_DISPARO.size));
        personaje.setNumeroSonidoGolpe(random.nextInt(ConstantesJuego.ARRAY_SONIDOS_GOLPE.size));
        if(personaje.getIdPj()==2)
            personaje.setNumeroSonidoCarga(random.nextInt(ConstantesJuego.ARRAY_SONIDOS_CARGA.size));
        else
            personaje.setNumeroSonidoCarga(-1);
    }

    /**
     * Se instancia un personaje dependiendo de la posicion en la que se encuentre en el array de personajes
     * @param idPJ posicion del personaje en el array
     * @param rutaTexturas ruta de las texturas del personaje
     */
    private void instanciarPersonaje(int idPJ, Array<String> rutaTexturas) {
        switch (idPJ) {
            case 1:
            case 2:
            case 3:
            case 7:
            case 8:
            case 9:
            case 10:
                personaje = new PersonajePol(rutaTexturas, idPJ, 5, ConstantesJuego.PPU / 4);
                break;
            case 4:
            case 5:
            case 6:
                personaje = new PersonajeCirc(rutaTexturas, idPJ, 5, ConstantesJuego.PPU / 4);
                break;
            default:
                Gdx.app.error("DEBUG", "[ERROR] Id de personaje no valido");
                break;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        actualizar(delta);
        pintar();
    }

    /**
     * Metodo que se encarga de la logica de la partida
     * @param delta deltaTime de la partida
     */
    private void actualizar(float delta) {
        moverFondo(delta);
        moverPersonaje(delta);
        recargar();
        comprobarToquePantalla();
        moverBalas(delta);
        comprobarColisiones(delta);
        comprobarLimites();
    }

    /**
     * Se actualiza el fondo de la pantalla
     * @param delta deltaTime de la partida
     */
    private void moverFondo(float delta) {
        fondoAnimado.actualizar(delta);
    }

    /**
     * Se comprueba si se tiene que anadir una nueva bala a la municion del jugador
     */
    private void recargar() {
        if (personaje.getMunicion() < ConstantesJuego.MUNICION_MAXIMA) {
            if (TimeUtils.millis() - personaje.getMomentoUltimaRecarga() >= ConstantesJuego.TIEMPO_REGENERACION_BALAS_MILIS) {
                Gdx.app.debug("DEBUG", "Se regenera una bala, municion actual: " + personaje.getMunicion());
                personaje.setMomentoUltimaRecarga(TimeUtils.millis());
                personaje.setMunicion(personaje.getMunicion() + 1);
            }
        }
    }

    /**
     * Se comprueba si el usuario ha tocado la pantalla
     */
    private void comprobarToquePantalla() {
        if (personaje.getIdPj() == 2) {
            if (Gdx.input.isTouched()){
                duracionPulsacion++;
                if (duracionPulsacion > 40 && duracionPulsacion <= 80) {
                    if (!personaje.isCarga1Sonado()){
                        juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.ARRAY_SONIDOS_CARGA.get(personaje.getNumeroSonidoCarga()), Sound.class));
                        personaje.setCarga1Sonado(true);
                    }
                } else if (duracionPulsacion > 80) {
                    if (!personaje.isCarga2Sonado()){
                        juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.ARRAY_SONIDOS_CARGA.get(personaje.getNumeroSonidoCarga()), Sound.class));
                        personaje.setCarga2Sonado(true);
                    }
                }
            }
        }
    }

    /**
     * Se mueve y actualiza el personaje
     * @param delta deltaTime de la partida
     */
    private void moverPersonaje(float delta) {
        accelX = Gdx.input.getAccelerometerX();
        accelY = Gdx.input.getAccelerometerY();

        if ((accelX < -1 || accelX > 1) || (accelY < -1 || accelY > 1)) {
            if (accelX > 1) {
                personaje.moverAbajo();
            }
            if (accelX < -1) {
                personaje.moverArriba();
            }

            if (accelY > 1) {
                personaje.moverDerecha();
            }
            if (accelY < -1) {
                personaje.moverIzquierda();
            }
        } else {
            personaje.setEstado(Personaje.EstadosPersonaje.QUIETO);
        }

        personaje.actualizarFrame(delta);
    }

    /**
     * Se mueven y actualizan las balas
     * @param delta deltaTime de la partida
     */
    private void moverBalas(float delta) {
        personaje.moverBalas(personaje.getBalas());
        personaje.moverBalasRival(Personaje.getBalasRival());
        for(Bala bala : personaje.getBalas()){
            bala.actualizarFrame(delta);
        }
        for(Bala bala : Personaje.getBalasRival()){
            bala.actualizarFrame(delta);
        }
    }

    /**
     * Se comprueba si los objetos han chocado entre si
     * @param delta deltaTime de la partida
     */
    private void comprobarColisiones(float delta) {
        for (Bala bala : personaje.getBalas()) {
            for (Bala balaRival : Personaje.getBalasRival()) {
                if (bala.comprobarColisiones(bala, balaRival)) {
                    Personaje.getBalasRival().removeValue(balaRival, false);
                    personaje.getBalas().removeValue(bala, false);
                }
            }
        }

        for (Bala balaRival : Personaje.getBalasRival()) {
            if (balaRival.comprobarColisiones(balaRival, personaje)) {
                Personaje.getBalasRival().removeValue(balaRival, false);
                personaje.getArrayExplosiones().add(new Explosion());
                personaje.setVida(personaje.getVida() - balaRival.getTamanoBala());
                if (personaje.getVida() <= 0) {
                    juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.SONIDO_FIN_PARTIDA, Sound.class));
                    Gdx.app.debug("DEBUG", "Me han matado");
                    juego.setRivalPuntuacion(juego.getRivalPuntuacion() + 1);
                    juego.writeLIBGDX("fin");
                    juego.setScreen(new ElegirPersonajeScreen(juego));
                    this.dispose();
                }
                juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.ARRAY_SONIDOS_GOLPE.get(personaje.getNumeroSonidoGolpe()), Sound.class));
            }
        }
        for (Explosion explosion : personaje.getArrayExplosiones()) {
            explosion.actualizar(delta);
        }
    }

    /**
     * Se comprueba si los objetos han llegado al los limites de la pantalla
     */
    private void comprobarLimites() {

        if (personaje.getPosicion().x < 0)
            personaje.getPosicion().x = 0;
        if (personaje.getPosicion().x > ConstantesJuego.ANCHO_PANTALLA - personaje.getAnchoRelativoAspecto())
            personaje.getPosicion().x = ConstantesJuego.ANCHO_PANTALLA - personaje.getAnchoRelativoAspecto();

        if (personaje.getPosicion().y < 0)
            personaje.getPosicion().y = 0;
        if (personaje.getPosicion().y > ConstantesJuego.ALTO_PANTALLA - personaje.getAltoRelativoAspecto())
            personaje.getPosicion().y = ConstantesJuego.ALTO_PANTALLA - personaje.getAltoRelativoAspecto();

        personaje.recolocarHitbox();

        for (Bala bala : personaje.getBalas()) {
            if (bala.getPosicion().y >= ConstantesJuego.ALTO_PANTALLA) {
                float posicionRelativaEnvio = 100 - bala.posicionRelativaPantallaEnvio();
                juego.writeLIBGDX(posicionRelativaEnvio + ConstantesJuego.SEPARADOR_DATOS + personaje.getIdPj() + ConstantesJuego.SEPARADOR_DATOS + bala.getTamanoBala());
                personaje.getBalas().removeValue(bala, false);
            }
        }

        for (Bala balaRival : Personaje.getBalasRival()){
            if(balaRival.getPosicion().y <= -balaRival.getAltoRelativoAspecto()){
                Personaje.getBalasRival().removeValue(balaRival, false);
            }
        }
    }

    /**
     * Pinta lo que se vaya a ver en la pantalla
     */
    private void pintar() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        juego.batch.begin();
        fondoAnimado.pintar(juego.batch);
        personaje.pintar(juego.batch);
        for (Bala bala : personaje.getBalas())
            bala.pintar(juego.batch);
        for (Bala balaRival : Personaje.getBalasRival()) {
            balaRival.pintar(juego.batch);
        }
        for (Explosion explosion : personaje.getArrayExplosiones()) {
            explosion.pintar(juego.batch, personaje.getPosicion().x + personaje.getAnchoRelativoAspecto()/2, personaje.getPosicion().y + personaje.getAltoRelativoAspecto());
            if (explosion.animacionTerminada){
                personaje.getArrayExplosiones().removeValue(explosion, false);
            }
        }
        personaje.dibujarHud(juego.batch);
        juego.batch.end();

        //Pintarhitbox.pintarHitboxes(personaje);
    }

    @Override
    public void resize(int width, int height) {
        juego.viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        personaje.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        //Dependiendo de cuanto tiempo se haya pulsado la pantalla, el tamano de la bala aumentara
        int tamanoBala = 1;
        if (personaje.getIdPj() == 2) {
            if (duracionPulsacion > 40 && duracionPulsacion <= 80) {
                tamanoBala = 2;
            } else if (duracionPulsacion > 80) {
                tamanoBala = 3;
            }
        }

        personaje.setCarga1Sonado(false);
        personaje.setCarga2Sonado(false);

        personaje.disparar(tamanoBala, juego);
        duracionPulsacion = 0;

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
