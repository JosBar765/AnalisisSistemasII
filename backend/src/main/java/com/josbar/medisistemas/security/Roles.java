package com.josbar.medisistemas.security;

/** Nombres de los roles tal como están en la tabla Rol y en el claim "rol" del JWT. */
public final class Roles {

    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String SECRETARIA = "SECRETARIA";
    public static final String MEDICO = "MEDICO";

    private Roles() {
    }
}
