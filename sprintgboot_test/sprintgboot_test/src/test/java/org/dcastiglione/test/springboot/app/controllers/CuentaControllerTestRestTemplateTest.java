package org.dcastiglione.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.LifecycleState;
import org.dcastiglione.test.springboot.app.models.Cuenta;
import org.dcastiglione.test.springboot.app.models.TransaccionDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integracion_rt")
class CuentaControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransferir() throws JsonProcessingException {
        TransaccionDto dto = new TransaccionDto(1L, 2L, new BigDecimal("100"), 1L );

        ResponseEntity<String> response = client.postForEntity("/api/cuentas/transferir", dto, String.class);
        String json = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con exito"));

        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("Transferencia realizada con exito", jsonNode.path("mensaje").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transaccion").path("monto").asText());
        assertEquals(1L, jsonNode.path("transaccion").path("cuentaOrigenId").asLong());

        Map<String, Object> responseJsonCompleted = new HashMap<>();
        responseJsonCompleted.put("date", LocalDate.now().toString());
        responseJsonCompleted.put("status", "OK");
        responseJsonCompleted.put("mensaje", "Transferencia realizada con exito");
        responseJsonCompleted.put("transaccion", dto);

        //comparo json completos!!!
        assertEquals(objectMapper.writeValueAsString(responseJsonCompleted), json);

    }

    @Test
    @Order(2)
    void testDetella() {
        ResponseEntity<Cuenta> response = client.getForEntity("/api/cuentas/1", Cuenta.class);
        Cuenta cuenta = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(cuenta);

        assertEquals(1L, cuenta.getId());
        assertEquals("Andres", cuenta.getPersona());
        assertEquals("900.00", cuenta.getSaldo().toPlainString());
    }

    @Test
    @Order(3)
    void testLista() throws JsonProcessingException {

        ResponseEntity<Cuenta[]> response = client.getForEntity("/api/cuentas", Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        //Objeto Cuenta
        assertEquals(2, cuentas.size());
        assertEquals("Andres", cuentas.get(0).getPersona());
        assertEquals(1L, cuentas.get(0).getId());
        assertEquals("900.00", cuentas.get(0).getSaldo().toPlainString());
        assertEquals("John", cuentas.get(1).getPersona());
        assertEquals(2L, cuentas.get(1).getId());
        assertEquals("2100.00", cuentas.get(1).getSaldo().toPlainString());

        //JsonNode

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(cuentas));
        assertEquals(1L, json.get(0).path("id").asLong());
        assertEquals("Andres", json.get(0).path("persona").asText());
        assertEquals("900.0", json.get(0).path("saldo").asText());

        assertEquals(2L, json.get(1).path("id").asLong());
        assertEquals("John", json.get(1).path("persona").asText());
        assertEquals("2100.0", json.get(1).path("saldo").asText());


    }

    @Test
    @Order(4)
    void testGuardar() {

        Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("3800"));

        ResponseEntity<Cuenta> cuentaResponseEntity = client.postForEntity("/api/cuentas", cuenta, Cuenta.class);

        assertEquals(HttpStatus.CREATED, cuentaResponseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, cuentaResponseEntity.getHeaders().getContentType());

        Cuenta cuentaCreada = cuentaResponseEntity.getBody();

        assertEquals(3L, cuentaCreada.getId());
        assertEquals("Pepa", cuentaCreada.getPersona());
        assertEquals("3800", cuentaCreada.getSaldo().toPlainString());

    }

    @Test
    @Order(5)
    void testDelete() {

        ResponseEntity<Cuenta[]> response = client.getForEntity("/api/cuentas", Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(response.getBody());
        assertEquals(3, cuentas.size());

        client.delete("/api/cuentas/3");

        response = client.getForEntity("/api/cuentas", Cuenta[].class);
        cuentas = Arrays.asList(response.getBody());
        assertEquals(2, cuentas.size());

        ResponseEntity<Cuenta> responseCuenta = client.getForEntity("/api/cuentas/3", Cuenta.class);


        assertEquals(HttpStatus.NOT_FOUND, responseCuenta.getStatusCode());
        assertFalse(responseCuenta.hasBody());

    }

    @Test
    @Order(6)
    void testDeleteOpcionDos() {

        Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("3800"));

        ResponseEntity<Cuenta> cuentaResponseEntity = client.postForEntity("/api/cuentas", cuenta, Cuenta.class);

        //Otra forma de mandar los parametros:
        Map<String, Long> pathVariable = new HashMap<>();
        pathVariable.put("id", 4L);

        //ResponseEntity<Void> exchange = client.exchange("/api/cuentas/4", HttpMethod.DELETE, null, Void.class);
        ResponseEntity<Void> exchange = client.exchange("/api/cuentas/{id}", HttpMethod.DELETE, null, Void.class, pathVariable);

        assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
        assertFalse(exchange.hasBody());

    }

}
