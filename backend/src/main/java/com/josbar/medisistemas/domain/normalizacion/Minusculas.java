package com.josbar.medisistemas.domain.normalizacion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marca un campo de texto de una entidad que se guarda recortado y en minúsculas (ver NormalizadorTextos). */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Minusculas {
}
