package org.emgs.junit5app.ejemplos.models;


import org.emgs.junit5app.ejemplos.exceptions.DineroInsuficienteException;
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

class CuentaTest {
    Cuenta cuenta;
    private TestInfo testInfo; //para poder acceder a esta variable desde cualquier metodo, como un atributo
    private TestReporter testReporter;

    @BeforeEach //antes de iniciar un metodo se ejecuta este
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter){
        System.out.println("Iniciando metodo");
        this.cuenta = new Cuenta("Eduardo", new BigDecimal("1000.12345"));
        this.testInfo= testInfo; //se realiza la inyeccion al metodo
        this.testReporter= testReporter;
        testReporter.publishEntry("Ejecutando: "+testInfo.getDisplayName() + " "+ testInfo.getTestMethod().orElse(null).getName()+
                " con las etiquetas: "+ testInfo.getTags()); //imprime directamente desde el log de junit
    }

    @AfterEach //despues de cada metodo se ejecuta este
    void tearDown() {
        System.out.println("Finalizando metodo de prueba");
    }

    @BeforeAll //metodo asociado a la clase no a la instancia
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Tag("cuenta") // desde barra de intelliJ se configura que se desea testear solo el tag correspondiente
    @Nested
    @DisplayName("probando atributos de cuenta")
    class CuentaTestNombreSaldo{
        @Test
        @DisplayName("el nombre")
        void testNombreCuenta() {
            testReporter.publishEntry(testInfo.getTags().toString());
            if(testInfo.getTags().contains("cuenta")){
                testReporter.publishEntry("contiene etiqueta cuenta");
            }
//        cuenta.setPersona("Eduardo");
            String expected = "Eduardo";
            String actual = cuenta.getPersona();
            assertNotNull(actual,()-> "La cuenta no puede ser nula"); //lambda para crear mensaje en caso de error, de lo contrario no lo crea
            assertEquals(expected, actual, ()-> "El nombre de la cuenta no es el esperado: se esperba "+
                    expected+" pero se recibio "+actual);
            assertTrue(actual.equals("Eduardo"),()-> "nombre cuenta esperada debe ser igual a la real");
        }

        @Test
        @DisplayName("el saldo no sea nulo, mayor que cero, valor esperado")
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345,cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0 ); //validacion de que el saldo no es negativo
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0 ); //misma validacion
        }

        @Test
        @DisplayName("referencias iguales con metodo equals")
        void testReferenciaCuenta() {
            cuenta = new Cuenta("John Doe", new BigDecimal("89000.9997"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("89000.9997"));

//        assertNotEquals(cuenta2,cuenta);
            assertEquals(cuenta2,cuenta); //fallo de instancias con mismo valor, se tiene que comparar por valor (refactor)
        }
    }

    @Nested
    class CuentaOperaciones{
        @Tag("cuenta")
        @Test
        @DisplayName("probando debito (resta) de la cuenta")
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal("100"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900,cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Test
        @Tag("cuenta")
        @DisplayName("probando credito (suma) de la cuenta")
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal("100"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100,cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }
        @Tag("cuenta")
        @Tag("banco")
        @Test
        void testTransferirDineroCuentas() {
            Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Eduardo", new BigDecimal("1500.8989"));

            Banco banco = new Banco();
            banco.setNombre("banamex");
            banco.transferir(cuenta2,cuenta1, new BigDecimal("500"));
            assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta1.getSaldo().toPlainString());
        }
    }

    @Test
    @Tag("cuenta")
    @Tag("error")
    void testDineroInsuficienteExceptionCuenta() {
        // validar que se ejecuta la excepcion llamada directamente de su clase, expresi??n lambda para forzar error
        Exception exception =assertThrows(DineroInsuficienteException.class,()->{
            cuenta.debito(new BigDecimal("1500"));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado,actual);
    }

    @Test
    //@Disabled
    @DisplayName("probando relaciones entre cuentas y banco con assertAll")
    @Tag("cuenta")
    @Tag("banco")
    void testRelacionBancoCuentas() {
       // fail();
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Eduardo", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("banamex");
        banco.transferir(cuenta2,cuenta1, new BigDecimal("500"));
        //ejecuta todos los asserts a un cuando tenga errores para que no se estanque en el primer error
        assertAll(
                ()-> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
                        ()->"El valor del saldo de cuenta2 no es el esperado"),
                ()-> assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                        ()->"El valor del saldo de cuenta1 no es el esperado"),
                //cantidad de cuentas en el banco
                ()-> assertEquals(2,banco.getCuentas().size(),
                        ()->"La cantidad de cuentas no es el esperado"),
                ()-> assertEquals("banamex", cuenta1.getBanco().getNombre()),
                //buscar el nombre de la persona en las cuentas del banco filtrando primero la lista
                ()->{assertEquals("Eduardo", banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Eduardo"))
                        .findFirst().get().getPersona());},
                //valida que existe la persona en las cuentas del banco
                ()->{assertTrue(banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Eduardo"))
                        .findFirst().isPresent());},
                //validaci??n reducida
                ()-> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Eduardo")))
        );

        //assert iniciales antes de assertAll
        /*
        assertEquals("1000.89891", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
        //cantidad de cuentas en el banco
        assertEquals(2,banco.getCuentas().size());
        assertEquals("banamex.", cuenta1.getBanco().getNombre());
        //buscar el nombre de la persona en las cuentas del banco filtrando primero la lista
        assertEquals("Eduardo", banco.getCuentas().stream()
                .filter(c -> c.getPersona().equals("Eduardo"))
                .findFirst().get().getPersona());
        //valida que existe la persona en las cuentas del banco
        assertTrue(banco.getCuentas().stream()
                .filter(c -> c.getPersona().equals("Eduardo"))
                .findFirst().isPresent());
        //validaci??n reducida
        assertTrue(banco.getCuentas().stream()
                .anyMatch(c -> c.getPersona().equals("Eduardo")));
                */
    }
    @Nested
    class SistemaOperativoTest{
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX,OS.MAC})
        void testLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }

    @Nested
    class JavaVersionTest{
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJDK8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_17)
        void soloJDK17() {
        }
        @Test
        @DisabledOnJre(JRE.JAVA_17)
        void testNoJDK17() {
        }
    }

    @Nested
    class SystemPropertiesTest{
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((key,value)->System.out.println(key+": "+value));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = ".*17.*") //todos similares a jdk 17
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named ="os.arch", matches =".*32.*")
        void testSolo64() {
        }

        @Test
        @EnabledIfSystemProperty(named ="os.arch", matches =".*32.*")
        void testNo64() {
        }

        @Test
        @EnabledIfSystemProperty(named ="user.name", matches ="egsan")
        void testUserName() {
        }

        @Test
        @EnabledIfSystemProperty(named ="ENV", matches ="dev") //configurar variable desde intelliJ (build and run -ea -DENV=dev)
        void testDev() {
        }
    }

    @Nested
    class VariableAmbienteTest{
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((key, value)-> System.out.println(key+" = "+value));
        }

        @Test
        @EnabledIfEnvironmentVariable(named ="JAVA_HOME", matches=".*jdk-17.0.2.*")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named ="NUMBER_OF_PROCESSORS", matches="8")
        void testProcesadores() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named ="ENVIRONMENT", matches="dev")
        void testEnv() {
        }
    }


    @Test
    @DisplayName("testSaldoCuenta en modo dev")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev); //si es true continua la ejecucion, si es false detiene el metodo (como un if booleano)
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345,cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0 ); //validacion de que el saldo no es negativo
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0 ); //misma validacion
    }

    @Test
    @DisplayName("testSaldoCuenta en modo dev 2")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev,()->{
            //si es true continua la ejecucion, si es false no ejecuta el lambda
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345,cuenta.getSaldo().doubleValue());
        });
        //a partir de aqu?? se eejcuta el codigo aunque el boolean sea false gracias a expresion lambda
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0 ); //validacion de que el saldo no es negativo
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0 ); //misma validacion
    }

    @RepeatedTest(value=5,name="Repetici??n no {currentRepetition} de {totalRepetitions}" )
    void testDebitoCuentaRepetido() {
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900,cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("param") // se aplica en toda la clase y sus metodos
    @Nested
    class PruebasParametrizadasTest{

        @ParameterizedTest(name="num {index} ejecutando con valor {0} - {argumentsWithNames}") //validar muchos saldos mayor a 0
        @ValueSource(strings = {"100","200","500","700","1000","1000.12345"})
        void testDebitoCuenta(String monto) { //aqui se inyecta cada valor de valueSource
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @ParameterizedTest(name="num {index} ejecutando con valor {0} - {argumentsWithNames}") //validar muchos saldos mayor a 0
        @CsvSource({"1,100","2,200","3,500","4,700","5,1000","6,1000.12345"})
        void testDebitoCuentaCSVSource(String index, String monto) { //aqui se inyecta cada valor de valueSource
            System.out.println(index+" -> "+monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @ParameterizedTest(name="num {index} ejecutando con valor {0} - {argumentsWithNames}") //validar muchos saldos mayor a 0 agregando saldo y monto a retirar
        @CsvSource({"200,100,John,Eduardo","250,200,Manuel,Manuel","499,500,Jose,jose","750,700,javier,felipe","1000,1000,Lucas,Luca","1000.12345,1000,Ana,Ana"})
        void testDebitoCuentaCSVSource2(String saldo, String monto, String esperado, String actual) { //aqui se inyecta cada valor de valueSource
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @ParameterizedTest(name="num {index} ejecutando con valor {0} - {argumentsWithNames}") //validar muchos saldos mayor a 0
        @CsvFileSource(resources = "/data.csv ") //debe estar creado en la carpeta resources este archivo
        void testDebitoCuentaCSVFileSource(String monto) { //aqui se inyecta cada valor del csv
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @ParameterizedTest(name="num {index} ejecutando con valor {0} - {argumentsWithNames}") //validar muchos saldos mayor a 0
        @CsvFileSource(resources = "/data2.csv ") //debe estar creado en la carpeta resources este archivo
        void testDebitoCuentaCSVFileSource2(String saldo, String monto, String esperado, String actual) { //aqui se inyecta cada valor del csv
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }
    }
    //este metodo no puede integrarse en la clase pruebasparametrizadastest ya que la fuente es un metodo estatico y debe estar en la clase raiz
    @Tag("param")
    @ParameterizedTest(name="num {index} ejecutando con valor {0} - {argumentsWithNames}") //validar muchos saldos mayor a 0
    @MethodSource("montoList") //unicamente el nombre del metodo
    void testDebitoCuentaMethodSource(String monto) { //aqui se inyecta cada valor del csv
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
    }

    static List<String> montoList() {
    return Arrays.asList("100","200","300","500","1000","1000.12345");
    }

    @Nested
    @Tag("timeout")
    public class EjemploTimeOutTest{
        @Test
        @Timeout(4) //est?? en segundos
        public void purebaTimeOut() throws InterruptedException {
            TimeUnit.SECONDS.sleep(5); // asignando que tarda 6 segundos la tarea
        }
        @Test
        @Timeout(value=5000, unit =TimeUnit.MILLISECONDS)
        public void purebaTimeOut2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(4000); // asignando que tarda 6 segundos la tarea
        }

        @Test
        public void testTimeOut(){
            assertTimeout(Duration.ofSeconds(5),()->{
                TimeUnit.MILLISECONDS.sleep(4500);
            });
        }
    }

}