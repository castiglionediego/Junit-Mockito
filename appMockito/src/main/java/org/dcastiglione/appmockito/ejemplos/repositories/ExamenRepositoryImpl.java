package org.dcastiglione.appmockito.ejemplos.repositories;

import org.dcastiglione.appmockito.ejemplos.Datos;
import org.dcastiglione.appmockito.ejemplos.models.Examen;

import java.util.List;

public class ExamenRepositoryImpl implements ExamenRepository{
    @Override
    public List<Examen> findAll() {
        System.out.println("ExamenRepositoryImpl.findAll");
        return Datos.EXAMEN_LIST;
    }

    @Override
    public Examen save(Examen examen) {
        System.out.println("ExamenRepositoryImpl.save");
        return Datos.EXAMEN;
    }
}
