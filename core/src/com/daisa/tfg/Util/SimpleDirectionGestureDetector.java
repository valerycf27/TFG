package com.daisa.tfg.Util;

import com.badlogic.gdx.input.GestureDetector;

public class SimpleDirectionGestureDetector extends GestureDetector {

    public SimpleDirectionGestureDetector(DireccionListener direccionListener) {
        super(new DirectionGestureListener(direccionListener));
    }

    private static class DirectionGestureListener extends GestureAdapter {
        DireccionListener direccionListener;

        public DirectionGestureListener(DireccionListener direccionListener) {
            this.direccionListener = direccionListener;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            if (Math.abs(velocityX) > Math.abs(velocityY))
                if (velocityX > 0)
                    direccionListener.onRight();
                else
                    direccionListener.onLeft();

            else if (velocityY > 0)
                direccionListener.onDown();
            else
                direccionListener.onUp();


            return super.fling(velocityX, velocityY, button);
        }
    }

    /**
     * interfaz encargada que comunica DirectionGestureListener con la Screen de LIBGDX
     */
    public interface DireccionListener {
        void onLeft();

        void onRight();

        void onUp();

        void onDown();
    }
}
