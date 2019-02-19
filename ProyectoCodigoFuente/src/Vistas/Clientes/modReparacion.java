/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * modReparacion.java
 *
 * Created on 03-feb-2012, 17:18:46
 */

package Vistas.Clientes;

import Controlador.ClientesJpaController;
import Modelo.Acciones;
import Modelo.Empleados;
import Modelo.Reparaciones;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author JuanPaulo
 */
public class modReparacion extends javax.swing.JDialog {

    private boolean aceptaModificar; // variable para saber si se pulsa el boton aceptar
    private Reparaciones reparacion; // reparacion que se va a editar, sera la recibida como parametro en el constructor
    private List<String> listaEstados= new ArrayList(); // lista que contendra los estados de reparacion
    ClientesJpaController controladorClientes; //varaiable que almacenara el controlador pasado como parametro al constructor
    private Query ConsultaListaEmpleados; // consulta para obtener la lista de empleados
    /** Creates new form modReparacion */
    public modReparacion(java.awt.Dialog parent, boolean modal, Reparaciones reparacion,ClientesJpaController controladorClientes) {
        super(parent, modal);
        this.reparacion=reparacion;
        aceptaModificar=false;
        listaEstados.add("reparacion");
        listaEstados.add("reparado");
        
        this.controladorClientes= controladorClientes;
        initComponents();
        // se crea la consulta con el entitymanager del controlador
        ConsultaListaEmpleados = this.controladorClientes.getEm().createQuery("SELECT e FROM Empleados e");
        empleadosList.clear(); // se vacia la lista de empleados (esta lista se creo con el editor grafico y esta en lazada con el combobox cbEmpleados)
        empleadosList.addAll(ConsultaListaEmpleados.getResultList());// se llena la lista con el resultado de la consulta
        mostrarReparacion();
        btEliminarAccion.setEnabled(false);
         /* Este listener es de modelo de la selección del jTable que muestra las acciones de la reparacion. Cuando cambie el modelo de seleccion se tratara el evento.
         * Si hay hay una fila selecciojada del jtAcciones se activara el boton eliminar, en caso contrario se desactiva.*/
         jtAcciones.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                        if(jtAcciones.getSelectedRow()==-1){
                            btEliminarAccion.setEnabled(false);
                        }else{
                            btEliminarAccion.setEnabled(true);
                        }
                }
        });

        Utilidades.Utilidades.centrar(this);
    }

    /**
     * metodo que mostrara la reparacion por pantalla
     */
    private void mostrarReparacion(){
        this.lbIdReparacion.setText(reparacion.getIdReparacion().toString());
        this.cbClientes.setSelectedItem(reparacion.getIdCliente());
        this.cbEmpleados.setSelectedItem(reparacion.getIdEmpleado());
        this.cbEstados.setSelectedItem(reparacion.getEstado());
        this.lbFechaEntrada.setText(reparacion.getFormatoFechaEntrada());
        this.lbFechaSalida.setText(reparacion.getFormatoFechaSalida());
        this.taDiagnostico.setText(reparacion.getDiagnostico());
        this.taObservaciones.setText(reparacion.getObservaciones());
        accionesList1.clear(); // se vacia la lista de acciones (esta lista se creo con el editor grafico y esta en lazada con la tabla jtAcciones)
        accionesList1.addAll(reparacion.getAccionesList()); // se llena la lista con el las acciones de la reparacion
        this.lbCompraSubtotalValor.setText(String.valueOf(reparacion.getTotalReparacion()) + " €");
        this.lbCompraTotalValor.setText(String.valueOf(reparacion.getTotalReparacionIva())+" €");
    }

    /**
     * metodo que realiza la modificacion en la reparacion pasada como parametro al constructor de esta clase
     */
    public void modificarReparacion(){
        reparacion.setIdEmpleado((Empleados) cbEmpleados.getSelectedItem());
        reparacion.setEstado((String) cbEstados.getSelectedItem());
        reparacion.setDiagnostico(taDiagnostico.getText());
        reparacion.setObservaciones(taObservaciones.getText());
    }

    /**
     * Metodo que añadira una accion a la lista de acciones
     */
    private void anadirAccion(){
        // se obtiene la accion seleccionada
        Acciones a = (Acciones) cbAcciones.getSelectedItem();
        boolean encontrado=false;
        // se comprobata si ya esta añadida esa accion a la lista
        for(int i=0;i<accionesList1.size() && !encontrado;i++){
            if(accionesList1.get(i).equals(a)){
                encontrado=true;
            }
        }
        if(!encontrado){
            // se añade la laccion a la lista de acciones y a la lista de acciones de la reparacion
            accionesList1.add(a);
            reparacion.getAccionesList().add(a);
            // se actualiza el total y subtotal de la compra
            this.lbCompraSubtotalValor.setText(String.valueOf(reparacion.getTotalReparacion()) + " €");
            this.lbCompraTotalValor.setText(String.valueOf(reparacion.getTotalReparacionIva())+" €");
        }
    }

    /**
     * Metodo para eliminar una accion de la lista de acciones
     */
    private void eliminarAccion(){
           List paraEliminar = new ArrayList<Acciones> (); // lista que contendra las acciones para eliminar
        int [] seleccionados = jtAcciones.getSelectedRows(); // se otiene un array de las acciones seleccionadas con los indices donde se encuentran las acciones en la lista de acciones
        // se recorre el array con las acciones selecciondas y se llena la lista de acciones para eliminar
        for(int i=0;i<seleccionados.length;i++){
            // se van obteniendo uno a uno las acciones seleccionadas y se añaden a la lista de acciones para eliminar
            Acciones ac = accionesList1.get(seleccionados[i]);
            paraEliminar.add(ac);

        }
        // se eliminana las acciones de la lista de acciones y de la lista de acciones de la reparacion
        accionesList1.removeAll(paraEliminar);
        reparacion.getAccionesList().removeAll(paraEliminar);
        // se actualiza el total y subtotal de la compra
        this.lbCompraSubtotalValor.setText(String.valueOf(reparacion.getTotalReparacion()) + " €");
        this.lbCompraTotalValor.setText(String.valueOf(reparacion.getTotalReparacionIva())+" €");
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

        ProyectoDAIPUEntityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("ProyectoDAIPU").createEntityManager();
        accionesQuery = java.beans.Beans.isDesignTime() ? null : ProyectoDAIPUEntityManager.createQuery("SELECT a FROM Acciones a");
        accionesList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(accionesQuery.getResultList());
        clientesQuery = java.beans.Beans.isDesignTime() ? null : ProyectoDAIPUEntityManager.createQuery("SELECT c FROM Clientes c");
        clientesList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(clientesQuery.getResultList());
        empleadosQuery = java.beans.Beans.isDesignTime() ? null : ProyectoDAIPUEntityManager.createQuery("SELECT e FROM Empleados e");
        empleadosList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(empleadosQuery.getResultList());
        accionesQuery1 = java.beans.Beans.isDesignTime() ? null : ProyectoDAIPUEntityManager.createQuery("SELECT a FROM Acciones a");
        accionesList1 = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(accionesQuery1.getResultList());
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lbTitulo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lbNumero = new javax.swing.JLabel();
        lbDni = new javax.swing.JLabel();
        lbNombre = new javax.swing.JLabel();
        lbApellidos = new javax.swing.JLabel();
        lbDireccion = new javax.swing.JLabel();
        lbApellidos1 = new javax.swing.JLabel();
        cbEmpleados = new javax.swing.JComboBox();
        cbClientes = new javax.swing.JComboBox();
        cbEstados = new javax.swing.JComboBox();
        lbApellidos2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        taObservaciones = new javax.swing.JTextArea();
        lbApellidos3 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        taDiagnostico = new javax.swing.JTextArea();
        lbIdReparacion = new javax.swing.JLabel();
        lbFechaEntrada = new javax.swing.JLabel();
        lbFechaSalida = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        cbAcciones = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        btAnadir = new javax.swing.JButton();
        btEliminarAccion = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtAcciones = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbCompraSubtotal = new javax.swing.JLabel();
        lbIvaCompra = new javax.swing.JLabel();
        lbCompraTotal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lbCompraSubtotalValor = new javax.swing.JLabel();
        lbCompraIvaValor = new javax.swing.JLabel();
        lbCompraTotalValor = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel6 = new javax.swing.JPanel();
        btAceptar = new javax.swing.JButton();
        btCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modificar Reparacion");
        setResizable(false);

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionReparaciones.png"))); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        lbTitulo.setFont(new java.awt.Font("Tahoma 18 Negrita 18 Negrita", 1, 18));
        lbTitulo.setText("Gestion de Reparaciones");
        lbTitulo.setName("lbTitulo"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(252, 252, 252)
                .addComponent(jLabel5)
                .addGap(5, 5, 5)
                .addComponent(lbTitulo))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel5))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lbTitulo))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setName("jPanel2"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Reparacion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(545, 271));

        lbNumero.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNumero.setText("Cliente:");
        lbNumero.setName("lbNumero"); // NOI18N

        lbDni.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDni.setText("Nº Reparacion:");
        lbDni.setName("lbDni"); // NOI18N

        lbNombre.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNombre.setText("Empleado:");
        lbNombre.setName("lbNombre"); // NOI18N

        lbApellidos.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbApellidos.setText("Fecha Entrada:");
        lbApellidos.setName("lbApellidos"); // NOI18N

        lbDireccion.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDireccion.setText("Estado");
        lbDireccion.setName("lbDireccion"); // NOI18N

        lbApellidos1.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbApellidos1.setText("Fecha Salida:");
        lbApellidos1.setName("lbApellidos1"); // NOI18N

        cbEmpleados.setName("cbEmpleados"); // NOI18N

        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, empleadosList, cbEmpleados);
        jComboBoxBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jComboBoxBinding);

        cbClientes.setEnabled(false);
        cbClientes.setName("cbClientes"); // NOI18N

        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, clientesList, cbClientes);
        jComboBoxBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jComboBoxBinding);

        cbEstados.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbEstados.setName("cbEstados"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${listaEstados}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, cbEstados);
        bindingGroup.addBinding(jComboBoxBinding);

        lbApellidos2.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbApellidos2.setText("Observaciones");
        lbApellidos2.setName("lbApellidos2"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        taObservaciones.setColumns(20);
        taObservaciones.setLineWrap(true);
        taObservaciones.setRows(5);
        taObservaciones.setName("taObservaciones"); // NOI18N
        jScrollPane4.setViewportView(taObservaciones);

        lbApellidos3.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbApellidos3.setText("Diagnostico");
        lbApellidos3.setName("lbApellidos3"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        taDiagnostico.setColumns(20);
        taDiagnostico.setLineWrap(true);
        taDiagnostico.setRows(5);
        taDiagnostico.setName("taDiagnostico"); // NOI18N
        jScrollPane5.setViewportView(taDiagnostico);

        lbIdReparacion.setForeground(new java.awt.Color(204, 0, 51));
        lbIdReparacion.setName("lbIdReparacion"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${reparacion.idReparacion}"), lbIdReparacion, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        lbFechaEntrada.setName("lbFechaEntrada"); // NOI18N

        lbFechaSalida.setName("lbFechaSalida"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbDni)
                        .addGap(6, 6, 6)
                        .addComponent(lbIdReparacion, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(lbNumero)
                        .addGap(4, 4, 4)
                        .addComponent(cbClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(84, 84, 84)
                        .addComponent(lbNombre)
                        .addGap(10, 10, 10)
                        .addComponent(cbEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbApellidos)
                        .addGap(6, 6, 6)
                        .addComponent(lbFechaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lbApellidos1)
                        .addGap(6, 6, 6)
                        .addComponent(lbFechaSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81)
                        .addComponent(lbDireccion)
                        .addGap(4, 4, 4)
                        .addComponent(cbEstados, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbApellidos2)
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(100, 100, 100)
                        .addComponent(lbApellidos3)
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbDni))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(lbIdReparacion, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbNumero))
                    .addComponent(cbClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbNombre))
                    .addComponent(cbEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbApellidos))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(lbFechaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbApellidos1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(lbFechaSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbDireccion))
                    .addComponent(cbEstados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(lbApellidos2))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(lbApellidos3))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Añadir Accion"));
        jPanel9.setName("jPanel9"); // NOI18N

        cbAcciones.setName("cbAcciones"); // NOI18N

        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, accionesList, cbAcciones);
        bindingGroup.addBinding(jComboBoxBinding);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Accion.png"))); // NOI18N
        jLabel1.setText("Accion:");
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(97, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        btAnadir.setText("Añadir");
        btAnadir.setName("btAnadir"); // NOI18N
        btAnadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAnadirActionPerformed(evt);
            }
        });

        btEliminarAccion.setText("Eliminar");
        btEliminarAccion.setName("btEliminarAccion"); // NOI18N
        btEliminarAccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEliminarAccionActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Detalles"), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jtAcciones.setName("jtAcciones"); // NOI18N

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, accionesList1, jtAcciones);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${idAccion}"));
        columnBinding.setColumnName("Id Accion");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nombre}"));
        columnBinding.setColumnName("Accion");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${precio} €"));
        columnBinding.setColumnName("Precio €");
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane2.setViewportView(jtAcciones);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalles Accion"));
        jPanel5.setName("jPanel5"); // NOI18N

        jLabel2.setText("Id.Accion:");
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText("Precio:");
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText("Descripcion:");
        jLabel4.setName("jLabel4"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTextArea3.setColumns(20);
        jTextArea3.setEditable(false);
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(5);
        jTextArea3.setName("jTextArea3"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbAcciones, org.jdesktop.beansbinding.ELProperty.create("${selectedItem.descripcion}"), jTextArea3, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(jTextArea3);

        jLabel8.setName("jLabel8"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbAcciones, org.jdesktop.beansbinding.ELProperty.create("${selectedItem.idAccion}"), jLabel8, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel9.setName("jLabel9"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbAcciones, org.jdesktop.beansbinding.ELProperty.create("${selectedItem.precio} €"), jLabel9, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE))
                .addContainerGap())
        );

        lbCompraSubtotal.setFont(new java.awt.Font("Tahoma", 1, 11));
        lbCompraSubtotal.setText("Subtotal:");
        lbCompraSubtotal.setName("lbCompraSubtotal"); // NOI18N

        lbIvaCompra.setFont(new java.awt.Font("Tahoma", 1, 11));
        lbIvaCompra.setText("IVA");
        lbIvaCompra.setName("lbIvaCompra"); // NOI18N

        lbCompraTotal.setFont(new java.awt.Font("Tahoma", 1, 11));
        lbCompraTotal.setText("Total:");
        lbCompraTotal.setName("lbCompraTotal"); // NOI18N

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/total1.png"))); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        lbCompraSubtotalValor.setName("lbCompraSubtotalValor"); // NOI18N

        lbCompraIvaValor.setText("16%");
        lbCompraIvaValor.setName("lbCompraIvaValor"); // NOI18N

        lbCompraTotalValor.setName("lbCompraTotalValor"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btAnadir)
                        .addGap(18, 18, 18)
                        .addComponent(btEliminarAccion))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbCompraSubtotal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                            .addComponent(lbIvaCompra, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbCompraTotal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbCompraTotalValor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                            .addComponent(lbCompraIvaValor, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbCompraSubtotalValor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
                        .addGap(45, 45, 45))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                        .addGap(7, 7, 7)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lbCompraSubtotal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbIvaCompra)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbCompraTotal))
                            .addComponent(jLabel7)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lbCompraSubtotalValor, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbCompraIvaValor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbCompraTotalValor, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btAnadir)
                            .addComponent(btEliminarAccion))
                        .addGap(8, 8, 8)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jSeparator1.setName("jSeparator1"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel6.setName("jPanel6"); // NOI18N

        btAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Aceptar.png"))); // NOI18N
        btAceptar.setText("Aceptar");
        btAceptar.setName("btAceptar"); // NOI18N
        btAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAceptarActionPerformed(evt);
            }
        });
        jPanel6.add(btAceptar);

        btCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Cancelar.png"))); // NOI18N
        btCancelar.setText("Cancelar");
        btCancelar.setName("btCancelar"); // NOI18N
        btCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelarActionPerformed(evt);
            }
        });
        jPanel6.add(btCancelar);

        getContentPane().add(jPanel6, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btAnadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAnadirActionPerformed
        // TODO add your handling code here:
        anadirAccion();
}//GEN-LAST:event_btAnadirActionPerformed

    private void btEliminarAccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEliminarAccionActionPerformed
        // TODO add your handling code here:
        eliminarAccion();
}//GEN-LAST:event_btEliminarAccionActionPerformed

    private void btAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAceptarActionPerformed
        // TODO add your handling code here:
        //alta();
        setAceptaModificar(true);
        //modificarReparacion();
        this.setVisible(false);
}//GEN-LAST:event_btAceptarActionPerformed

    private void btCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarActionPerformed
        // TODO add your handling code here:+
        //Cerrar();
        setAceptaModificar(false);
        this.setVisible(false);
}//GEN-LAST:event_btCancelarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.persistence.EntityManager ProyectoDAIPUEntityManager;
    private java.util.List<Modelo.Acciones> accionesList;
    private java.util.List<Modelo.Acciones> accionesList1;
    private javax.persistence.Query accionesQuery;
    private javax.persistence.Query accionesQuery1;
    private javax.swing.JButton btAceptar;
    private javax.swing.JButton btAnadir;
    private javax.swing.JButton btCancelar;
    private javax.swing.JButton btEliminarAccion;
    private javax.swing.JComboBox cbAcciones;
    private javax.swing.JComboBox cbClientes;
    private javax.swing.JComboBox cbEmpleados;
    private javax.swing.JComboBox cbEstados;
    private java.util.List<Modelo.Clientes> clientesList;
    private javax.persistence.Query clientesQuery;
    private java.util.List<Modelo.Empleados> empleadosList;
    private javax.persistence.Query empleadosQuery;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTable jtAcciones;
    private javax.swing.JLabel lbApellidos;
    private javax.swing.JLabel lbApellidos1;
    private javax.swing.JLabel lbApellidos2;
    private javax.swing.JLabel lbApellidos3;
    private javax.swing.JLabel lbCompraIvaValor;
    private javax.swing.JLabel lbCompraSubtotal;
    private javax.swing.JLabel lbCompraSubtotalValor;
    private javax.swing.JLabel lbCompraTotal;
    private javax.swing.JLabel lbCompraTotalValor;
    private javax.swing.JLabel lbDireccion;
    private javax.swing.JLabel lbDni;
    private javax.swing.JLabel lbFechaEntrada;
    private javax.swing.JLabel lbFechaSalida;
    private javax.swing.JLabel lbIdReparacion;
    private javax.swing.JLabel lbIvaCompra;
    private javax.swing.JLabel lbNombre;
    private javax.swing.JLabel lbNumero;
    private javax.swing.JLabel lbTitulo;
    private javax.swing.JTextArea taDiagnostico;
    private javax.swing.JTextArea taObservaciones;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the reparacion
     */
    public Reparaciones getReparacion() {
        return reparacion;
    }

    /**
     * @param reparacion the reparacion to set
     */
    public void setReparacion(Reparaciones reparacion) {
        this.reparacion = reparacion;
    }

    /**
     * @return the listaEstados
     */
    public List<String> getListaEstados() {
        return listaEstados;
    }

    /**
     * @param listaEstados the listaEstados to set
     */
    public void setListaEstados(List<String> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * @return the aceptaModificar
     */
    public boolean isAceptaModificar() {
        return aceptaModificar;
    }

    /**
     * @param aceptaModificar the aceptaModificar to set
     */
    public void setAceptaModificar(boolean aceptaModificar) {
        this.aceptaModificar = aceptaModificar;
    }

}
