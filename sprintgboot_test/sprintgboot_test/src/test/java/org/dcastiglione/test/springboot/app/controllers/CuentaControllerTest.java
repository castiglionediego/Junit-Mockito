package org.dcastiglione.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dcastiglione.test.springboot.app.Datos;
import org.dcastiglione.test.springboot.app.models.Cuenta;
import org.dcastiglione.test.springboot.app.models.TransaccionDto;
import org.dcastiglione.test.springboot.app.services.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CuentaService cuentaService;

    ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDetalle() throws Exception {
        //given
        when(cuentaService.findById(1L)).thenReturn(Datos.getCuenta001().orElseThrow());

        //when
        mvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persona").value("Andres"))
                .andExpect(jsonPath("$.saldo").value("1000"));

        verify(cuentaService).findById(1L);
    }

    @Test
    void testTransferir() throws Exception {

        //given
        TransaccionDto transaccionDto = new TransaccionDto();
        transaccionDto.setCuentaOrigenId(1L);
        transaccionDto.setCuentaDestinoId(2L);
        transaccionDto.setMonto(new BigDecimal("100"));
        transaccionDto.setBancoId(1L);

        System.out.println(objectMapper.writeValueAsString(transaccionDto));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con exito");
        response.put("transaccion", transaccionDto);

        System.out.println(objectMapper.writeValueAsString(response));

        //when
        mvc.perform(post("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaccionDto)))

        //then
        .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con exito"))
                .andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(1L))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

    }

    @Test
    void testListarCuentas() throws Exception {

        List<Cuenta> cuentas = Arrays.asList(Datos.getCuenta001().orElseThrow(), Datos.getCuenta002().orElseThrow());
        when(cuentaService.findAll()).thenReturn(cuentas);

        mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))

        .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].persona").value("Andres"))
                .andExpect(jsonPath("$[1].persona").value("Jhon"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(content().json(objectMapper.writeValueAsString(cuentas)));


    }

    @Test
    void testGuardar() throws Exception {
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        when(cuentaService.save(any())).then(invocation -> {
            Cuenta c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(cuenta)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.persona", is("Pepe")))
                .andExpect(jsonPath("$.saldo",is(3000)));


        verify(cuentaService).save(any());

    }
}