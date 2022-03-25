package org.dcastiglione.appmockito.ejemplos.repositories;

import java.util.List;

public interface PreguntaRepository {

    List<String> findPreguntasPorExamenId(Long id);
    void saveAll(List<String> preguntas);
}
