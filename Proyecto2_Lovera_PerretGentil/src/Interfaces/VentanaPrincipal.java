/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaces;


/**
 *
 * @author adrianlovera
 */

import Utilidades.SimuladorDisco;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import modelos.*;

public class VentanaPrincipal extends JFrame {

    private SimuladorDisco sd;
    private JTree tree;
    private DefaultTreeModel modeloArbol;
    private DefaultMutableTreeNode raiz;
    private JTextArea estadoDisco;
    
    public VentanaPrincipal() {
        setTitle("Simulador de Sistema de Archivos");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializando Simulador de Disco
        sd = new SimuladorDisco(10);

        // Componentes gráficos
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Panel Izquierdo (JTree - Estructura de archivos)
        raiz = new DefaultMutableTreeNode("Raíz");
        modeloArbol = new DefaultTreeModel(raiz);
        tree = new JTree(modeloArbol);
        JScrollPane panelIzquierdo = new JScrollPane(tree);
        panelIzquierdo.setPreferredSize(new Dimension(200, 400));

        // Panel Central (Estado del disco)
        estadoDisco = new JTextArea();
        estadoDisco.setEditable(false);
        actualizarEstadoDisco();
        JScrollPane scrollCentral = new JScrollPane(estadoDisco);

        // Panel Inferior (Botones para gestión de archivos y directorios)
        JButton btnCrearArchivo = new JButton("Crear Archivo");
        btnCrearArchivo.addActionListener(e -> crearArchivo());

        JButton btnCrearDirectorio = new JButton("Crear Directorio");
        btnCrearDirectorio.addActionListener(e -> crearDirectorio());

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarNodo());

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnCrearArchivo);
        panelBotones.add(btnCrearDirectorio);
        panelBotones.add(btnEliminar);

        // Agregar componentes a la ventana
        add(panelIzquierdo, BorderLayout.WEST);
        add(scrollCentral, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void crearArchivo() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del archivo:");
        if (nombre != null && !nombre.isEmpty()) {
            DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (nodoSeleccionado == null) nodoSeleccionado = raiz;
            DefaultMutableTreeNode nuevoArchivo = new DefaultMutableTreeNode(nombre + " (Archivo)");
            modeloArbol.insertNodeInto(nuevoArchivo, nodoSeleccionado, nodoSeleccionado.getChildCount());
            modeloArbol.reload();
        }
    }

    private void crearDirectorio() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del directorio:");
        if (nombre != null && !nombre.isEmpty()) {
            DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (nodoSeleccionado == null) nodoSeleccionado = raiz;
            DefaultMutableTreeNode nuevoDirectorio = new DefaultMutableTreeNode(nombre + " (Directorio)");
            modeloArbol.insertNodeInto(nuevoDirectorio, nodoSeleccionado, nodoSeleccionado.getChildCount());
            modeloArbol.reload();
        }
    }

    private void eliminarNodo() {
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (nodoSeleccionado != null && nodoSeleccionado != raiz) {
            modeloArbol.removeNodeFromParent(nodoSeleccionado);
            modeloArbol.reload();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un nodo válido para eliminar.");
        }
    }

    private void actualizarEstadoDisco() {
        StringBuilder sb = new StringBuilder();
        for (Bloque bloque : sd.getBloques()) {
            sb.append("Bloque ").append(bloque.getId()).append(": ")
              .append(bloque.estaOcupado() ? "Ocupado" : "Libre")
              .append("\n");
        }
        estadoDisco.setText(sb.toString());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            VentanaPrincipal vp = new VentanaPrincipal();
            vp.setVisible(true);
        });
    }
}
