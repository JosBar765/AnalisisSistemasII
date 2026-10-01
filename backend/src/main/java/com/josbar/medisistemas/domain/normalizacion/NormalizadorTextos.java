package com.josbar.medisistemas.domain.normalizacion;

import com.josbar.medisistemas.utils.Texto;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.lang.reflect.Field;

/**
 * Listener de entidades JPA: antes de insertar o actualizar pasa a minúsculas los campos marcados con
 * {@link Minusculas}. Así ninguna ruta de escritura (mapper, servicio, script) puede guardar otra capitalización,
 * y la unicidad de correos y nombres no depende de mayúsculas.
 */
public class NormalizadorTextos {

    @PrePersist
    @PreUpdate
    public void normalizar(Object entidad) {
        for (Field campo : entidad.getClass().getDeclaredFields()) {
            if (campo.isAnnotationPresent(Minusculas.class) && campo.getType() == String.class) {
                try {
                    campo.setAccessible(true);
                    campo.set(entidad, Texto.minusculas((String) campo.get(entidad)));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("No se pudo normalizar el campo " + campo.getName(), e);
                }
            }
        }
    }
}
