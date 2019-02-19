/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utilidades;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import javax.swing.text.Document;

/**
 * Esta clase es una implementacion de KeyListiner que servira para restringir los caracteres de un JTextArea
 * @author weejdu01
 */
public class RestringirTextArea implements Serializable,KeyListener {
    private int longitud=100; // longitud maxima de los caracteres que podra tener el JTextArea

    private Document text;// Document al cual se le reestringiran los caracteres permitidos

    public RestringirTextArea( Document documento){
        text=documento;
    }

    public void keyTyped(KeyEvent e) {
        comprobarlongitud(e);
    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }
    /**
     * Metodo que comprobara que no se admitan mas caracteres que la variable longitud
     * @param evt
     */
    private void comprobarlongitud(java.awt.event.KeyEvent evt) {
        // TODO add your handling code here:
        if(getLongitud() >0 && text.getLength()>=getLongitud()){
            Toolkit.getDefaultToolkit().beep();
            evt.consume();
        }
    }

    /**
     * @return the longitud
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * @param longitud the longitud to set
     */
    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }
}
