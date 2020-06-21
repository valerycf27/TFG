package com.daisa.tfg.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

import java.util.ArrayList;

public class RankingScreen implements Screen {

    Stage stage;
    Juego juego;
    ScrollPane scrollPane;
    List<String> list;
    Array<String> puntuaciones = new Array<>();

    public RankingScreen(Juego juego) {
        this.juego = juego;
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        juego.recogerPuntuacionesLibGDX();
    }

    @Override
    public void show() {
        stage = new Stage(juego.viewport);

        Table tabla = new Table();
        tabla.setFillParent(true);
        stage.addActor(tabla);
        tabla.setBackground(new TiledDrawable(juego.getFondoMenu()));

        list = new List<>(juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        list.setItems(puntuaciones);

        TextButton button = new TextButton("Volver", juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new MenuPrincipalScreen(juego));
            }
        });

        scrollPane = new ScrollPane(list);

        tabla.row().height(900).width(1200).padBottom(100);
        tabla.add(scrollPane);

        tabla.row().width(800).height(100);
        tabla.add(button);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Pinta la UI en la pantalla
        stage.act(delta);
        stage.draw();
    }

    /**
     * Se refrescan las puntuaciones
     * @param ranking ranking con las puntuaciones actualizadas
     */
    public void refrescarLista(Array<String> ranking) {
        this.puntuaciones = ranking;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                show();
            }
        });
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
