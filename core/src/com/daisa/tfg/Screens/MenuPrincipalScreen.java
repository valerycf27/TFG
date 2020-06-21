package com.daisa.tfg.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.daisa.tfg.Constantes.ConstantesJuego;
import com.daisa.tfg.Principal.Juego;

public class MenuPrincipalScreen implements Screen {

    Juego juego;
    Stage stage;

    TextButton btJugar;
    TextButton btRanking;
    TextButton btAjustes;

    public MenuPrincipalScreen(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void show() {

        cambiarMusica();

        juego.reproducirMusica(juego.manager.getManagerJuego().get(ConstantesJuego.MUSICA_MENU, Music.class));

        stage = new Stage(juego.viewport);

        Table tabla = new Table();
        tabla.setFillParent(true);
        stage.addActor(tabla);
        tabla.setBackground(new TiledDrawable(juego.getFondoMenu()));

        Image imgLogo = new Image(juego.manager.getManagerJuego().get(ConstantesJuego.IMAGEN_LOGO, Texture.class));

        btJugar = new TextButton("Jugar", juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        btJugar.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.reproducirSonido(juego.manager.managerJuego.get(ConstantesJuego.SONIDO_PULSAR_BOTON, Sound.class));
                if(juego.bluetoothEncendidoLIBGDX()){
                    Gdx.app.debug("DEBUG", "MenuPrincipalScreen::Bluetooth encendido, se crea la Screen ConectarJugadoresScreen");
                    juego.conectarJugadoresScreen = new ConectarJugadoresScreen(juego);
                    juego.setScreen(juego.conectarJugadoresScreen);
                }else{
                    Gdx.app.debug("DEBUG", "MenuPrincipalScreen::Bluetooth apagado, se pide permiso para encenderlo");
                    juego.activityForResultBluetoothLIBGDX();
                }
            }
        });

        btRanking = new TextButton("Ranking", juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        btRanking.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("DEBUG", "MenuPrincipalScreen::Se crea la Screen RankingScreen");
                juego.reproducirSonido(juego.manager.getManagerJuego().get(ConstantesJuego.SONIDO_PULSAR_BOTON, Sound.class));
                juego.rankingScreen = new RankingScreen(juego);
                juego.setScreen(juego.rankingScreen);
            }
        });

        btAjustes = new TextButton("Ajustes", juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        btAjustes.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.reproducirSonido(juego.manager.getManagerJuego().get(ConstantesJuego.SONIDO_PULSAR_BOTON, Sound.class));
                juego.setScreen(new AjustesScreen(juego));
            }
        });

        Table tablaImagenLogo = new Table();
        Table tablaBotones = new Table();

        tablaImagenLogo.row();
        tablaImagenLogo.add(imgLogo).width(900).height(900);

        tablaBotones.row().padBottom(45);
        tablaBotones.add(btJugar).width(700).height(150);
        tablaBotones.row().padBottom(45);
        tablaBotones.add(btRanking).width(700).height(150);
        tablaBotones.row().padBottom(45);
        tablaBotones.add(btAjustes).width(700).height(150);

        tabla.row();
        tabla.add(tablaImagenLogo);
        tabla.add(tablaBotones);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    /**
     * Cambia la musica que se esta reproduciendo
     */
    private void cambiarMusica() {
        if(juego.manager.managerJuego.get(ConstantesJuego.MUSICA_JUEGO, Music.class).isPlaying()){
            juego.manager.managerJuego.get(ConstantesJuego.MUSICA_JUEGO, Music.class).stop();
        }
        juego.reproducirMusica(juego.manager.managerJuego.get(ConstantesJuego.MUSICA_MENU, Music.class));
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
        stage.dispose();
    }
}
