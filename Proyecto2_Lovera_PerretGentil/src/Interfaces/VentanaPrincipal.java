/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaces;


/**
 *
 * @author adrianlovera
 */

import Utilidades.Archivo;
import Utilidades.SimuladorDisco;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import modelos.*;

public class VentanaPrincipal extends JFrame {

    private SimuladorDisco sd;
    private JTree tree;
    private DefaultTreeModel modeloArbol;
    private DefaultMutableTreeNode raiz;
    private JTextArea estadoDisco;
    private JTable tablaAsignacion;
    private DefaultTableModel modeloTabla;
    private boolean esAdmin = true;

    public VentanaPrincipal() {
        setTitle("Simulador de Sistema de Archivos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        sd = new SimuladorDisco(10);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // JTree inicial con nodo raíz
        raiz = new DefaultMutableTreeNode("Raíz");
        modeloArbol = new DefaultTreeModel(raiz);
        tree = new JTree(modeloArbol);
        JScrollPane panelTree = new JScrollPane(tree);
        panelTree.setPreferredSize(new Dimension(250, 300));

        // Área central mostrando estado del disco
        estadoDisco = new JTextArea();
        estadoDisco.setEditable(false);
        JScrollPane panelEstadoDisco = new JScrollPane(estadoDisco);
        actualizarEstadoDisco();

        // Tabla de asignación (JTable)
        String[] columnas = {"Nombre Archivo", "Bloques Asignados", "Primer Bloque"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaAsignacion = new JTable(modeloTabla);
        JScrollPane panelTablaAsignacion = new JScrollPane(tablaAsignacion);
        panelTablaAsignacion.setPreferredSize(new Dimension(250, 300));

        // Panel Inferior (Botones)
        JButton btnCrearArchivo = new JButton("Crear Archivo");
        btnCrearArchivo.addActionListener(e -> crearArchivo());

        JButton btnCrearDirectorio = new JButton("Crear Directorio");
        btnCrearDirectorio.addActionListener(e -> crearDirectorio());

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarNodo());

        JButton btnModificar = new JButton("Modificar Archivo");
        btnModificar.addActionListener(e -> modificarNodo());

        JButton btnAlternarModo = new JButton("Cambiar Modo (Admin/Usuario)");
        btnAlternarModo.addActionListener(e -> alternarModo(btnCrearArchivo, btnCrearDirectorio, btnEliminar, btnModificar));

        JPanel panelInferior = new JPanel();
        panelInferior.add(btnCrearArchivo);
        panelInferior.add(btnCrearDirectorio);
        panelInferior.add(btnEliminar);
        panelInferior.add(btnModificar);
        panelInferior.add(btnAlternarModo);

        // Paneles divididos usando JSplitPane
        JSplitPane splitIzquierda = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelTree, panelTablaAsignacion);
        splitIzquierda.setDividerLocation(300);

        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitIzquierda, panelEstadoDisco);
        splitPrincipal.setDividerLocation(500);

