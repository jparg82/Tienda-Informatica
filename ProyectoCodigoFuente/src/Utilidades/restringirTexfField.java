/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utilidades;

import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import javax.swing.JTextField;

/**
 * Esta clase es una implementacion de KeyListiner que servira para restringir los caracteres de un JTextField
 * @author weejdu01
 */
public class restringirTexfField implements Serializable,KeyListener {

    private int longitud=10; // longitud maxima de los caracteres que podra tener el textField

    private JTextField text;// JTextField al cual se le reestringiran los caracteres permitidos

    public restringirTexfField( JTextField textField){
        text=textField;
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
        if(getLongitud() >0 && text.getText().length()>=getLongitud()){
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
