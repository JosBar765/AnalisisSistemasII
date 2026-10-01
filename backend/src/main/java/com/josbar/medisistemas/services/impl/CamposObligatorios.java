package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.exceptions.BusinessRuleException;

/** Mensajes claros cuando falta un dato que la base de datos exigiría (NOT NULL) al crear un registro. */
final class CamposObligatorios {

    private CamposObligatorios() {
    }

    static void exigir(Object valor, String campo) {
        boolean vacio = valor == null || (valor instanceof String texto && texto.isBlank());
        if (vacio) {
            throw new BusinessRuleException("El campo «" + campo + "» es obligatorio.");
        }
    }
}
