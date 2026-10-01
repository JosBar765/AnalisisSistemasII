package com.josbar.medisistemas.utils;

import java.util.Locale;

/**
 * Normalización de texto: los datos de identidad y catálogos se guardan en minúsculas y se muestran en TitleCase.
 */
public final class Texto {

    private static final Locale ESPANOL = Locale.of("es");

    private Texto() {
    }

    /** Quita espacios sobrantes y pasa a minúsculas. Conserva null. */
    public static String minusculas(String texto) {
        return texto == null ? null : texto.trim().toLowerCase(ESPANOL);
    }

    /**
     * Primera letra de cada palabra en mayúscula y el resto en minúscula ("josé luis" -> "José Luis"). Las palabras
     * se separan por espacios, guion, apóstrofo y punto; una letra tras un dígito no se capitaliza ("50mg" no cambia).
     */
    public static String titulo(String texto) {
        if (texto == null) {
            return null;
        }
        String minusculas = texto.trim().toLowerCase(ESPANOL);
        StringBuilder resultado = new StringBuilder(minusculas.length());
        boolean inicioDePalabra = true;
        for (int i = 0; i < minusculas.length(); i++) {
            char c = minusculas.charAt(i);
            resultado.append(inicioDePalabra && Character.isLetter(c) ? Character.toUpperCase(c) : c);
            inicioDePalabra = Character.isWhitespace(c) || c == '-' || c == '\'' || c == '\u2019' || c == '.';
        }
        return resultado.toString();
    }
}
