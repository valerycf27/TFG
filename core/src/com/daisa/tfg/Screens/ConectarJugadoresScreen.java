package com.daisa.tfg.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;
import com.daisa.tfg.Constantes.ConstantesJuego;
import com.daisa.tfg.Principal.Juego;

public class ConectarJugadoresScreen implements Screen {

    ScrollPane scrollPane;
    Juego juego;
    Stage stage;
    List<String> list;
    Array<String> dispositivosConectados;

    public ConectarJugadoresScreen(Juego juego) {
        this.juego = juego;
        dispositivosConectados = new Array<>();
    }

    @Override
    public void show() {
        juego.reproducirMusica(juego.manager.managerJuego.get(ConstantesJuego.MUSICA_MENU, Music.class));

        stage = new Stage(juego.viewport);

        Table tabla = new Table();
        tabla.setFillParent(true);
        stage.addActor(tabla);
        tabla.setBackground(new TiledDrawable(juego.getFondoMenu()));

        list = new List<>(juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        list.setItems(dispositivosConectados);

        TextButton button = new TextButton("Conectar", juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.SONIDO_PULSAR_BOTON, Sound.class));
                if(list.getItems().size > 0){
                    String clickedItem = list.getSelected();
                    juego.conectarDispositivosBluetoothLIBGDX(clickedItem);
                }
            }
        });

        TextButton hostButton = new TextButton("Host", juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        hostButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.SONIDO_PULSAR_BOTON, Sound.class));
                juego.crearToastLIBGDX("Se permite que otros dispositivos se conecten a este");
                juego.stopLIBGDX();
                juego.habilitarSerDescubiertoBluetoothLIBGDX();
                juego.escucharBluetoothLIBGDX();
            }
        });
        TextButton listenButton = new TextButton("Descubrir", juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        listenButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.SONIDO_PULSAR_BOTON, Sound.class));
                juego.crearToastLIBGDX("Se empieza a descubrir dispositivos cercanos");
                juego.stopLIBGDX();
                juego.descubrirDispositivosBluetoothLIBGDX();
            }
        });

        TextButton botonVolver = new TextButton("Volver", juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        botonVolver.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.SONIDO_PULSAR_BOTON, Sound.class));
                juego.setScreen(new MenuPrincipalScreen(juego));
            }
        });

        scrollPane = new ScrollPane(list);

        Table tablaScrollPane = new Table();
        Table tablaBotones = new Table();

        tablaScrollPane.row();
        tablaScrollPane.add(scrollPane).height(800).width(750);

        tablaBotones.row();
        tablaBotones.add(button).height(150).width(700);
        tablaBotones.row();
        tablaBotones.add().height(100);
        tablaBotones.row();
        tablaBotones.add(hostButton).height(150).width(700);
        tablaBotones.row();
        tablaBotones.add().height(100);
        tablaBotones.row();
        tablaBotones.add(listenButton).height(150).width(700);
        tablaBotones.row();
        tablaBotones.add().height(100);
        tablaBotones.row();
        tablaBotones.add(botonVolver).height(150).width(700);

        tabla.row();
        tabla.add(tablaScrollPane);
        tabla.add().width(250);
        tabla.add(tablaBotones);

        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Refresca el nombre de los dispositivos Bluetooth disponibles
     * @param dispositivosConectados lista actualizada de los nombres de los dispositivos
     */
    public void refrescarLista(Array<String> dispositivosConectados) {
        this.dispositivosConectados = dispositivosConectados;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                show();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

    }
}
