package org.emgs.junit5app.ejemplos.models;

import org.emgs.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

public class Cuenta {
    private String persona;
    private BigDecimal saldo;
    private Banco banco;

    public Cuenta(String persona, BigDecimal saldo) {
        this.saldo = saldo;
        this.persona= persona;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public void debito(BigDecimal monto){
//        this.saldo.subtract(monto); //error ya que Bigdecimal es inmutable
        //this.saldo = this.saldo.subtract(monto);
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        //validacion de que el monto a restar es menor que el total que tiene la cuenta
        if(nuevoSaldo.compareTo(BigDecimal.ZERO)<0){
            throw new DineroInsuficienteException("Dinero insuficiente");
        }
        this.saldo = nuevoSaldo;
    }

    public void credito(BigDecimal monto){
        this.saldo = this.saldo.add(monto);
    }
    /*
    sobreescritura de metodo equals para validar instancias por sus atributos, no por memoria
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cuenta)){ //que el objeto sea instancia de cuenta
            return false;
        }
        Cuenta c = (Cuenta) obj;
        if(this.persona == null|| this.saldo == null){
            return false;
        }
        return this.persona.equals(c.getPersona()) && this.saldo.equals(c.getSaldo());
    }
}
