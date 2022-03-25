package org.dcastiglione.appmockito.ejemplos.repositories;

import org.dcastiglione.appmockito.ejemplos.Datos;
import org.dcastiglione.appmockito.ejemplos.models.Examen;
import org.dcastiglione.appmockito.ejemplos.services.ExamenService;
import org.dcastiglione.appmockito.ejemplos.services.ExamenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenRepositoryImplSpyTest {


    @Spy
    PreguntaRepositoryImpl preguntaRepository;

    @Spy
    ExamenRepositoryImpl examenRepository;

    @InjectMocks
    ExamenServiceImpl examenService;


    @Test
    void testSpy() {


        doReturn(Datos.PREGUNTAS).when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");

        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
    }
}