        add(splitPrincipal, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void alternarModo(JButton crearArchivo, JButton crearDirectorio, JButton eliminar, JButton modificar) {
        esAdmin = !esAdmin;
        crearArchivo.setEnabled(esAdmin);
        crearDirectorio.setEnabled(esAdmin);
        eliminar.setEnabled(esAdmin);
        modificar.setEnabled(esAdmin);
        JOptionPane.showMessageDialog(this, esAdmin ? "Modo Administrador activado" : "Modo Usuario activado");
    }

    private void crearArchivo() {
        if (!esAdmin) {
            JOptionPane.showMessageDialog(this, "Modo usuario: no puede crear archivos.");
            return;
        }

        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        DefaultMutableTreeNode nodoPadre = (nodoSeleccionado == null) ? raiz : nodoSeleccionado;

        if (!nodoPadre.getAllowsChildren()) {
            JOptionPane.showMessageDialog(this, "Selecciona un directorio válido para crear un archivo.");
            return;
        }

        String nombre = JOptionPane.showInputDialog(this, "Nombre del archivo:");
        if (nombre != null && !nombre.isEmpty()) {

            int bloquesNecesarios = Integer.parseInt(JOptionPane.showInputDialog(this, "Cantidad de bloques a asignar:"));
            Bloque bloqueInicial = sd.asignarBloques(bloquesNecesarios);

            if (bloqueInicial != null) {
                Archivo nuevoArchivo = new Archivo(nombre, bloquesNecesarios, bloqueInicial);

                // Agregar visualmente al árbol
                DefaultMutableTreeNode nodoArchivo = new DefaultMutableTreeNode(nuevoArchivo.getNombre() + " (Archivo)", false);
                modeloArbol.insertNodeInto(nodoArchivo, nodoPadre, nodoPadre.getChildCount());
                tree.expandPath(new TreePath(nodoPadre.getPath()));

                // Actualizar tabla
                actualizarTablaAsignacion(nuevoArchivo.getNombre(), nuevoArchivo.getTamano(), nuevoArchivo.getPrimerBloque().getId());

                // Actualizar visualmente el estado del disco
                actualizarEstadoDisco();
            } else {
                JOptionPane.showMessageDialog(this, "No hay suficientes bloques disponibles para crear este archivo.");
            }
        }
    }

    private void crearDirectorio() {
        if (!esAdmin) {
            JOptionPane.showMessageDialog(this, "Modo usuario: no puede crear directorios.");
            return;
        }

        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        DefaultMutableTreeNode nodoPadre = (nodoSeleccionado == null) ? raiz : nodoSeleccionado;

        if (!nodoPadre.getAllowsChildren()) {
            JOptionPane.showMessageDialog(this, "Selecciona un directorio válido para crear un subdirectorio.");
            return;
        }

        String nombre = JOptionPane.showInputDialog(this, "Nombre del directorio:");
        if (nombre != null && !nombre.isEmpty()) {
            DefaultMutableTreeNode nuevoDirectorio = new DefaultMutableTreeNode(nombre + " (Directorio)", true);
            modeloArbol.insertNodeInto(nuevoDirectorio, nodoPadre, nodoPadre.getChildCount());
        }
    }

    private void eliminarNodo() {
    if (!esAdmin) {
        JOptionPane.showMessageDialog(this, "Modo usuario: no puede eliminar elementos.");
        return;
    }
    DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

    if (nodoSeleccionado != null && nodoSeleccionado != raiz) {
        String nombreNodo = nodoSeleccionado.getUserObject().toString();
        if (nombreNodo.contains("(Archivo)")) {
            // Obtén la referencia del archivo y libera bloques asociados (Aquí suponiendo ejemplo simple)
            // Esto debe buscarse en una lista enlazada real idealmente
            int primerBloque = buscarPrimerBloque(nombreNodo.replace(" (Archivo)", ""));
            if (primerBloque >= 0) {
                sd.liberarBloques(sd.getBloques()[primerBloque]);
                eliminarDeTabla(nombreNodo);
                actualizarEstadoDisco();
            }
        }

        modeloArbol.removeNodeFromParent(nodoSeleccionado);
    } else {
        JOptionPane.showMessageDialog(this, "Seleccione un nodo válido para eliminar.");
    }
}

    // Método auxiliar para buscar en tabla y eliminar fila
    private void eliminarDeTabla(String nombreNodo) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (modeloTabla.getValueAt(i, 0).equals(nombreNodo.replace(" (Archivo)", ""))) {
                modeloTabla.removeRow(i);
                break;
            }
        }
    }

    // Busca primer bloque asociado claramente al archivo
    private int buscarPrimerBloque(String nombreArchivo) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (modeloTabla.getValueAt(i, 0).equals(nombreArchivo)) {
                return (int) modeloTabla.getValueAt(i, 2);
            }
        }
        return -1;
    }

    private void modificarNodo() {
        if (!esAdmin) {
            JOptionPane.showMessageDialog(this, "Modo usuario: no puede modificar elementos.");
            return;
        }
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (nodoSeleccionado != null && nodoSeleccionado != raiz) {
            String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", nodoSeleccionado.getUserObject().toString());
            if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
                nodoSeleccionado.setUserObject(nuevoNombre);
                modeloArbol.nodeChanged(nodoSeleccionado);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un nodo válido para modificar.");
        }
    }

    private void actualizarTablaAsignacion(String nombreArchivo, int bloques, int primerBloque) {
        modeloTabla.addRow(new Object[]{nombreArchivo, bloques, primerBloque});
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
            new VentanaPrincipal().setVisible(true);
        });
    }
}

