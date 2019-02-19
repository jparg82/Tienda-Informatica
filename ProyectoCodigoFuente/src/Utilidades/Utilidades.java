/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utilidades;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author JuanPaulo
 */
public class Utilidades {


    /*se centraliza el entityManagerFactory para evitar que cada vez que se crea un controlador se tenga que inicializar un nuevo motor de persistencia
     de esta forma evitamosque la aplicacion se vuelva lenta y pesada*/
    private static final EntityManagerFactory emf= Persistence.createEntityManagerFactory("ProyectoDAIPU");

    /**
     * Metodo para centrar un JDialog, recibe como parametro un cuadro de dialogo
     * @param f
     */
    public static void centrar(JDialog f) {
        // Calculo el tamaño de la pantalla y de la ventana
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = f.getSize();
        // Si es mayor, igualo el tamaño de la ventana al de la pantalla
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        // Sitúo la ventana en el centro de la pantalla
        f.setLocation( ( screenSize.width - frameSize.width ) / 2, (screenSize.height - frameSize.height ) / 2 );
        //f.setVisible(true);
    }

    /**
     * Metodo para centrar un JDialog, recibe como parametro un cuadro de dialogo
     * @param f
     */
    public static void centrar(JFrame f) {
        // Calculo el tamaño de la pantalla y de la ventana
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = f.getSize();
        // Si es mayor, igualo el tamaño de la ventana al de la pantalla
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        // Sitúo la ventana en el centro de la pantalla
        f.setLocation( ( screenSize.width - frameSize.width ) / 2, (screenSize.height - frameSize.height ) / 2 );
        //f.setVisible(true);
    }

    /**
     * Metodo para maximizar un JFrame
     * @param f
     */
    public static void maximizar(JFrame f){
        int state = f.getExtendedState();
        state |= f.MAXIMIZED_BOTH;
        // Maximizamos el frame
        f.setExtendedState(state);
    }

    /**
     * Metodo para cambiar el color de fondo de un jFrame
     * @param f
     * @param c
     */
    public static void cambiarColor(JFrame f,Color c){
        f.getContentPane().setBackground(c);

    }

    public static String hora(){
        Date fechaAhora = new Date();
        SimpleDateFormat sdf=new java.text.SimpleDateFormat("HH:mm a");
        String fecha = sdf.format(fechaAhora);
        return fecha;
    }

    /**Metodo que hace una pausa de tantos milisegundos como diga el parámetro
     * @param tiempoMS int
     */
    public static void pausa(int tiempoMS) {
        try {
            Thread.sleep(tiempoMS);
        } catch (InterruptedException ie) {
            
        }
    }

    /**
     * @return the emf
     */
    public static EntityManagerFactory getEmf() {
        return emf;
    }


}


