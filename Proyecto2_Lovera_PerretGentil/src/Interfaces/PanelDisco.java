/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaces;

/**
 *
 * @author arianneperret-gentil
 */
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import modelos.Bloque;

public class PanelDisco extends JPanel {

    private Bloque[] bloques;
    private Map<String, Color> mapaColoresArchivos;
    private Color[] paletaColores = {
        Color.CYAN, Color.PINK, Color.YELLOW, Color.GREEN, Color.ORANGE,
        Color.MAGENTA, Color.LIGHT_GRAY, new Color(255, 153, 153), 
        new Color(153, 255, 204), new Color(204, 153, 255)
    };
    private int indiceColor = 0;

    public PanelDisco(Bloque[] bloques) {
        this.bloques = bloques;
        this.mapaColoresArchivos = new HashMap<>();
        setPreferredSize(new Dimension(600, 100));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int bloqueAncho = 50;
        int bloqueAlto = 50;
        int espacio = 10;

        for (int i = 0; i < bloques.length; i++) {
            int x = i * (bloqueAncho + espacio);
            int y = 20;

            Bloque bloque = bloques[i];
            if (bloque.estaOcupado()) {
                String nombreArchivo = bloque.getNombreArchivo();
                if (!mapaColoresArchivos.containsKey(nombreArchivo)) {
                    mapaColoresArchivos.put(nombreArchivo, paletaColores[indiceColor % paletaColores.length]);
                    indiceColor++;
                }
                g.setColor(mapaColoresArchivos.get(nombreArchivo));
            } else {
                g.setColor(Color.LIGHT_GRAY);
            }

            g.fillRect(x, y, bloqueAncho, bloqueAlto);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, bloqueAncho, bloqueAlto);
            g.drawString(String.valueOf(i), x + 18, y + 30);
        }
    }

    public void actualizarBloques(Bloque[] nuevosBloques) {
        this.bloques = nuevosBloques;
        repaint();
    }

    public void reiniciarColores() {
        mapaColoresArchivos.clear();
        indiceColor = 0;
    }
}

