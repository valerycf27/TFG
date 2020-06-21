package com.daisa.tfg.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Util {

    public static Array<TextureRegion> ficherosEnDirectorio(final FileHandle directorio) {
        Array<TextureRegion> temp = new Array<>();
        for (FileHandle fileHandle : directorio.list())
            temp.add(new TextureRegion(new Sprite(new Texture(Gdx.files.internal(fileHandle.path())))));

        return temp;
    }
}
