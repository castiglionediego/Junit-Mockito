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
class ExamenRepositoryImplDoMethodRealTest {

    @Mock
    ExamenRepository examenRepository;
    @Mock
    PreguntaRepository preguntaRepository;

    @InjectMocks
    ExamenServiceImpl examenService;

    @Captor
    ArgumentCaptor<Long> captor;


    @BeforeEach
    void setUp() {
      //MockitoAnnotations.openMocks(this);
        //examenRepository = mock(ExamenRepository.class);
        //preguntaRepository = mock(PreguntaRepository.class);
        //examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);
    }

    @Test
    void findExamenPorNombre() {

        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);

        Optional<Examen> examen = examenService.findExamenPorNombre("Matematicas");
        assertTrue(examen.isPresent());
        assertEquals(5L, examen.orElseThrow().getId());
        assertEquals("Matematicas", examen.get().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
        List<Examen> examenList = List.of();

        when(examenRepository.findAll()).thenReturn(examenList);

        Optional<Examen> examen = examenService.findExamenPorNombre("Matematicas");
        assertFalse(examen.isPresent());
    }

    @Test
    void testPreguntasExamen() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5, examen.getPreguntas().size());

    }

    @Test
    void testPreguntasExamenVerify() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5, examen.getPreguntas().size());
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());

    }


    @Test
    void testNoExisteExamenVerify() {
        when(examenRepository.findAll()).thenReturn(Collections.EMPTY_LIST);
        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
        assertNull(examen);
        verify(examenRepository).findAll();

    }


    @Test
    void testGuardaExamen() {
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        when(examenRepository.save(any(Examen.class))).then(new Answer<Examen>() {

            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });
        Examen examen = examenService.save(newExamen);
        assertNotNull(examen.getId());
        assertEquals(8, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(examenRepository).save(any(Examen.class));
        verify(preguntaRepository).saveAll(anyList());
    }

    @Test
    void testManejoException() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenThrow(IllegalArgumentException.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> examenService.findExamenPorNombreConPreguntas("Matematicas"));

        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());

    }

    @Test
    void testArgumentMatchers() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("Matematicas");

        verify(examenRepository).findAll();
        //verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg.equals(5L)));
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg >= 5L));
        //verify(preguntaRepository).findPreguntasPorExamenId(eq(5L));
    }

    @Test
    void testArgumentMatchersPersonalizado() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("Matematicas");

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(new ArgsMatchersPersonalizado()));
    }


    public static class ArgsMatchersPersonalizado implements ArgumentMatcher<Long>{

        @Override
        public boolean matches(Long argument) {
            return argument !=null && argument >0;
        }

        @Override
        public String toString() {
            return "ArgsMatchersPersonalizado - este mensaje se imprime como falla el test.";
        }


    }

    @Test
    void testArgumentCaptor() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        //queremos capturar el parametro que le pasamos al método findPreguntas.....
        examenService.findExamenPorNombreConPreguntas("Matematicas");

        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());

        assertEquals(5L, captor.getValue());

    }

    @Test
    void testDoThrow() {

        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        doThrow(IllegalArgumentException.class).when(preguntaRepository).saveAll(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            examenService.save(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);
        doAnswer( invocationOnMock -> {
           Long id = invocationOnMock.getArgument(0);
           return id == 5L ? Datos.PREGUNTAS : Collections.emptyList();
        }).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size());

    }

    @Test
    void testSpy() {

        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository = spy(PreguntaRepositoryImpl.class);
        ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);

        doReturn(Datos.PREGUNTAS).when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");

        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
    }

    @Test
    void testOrdenDeInvocaciones() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);
        examenService.findExamenPorNombreConPreguntas("Matematicas");
        examenService.findExamenPorNombreConPreguntas("Lenguaje");

        //pasamos por argumento el o los mocks
        InOrder inOrder = inOrder(preguntaRepository);
        //verificamos el orden de invocación
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);

    }

    @Test
    void testOrdenDeInvocaciones2() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        examenService.findExamenPorNombreConPreguntas("Matematicas");
        examenService.findExamenPorNombreConPreguntas("Lenguaje");

        //pasamos por argumento el o los mocks
        //InOrder inOrder = inOrder(examenRepository,preguntaRepository);
        //verificamos el orden de invocación
        //inOrder.verify(examenRepository).findAll();
        //inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);

        //inOrder.verify(examenRepository).findAll();
        //inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);

        verify(preguntaRepository).findPreguntasPorExamenId(5L);
        verify(preguntaRepository).findPreguntasPorExamenId(6L);


    }

    @Test
    void testNumeroInvocaciones() {

        when(examenRepository.findAll()).thenReturn(Datos.EXAMEN_LIST);

        examenService.findExamenPorNombreConPreguntas("Matematicas");

        verify(preguntaRepository, times(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atMost(1)).findPreguntasPorExamenId(5L);
        //verify(preguntaRepository, never()).findPreguntasPorExamenId(5L);


    }

    @Test
    void testNumeroInvocaciones2() {

        when(examenRepository.findAll()).thenReturn(Collections.emptyList());

        examenService.findExamenPorNombreConPreguntas("Matematicas");
        verify(preguntaRepository, never()).findPreguntasPorExamenId(5L);

        verify(examenRepository).findAll();
        verify(examenRepository, times(1)).findAll();
        verify(examenRepository, atLeastOnce()).findAll();
        verify(examenRepository, atLeast(1)).findAll();

    }
}