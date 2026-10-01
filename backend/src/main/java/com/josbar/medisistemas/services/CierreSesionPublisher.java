package com.josbar.medisistemas.services;

/** Cierra las sesiones en tiempo real abiertas de un usuario (p. ej. cuando se desactiva). */
public interface CierreSesionPublisher {

    void cerrarSesionesDe(Integer idUsuario);
}
