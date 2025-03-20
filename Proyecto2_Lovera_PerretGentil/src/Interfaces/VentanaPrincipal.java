package Interfaces;

import Utilidades.Archivo;
import Utilidades.SimuladorDisco;
import Utilidades.PersistenciaSistema;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import modelos.Bloque;
import Utilidades.NodoSerializable;
import java.util.List;
import java.util.ArrayList;

public class VentanaPrincipal extends JFrame {

    private SimuladorDisco sd;
    private JTree tree;
    private DefaultTreeModel modeloArbol;
    private DefaultMutableTreeNode raiz;
    private JTextArea estadoDisco;
    private JTable tablaAsignacion;
    private DefaultTableModel modeloTabla;
    private boolean esAdmin = true;
    private static final int CANTIDAD_BLOQUES = 10;

    public VentanaPrincipal() {
        setTitle("Simulador de Sistema de Archivos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Intentamos cargar el estado
        Bloque[] bloquesCargados = PersistenciaSistema.cargarEstado(CANTIDAD_BLOQUES);
        NodoSerializable nodoRaizCargado = PersistenciaSistema.cargarArbol();
        sd = new SimuladorDisco(CANTIDAD_BLOQUES, bloquesCargados);

        initUI(nodoRaizCargado); // Pasa nodo cargado (puede ser null)

        // Reconstruir la tabla de asignación a partir de los bloques ocupados (si hay estado guardado)
        reconstruirTablaAsignacion();
        actualizarEstadoDisco();

        // Guardar al cerrar ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                PersistenciaSistema.guardarEstado(sd.getBloques());

                // Guardar árbol
                NodoSerializable nodoGuardar = construirNodoSerializable(raiz);
                PersistenciaSistema.guardarArbol(nodoGuardar);

                System.exit(0);
            }
        });
    }

    private void initUI(NodoSerializable nodoRaizCargado) {
        setLayout(new BorderLayout());

        // Configurar JTree
        if (nodoRaizCargado != null) {
            raiz = reconstruirNodo(nodoRaizCargado);
        } else {
            raiz = new DefaultMutableTreeNode("Raíz");
        }
        modeloArbol = new DefaultTreeModel(raiz);
        tree = new JTree(modeloArbol);
        JScrollPane panelTree = new JScrollPane(tree);
        panelTree.setPreferredSize(new Dimension(250, 300));

        // Área estado disco
        estadoDisco = new JTextArea();
        estadoDisco.setEditable(false);
        JScrollPane panelEstadoDisco = new JScrollPane(estadoDisco);
        actualizarEstadoDisco();

        // Tabla de asignación
        String[] columnas = {"Nombre Archivo", "Bloques Asignados", "Primer Bloque"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaAsignacion = new JTable(modeloTabla);
        JScrollPane panelTablaAsignacion = new JScrollPane(tablaAsignacion);
        panelTablaAsignacion.setPreferredSize(new Dimension(250, 300));

        // Botones
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

        // Paneles divididos
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
            Bloque bloqueInicial = sd.asignarBloques(bloquesNecesarios, nombre);

            if (bloqueInicial != null) {
                Archivo nuevoArchivo = new Archivo(nombre, bloquesNecesarios, bloqueInicial);

                DefaultMutableTreeNode nodoArchivo = new DefaultMutableTreeNode(nuevoArchivo.getNombre() + " (Archivo)", false);
                modeloArbol.insertNodeInto(nodoArchivo, nodoPadre, nodoPadre.getChildCount());
                tree.expandPath(new TreePath(nodoPadre.getPath()));

                actualizarTablaAsignacion(nuevoArchivo.getNombre(), nuevoArchivo.getTamano(), nuevoArchivo.getPrimerBloque().getId());
                actualizarEstadoDisco();
            } else {
                JOptionPane.showMessageDialog(this, "No hay suficientes bloques disponibles.");
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
            tree.expandPath(new TreePath(nodoPadre.getPath()));
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
                // Se obtiene el nombre real del archivo sin la etiqueta "(Archivo)"
                String nombreArchivo = nombreNodo.replace(" (Archivo)", "");
                // Recorrer todos los bloques y liberar los que pertenezcan a este archivo
                for (Bloque bloque : sd.getBloques()) {
                    if (bloque.estaOcupado() && bloque.getNombreArchivo().equals(nombreArchivo)) {
                        bloque.liberar();
                    }
                }
                // Se elimina la fila de la tabla y se actualiza el estado del disco
                eliminarDeTabla(nombreNodo);
                actualizarEstadoDisco();
                reconstruirTablaAsignacion();
            }
            modeloArbol.removeNodeFromParent(nodoSeleccionado);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un nodo válido para eliminar.");
        }
    }

    private void eliminarDeTabla(String nombreNodo) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (modeloTabla.getValueAt(i, 0).equals(nombreNodo.replace(" (Archivo)", ""))) {
                modeloTabla.removeRow(i);
                break;
            }
        }
    }

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
    
    // Método que recorre los bloques ocupados y reconstruye la tabla de asignación
    private void reconstruirTablaAsignacion() {
        modeloTabla.setRowCount(0); // Limpiar la tabla
        Bloque[] bloques = sd.getBloques();
        for (Bloque bloque : bloques) {
            if (bloque.estaOcupado() && esPrimerBloque(bloque, bloques)) {
                int contador = 0;
                Bloque actual = bloque;
                while (actual != null && actual.estaOcupado() && 
                       actual.getNombreArchivo().equals(bloque.getNombreArchivo())) {
                    contador++;
                    actual = actual.getSiguiente();
                }
                modeloTabla.addRow(new Object[]{bloque.getNombreArchivo(), contador, bloque.getId()});
            }
        }
    }
    
    // Método auxiliar para determinar si el bloque es el primero de la cadena
    private boolean esPrimerBloque(Bloque bloque, Bloque[] bloques) {
        for (Bloque b : bloques) {
            if (b.getSiguiente() != null && b.getSiguiente().getId() == bloque.getId()) {
                return false;
            }
        }
        return true;
    }

    private void actualizarEstadoDisco() {
        StringBuilder sb = new StringBuilder();
        for (Bloque bloque : sd.getBloques()) {
            sb.append("Bloque ").append(bloque.getId()).append(": ");
            if (bloque.estaOcupado()) {
                sb.append("Ocupado por '").append(bloque.getNombreArchivo()).append("'");
            } else {
                sb.append("Libre");
            }
            sb.append("\n");
        }
        estadoDisco.setText(sb.toString());
    }

    // ======== MÉTODOS para persistencia del árbol =========
    private NodoSerializable construirNodoSerializable(DefaultMutableTreeNode nodo) {
        String nombre = nodo.getUserObject().toString();
        NodoSerializable nodoSer = new NodoSerializable(nombre, nodo.getAllowsChildren());
        for (int i = 0; i < nodo.getChildCount(); i++) {
            DefaultMutableTreeNode hijo = (DefaultMutableTreeNode) nodo.getChildAt(i);
            nodoSer.getHijos().add(construirNodoSerializable(hijo));
        }
        return nodoSer;
    }

    private DefaultMutableTreeNode reconstruirNodo(NodoSerializable nodoSer) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(nodoSer.getNombre(), nodoSer.isEsDirectorio());
        for (NodoSerializable hijoSer : nodoSer.getHijos()) {
            nodo.add(reconstruirNodo(hijoSer));
        }
        return nodo;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}


