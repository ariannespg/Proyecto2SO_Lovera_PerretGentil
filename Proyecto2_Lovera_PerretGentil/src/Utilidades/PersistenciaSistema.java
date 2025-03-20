/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilidades;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import modelos.Bloque;

import java.io.*;

/**
 *
 * @author arianneperret-gentil
 */
public class PersistenciaSistema {
    private static final String ARCHIVO_JSON = "estado_sistema.json";
    private static final String ARCHIVO_ARBOL = "arbol_directorios.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static class EstadoSistema {
        public Bloque[] bloques;
    }

    // Guardar bloques
    public static void guardarEstado(Bloque[] bloques) {
        EstadoSistema estado = new EstadoSistema();
        estado.bloques = bloques;
        try (FileWriter writer = new FileWriter(ARCHIVO_JSON)) {
            gson.toJson(estado, writer);
            System.out.println("Estado de bloques guardado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bloque[] cargarEstado(int cantidadBloques) {
        File archivo = new File(ARCHIVO_JSON);
        if (!archivo.exists()) {
            System.out.println("No se encontró archivo de bloques.");
            return null;
        }

        try (FileReader reader = new FileReader(archivo)) {
            EstadoSistema estado = gson.fromJson(reader, EstadoSistema.class);
            System.out.println("Estado de bloques cargado.");
            return estado.bloques;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===================== NUEVO: Guardado del árbol =====================

    public static void guardarArbol(NodoSerializable raiz) {
        try (FileWriter writer = new FileWriter(ARCHIVO_ARBOL)) {
            gson.toJson(raiz, writer);
            System.out.println("Árbol guardado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NodoSerializable cargarArbol() {
        File archivo = new File(ARCHIVO_ARBOL);
        if (!archivo.exists()) {
            System.out.println("No se encontró archivo del árbol.");
            return null;
        }

        try (FileReader reader = new FileReader(archivo)) {
            NodoSerializable raiz = gson.fromJson(reader, NodoSerializable.class);
            System.out.println("Árbol cargado.");
            return raiz;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
   
