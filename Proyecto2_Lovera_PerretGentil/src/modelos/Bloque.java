/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

public class Bloque {
    private int id;
    private boolean ocupado;
    private Bloque siguiente;
    private String nombreArchivo; // NUEVO CAMPO

    public Bloque(int id) {
        this.id = id;
        this.siguiente = null;
        this.ocupado = false;
        this.nombreArchivo = null;
    }

    public int getId() {
        return id;
    }

    public boolean estaOcupado() {
        return ocupado;
    }

    public void ocupar(String nombreArchivo) {
        this.ocupado = true;
        this.nombreArchivo = nombreArchivo;
    }

    public void liberar() {
        this.ocupado = false;
        this.siguiente = null;
        this.nombreArchivo = null;
    }

    public Bloque getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Bloque siguiente) {
        this.siguiente = siguiente;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }
}
