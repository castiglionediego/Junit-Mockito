package org.dcastiglione.junit5app.models;

import org.dcastiglione.junit5app.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;


    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter){
        System.out.println("Iniciando método");
        testReporter.publishEntry("ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().get().getName() + " con las etiquetas " + testInfo.getTags());
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el método de prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el Test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el Test");
    }

    @Nested
    class cuentaTest {

        @Tag("cuenta")
        @Test
        @DisplayName("Probando nombre de la cuenta")
        void testNombreCuenta() {
            String esperado = "Andres";
            String real = cuenta.getPersona();
            //Assertions.assertEquals(esperado,real);
            assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba");
            assertTrue(real.equals("Andres"));
        }

        @Test
        void testReferenciaCuenta() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));

            //assertNotEquals(cuenta2, cuenta);
            assertEquals(cuenta2, cuenta);
        }

    }

    @Nested
   class SaldoDebitoCreditoCuenta {
        @Test
        void testSaldoCuenta() {
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        }


        @Test
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal("100"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal("100"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Test
        void testDineroInsuficienteException() {
            Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
                cuenta.debito(new BigDecimal("1500"));
            });
            String actual = exception.getMessage();
            String esperado = "Dinero Insuficiente";
            assertEquals(esperado, actual);
        }

        @Test
        void testTransferirDineroCuentas() {
            Cuenta cuentaDestino = new Cuenta("Jhon Dow", new BigDecimal("2500"));
            Cuenta cuentaOrigen = new Cuenta("Andres", new BigDecimal("1500.8989"));

            Banco banco = new Banco();
            banco.setNombre("Banco del Estado");
            banco.transferir(cuentaOrigen, cuentaDestino, new BigDecimal("500"));
            assertEquals("1000.8989", cuentaOrigen.getSaldo().toPlainString());
            assertEquals("3000", cuentaDestino.getSaldo().toPlainString());
        }

        @Test
        @Disabled
        void testRelacionBancoCuentas() {
            Cuenta cuentaDestino = new Cuenta("Jhon Dow", new BigDecimal("2500"));
            Cuenta cuentaOrigen = new Cuenta("Andres", new BigDecimal("1500.8989"));

            Banco banco = new Banco();
            banco.addCuenta(cuentaOrigen);
            banco.addCuenta(cuentaDestino);
            banco.setNombre("Banco del Estado");
            banco.transferir(cuentaOrigen, cuentaDestino, new BigDecimal("500"));

            assertAll(() -> assertEquals("1000.8989", cuentaOrigen.getSaldo().toPlainString()),
                    () -> assertEquals("3000", cuentaDestino.getSaldo().toPlainString()),
                    () -> assertEquals(2, banco.getCuentas().size()),
                    () -> assertEquals("Banco del Estado", cuentaOrigen.getBanco().getNombre()),
                    () -> {
                        assertEquals("Andres", banco.getCuentas().stream()
                                .filter(c -> c.getPersona().equals("Andres"))
                                .findFirst()
                                .get().getPersona());
                    },
                    () -> {
                        assertTrue(banco.getCuentas().stream()
                                .filter(c -> c.getPersona().equals("Andres"))
                                .findFirst()
                                .isPresent());
                    });

        }
   }


    @Nested
    class SistemaOperativoTest{
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {

        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac() {

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }

    @Nested
    class JavaVersionTest {

        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void testSoloJdk8() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_8)
        void testCualquieraMenosJdk8() {
        }
    }

    @Nested
       class SistemPropertiesTest {
           @Test
           void imprimirSystemProperties() {
               Properties properties = System.getProperties();
               properties.forEach((k,v) -> System.out.println(k + ":" + v));

           }

           @Test
           @EnabledIfSystemProperty(named = "java.version", matches = "11.0.7")
           void testJavaVersion() {
           }
       }

    @Nested
       class VariableAmbienteTest {
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k,v) -> System.out.println(k + "=" + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk1.8.0_171.*")
        void testJavaHome() {
        }
    }




    @Test
    @DisplayName("Esta prueba solo se ejecuta si el ambiente es desarrollo")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    @DisplayName("test saldo cuenta dev 2")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @RepeatedTest(5)
    void testDebitoCuentaRepetitido(RepetitionInfo info) {

        if (info.getCurrentRepetition() == 3){
            System.out.println("estamos en la repeticion " + info.getCurrentRepetition());
        }
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadasTest {
        @ParameterizedTest(name = "numerdo {index} ejecutando con valor {0} {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000"})
        void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

        }

        @ParameterizedTest(name = "numerdo {index} ejecutando con valor {0} {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300"})
        void testDebitoCuentaCscSource(String index, String monto){
            System.out.println(index + " ->" + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

        }

        @ParameterizedTest(name = "numerdo {index} ejecutando con valor {0}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto){
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

        }



        @ParameterizedTest(name = "numerdo {index} ejecutando con valor {0}")
        @CsvSource({"200,100, John, John", "250,200, Pepe, Pepe", "305,300, Maria, Maria", "510,500, Jose, Jose", "750, 700, Jacinto, Jacinto"})
        void testDebitoCuentaCscSource2(String saldo, String monto, String esperado, String actual){
            System.out.println(saldo + " ->" + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, cuenta.getPersona());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);


        }
    }

    @Tag("param")
    @ParameterizedTest(name = "numerdo {index} ejecutando con valor {0}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto){
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

    }

    private static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000");
    }

    @Nested
    @Tag("timeout")
    class timeOutTest {
        @Test
        @Timeout(2)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1); //Aca similamos lo que queremos probar.
        }

        @Test
        @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(1); //Aca similamos lo que queremos probar.
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.SECONDS.sleep(4); //Aca similamos lo que queremos probar.
            });
        }
    }


}