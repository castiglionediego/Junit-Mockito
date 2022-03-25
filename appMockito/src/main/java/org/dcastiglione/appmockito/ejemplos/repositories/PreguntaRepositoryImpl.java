package org.dcastiglione.appmockito.ejemplos.repositories;

import org.dcastiglione.appmockito.ejemplos.Datos;

import java.util.List;

public class PreguntaRepositoryImpl implements PreguntaRepository{
    @Override
    public List<String> findPreguntasPorExamenId(Long id) {
        System.out.println("PreguntaRepositoryImpl.findPreguntasPorExamenId");
        return Datos.PREGUNTAS;
    }

    @Override
    public void saveAll(List<String> preguntas) {
        System.out.println("PreguntaRepositoryImpl.saveAll");
    }
}
