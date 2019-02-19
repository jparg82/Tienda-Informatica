/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AltaCliente.java
 *
 * Created on 22-dic-2011, 21:19:43
 */

package Vistas.Clientes;

import Modelo.Clientes;
import Modelo.Provincias;
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
public class AltaCliente1 extends javax.swing.JDialog {

    /** Creates new form AltaCliente */
    private static boolean necesitaGuardar=false; // variable para saber si se han hecho cambios en el cliente
    private Clientes cliente;// cliente que se va a dar de alta y que sera el pasado como parametro al constructor
    EntityManager em;// varaiable que almacenara el entitymanager pasado como parametro al constructor
    private boolean acaptaAlta;// variable que se utilizara para saber si se ha pulsado el boton acaptar o cancelar

    private Query Consulta; /* variable que tendra el resultado de la consulta "SELECT c FROM Clientes c WHERE c.dniCliente = :dniCliente"
     y se utilizara para comprobar si el cliente que se va a insertar no este en la BD*/
    private String jpql = "SELECT c FROM Clientes c WHERE c.dniCliente = :dniCliente" ;
    public AltaCliente1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    /**
     * Consdtructor que recibe como parametro un cliente y el entityManager de la clase gestionCliente
     * @param parent
     * @param modal
     * @param cliente
     * @param em
     */
     public AltaCliente1(java.awt.Dialog parent, boolean modal, Clientes cliente, EntityManager em) {
        super(parent, modal);

        this.cliente=cliente;
        initComponents();
        Utilidades.Utilidades.centrar(this);
        this.em=em;
        /* Se crea la consulta a travez del metodo createquery() del entity manager, el metodo recibe como parametro
         la consulta */
        Consulta = this.em.createQuery(jpql);
        // Se crea una variable restringirTexfField para restringir los caracteres del jTextField que contendra el dni
        restringirTexfField restringirTFDni = new restringirTexfField(tfDni);
        restringirTFDni.setLongitud(9);
        tfDni.addKeyListener(restringirTFDni);
        // Se crea una variable restringirTexfField para restringir los caracteres del jTextField que contendra el telefono
        restringirTexfField restringirTFtfno = new restringirTexfField(tfTelefono);
        restringirTFtfno.setLongitud(9);
        tfTelefono.addKeyListener(restringirTFtfno);
        //este evento se ejecutara cuando halla algun cambio en el objeto cliente y modificara el valor de la varieble
       //necesitaGuardar a true
       cliente.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setNecesitaGuardar(true);
            }
        });

    }

     /**
     * Metodo para dar de alta un cliente, tambien se encarga de comprobar que los campos obligatorios sean correctos
     */
    private void altaCliente(){
        String dni = tfDni.getText();
        String cp = this.tfCp.getText();
        boolean dnicorrecto;
        boolean cpcorrecto;
        /* Se obtiene el resultado de comprobar si el dni y el cp son correctos*/
        dnicorrecto=Utilidades.UtilidadesComprobar.comprobarNif(dni);
        cpcorrecto = Utilidades.UtilidadesComprobar.comprobarCodigoPostal(cp);
        /* Se comprueba que los compos obligatorios no esten vacios*/
        if(this.tfDni.getText().trim().equals("") || this.tfCp.getText().trim().equals("")|| this.tfNombre.getText().trim().equals("") ||
           this.tfApellidos.getText().trim().equals("") || this.tfTelefono.getText().trim().equals("")){
             JOptionPane.showMessageDialog(this, "Los campos con * son obligatorios", "Atencion", (int) CENTER_ALIGNMENT);
        }else{
             //Si los campos obligatorios estan correctos se comprueban que el campo
             // codigo postal sea correcto
            if(!cpcorrecto){
                    JOptionPane.showMessageDialog(this, "El Codigo postal deben ser 5 digitos", "Atencion", (int) CENTER_ALIGNMENT);
            }else{
                /*Si los campos obligatorios y el campos cp son correctos se comprueba que el clienteAux que se
                va a insertar en la BD ya exista*/
                Consulta.setParameter("dniCliente", dni);
               
                Clientes clienteAux=null;
                try{
                    // si el resultado de la consulta devuelve null es porque el clienteAux no existe en la BD                  
                    clienteAux=(Clientes) Consulta.getSingleResult();
                }catch(javax.persistence.NoResultException e){
                    System.out.println("prueba");
                }

                try{
                    int CodigoPostal = Integer.parseInt(tfCp.getText());
                    /*si el clienteAux no existe se inserta en la BD si no se avisa de que ya existe,*/
                    if(clienteAux!=null){
                        JOptionPane.showMessageDialog(this, "El cliente con DNI : "+dni+" ya existe", "Atencion", (int) CENTER_ALIGNMENT);
                    }else{
                        
                        cliente.setDniCliente(tfDni.getText());
                        cliente.setNombre(tfNombre.getText());
                        cliente.setApellidos(tfApellidos.getText());
                        cliente.setDireccion(tfDireccion.getText());
                        cliente.setCp(CodigoPostal);
                        cliente.setTelefono(tfTelefono.getText());
                        cliente.setMail(tfEmail.getText());
                        cliente.setCodProvincia((Provincias) this.jcbProvincia.getSelectedItem());
                        cliente.setCiudad(tfCiudad.getText());
                        acaptaAlta=true;
                        this.setVisible(false);
                       }

                    }catch(NumberFormatException ex){
                       JOptionPane.showMessageDialog(this, "El codigo postal debe ser un valor numerico", "Atencion", (int) CENTER_ALIGNMENT);
                    }
            }
        }

    }
    
    /**
     * Se encargara de cerrar la ventana de alta
     */
    private void Cerrar(){
        int opcion= JOptionPane.showConfirmDialog(this, "Desea cancelar el alta", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);

        if(opcion==JOptionPane.YES_OPTION){
           this.setVisible(false);
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
     * @return the clienteAux
     */
    public Clientes getCliente() {
        return cliente;
    }

    /**
     * @param clienteAux the clienteAux to set
     */
    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
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

        entityManager1 = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("ProyectoDAIPU").createEntityManager();
        queryProvincias = java.beans.Beans.isDesignTime() ? null : entityManager1.createQuery("SELECT p FROM Provincias p");
        listaProvincias = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(queryProvincias.getResultList());
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
        jPanel10 = new javax.swing.JPanel();
        lbNumero = new javax.swing.JLabel();
        lbDni = new javax.swing.JLabel();
        lbNombre = new javax.swing.JLabel();
        lbApellidos = new javax.swing.JLabel();
        lbCiudad = new javax.swing.JLabel();
        lbProvincia = new javax.swing.JLabel();
        lbCp = new javax.swing.JLabel();
        lbTelefono = new javax.swing.JLabel();
        lbEmail = new javax.swing.JLabel();
        tfIdCliente = new javax.swing.JTextField();
        tfDni = new javax.swing.JTextField();
        tfNombre = new javax.swing.JTextField();
        tfApellidos = new javax.swing.JTextField();
        tfCiudad = new javax.swing.JTextField();
        jcbProvincia = new javax.swing.JComboBox();
        tfCp = new javax.swing.JTextField();
        tfTelefono = new javax.swing.JTextField();
        tfEmail = new javax.swing.JTextField();
        lbDireccion = new javax.swing.JLabel();
        tfDireccion = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Nuevo Cliente");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(388, 408));
        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionClientes.png"))); // NOI18N

        lbTitulo.setFont(new java.awt.Font("Tahoma", 1, 18));
        lbTitulo.setText("Nuevo Cliente");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(210, 210, 210)
                .addComponent(jLabel2)
                .addGap(5, 5, 5)
                .addComponent(lbTitulo))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel2))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lbTitulo))
        );

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 8)); // NOI18N
        jLabel1.setText("Los campos con * son obligatorios");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(451, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));
        jPanel8.add(jSeparator1);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente"));

        lbNumero.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNumero.setText("Numero de Cliente:");

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

        lbCp.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbCp.setText("*CP:");

        lbTelefono.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbTelefono.setText("*Telefono:");

        lbEmail.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbEmail.setText("Email:");

        tfIdCliente.setEditable(false);

        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listaProvincias, jcbProvincia);
        bindingGroup.addBinding(jComboBoxBinding);

        lbDireccion.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDireccion.setText("Direccion:");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(lbDireccion)
                        .addGap(4, 4, 4)
                        .addComponent(tfDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(lbCiudad)
                        .addGap(4, 4, 4)
                        .addComponent(tfCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(lbProvincia)
                        .addGap(4, 4, 4)
                        .addComponent(jcbProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(lbCp)
                        .addGap(10, 10, 10)
                        .addComponent(tfCp, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(lbNumero)
                        .addGap(4, 4, 4)
                        .addComponent(tfIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lbDni)
                        .addGap(4, 4, 4)
                        .addComponent(tfDni, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(lbNombre)
                        .addGap(4, 4, 4)
                        .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(lbApellidos)
                        .addGap(4, 4, 4)
                        .addComponent(tfApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(lbTelefono)
                        .addGap(4, 4, 4)
                        .addComponent(tfTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(lbEmail)
                        .addGap(4, 4, 4)
                        .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(44, 44, 44))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbNumero)
                    .addComponent(tfIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDni)
                    .addComponent(tfDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbNombre))
                    .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbApellidos))
                    .addComponent(tfApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbCiudad))
                    .addComponent(tfCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbProvincia))
                    .addComponent(jcbProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(lbCp))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(tfCp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(33, 33, 33)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbDireccion))
                    .addComponent(tfDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbTelefono))
                    .addComponent(tfTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbEmail))
                    .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel7, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAceptarActionPerformed
        // TODO add your handling code here:
        altaCliente();

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
                AltaCliente1 dialog = new AltaCliente1(new javax.swing.JFrame(), true);

                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAceptar;
    private javax.swing.JButton btCancelar;
    private javax.persistence.EntityManager entityManager1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox jcbProvincia;
    private javax.swing.JLabel lbApellidos;
    private javax.swing.JLabel lbCiudad;
    private javax.swing.JLabel lbCp;
    private javax.swing.JLabel lbDireccion;
    private javax.swing.JLabel lbDni;
    private javax.swing.JLabel lbEmail;
    private javax.swing.JLabel lbNombre;
    private javax.swing.JLabel lbNumero;
    private javax.swing.JLabel lbProvincia;
    private javax.swing.JLabel lbTelefono;
    private javax.swing.JLabel lbTitulo;
    private java.util.List listaProvincias;
    private javax.persistence.Query queryProvincias;
    private javax.swing.JTextField tfApellidos;
    private javax.swing.JTextField tfCiudad;
    private javax.swing.JTextField tfCp;
    private javax.swing.JTextField tfDireccion;
    private javax.swing.JTextField tfDni;
    private javax.swing.JTextField tfEmail;
    private javax.swing.JTextField tfIdCliente;
    private javax.swing.JTextField tfNombre;
    private javax.swing.JTextField tfTelefono;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
