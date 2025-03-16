package Utilidades;


import modelos.Bloque;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author adrianlovera
 */
public class Archivo {
    private String nombre;
    private int tamano; // Cantidad de bloques que ocupa
    private Bloque primerBloque;

    public Archivo(String nombre, int tamano, Bloque primerBloque) {
        this.nombre = nombre;
        this.tamano = tamano;
        this.primerBloque = primerBloque;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTamano() {
        return tamano;
    }

    public Bloque getPrimerBloque() {
        return primerBloque;
    }

    public void setPrimerBloque(Bloque bloque) {
        this.primerBloque = bloque;
    }
}