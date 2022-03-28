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

import static org.mockito.Mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamenRepositoryImplTest {


    @Mock
    ExamenRepository examenRepository;

    @Mock
    PreguntaRepositoryImpl preguntaRepositoryImpl;

    @InjectMocks
    ExamenServiceImpl examenService;



    @Test
    void testDoCallRealMethod() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        //invocar el método real no el simulado, no podriamos utilizar el objeto preguntaRepository porque es una interfaz
        doCallRealMethod().when(preguntaRepositoryImpl).findPreguntasPorExamenId(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
        assertNotNull(examen);
        assertEquals(5, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size());
    }


}