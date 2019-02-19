/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * cambiarClave.java
 *
 * Created on 20-feb-2012, 15:52:03
 */

package Utilidades;

import Controlador.EmpleadosJpaController;
import Modelo.Empleados;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import proyectodai.Inicio;

/**
 *
 * @author weejdu01
 */
public class cambiarClave extends javax.swing.JDialog {

    private boolean salir;// varaiable que se utilizara para saber si se tiene que salir o no del sistema, si es true
    // es porque hay que salir del sistema en caso contrario no
    Empleados empleado;
    private boolean validar;
    private EmpleadosJpaController controladorEmpleados;
    Query consultaEmpleado;

    /** Creates new form cambiarClave */
    public cambiarClave(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        salir=false;
        validar=false;
        controladorEmpleados = new EmpleadosJpaController();
        empleado=Inicio.getEmpleado();
        consultaEmpleado= controladorEmpleados.getEm().createQuery("SELECT e FROM Empleados e WHERE e.usuario.usuario = :usuario");
        Utilidades.centrar(this);
    }

    private boolean validar(){
        String pass = new String(tfPass.getPassword());
        String passNuevo = new String(tfPassNuevo.getPassword());
        String passNuevo1 = new String(tfPassNuevo1.getPassword());
        if(pass.equals(empleado.getUsuario().getPassword())){
            if(passNuevo.equals(passNuevo1)){
                consultaEmpleado.setParameter("usuario", empleado.getUsuario().getUsuario());
                Empleados emp = (Empleados) consultaEmpleado.getSingleResult();
                emp.getUsuario().setPassword(passNuevo);
                controladorEmpleados.Guardar();
                Inicio.setEmpleado(emp);
                JOptionPane.showMessageDialog(this, "La clave se ha cambiado correctamente", "Informacion",JOptionPane.INFORMATION_MESSAGE);
                validar= true;
            }else{
                 JOptionPane.showMessageDialog(this, "las claves nuevas no coinciden", "Informacion",JOptionPane.INFORMATION_MESSAGE);
                 validar=  false;
            }
        }else{
             JOptionPane.showMessageDialog(this, "La clave no es correcta", "Informacion",JOptionPane.INFORMATION_MESSAGE);
             validar=  false;
        }

        return validar;
    }

     /**
     * @return the salir
     */
    public boolean isSalir() {
        return salir;
    }

    /**
     * @param salir the salir to set
     */
    public void setSalir(boolean salir) {
        this.salir = salir;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        tfPass = new javax.swing.JPasswordField();
        lbUsuario = new javax.swing.JLabel();
        lbPass = new javax.swing.JLabel();
        lbUsuario1 = new javax.swing.JLabel();
        tfPassNuevo = new javax.swing.JPasswordField();
        tfPassNuevo1 = new javax.swing.JPasswordField();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        lbTitulo = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        btAceptar = new javax.swing.JButton();
        btCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cambiar Password");
        setResizable(false);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.BorderLayout(10, 10));

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridLayout(1, 2, 0, 5));

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        tfPass.setName("tfPass"); // NOI18N
        tfPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(4, 29, 4, 29);
        jPanel4.add(tfPass, gridBagConstraints);

        lbUsuario.setText("Password Nuevo");
        lbUsuario.setName("lbUsuario"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        jPanel4.add(lbUsuario, gridBagConstraints);

        lbPass.setText("Repetir password Nuevo");
        lbPass.setName("lbPass"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        jPanel4.add(lbPass, gridBagConstraints);

        lbUsuario1.setText("Password Antiugo");
        lbUsuario1.setName("lbUsuario1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        jPanel4.add(lbUsuario1, gridBagConstraints);

        tfPassNuevo.setName("tfPassNuevo"); // NOI18N
        tfPassNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPassNuevoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(4, 29, 4, 29);
        jPanel4.add(tfPassNuevo, gridBagConstraints);

        tfPassNuevo1.setName("tfPassNuevo1"); // NOI18N
        tfPassNuevo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPassNuevo1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(4, 29, 4, 29);
        jPanel4.add(tfPassNuevo1, gridBagConstraints);

        jPanel2.add(jPanel4);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jPanel6.setName("jPanel6"); // NOI18N

        lbTitulo.setFont(new java.awt.Font("Tahoma", 1, 18));
        lbTitulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/keys.gif"))); // NOI18N
        lbTitulo.setText("Cambiar Password");
        lbTitulo.setName("lbTitulo"); // NOI18N
        jPanel6.add(lbTitulo);

        jPanel3.add(jPanel6);

        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setLayout(new java.awt.GridLayout(1, 0));

        jSeparator1.setName("jSeparator1"); // NOI18N
        jPanel7.add(jSeparator1);

        jPanel3.add(jPanel7);

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel5.setName("jPanel5"); // NOI18N

        btAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Aceptar.png"))); // NOI18N
        btAceptar.setText("Aceptar");
        btAceptar.setName("btAceptar"); // NOI18N
        btAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAceptarActionPerformed(evt);
            }
        });
        jPanel5.add(btAceptar);

        btCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Cancelar.png"))); // NOI18N
        btCancelar.setText("Cancelar");
        btCancelar.setName("btCancelar"); // NOI18N
        btCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelarActionPerformed(evt);
            }
        });
        jPanel5.add(btCancelar);

        jPanel1.add(jPanel5, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPassActionPerformed
        // TODO add your handling code here:
       setSalir(false);
       if(validar()){
            this.setVisible(false);
        }
}//GEN-LAST:event_tfPassActionPerformed

    private void btAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAceptarActionPerformed
        // TODO add your handling code here:
        setSalir(false);
        if(validar()){
            this.setVisible(false);
        }
}//GEN-LAST:event_btAceptarActionPerformed

    private void btCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarActionPerformed
        // TODO add your handling code here:
        setSalir(true);
        this.setVisible(false);
}//GEN-LAST:event_btCancelarActionPerformed

    private void tfPassNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPassNuevoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPassNuevoActionPerformed

    private void tfPassNuevo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPassNuevo1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPassNuevo1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                cambiarClave dialog = new cambiarClave(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btAceptar;
    private javax.swing.JButton btCancelar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbPass;
    private javax.swing.JLabel lbTitulo;
    private javax.swing.JLabel lbUsuario;
    private javax.swing.JLabel lbUsuario1;
    private javax.swing.JPasswordField tfPass;
    private javax.swing.JPasswordField tfPassNuevo;
    private javax.swing.JPasswordField tfPassNuevo1;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the validar
     */
    public boolean isValidar() {
        return validar;
    }

    /**
     * @param validar the validar to set
     */
    public void setValidar(boolean validar) {
        this.validar = validar;
    }

}