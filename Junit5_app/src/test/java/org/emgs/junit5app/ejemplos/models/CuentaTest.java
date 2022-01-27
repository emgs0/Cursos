package org.emgs.junit5app.ejemplos.models;


import org.emgs.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
class CuentaTest {

    @Test
    void testNombreCuenta() {

        Cuenta cuenta = new Cuenta("Eduardo", new BigDecimal("1000.12345"));
//        cuenta.setPersona("Eduardo");
        String expected = "Eduardo";
        String actual = cuenta.getPersona();
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertTrue(actual.equals("Eduardo"));
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Eduardo", new BigDecimal("1000.12345"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345,cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0 ); //validacion de que el saldo no es negativo
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0 ); //misma validacion
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("89000.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("89000.9997"));

//        assertNotEquals(cuenta2,cuenta);
        assertEquals(cuenta2,cuenta); //fallo de instancias con mismo valor, se tiene que comparar por valor (refactor)
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Eduardo", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900,cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }
    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Eduardo", new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100,cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionCuenta() {
        Cuenta cuenta = new Cuenta("Eduardo",new BigDecimal("1000.12345"));
        // validar que se ejecuta la excepcion llamada directamente de su clase, expresión lambda para forzar error
        Exception exception =assertThrows(DineroInsuficienteException.class,()->{
            cuenta.debito(new BigDecimal("1500"));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado,actual);
    }

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
    @Test
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Eduardo", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("banamex");
        banco.transferir(cuenta2,cuenta1, new BigDecimal("500"));
        //ejecuta todos los asserts a un cuando tenga errores para que no se estanque en el primer error
        assertAll(
                ()-> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString()),
                ()-> assertEquals("3000", cuenta1.getSaldo().toPlainString()),
                //cantidad de cuentas en el banco
                ()-> assertEquals(2,banco.getCuentas().size()),
                ()-> assertEquals("banamex", cuenta1.getBanco().getNombre()),
                //buscar el nombre de la persona en las cuentas del banco filtrando primero la lista
                ()->{assertEquals("Eduardo", banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Eduardo"))
                        .findFirst().get().getPersona());},
                //valida que existe la persona en las cuentas del banco
                ()->{assertTrue(banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Eduardo"))
                        .findFirst().isPresent());},
                //validación reducida
                ()->{assertTrue(banco.getCuentas().stream()
                        .anyMatch(c -> c.getPersona().equals("Eduardo")));}
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
        //validación reducida
        assertTrue(banco.getCuentas().stream()
                .anyMatch(c -> c.getPersona().equals("Eduardo")));
                */
    }

}