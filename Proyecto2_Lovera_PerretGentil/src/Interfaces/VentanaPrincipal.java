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
import javax.swing.tree.DefaultMutableTreeNode;
import modelos.*;

public class VentanaPrincipal extends JFrame {

    private SimuladorDisco sd;
    private JTree tree;
    private JTextArea estadoDisco;

    public VentanaPrincipal() {
        // Inicialización básica
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

    // JTree inicial con nodo raíz
    DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Raíz");
    tree = new JTree(raiz);
    JScrollPane panelTree = new JScrollPane(tree);
    panelTree.setPreferredSize(new Dimension(150, 300));

    // Área central mostrando estado del disco
    estadoDisco = new JTextArea();
    estadoDisco.setEditable(false);
    JScrollPane panelEstadoDisco = new JScrollPane(estadoDisco);
    actualizarEstadoDisco();

    // Botones inferiores
    JButton btnAsignar = new JButton("Asignar 3 bloques");
    btnAsignar.addActionListener(e -> {
        Bloque primerBloque = sd.asignarBloques(3);
        if (primerBloque != null) {
            JOptionPane.showMessageDialog(this, "Se asignaron 3 bloques correctamente.");
        } else {
            JOptionPane.showMessageDialog(this, "No hay bloques suficientes.");
        }
        actualizarEstadoDisco();
    });

    JButton btnLiberar = new JButton("Liberar bloques");
    btnLiberar.addActionListener(e -> {
        Bloque primerBloque = sd.getBloques()[0]; // Solo prueba básica
        sd.liberarBloques(primerBloque);
        JOptionPane.showMessageDialog(this, "Bloques liberados correctamente.");
        actualizarEstadoDisco();
    });

    // Define claramente panelInferior aquí:
    JPanel panelInferior = new JPanel();
    panelInferior.add(btnAsignar);
    panelInferior.add(btnLiberar);

    // Agregando componentes al frame principal
    add(panelTree, BorderLayout.WEST);
    add(new JScrollPane(estadoDisco = new JTextArea()), BorderLayout.CENTER);
    add(panelInferior, BorderLayout.SOUTH);

    actualizarEstadoDisco();

    setSize(600, 400);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
}

    private JPanel panelInferior(JButton btnAsignar, JButton btnLiberar) {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());

    panel.add(btnAsignar);
    panel.add(btnLiberar);

    return panel;
}

    private JPanel panelTree(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tree, BorderLayout.CENTER);
        return panel;
    }

    private JPanel panelInferior(){
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        return panel;
    }

    private void actualizarEstadoDisco() {
    StringBuilder sb = new StringBuilder();
    for (Bloque bloque : sd.getBloques()) {
        sb.append("Bloque ")
          .append(bloque.getId())
          .append(": ")
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


    private boolean asignadoExitosamente(Bloque bloque) {
    return bloque != null;
}
}