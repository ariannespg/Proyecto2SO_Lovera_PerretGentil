package Utilidades;

import Utilidades.Archivo;
import Estructuras.ListaEnlazada;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author adrianlovera
 */
public class Directorio {
    private String nombre;
    private Directorio padre;
    private ListaEnlazada<Archivo> archivos;
    private ListaEnlazada<Directorio> subdirectorios;

    // Constructor
    public Directorio(String nombre, Directorio padre) {
        this.nombre = nombre;
        this.padre = padre;
        this.archivos = new ListaEnlazada<>();
        this.subdirectorios = new ListaEnlazada<>();
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public Directorio getPadre() {
        return padre;
    }

    public ListaEnlazada<Archivo> getArchivos() {
        return archivos;
    }

    public ListaEnlazada<Directorio> getSubdirectorios() {
        return subdirectorios;
    }

    // Opcional: Setter para nombre (para futuras modificaciones)
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}