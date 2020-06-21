package com.daisa.tfg;

public class UtilAndroid {

    /**
     * Comprueba que si la contrasena contiene algun numero
     * @param contrasena string que se quiere comprobar
     * @return true si tiene numero, false si no
     */
    boolean hayNumerosEnString(String contrasena) {
        int pos = 0;
        boolean esNumero = false;
        do {
            char car = contrasena.charAt(pos);
            try {
                Integer.parseInt(String.valueOf(car));
                esNumero = true;
            } catch (NumberFormatException ignored) {}
            pos++;

        } while (!esNumero && pos < contrasena.length());

        return !esNumero;
    }

}
