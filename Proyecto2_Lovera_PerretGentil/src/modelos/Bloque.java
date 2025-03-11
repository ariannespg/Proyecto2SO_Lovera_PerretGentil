/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

public class Bloque {
    private int id;
    private boolean ocupado;
    private Bloque siguiente;

    public Bloque(int id) {
        this.id = id;
        this.siguiente = null;
        this.ocupado = false;
    }

    public int getId() {
        return id;
    }

    public boolean estaOcupado() {
        return ocupado;
    }

    public void ocupar() {
        this.ocupado = true;
    }

    public void liberar() {
        this.ocupado = false;
        this.siguiente = null;
    }

    public Bloque getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Bloque siguiente) {
        this.siguiente = siguiente;
    }
}