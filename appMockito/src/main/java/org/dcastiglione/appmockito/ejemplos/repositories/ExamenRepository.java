package org.dcastiglione.appmockito.ejemplos.repositories;

import org.dcastiglione.appmockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {

    List<Examen> findAll();
    Examen save(Examen examen);
}
