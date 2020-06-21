package com.daisa.tfg.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.daisa.tfg.Constantes.ConstantesJuego;

public class Explosion {

    Animation animation;

    float stateTime;
    public boolean animacionTerminada;
    Array<TextureRegion> text = new Array<>();

    public Explosion() {

        stateTime = 0;
        animacionTerminada = false;

        TextureRegion ex1 = new TextureRegion(new Sprite(new Texture(Gdx.files.internal("explosion.png"))));
        TextureRegion ex2 = new TextureRegion(new Sprite(new Texture(Gdx.files.internal("explosion.png"))));


        text.add(ex1);
        text.add(ex2);

        animation = new Animation(.25f, text);
    }

    /**
     * Actualiza la textura que hay que mostrar
     * @param delta deltaTime del juego
     */
    public void actualizar(float delta){
        stateTime += delta;
        if(animation.isAnimationFinished(stateTime)){
            animacionTerminada = true;
        }
    }

    /**
     * Pinta la textura escalada
     * @param batch objeto para pintar
     * @param x posicion X donde se va a pintar
     * @param y posicion Y donde se va a pintar
     */
    public void pintar(SpriteBatch batch, float x, float y){
        TextureRegion aspectoActual = (TextureRegion) animation.getKeyFrame(stateTime, false);
        if(aspectoActual == text.get(0))
            batch.draw(aspectoActual, x- ConstantesJuego.PPU*0.75f, y, ConstantesJuego.PPU*3/2, ConstantesJuego.PPU*3/2);
        else
            batch.draw(aspectoActual, x-ConstantesJuego.PPU, y, ConstantesJuego.PPU*2, ConstantesJuego.PPU*2);
    }
}
