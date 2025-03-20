/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilidades;

/**
 *
 * @author arianneperret-gentil
 */

import java.util.ArrayList;
import java.util.List;

public class NodoSerializable {
    private String nombre;
    private boolean esDirectorio;
    private List<NodoSerializable> hijos;

    public NodoSerializable(String nombre, boolean esDirectorio) {
        this.nombre = nombre;
        this.esDirectorio = esDirectorio;
        this.hijos = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isEsDirectorio() {
        return esDirectorio;
    }

    public List<NodoSerializable> getHijos() {
        return hijos;
    }
}
