/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AltaEmpleado.java
 *
 * Created on 22-dic-2011, 21:34:50
 */

package Vistas.Emplados;

import Modelo.Empleados;
import Modelo.Provincias;
import Modelo.Usuarios;
import Utilidades.restringirTexfField;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JOptionPane;

/**
 *
 * @author JuanPaulo
 */
public class AltaEmpleado1 extends javax.swing.JDialog {

    private Query Consulta; /* variable que tendra el resultado de la consulta "SELECT e FROM Empleados e WHERE e.dniEmpleado = :dniEmpleado"
    y se utilizara para comprobar si el Empleados que se va a insertar no este en la BD*/
    private String jpql = "SELECT e FROM Empleados e WHERE e.dniEmpleado = :dniEmpleado" ;
    private Empleados empleado; // empleado que se dara de alta
    private Usuarios usuario;// usuario que se asicioara al empleado
    private static boolean necesitaGuardar; // variable para saber si se han hecho cambios en la accion

    EntityManager em;// varaiable que almacenara el entitymanager pasado como parametro al constructor
    private boolean acaptaAlta;// variable que se utilizara para saber si se ha pulsado el boton acaptar o cancelar

    /** Creates new form AltaEmpleado */
    public AltaEmpleado1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
       
    }
    /**
     * Constructor que recibe como parametro el empleado y usuario que se va a dar de alta. tambien recibe como parametro el entitymanager
     * @param parent
     * @param modal
     * @param empleado
     * @param usuario
     * @param em
     */
    public AltaEmpleado1(java.awt.Dialog parent, boolean modal, Empleados empleado,Usuarios usuario, EntityManager em) {
        super(parent, modal);
        initComponents();

        Utilidades.Utilidades.centrar(this);

        this.empleado=empleado;
        this.usuario=usuario;

        Utilidades.Utilidades.centrar(this);
      
        // se reestringen los caracteres del tfDni
        restringirTexfField restringirTFDni = new restringirTexfField(tfDni);
        restringirTFDni.setLongitud(9);
        tfDni.addKeyListener(restringirTFDni);
        // se reestringen los caracteres del tfTelefono
        restringirTexfField restringirTFtfno = new restringirTexfField(tfTelefono);
        restringirTFtfno.setLongitud(9);
        tfTelefono.addKeyListener(restringirTFtfno);

        this.em=em;
        /* Se crea la consulta a travez del metodo createquery() del entity manager, el metodo recibe como parametro
         la consulta */
        Consulta = this.em.createQuery(jpql);

        //este evento se ejecutara cuando halla algun cambio en el objeto empleado y modificara el valor de la varieble
       //necesitaGuardar a true
       this.empleado.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setNecesitaGuardar(true);
            }
        });
    }

     /**
     * Metodo para dar de alta un empleado, tambien se encarga de comprobar que los campos obligatorios sean correctos
     */
    private void altaEmpleado() {
        String dni = tfDni.getText();

        /* Se comprueba que los compos obligatorios no esten vacios*/
        if(this.tfDni.getText().trim().equals("") ||  this.tfNombre.getText().trim().equals("") ||
           this.tfApellidos.getText().trim().equals("") || this.tfTelefono.getText().trim().equals("")){

             JOptionPane.showMessageDialog(this, "Los campos con * son obligatorios", "Atencion", (int) CENTER_ALIGNMENT);

        }else{

            /*Si los campos obligatorios  son correctos se comprueba que el cliente que se
            va a insertar en la BD ya exista*/
            Consulta.setParameter("dniEmpleado", dni);
            Empleados empleadoAux=null;
            try{
                // si el resultado de la consulta devuelve null es porque el cliente no existe en la BD
                empleadoAux = (Empleados) Consulta.getSingleResult();
            }catch(javax.persistence.NoResultException e){
                System.out.println("prueba");
            }

                /*si el cliente no existe se inserta en la BD si no se avisa de que ya existe,*/
                if(empleadoAux!=null){
                    JOptionPane.showMessageDialog(this, "El cliente con DNI : "+dni+" ya existe", "Atencion", (int) CENTER_ALIGNMENT);
                }else{
                    
                    empleado.setDniEmpleado(tfDni.getText());
                    empleado.setNombre(tfNombre.getText());
                    empleado.setApellidos(tfApellidos.getText());
                    empleado.setDireccion(tfDireccion.getText());
                    empleado.setTelefono(tfTelefono.getText());
                    Provincias prov = (Provincias) this.cbProvincias.getSelectedItem();
                    empleado.setCodProvincia(prov);
                    empleado.setCiudad(tfCiudad.getText());
                    String uspass = empleado.getDniEmpleado();
                    usuario.setUsuario(uspass);
                    usuario.setPassword(uspass);
                    usuario.setTipo(jchbUsuario.isSelected());
                    empleado.setUsuario(usuario);
                    //usuario.getEmpleadosList().add(empleado);
                    acaptaAlta=true;                  
                     JOptionPane.showMessageDialog(this, "Se ha creado el usuario: " +empleado.getUsuario().getUsuario()+" y password: "+empleado.getUsuario().getUsuario() , "Informacion",JOptionPane.INFORMATION_MESSAGE);
                    
                     this.dispose();
                }
        }
    }
    /**
     * Se encargara de cerrar la ventana.
     */
    private void Cerrar() {
        int opcion= JOptionPane.showConfirmDialog(this, "Desea cancelar el alta", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);

        if(opcion==JOptionPane.YES_OPTION){
           acaptaAlta=false;
           this.dispose();
        }
    }

    /**
     * @return the necesitaGuardar
     */
    public static boolean isNecesitaGuardar() {
        return necesitaGuardar;
    }

    /**
     * @param aNecesitaGuardar the necesitaGuardar to set
     */
    public static void setNecesitaGuardar(boolean aNecesitaGuardar) {
        necesitaGuardar = aNecesitaGuardar;
    }
    
     /**
     * @return the empleadoAux
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleadoAux the empleadoAux to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }
    /**
     * @return the acaptaAlta
     */
    public boolean isAcaptaAlta() {
        return acaptaAlta;
    }

    /**
     * @param acaptaAlta the acaptaAlta to set
     */
    public void setAcaptaAlta(boolean acaptaAlta) {
        this.acaptaAlta = acaptaAlta;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        TiendaInformaticaPUEntityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("ProyectoDAIPU").createEntityManager();
        provinciasQuery = java.beans.Beans.isDesignTime() ? null : TiendaInformaticaPUEntityManager.createQuery("SELECT p FROM Provincias p");
        provinciasList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : provinciasQuery.getResultList();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lbTitulo = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btAceptar = new javax.swing.JButton();
        btCancelar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel9 = new javax.swing.JPanel();
        lbNumero = new javax.swing.JLabel();
        lbDni = new javax.swing.JLabel();
        lbNombre = new javax.swing.JLabel();
        lbApellidos = new javax.swing.JLabel();
        lbCiudad = new javax.swing.JLabel();
        lbProvincia = new javax.swing.JLabel();
        lbTelefono = new javax.swing.JLabel();
        tfIdCliente = new javax.swing.JTextField();
        tfDni = new javax.swing.JTextField();
        tfNombre = new javax.swing.JTextField();
        tfApellidos = new javax.swing.JTextField();
        tfCiudad = new javax.swing.JTextField();
        cbProvincias = new javax.swing.JComboBox();
        tfTelefono = new javax.swing.JTextField();
        lbDireccion = new javax.swing.JLabel();
        tfDireccion = new javax.swing.JTextField();
        jchbUsuario = new javax.swing.JCheckBox();
        lbUsuario = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Nuevo Epleado");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(365, 361));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionEmpleados.png"))); // NOI18N
        jPanel1.add(jLabel2);

        lbTitulo.setFont(new java.awt.Font("Tahoma 18 Negrita", 1, 18));
        lbTitulo.setText("Nuevo Empleado");
        jPanel1.add(lbTitulo);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(jPanel5, java.awt.BorderLayout.LINE_START);

        btAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Aceptar.png"))); // NOI18N
        btAceptar.setText("Aceptar");
        btAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAceptarActionPerformed(evt);
            }
        });
        jPanel3.add(btAceptar);

        btCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Cancelar.png"))); // NOI18N
        btCancelar.setText("Cancelar");
        btCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelarActionPerformed(evt);
            }
        });
        jPanel3.add(btCancelar);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);
        getContentPane().add(jPanel4, java.awt.BorderLayout.LINE_END);

        jPanel7.setLayout(new java.awt.BorderLayout());

        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING, 5, 20));

        jLabel1.setFont(new java.awt.Font("Tahoma 8 Negrita 8 Negrita", 1, 8));
        jLabel1.setText("Los campos con * son obligatorios");
        jPanel6.add(jLabel1);

        jPanel7.add(jPanel6, java.awt.BorderLayout.SOUTH);

        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));
        jPanel8.add(jSeparator1);

        jPanel7.add(jPanel8, java.awt.BorderLayout.PAGE_START);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Empleado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        lbNumero.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNumero.setText("Numero de Empleado:");

        lbDni.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDni.setText("*DNI:");

        lbNombre.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNombre.setText("*Nombre:");

        lbApellidos.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbApellidos.setText("*Apellidos:");

        lbCiudad.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbCiudad.setText("Ciudad:");

        lbProvincia.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbProvincia.setText("Provincia:");

        lbTelefono.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbTelefono.setText("*Telefono:");

        tfIdCliente.setEditable(false);

        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, provinciasList, cbProvincias);
        bindingGroup.addBinding(jComboBoxBinding);

        lbDireccion.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDireccion.setText("Direccion:");

        jchbUsuario.setText("Administrador");

        lbUsuario.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbUsuario.setText("Tipo de usuario");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(lbTelefono)
                        .addGap(4, 4, 4)
                        .addComponent(tfTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(lbUsuario)
                        .addGap(7, 7, 7)
                        .addComponent(jchbUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(lbDireccion)
                        .addGap(4, 4, 4)
                        .addComponent(tfDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(lbCiudad)
                        .addGap(4, 4, 4)
                        .addComponent(tfCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(lbProvincia)
                        .addGap(4, 4, 4)
                        .addComponent(cbProvincias, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(lbNombre)
                        .addGap(4, 4, 4)
                        .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(lbApellidos)
                        .addGap(4, 4, 4)
                        .addComponent(tfApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(lbNumero)
                        .addGap(4, 4, 4)
                        .addComponent(tfIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lbDni)
                        .addGap(4, 4, 4)
                        .addComponent(tfDni, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbNumero)
                    .addComponent(tfIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDni)
                    .addComponent(tfDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbNombre))
                    .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbApellidos))
                    .addComponent(tfApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbCiudad))
                    .addComponent(tfCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbProvincia))
                    .addComponent(cbProvincias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbDireccion))
                    .addComponent(tfDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbTelefono))
                    .addComponent(tfTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(lbUsuario))
                    .addComponent(jchbUsuario))
                .addGap(25, 25, 25))
        );

        jPanel7.add(jPanel9, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel7, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAceptarActionPerformed
        // TODO add your handling code here:
        altaEmpleado();
}//GEN-LAST:event_btAceptarActionPerformed

    private void btCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarActionPerformed
        // TODO add your handling code here:
        Cerrar();
}//GEN-LAST:event_btCancelarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
         Cerrar();
    }//GEN-LAST:event_formWindowClosing

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AltaEmpleado1 dialog = new AltaEmpleado1(new javax.swing.JFrame(), true);
                
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.persistence.EntityManager TiendaInformaticaPUEntityManager;
    private javax.swing.JButton btAceptar;
    private javax.swing.JButton btCancelar;
    private javax.swing.JComboBox cbProvincias;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox jchbUsuario;
    private javax.swing.JLabel lbApellidos;
    private javax.swing.JLabel lbCiudad;
    private javax.swing.JLabel lbDireccion;
    private javax.swing.JLabel lbDni;
    private javax.swing.JLabel lbNombre;
    private javax.swing.JLabel lbNumero;
    private javax.swing.JLabel lbProvincia;
    private javax.swing.JLabel lbTelefono;
    private javax.swing.JLabel lbTitulo;
    private javax.swing.JLabel lbUsuario;
    private java.util.List<Modelo.Provincias> provinciasList;
    private javax.persistence.Query provinciasQuery;
    private javax.swing.JTextField tfApellidos;
    private javax.swing.JTextField tfCiudad;
    private javax.swing.JTextField tfDireccion;
    private javax.swing.JTextField tfDni;
    private javax.swing.JTextField tfIdCliente;
    private javax.swing.JTextField tfNombre;
    private javax.swing.JTextField tfTelefono;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
