package Interfaces;

import Utilidades.Archivo;
import Utilidades.SimuladorDisco;
import Utilidades.PersistenciaSistema;
import Utilidades.NodoSerializable;
import modelos.Bloque;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VentanaPrincipal extends JFrame {

    private SimuladorDisco sd;
    private JTree tree;
    private DefaultTreeModel modeloArbol;
    private DefaultMutableTreeNode raiz;
    private JTextArea estadoDisco;
    private JTable tablaAsignacion;
    private DefaultTableModel modeloTabla;
    private PanelDisco panelGraficoDisco;
    private JTextArea logArea; // Área para registrar auditoría
    private boolean esAdmin = true;
    private static final int CANTIDAD_BLOQUES = 10;

    public VentanaPrincipal() {
        setTitle("Simulador de Sistema de Archivos");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Cargar estado del sistema
        Bloque[] bloquesCargados = PersistenciaSistema.cargarEstado(CANTIDAD_BLOQUES);
        NodoSerializable nodoRaizCargado = PersistenciaSistema.cargarArbol();
        sd = new SimuladorDisco(CANTIDAD_BLOQUES, bloquesCargados);

        initUI(nodoRaizCargado);
        reconstruirTablaAsignacion();
        actualizarEstadoDisco();
        panelGraficoDisco.actualizarBloques(sd.getBloques());

        // Guardar al cerrar ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                PersistenciaSistema.guardarEstado(sd.getBloques());
                NodoSerializable nodoGuardar = construirNodoSerializable(raiz);
                PersistenciaSistema.guardarArbol(nodoGuardar);
                panelGraficoDisco.reiniciarColores();
                logEvent("Se cerró la aplicación.");
                System.exit(0);
            }
        });
    }

    private void initUI(NodoSerializable nodoRaizCargado) {
        setLayout(new BorderLayout());

        // Panel de botones en la parte superior
        JPanel panelBotones = new JPanel();
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
        panelBotones.add(btnCrearArchivo);
        panelBotones.add(btnCrearDirectorio);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnAlternarModo);
        add(panelBotones, BorderLayout.NORTH);

        // Panel Izquierdo: árbol, tabla y registro de auditoría
        // Árbol de directorios
        if (nodoRaizCargado != null) {
            raiz = reconstruirNodo(nodoRaizCargado);
        } else {
            raiz = new DefaultMutableTreeNode("Raíz");
        }
        modeloArbol = new DefaultTreeModel(raiz);
        tree = new JTree(modeloArbol);
        JScrollPane panelTree = new JScrollPane(tree);
        panelTree.setBorder(BorderFactory.createTitledBorder("Directorios"));
        panelTree.setPreferredSize(new Dimension(300, 300));

        // Tabla de asignación de archivos
        String[] columnas = {"Nombre Archivo", "Bloques Asignados", "Primer Bloque"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaAsignacion = new JTable(modeloTabla);
        JScrollPane panelTablaAsignacion = new JScrollPane(tablaAsignacion);
        panelTablaAsignacion.setBorder(BorderFactory.createTitledBorder("Asignación de Archivos"));
        panelTablaAsignacion.setPreferredSize(new Dimension(300, 300));

        // Registro de auditoría
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Registro de auditorías"));
        logScroll.setPreferredSize(new Dimension(300, 150));

        // Organizar en el panel izquierdo (usando un BoxLayout vertical)
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.add(panelTree);
        panelIzquierdo.add(panelTablaAsignacion);
        panelIzquierdo.add(logScroll);

        // Panel Derecho: estado gráfico y textual del disco
        // Panel gráfico de bloques
        panelGraficoDisco = new PanelDisco(sd.getBloques());
        JScrollPane scrollPanelGrafico = new JScrollPane(panelGraficoDisco);
        scrollPanelGrafico.setBorder(BorderFactory.createTitledBorder("Bloques libres"));
        scrollPanelGrafico.setPreferredSize(new Dimension(500, 250));

        // Área de estado del disco (texto)
        estadoDisco = new JTextArea();
        estadoDisco.setEditable(false);
        JScrollPane panelEstadoDisco = new JScrollPane(estadoDisco);
        panelEstadoDisco.setBorder(BorderFactory.createTitledBorder("Estado del Disco"));
        panelEstadoDisco.setPreferredSize(new Dimension(500, 250));

        // Organizar panel derecho en vertical
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.add(scrollPanelGrafico);
        panelDerecho.add(panelEstadoDisco);

        // Dividir la interfaz en dos partes: Izquierda y Derecha
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPrincipal.setDividerLocation(320);
        add(splitPrincipal, BorderLayout.CENTER);
    }

    private void alternarModo(JButton crearArchivo, JButton crearDirectorio, JButton eliminar, JButton modificar) {
        esAdmin = !esAdmin;
        crearArchivo.setEnabled(esAdmin);
        crearDirectorio.setEnabled(esAdmin);
        eliminar.setEnabled(esAdmin);
        modificar.setEnabled(esAdmin);
        String mensaje = esAdmin ? "Modo Administrador activado" : "Modo Usuario activado";
        JOptionPane.showMessageDialog(this, mensaje);
        logEvent("Se cambió al " + (esAdmin ? "modo Administrador" : "modo Usuario") + ".");
    }

    private void crearArchivo() {
        if (!esAdmin) {
            JOptionPane.showMessageDialog(this, "Modo usuario: no puede crear archivos.");
            logEvent("Intento de creación de archivo en modo Usuario.");
            return;
        }
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        DefaultMutableTreeNode nodoPadre = (nodoSeleccionado == null) ? raiz : nodoSeleccionado;
        if (!nodoPadre.getAllowsChildren()) {
            JOptionPane.showMessageDialog(this, "Selecciona un directorio válido para crear un archivo.");
            logEvent("Intento de creación de archivo en nodo inválido.");
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
                panelGraficoDisco.actualizarBloques(sd.getBloques());
                logEvent("Se creó el archivo '" + nombre + "' asignando " + bloquesNecesarios + " bloques.");
            } else {
                JOptionPane.showMessageDialog(this, "No hay suficientes bloques disponibles.");
                logEvent("Error al crear archivo '" + nombre + "': no hay bloques disponibles.");
            }
        }
    }

    private void crearDirectorio() {
        if (!esAdmin) {
            JOptionPane.showMessageDialog(this, "Modo usuario: no puede crear directorios.");
            logEvent("Intento de creación de directorio en modo Usuario.");
            return;
        }
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        DefaultMutableTreeNode nodoPadre = (nodoSeleccionado == null) ? raiz : nodoSeleccionado;
        if (!nodoPadre.getAllowsChildren()) {
            JOptionPane.showMessageDialog(this, "Selecciona un directorio válido para crear un subdirectorio.");
            logEvent("Intento de creación de directorio en nodo inválido.");
            return;
        }
        String nombre = JOptionPane.showInputDialog(this, "Nombre del directorio:");
        if (nombre != null && !nombre.isEmpty()) {
            DefaultMutableTreeNode nuevoDirectorio = new DefaultMutableTreeNode(nombre + " (Directorio)", true);
            modeloArbol.insertNodeInto(nuevoDirectorio, nodoPadre, nodoPadre.getChildCount());
            tree.expandPath(new TreePath(nodoPadre.getPath()));
            logEvent("Se creó el directorio '" + nombre + "'.");
        }
    }

    // Método para eliminar nodos y sus subnodos, liberando bloques de archivos
    private void eliminarNodo() {
        if (!esAdmin) {
            JOptionPane.showMessageDialog(this, "Modo usuario: no puede eliminar elementos.");
            logEvent("Intento de eliminación en modo Usuario.");
            return;
        }
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (nodoSeleccionado != null && nodoSeleccionado != raiz) {
            eliminarSubarbol(nodoSeleccionado);
            modeloArbol.removeNodeFromParent(nodoSeleccionado);
            actualizarEstadoDisco();
            reconstruirTablaAsignacion();
            panelGraficoDisco.actualizarBloques(sd.getBloques());
            logEvent("Se eliminó el nodo '" + nodoSeleccionado.getUserObject().toString() + "' y sus subnodos.");
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un nodo válido para eliminar.");
        }
    }
    
    // Método recursivo para eliminar nodos hijos y liberar bloques (si son archivos)
    private void eliminarSubarbol(DefaultMutableTreeNode nodo) {
        for (int i = nodo.getChildCount() - 1; i >= 0; i--) {
            DefaultMutableTreeNode hijo = (DefaultMutableTreeNode) nodo.getChildAt(i);
            eliminarSubarbol(hijo);
        }
        String nombreNodo = nodo.getUserObject().toString();
        if (nombreNodo.contains("(Archivo)")) {
            String nombreArchivo = nombreNodo.replace(" (Archivo)", "");
            for (Bloque bloque : sd.getBloques()) {
                if (bloque.estaOcupado() && bloque.getNombreArchivo().equals(nombreArchivo)) {
                    bloque.liberar();
                }
            }
            eliminarDeTabla(nombreNodo);
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
            logEvent("Intento de modificación en modo Usuario.");
            return;
        }
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (nodoSeleccionado != null && nodoSeleccionado != raiz) {
            String nombreAnterior = nodoSeleccionado.getUserObject().toString();
            String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", nombreAnterior);
            if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
                nodoSeleccionado.setUserObject(nuevoNombre);
                modeloArbol.nodeChanged(nodoSeleccionado);
                logEvent("Se modificó el nodo '" + nombreAnterior + "' a '" + nuevoNombre + "'.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un nodo válido para modificar.");
        }
    }

    private void actualizarTablaAsignacion(String nombreArchivo, int bloques, int primerBloque) {
        modeloTabla.addRow(new Object[]{nombreArchivo, bloques, primerBloque});
    }
    
    // Reconstruir la tabla de asignación a partir de los bloques ocupados
    private void reconstruirTablaAsignacion() {
        modeloTabla.setRowCount(0);
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
    
    // Verifica si el bloque es el primero de la cadena
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

    // Método para registrar eventos en la auditoría con marca de tiempo y usuario
    private void logEvent(String evento) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String timestamp = dtf.format(LocalDateTime.now());
        String usuario = esAdmin ? "Administrador" : "Usuario";
        logArea.append("[" + timestamp + "][" + usuario + "]: " + evento + "\n");
    }

    // Métodos para persistir el árbol
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
        EventQueue.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}




