package org.emgs.junit5app.ejemplos.models;


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
    void TestDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Eduardo", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900,cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }
    @Test
    void TestCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Eduardo", new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100,cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }
}