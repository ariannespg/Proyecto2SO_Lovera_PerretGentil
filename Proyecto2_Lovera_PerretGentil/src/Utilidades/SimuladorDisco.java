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
public class SimuladorDisco {

    private Bloque[] bloques;
    private int cantidadBloques;

    // Constructor
    public SimuladorDisco(int cantidadBloques) {
        this.cantidadBloques = cantidadBloques;
        bloques = new Bloque[cantidadBloques];

        // Inicializa los bloques
        for (int i = 0; i < cantidadBloques; i++) {
            bloques[i] = new Bloque(i);
        }
    }

    // Método para asignar bloques usando asignación encadenada
    public Bloque asignarBloques(int bloquesNecesarios) {
        Bloque primerBloque = null;
        Bloque bloqueAnterior = null;
        int contador = 0;

        for (int i = 0; i < cantidadBloques && contador < bloquesNecesarios; i++) {
            if (!bloques[i].estaOcupado()) {
                bloques[i].ocupar();
                if (primerBloque == null) {
                    primerBloque = bloques[i];
                }
                if (bloqueAnterior != null) {
                    bloqueAnterior.setSiguiente(bloques[i]);
                }
                bloqueAnterior = bloques[i];
                contador++;
            }
        }

        // Si no hay suficientes bloques disponibles
        if (contador < bloquesNecesarios) {
            liberarBloques(primerBloque);
            return null;
        }

        return primerBloque;
    }

    // Método para liberar bloques
    public void liberarBloques(Bloque bloqueInicial) {
    Bloque actual = bloqueInicial;
    while (actual != null) {
        Bloque siguiente = actual.getSiguiente();  // Guarda referencia antes de liberar
        actual.liberar();
        actual = siguiente;
    }
}

    // Método para mostrar el estado del disco (útil para depuración y visualización básica)
    public void mostrarEstadoDisco() {
        for (int i = 0; i < cantidadBloques; i++) {
            System.out.println("Bloque " + i + ": " + (bloques[i].estaOcupado() ? "Ocupado" : "Libre"));
        }
    }

    // Getter para obtener bloques
    public Bloque[] getBloques() {
        return bloques;
    }
}

