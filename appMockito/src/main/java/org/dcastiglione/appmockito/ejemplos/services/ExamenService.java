package org.dcastiglione.appmockito.ejemplos.services;

import org.dcastiglione.appmockito.ejemplos.models.Examen;

import java.util.Optional;

public interface ExamenService {

    Optional<Examen> findExamenPorNombre(String nombre);

    Examen findExamenPorNombreConPreguntas(String nombre);

    Examen save(Examen examen);

}
