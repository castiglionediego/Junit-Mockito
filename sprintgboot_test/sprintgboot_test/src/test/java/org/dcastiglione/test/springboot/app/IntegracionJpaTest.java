package org.dcastiglione.test.springboot.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.dcastiglione.test.springboot.app.Datos.*;

import org.dcastiglione.test.springboot.app.models.Cuenta;
import org.dcastiglione.test.springboot.app.repositories.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@DataJpaTest
public class IntegracionJpaTest {

    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Andres", cuenta.orElseThrow().getPersona());

    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andres");
        assertTrue(cuenta.isPresent());
        assertEquals("Andres", cuenta.orElseThrow().getPersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());

    }

    @Test
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Roa");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void testSave() {
        //Given
        Cuenta cuenta = cuentaRepository.save(new Cuenta(null, "Pepe", new BigDecimal("3000")));

        //When
        //Cuenta cuentaPepe = cuentaRepository.findByPersona("Pepe").orElseThrow();
        //Cuenta cuentaPepe = cuentaRepository.findById(cuenta.getId()).orElseThrow();

        //Then
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        //assertEquals(3, cuenta.getId());

    }

    @Test
    void testUpdate() {
        //Given
        Cuenta cuenta = cuentaRepository.save(new Cuenta(null, "Pepe", new BigDecimal("3000")));

        //When
        //Cuenta cuentaPepe = cuentaRepository.findByPersona("Pepe").orElseThrow();
        //Cuenta cuentaPepe = cuentaRepository.findById(cuenta.getId()).orElseThrow();



        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        //Then
        assertEquals("Pepe", cuentaActualizada.getPersona());
        assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());

    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("John", cuenta.getPersona());

        cuentaRepository.delete(cuenta);
        assertThrows(NoSuchElementException.class, ()-> {
            cuentaRepository.findById(2L).orElseThrow();
        });

        assertEquals(1, cuentaRepository.findAll().size());
    }

}
