/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ventanaInformes.java
 *
 * Created on 13-feb-2012, 16:37:12
 */

package Utilidades;

import java.awt.Container;
import javax.swing.JDialog;

/**
 * Esta clase se utilizara solamente para mostrara un objeto JRViewer
 * @author weejdu01
 */
public class ventanaInformes extends JDialog {

    /** Creates new form ventanaInformes */
    public ventanaInformes(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public ventanaInformes(JDialog parent, String string, boolean b, Container c) {
         super(parent, b);  
         initComponents();
         this.setContentPane(c);
         this.setTitle(string);
         setSize(800,600);
         Utilidades.centrar(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 560, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ventanaInformes dialog = new ventanaInformes(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
