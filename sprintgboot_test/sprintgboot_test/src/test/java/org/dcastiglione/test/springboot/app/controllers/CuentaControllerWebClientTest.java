package org.dcastiglione.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dcastiglione.test.springboot.app.models.Cuenta;
import org.dcastiglione.test.springboot.app.models.TransaccionDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integracion_wc")
class CuentaControllerWebClientTest {

    @Autowired
    private WebTestClient client;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransferir() throws JsonProcessingException {

        //given
        TransaccionDto dto = new TransaccionDto(1L, 2L, new BigDecimal("100"), 1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con exito");
        response.put("transaccion", dto);

        //when
        client.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                //then
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(respuesta -> {
                    try {
                        JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
                        assertEquals("Transferencia realizada con exito", json.path("mensaje").asText());
                        assertEquals(1L, json.path("transaccion").path("cuentaOrigenId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals(100, json.path("transaccion").path("monto").asInt());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(is("Transferencia realizada con exito"))
                .jsonPath("$.mensaje").value(valor -> {
            assertEquals("Transferencia realizada con exito", valor);
        })
                .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con exito")
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));

    }

    @Test
    @Order(2)
    void testDetalle() throws JsonProcessingException {

        Cuenta cuenta = new Cuenta(1L, "Andres", new BigDecimal("900"));

        client.get().uri("/api/cuentas/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Andres")
                .jsonPath("$.saldo").isEqualTo(900)
                .json(objectMapper.writeValueAsString(cuenta));

    }

    @Test
    @Order(3)
    void testDetalle2() {

        client.get().uri("/api/cuentas/2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(respuestaCuenta -> {
                    Cuenta cuenta = respuestaCuenta.getResponseBody();
                    assertEquals("John", cuenta.getPersona());
                    assertEquals("2100.00", cuenta.getSaldo().toPlainString());

                });

    }

    @Test
    @Order(4)
    void testListar() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].persona").isEqualTo("Andres")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(900)
                .jsonPath("$[1].persona").isEqualTo("John")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));
    }

    @Test
    @Order(5)
    void testListar2() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(response -> {
                    List<Cuenta> cuentas = response.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(2, cuentas.size());
                    assertEquals("Andres", cuentas.get(0).getPersona());
                    assertEquals(1, cuentas.get(0).getId());
                    assertEquals("900.0", cuentas.get(0).getSaldo().toPlainString());
                    assertEquals(900, cuentas.get(0).getSaldo().intValue());
                    assertEquals("John", cuentas.get(1).getPersona());
                    assertEquals(2, cuentas.get(1).getId());
                    assertEquals("2100.0", cuentas.get(1).getSaldo().toPlainString());

                })
        .hasSize(2);

    }

    @Test
    @Order(6)
    void testGuardar() {

        //given
        Cuenta cuenta = new Cuenta(null, "Diego", new BigDecimal("3000"));

        //when
        client.post().uri("api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
        .exchange()
         //then
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.persona").isEqualTo("Diego")
        .jsonPath("$.id").isEqualTo(3)
        .jsonPath("$.saldo").isEqualTo(3000)
        ;
    }

    @Test
    @Order(7)
    void testGuardar2() {

        //given
        Cuenta cuenta = new Cuenta(null, "Flor", new BigDecimal("3000"));

        //when
        client.post().uri("api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                //then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
        .consumeWith(response -> {
            Cuenta cuenta1 = response.getResponseBody();
            assertNotNull(cuenta1);
            assertEquals("Flor", cuenta1.getPersona());
            assertEquals(4, cuenta1.getId());
            assertEquals(3000, cuenta1.getSaldo().intValue());

        });
    }

    @Test
    @Order(8)
    void testEliminar() {

        client.get().uri("api/cuentas").exchange()
                .expectStatus().isOk()
                .expectBodyList(Cuenta.class)
                .hasSize(4);

        client.delete().uri("/api/cuentas/3").exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();


        client.get().uri("api/cuentas").exchange()
                .expectStatus().isOk()
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        client.get().uri("api/cuentas/3").exchange()
                //.expectStatus().is5xxServerError();
        .expectStatus().isNotFound()
        .expectBody().isEmpty();

    }

}