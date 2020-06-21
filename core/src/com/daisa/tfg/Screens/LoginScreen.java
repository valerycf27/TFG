package com.daisa.tfg.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.daisa.tfg.Constantes.ConstantesJuego;
import com.daisa.tfg.Principal.Juego;

public class LoginScreen implements Screen {

    Juego juego;
    Stage stage;

    Label lbNombreJuego;
    Label lbUsuario;
    Label lbContra;
    TextField tfNombreUsuario;
    TextField tfContraseñaUsuario;
    TextButton btRegistro;
    TextButton btInicioSesion;

    boolean nombIntroducido = true;
    boolean contraIntroducida = true;
    String nombAlmacenado = "";
    String contraAlmacenada = "";

    public LoginScreen(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void show() {

        juego.reproducirMusica(juego.manager.managerJuego.get(ConstantesJuego.MUSICA_MENU, Music.class));

        stage = new Stage(juego.viewport);

        Table tabla = new Table();
        tabla.setFillParent(true);
        stage.addActor(tabla);

        tabla.setBackground(new TiledDrawable(juego.getFondoMenu()));

        Image imgLogo = new Image(juego.manager.getManagerJuego().get(ConstantesJuego.IMAGEN_LOGO, Texture.class));
        Image imgExclamacion = new Image(juego.manager.getManagerJuego().get(ConstantesJuego.IMAGEN_EXCLAMACION, Texture.class));
        Image imgExclamacion2 = new Image(juego.manager.getManagerJuego().get(ConstantesJuego.IMAGEN_EXCLAMACION, Texture.class));

        CharSequence nombreJuego = "Last Spaceship Standing";
        lbNombreJuego = new Label(nombreJuego, juego.manager.getEstiloLabel());
        lbNombreJuego.setFontScale(3);

        CharSequence nombUsuario = "Usuario";
        lbUsuario = new Label(nombUsuario, juego.manager.getEstiloLabel());
        lbUsuario.setFontScale(2);

        CharSequence nombContra = "Contrasena";
        lbContra = new Label(nombContra, juego.manager.getEstiloLabel());
        lbContra.setFontScale(2);

        tfNombreUsuario = new TextField(nombAlmacenado, juego.manager.getEstiloTextField());
        nombAlmacenado = "";

        tfNombreUsuario.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Input.TextInputListener textInputListener = new Input.TextInputListener() {
                    @Override
                    public void input(String input) {
                        tfNombreUsuario.setText(input);
                    }

                    @Override
                    public void canceled() {
                        tfNombreUsuario.setText("");
                    }
                };
                Gdx.input.getTextInput(textInputListener, "Usuario: ", tfNombreUsuario.getText(), "Introduce el nombre de usuario");
            }
        });


        tfContraseñaUsuario = new TextField(contraAlmacenada, juego.manager.getEstiloTextField());
        tfContraseñaUsuario.setPasswordMode(true);
        tfContraseñaUsuario.setPasswordCharacter('#');
        contraAlmacenada = "";
        tfContraseñaUsuario.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Input.TextInputListener textInputListener = new Input.TextInputListener() {
                    @Override
                    public void input(String input) {
                        tfContraseñaUsuario.setText(input);
                    }

                    @Override
                    public void canceled() {
                        tfContraseñaUsuario.setText("");
                    }
                };
                Gdx.input.getTextInput(textInputListener, "Contraseña: ", tfContraseñaUsuario.getText(), "Introduce la contraseña");
            }
        });

        btRegistro = new TextButton("Registrarse", juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        btRegistro.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //juego.reproducirSonido(juego.manager.getSonidoPulsarboton());
                juego.reproducirSonido(juego.manager.getManagerJuego().get(ConstantesJuego.SONIDO_PULSAR_BOTON, Sound.class));
                juego.setScreen(new RegistroScreen(juego));
            }
        });

        btInicioSesion = new TextButton("Iniciar de Sesion", juego.manager.managerJuego.get(ConstantesJuego.NOMBRE_JSON_SKIN, Skin.class));
        btInicioSesion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.reproducirSonido(juego.manager.getManagerJuego().get(ConstantesJuego.SONIDO_PULSAR_BOTON, Sound.class));
                nombIntroducido = !tfNombreUsuario.getText().equals("");
                contraIntroducida = !tfContraseñaUsuario.getText().equals("");

                if (!(nombIntroducido && contraIntroducida)){
                    nombAlmacenado = tfNombreUsuario.getText();
                    contraAlmacenada = tfContraseñaUsuario.getText();
                    show();
                }
                else {
                    juego.comprobacionUsuarioLIBGDX(tfNombreUsuario.getText(), tfContraseñaUsuario.getText(), Juego.Procedencia.LOGIN_SCREEN);
                }
            }
        });

        tabla.row().padBottom(15).padTop(30);
        tabla.add(imgLogo).width(450).height(450);
        tabla.add(lbNombreJuego).colspan(2).left();
        tabla.add();

        tabla.row().padBottom(50).padTop(50);
        tabla.add(lbUsuario).height(100);
        tabla.add(tfNombreUsuario).width(920).height(150).colspan(2);
        if (!nombIntroducido) {
            tabla.add(imgExclamacion).width(150).height(152);
        } else {
            tabla.add();
        }

        tabla.row().padBottom(20).padTop(20);
        tabla.add(lbContra).height(100);
        tabla.add(tfContraseñaUsuario).width(920).height(150).colspan(2);
        if (!contraIntroducida) {
            tabla.add(imgExclamacion2).width(150).height(152);
        } else {
            tabla.add();
        }
        tabla.row().padBottom(20).padTop(20);
        tabla.add().height(80).colspan(3);
        tabla.add();
        tabla.add();

        tabla.row().padTop(10);
        tabla.add(btRegistro).width(700).height(120);
        tabla.add().width(200);
        tabla.add(btInicioSesion).width(700).height(120);

        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
        stage.dispose();
    }
}
