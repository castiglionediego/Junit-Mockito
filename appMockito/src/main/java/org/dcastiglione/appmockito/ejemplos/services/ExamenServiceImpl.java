package org.dcastiglione.appmockito.ejemplos.services;

import org.dcastiglione.appmockito.ejemplos.models.Examen;
import org.dcastiglione.appmockito.ejemplos.repositories.ExamenRepository;
import org.dcastiglione.appmockito.ejemplos.repositories.PreguntaRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService{

    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {

       return  examenRepository.findAll()
                .stream()
                .filter(examen -> examen.getNombre().contains(nombre))
                .findFirst();

    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        System.out.println("ExamenServiceImpl.findExamenPorNombreConPreguntas: " + nombre);
        Optional<Examen> examenOptional = this.findExamenPorNombre(nombre);
        System.out.println("examenOptional: " + examenOptional);
        System.out.println("examenOptional.isPresent(): " + examenOptional.isPresent());

        Examen examen = null;
        if (examenOptional.isPresent()){
            examen = examenOptional.orElseThrow();
            List<String> preguntas = preguntaRepository.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen save(Examen examen) {
        if (!examen.getPreguntas().isEmpty()){
            preguntaRepository.saveAll(examen.getPreguntas());
        }
        return examenRepository.save(examen);
    }
}
