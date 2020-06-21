package com.daisa.tfg.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;
import com.daisa.tfg.Constantes.ConstantesJuego;
import com.daisa.tfg.Principal.Juego;
import com.daisa.tfg.Principal.PartidaMulti;
import com.daisa.tfg.Util.SimpleDirectionGestureDetector;

public class ElegirPersonajeScreen implements Screen, InputProcessor {
    Juego juego;

    Array<Image> regionsPequenos = new Array<>();
    Array<Image> regionGrandes = new Array<>();
    int mostrando;

    Stage stage;
    InputProcessor gestosProcesador;
    boolean esperando;
    Array<Array<String>> rutaMatrizAnimaciones = new Array<>();

    public ElegirPersonajeScreen(final Juego juego) {
        this.juego = juego;
        inicializar();
        mostrando = 0;

        /**
         * Objeto que nos permite implementar el deslizamiento sobre la pantalla
         */
        gestosProcesador = new SimpleDirectionGestureDetector(new SimpleDirectionGestureDetector.DireccionListener() {

            @Override
            public void onUp() {

            }

            @Override
            public void onRight() {
                juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.SONIDO_TRANSICION, Sound.class));
                if (mostrando > 0)
                    mostrando--;

                Gdx.app.debug("Desliza Der", String.valueOf(mostrando));
                show();
            }

            @Override
            public void onLeft() {
                juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.SONIDO_TRANSICION, Sound.class));
                if (mostrando < regionsPequenos.size - 1)
                    mostrando++;

                Gdx.app.debug("Desliza Izq", String.valueOf(mostrando));
                show();
            }

            @Override
            public void onDown() {

            }
        });
        esperando = false;
    }

    /**
     * Inicializa los arraysde texturas para mostrar los personajes
     */
    private void inicializar() {
        for (int i = 0; i < 10; i++) {
            regionsPequenos.add(new Image(new Texture(Gdx.files.internal("Personajes/Personajes Peques/p" + (i + 1) + ".png"))));
            regionGrandes.add(new Image(new Texture(Gdx.files.internal("Personajes/Personajes Grandes/p" + (i + 1) + ".png"))));
        }

        for (int i = 0; i < 10; i++) {
            rutaMatrizAnimaciones.add(new Array<String>());
            rutaMatrizAnimaciones.get(i).add("Personajes/Animaciones/p" + (i + 1) + ".png");
            for (int j = 0; j < 3; j++) {
                rutaMatrizAnimaciones.get(i).add("Personajes/Animaciones/p" + (i + 1) + "D" + (j + 1) + ".png");
                rutaMatrizAnimaciones.get(i).add("Personajes/Animaciones/p" + (i + 1) + "I" + (j + 1) + ".png");
            }
        }
    }

    /**
     * Cambia la musica que se esta reproduciendo
     */
    private void cambiarMusica() {
        if(juego.manager.managerJuego.get(ConstantesJuego.MUSICA_MENU, Music.class).isPlaying()){
            juego.manager.managerJuego.get(ConstantesJuego.MUSICA_MENU, Music.class).stop();
        }
        juego.reproducirMusica(juego.manager.managerJuego.get(ConstantesJuego.MUSICA_JUEGO, Music.class));
    }

    @Override
    public void show() {

        cambiarMusica();

        stage = new Stage(juego.viewport);

        Table tabla = new Table();
        tabla.setFillParent(true);
        stage.addActor(tabla);
        tabla.setBackground(new TiledDrawable(juego.getFondoMenu()));

        regionGrandes.get(mostrando).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.SONIDO_PULSAR_BOTON, Sound.class));
                if (!esperando) {
                    juego.comenzarPartida();
                    esperando = true;
                    juego.yoPreparado = true;
                    show();
                }
            }
        });


        CharSequence datos = juego.getNombreUsuario() + ": " + juego.getMiPuntuacion() + " - Rival: " + juego.getRivalPuntuacion();
        Label puntuacion = new Label(datos, juego.manager.getEstiloLabel());
        puntuacion.setFontScale(3);

        tabla.row().height(300).padBottom(150);
        tabla.add().width(200);
        tabla.add(puntuacion);
        tabla.add().width(200);

        tabla.row();
        if (mostrando > 0) {
            tabla.add(regionsPequenos.get(mostrando - 1)).padRight(50);
        } else {
            tabla.add().width(400).height(400).padRight(50);
        }

        tabla.add(regionGrandes.get(mostrando)).padRight(50).padLeft(50);

        if (mostrando < regionsPequenos.size - 1) {
            tabla.add(regionsPequenos.get(mostrando + 1)).padLeft(50);
        } else {
            tabla.add().width(400).height(400).padLeft(50);
        }

        if (!esperando) {
            //Objeto que nos permite aplicar varios InpputProcessor sobre un mismo objeto
            InputMultiplexer inputMultiplexer = new InputMultiplexer(gestosProcesador, stage);
            Gdx.input.setInputProcessor(inputMultiplexer);
        }else{
            Gdx.input.setInputProcessor(null);
        }

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (juego.yoPreparado && juego.rivalPreparado) {
            esperando = false;
            juego.yoPreparado = false;
            juego.rivalPreparado = false;
            juego.setScreen(new PartidaMulti(juego, rutaMatrizAnimaciones.get(mostrando), mostrando + 1));
            this.dispose();
        }

        stage.act(delta);
        stage.draw();
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
        regionsPequenos.clear();
        regionGrandes.clear();
        rutaMatrizAnimaciones.clear();
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
        return false;
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
