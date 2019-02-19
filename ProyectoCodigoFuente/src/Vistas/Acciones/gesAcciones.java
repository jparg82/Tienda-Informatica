/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * gesAcciones.java
 *
 * Created on 06-feb-2012, 11:23:30
 */

package Vistas.Acciones;

import Controlador.AccionesJpaController;
import Controlador.exceptions.NonexistentEntityException;
import Modelo.Acciones;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.PropertyStateEvent;

/**
 * Clase que mostrara el formulario de gestion de acciones y desde el que se haran las operaciones CRUD
 * @author weejdu01
 */
public class gesAcciones extends javax.swing.JDialog {

    private Query consultaAcciones; // consulta que se utilizara para obtener la lista de acciones
    private AccionesJpaController controladorAcciones; // controlador que gestionara las transacciones y las operacion sdobre el entityManager
    private boolean necesitaGuardar; // variable que se utilizara para saber si hay cambios que guardar
    private boolean alta; // variable para saber si se estadando de alta una accion
    /** Creates new form gesAcciones */
    public gesAcciones(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        controladorAcciones = new AccionesJpaController();
        // se crea la consulta con el entitymanager del controlador
        consultaAcciones = controladorAcciones.getEm().createQuery("SELECT a FROM Acciones a");
        accionesList.clear(); // se vacia la lista de acciones (esta lista se creo con el editor grafico y esta en lazada con el jList JlistAcciones)
        accionesList.addAll(consultaAcciones.getResultList()); // se llena la lista con el resultado de la consulta
        setAlta(false);
        // se activan los botones
        activarBotones();
        // se desactivan los campos de edicion, solo se activaran al modificar una accion o la dar de alta una nueva
        desactivarCampos();

        /*Cada vez que haya un cambio en los datos del empleado seleccionado debe ponerse a true la variable necesitaGuardar
            * y activarse el boton de guardar(lo hace el metodo setNecesitaGuardar).
            * Para esto agregamos un objeto AbstractBindingListener (que deriva de BindingListener e implementa la funcionalidad básica de
            * notificación de enlaces) y redefimos su método targetChanged (llamado cuando hay un cambio en el bean objetivo del enlace)
            * utilizando el metodo setNecesitaGuardar
            */
            bindingGroup.addBindingListener(new AbstractBindingListener() {
            public void targetChanged(Binding binding, PropertyStateEvent event) {
                    setNecesitaGuardar(true);
            }
        });

        /* Este listener es de modelo de la selección de nuestra lista. Cuando cambie el modelo de seleccion se tratara el evento y se comprobara
         si hay alguna fila seleccionada de la lista o no. si hay alguna fila seleccionada se activaran los botones modificar y eliminar, en caso contrario
         se desactivaran*/
        jListAcciones.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if(jListAcciones.getSelectedIndex()!=-1){

                        btEliminar.setEnabled(true);
                        btModificar.setEnabled(true);
                    }else{
                        btEliminar.setEnabled(false);
                        btModificar.setEnabled(false);
                    }
                }
        });
        
        Utilidades.Utilidades.centrar(this);
    }

    /*
     * Metodo que desactivara los botones del formularaio
     */
    private void desactivarBotones(){
        btAlta.setEnabled(false);
        btEliminar.setEnabled(false);
        btModificar.setEnabled(false);
        btBuscar.setEnabled(false);
        btCancelar.setEnabled(false);
        btActualizar.setEnabled(false);
        btSalir.setEnabled(false);
        setNecesitaGuardar(false);
        jListAcciones.setEnabled(false);
    }

    /**
     * metodo que activara los botones del formularaio(activa algunos botones y desactiva otros)
     */
    private void activarBotones(){
        jListAcciones.setEnabled(true);
        btAlta.setEnabled(true);
        btSalir.setEnabled(true);
        btActualizar.setEnabled(true);
        btBuscar.setEnabled(true);
        btCancelar.setEnabled(false);
        btModificar.setEnabled(false);
        btEliminar.setEnabled(false);
        setNecesitaGuardar(false);
    }
    /**
    * Metodo para cancelar un alta o una modificacion
    */
    private void cancelar(){
        // se comprueba que si hay alguna accion seleccionada
         if(jListAcciones.getSelectedIndex()!=-1){
             // se obtiene la accion seleccionada
             Acciones a = accionesList.get(jListAcciones.getSelectedIndex());
             desactivarCampos();
             activarBotones();
             // se borra la selecion
             jListAcciones.clearSelection();
             // si el id es null es porque se ha creado una accion nueva y no se ha guardado en la BD
             if(a.getIdAccion()!=null){
                 // si el id es distindto de null se actualizan los datos con los de la BD y se selecciona la accion
                 controladorAcciones.getEm().refresh(a);
                 jListAcciones.setSelectedIndex(accionesList.indexOf(a));
             }else{
                   // si el id de la accion es null ,es porque todavia no se ha guardado en la BD, se elimina de la lista de acciones
                 accionesList.remove(a);
             }
         }
     }

    /**
     * Metodo para cerrar el formulario
     */
    private void Cerrar(){
        if(isNecesitaGuardar()){
            int opcion= JOptionPane.showConfirmDialog(this, "Hay cambios sin guardar ¿desea salir?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(opcion==JOptionPane.YES_OPTION){
                controladorAcciones.Deshacer();
                this.dispose();
            }
        }else{
            this.dispose();
        }
    }

    /**
     * metodo que desactivara los campos de edicion
     */
    private void desactivarCampos(){
        tfNombre.setEditable(false);
        tfPrecio.setEditable(false);
        taDescripcion.setEditable(false);
        jListAcciones.setEnabled(true);
    }

    /**
     * Metodo que activara los campos de edicion
     */
    private void activarCampos(){
        tfNombre.setEditable(true);
        tfPrecio.setEditable(true);
        taDescripcion.setEditable(true);
        jListAcciones.setEnabled(false);
    }

    /**
     * Metodo para buscar acciones
     */
    public void buscar(){
        buscarAccion busqueda = new buscarAccion(this,true);
        busqueda.setVisible(true);
        Query consultaBusqueda;
        int tipoBusqueda = busqueda.getSeleccionBusqueda(); // se obtiene el valor de la variable seleccion busqueda de la clase buscarAccion para saber el tipo de busqueda
        List<Acciones> lista = new ArrayList(); // esta lista contendra el resultado de la consulta segun el tipo de busqueda

        switch (tipoBusqueda){
            // si el tipoBusqueda es 0 se listaran todos las acciones
            case 0:
                accionesList.clear(); // se vacia la lista de acciones
                accionesList.addAll(consultaAcciones.getResultList()); // se llena la lista de acciones
                busqueda.dispose();
                break;
            // si el tipoBusqueda es 1 se listaran todos las acciones con el id pasado como parametro en la consulta
            case 1:
                consultaBusqueda=controladorAcciones.getEm().createQuery("SELECT a FROM Acciones a WHERE a.idAccion = :idAccion");
                int id= busqueda.getId();
                consultaBusqueda.setParameter("idAccion", id);
                lista.addAll(consultaBusqueda.getResultList());
                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No se encontro la accion con id "+id, "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    accionesList.clear();
                    accionesList.addAll(lista);
                    jListAcciones.setSelectedIndex(0);
                }
                busqueda.dispose();
                break;
            case 2:
                break;
        }
    }

      /**
     * Metodo para eliminar una accion del sistema
     */
    public void Eliminar() {
         int opcion;
        // se comprueba si hay mas de una accion seleccionada para mostrar el mensaje adecuado
        if(jListAcciones.getSelectedIndices().length==1){
            opcion= JOptionPane.showConfirmDialog(this, "¿Desea eliminar la accion seleccionado?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        }else{
            opcion= JOptionPane.showConfirmDialog(this, "¿Desea eliminar las acciones seleccionados?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        }

        if(opcion == JOptionPane.YES_OPTION){
            List paraEliminar = new ArrayList<Acciones> (); // lista que contendra las acciones para eliminar
            int [] seleccionados = jListAcciones.getSelectedIndices(); // se otiene un array de las aaciones seleccionadas con los indices donde se encuentran las acciones en la lista de acciones
            // se recorre el array con las acciones selecciondas y se llena la lista de acciones para eliminar
            for(int i=0;i<seleccionados.length;i++){
                // se van obteniendo uno a uno las acciones seleccionadas y se añaden a la lista de acciones para eliminar y tambien se elimina del entitymanager con el controlador
                Acciones a = accionesList.get(seleccionados[i]);
                try {
                    controladorAcciones.destroy(a.getIdAccion());
                    paraEliminar.add(a);
                } catch (NonexistentEntityException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Atencion", WIDTH);
                }
                 
            }
            activarBotones();
            // se eliminana las acciones de la lista
            accionesList.removeAll(paraEliminar);
            setNecesitaGuardar(true);
            btAlta.setEnabled(false);
            btBuscar.setEnabled(false);
        }
    }

    /**
     * metodo para actualizar la lista de acciones. Para ello se vacia la lista y vuelve a llenar con el resultado de la consulta consultaAcciones
     */
    public void Actualizar(){

        Thread hebra = new Thread(new Runnable() {
            public void run() {
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    // se deshacen los cambios en el entitymanager con el controlador
                    desactivarBotones();
                    jProgressBar1.setValue(10);
                    controladorAcciones.Deshacer();
                    Thread.sleep(500);
                    // se vacia la lista de acciones
                    accionesList.clear();
                    // se vuelve a llenar la lista de acciones con el resultado de la consulta
                    accionesList.addAll(consultaAcciones.getResultList());
                    jProgressBar1.setValue(50);
                    Thread.sleep(500);
                    desactivarCampos();
                    desactivarBotones();
                    jProgressBar1.setValue(100);
                    Thread.sleep(500);
                    setAlta(false);
                    Thread.sleep(500);
                    jProgressBar1.setValue(0);
                    activarBotones();
                    jListAcciones.setSelectedIndex(-1);
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } catch (InterruptedException ex) {

                }
            }
        });
        hebra.start();
    }

    /**
     * metodo que activara los campos de edicion para modificar la cciones y desactivara los botones
     */
    public void modificar(){
        activarCampos();
        btAlta.setEnabled(false);
        btEliminar.setEnabled(false);
        btBuscar.setEnabled(false);
        btModificar.setEnabled(false);
        btCancelar.setEnabled(true);
        btActualizar.setEnabled(false);
    }

    /**
     * metodo que creara una accion y activaralos campos de edicion
     */
    private void nuevaAccion(){
        Acciones accion = new Acciones();
        activarCampos();
        // se añade la accion a la lista de acciones
        accionesList.add(accion);
        jListAcciones.setSelectedIndex(accionesList.size()-1);
        jListAcciones.setEnabled(false);
        btAlta.setEnabled(false);
        btEliminar.setEnabled(false);
        btBuscar.setEnabled(false);
        btModificar.setEnabled(false);
        btCancelar.setEnabled(true);
        btActualizar.setEnabled(false);
        setNecesitaGuardar(true);
        this.setAlta(true);
    }

    /**
     * metodo para guardar los cambios en la bd
     */
    public void Guardar() {
        try{
            // se cierra una transaccion del entity manager con un commit y se vuelve abrir una nueva con begin. todo ello lo hace el controlador
            //controladorUsuarios.Guardar();
            // se comprueba si hay una accion seleccionada, si la hay se obtendra la accion seleccionada
            if(jListAcciones.getSelectedIndex()!=-1){
                if(tfNombre.getText().equals("") || tfPrecio.getText().equals("")){
                    JOptionPane.showMessageDialog(this, "Los campos con * son obligatorios", "Atencion", JOptionPane.WARNING_MESSAGE);
                }else{
                    Acciones ac = accionesList.get(jListAcciones.getSelectedIndex());
                    try{
                        float precio = Float.parseFloat(tfPrecio.getText());
                        // se comprueba si se ha dado de alta una accion, si es verdadero se persiste en el entitymanager con el controlador
                        if(this.isAlta()){
                            controladorAcciones.create(ac);
                        }
                        // se guardan los cambios en la BD con el controlador
                        controladorAcciones.Guardar();
                        jListAcciones.clearSelection();
                        // se vuelve a comprobar si se hadado de alta la accion, si es verdadero se vuelve a llenar la lista de acciones con el resultado
                        // de la consulta consultaAcciones para que los cambios se reflejen en la lista de acciones
                        if(this.isAlta()){
                            accionesList.clear();
                            accionesList.addAll(consultaAcciones.getResultList());
                        }
                        activarBotones();
                        jListAcciones.setSelectedIndex(accionesList.indexOf(ac));
                        desactivarCampos();
                        this.setAlta(false);
                        JOptionPane.showMessageDialog(this, "Los cambios se guardaron correctamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    }catch(NumberFormatException nfe){
                        JOptionPane.showMessageDialog(this, "El precio debe se un valor numerico", "Atencion", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }else{
                controladorAcciones.Guardar();
                activarBotones();
                jListAcciones.clearSelection();
                desactivarCampos();
                this.setAlta(false);
                 JOptionPane.showMessageDialog(this, "Los cambios se guardaron correctamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            } 
            
            /*En caso de una excepción (la llamada a commit puede lanzar una RollbackException si la transacción ha sido deshecha anteriormente)
             * “mezclamos” los datos de los clientes con los datos almacenados en la base de datos.*/
            //controladorEmpleados.getEm().getTransaction().begin();
        } catch(RollbackException e){
            controladorAcciones.getEm().getTransaction().begin();
            List<Acciones> lista = new ArrayList<Acciones>(accionesList.size());
            for(int i=0;i<accionesList.size();i++){
                lista.add(controladorAcciones.getEm().merge(accionesList.get(i)));
            }
            accionesList.clear();
            accionesList.addAll(lista);
            this.setAlta(false);
        }
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
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lbTitulo = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListAcciones = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        lbNumero = new javax.swing.JLabel();
        lbNombre = new javax.swing.JLabel();
        lbApellidos = new javax.swing.JLabel();
        lbCiudad = new javax.swing.JLabel();
        tfNombre = new javax.swing.JTextField();
        tfPrecio = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taDescripcion = new javax.swing.JTextArea();
        lbCiudad1 = new javax.swing.JLabel();
        btActualizar = new javax.swing.JButton();
        btGuardar = new javax.swing.JButton();
        btBuscar = new javax.swing.JButton();
        btModificar = new javax.swing.JButton();
        btEliminar = new javax.swing.JButton();
        btAlta = new javax.swing.JButton();
        btSalir = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btCancelar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Gestion Acciones");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionAccion.png"))); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        lbTitulo.setFont(new java.awt.Font("Tahoma 18 Negrita 18 Negrita", 1, 18));
        lbTitulo.setText("Gestion de Acciones");
        lbTitulo.setName("lbTitulo"); // NOI18N

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Logo1.png"))); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(287, 287, 287)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbTitulo)
                .addGap(339, 339, 339))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addComponent(lbTitulo)
                .addGap(21, 21, 21))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setName("jPanel2"); // NOI18N

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Acciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 51, 255))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jListAcciones.setToolTipText("Lista de acciones(muestra el numero de accion)");
        jListAcciones.setName("jListAcciones"); // NOI18N

        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, accionesList, jListAcciones);
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create("${idAccion}"));
        bindingGroup.addBinding(jListBinding);

        jScrollPane1.setViewportView(jListAcciones);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Accion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        lbNumero.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNumero.setText("Numero de Accion:");
        lbNumero.setName("lbNumero"); // NOI18N

        lbNombre.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNombre.setText("*Nombre:");
        lbNombre.setName("lbNombre"); // NOI18N

        lbApellidos.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbApellidos.setText("Descripcion:");
        lbApellidos.setName("lbApellidos"); // NOI18N

        lbCiudad.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbCiudad.setText("*Precio");
        lbCiudad.setName("lbCiudad"); // NOI18N

        tfNombre.setName("tfNombre"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListAcciones, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.nombre}"), tfNombre, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        tfPrecio.setName("tfPrecio"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListAcciones, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.precio}"), tfPrecio, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 8));
        jLabel1.setText("Los campos con * son obligatorios");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setForeground(new java.awt.Color(204, 0, 51));
        jLabel2.setName("jLabel2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListAcciones, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idAccion}"), jLabel2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        taDescripcion.setColumns(20);
        taDescripcion.setLineWrap(true);
        taDescripcion.setRows(5);
        taDescripcion.setName("taDescripcion"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListAcciones, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.descripcion}"), taDescripcion, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(taDescripcion);

        lbCiudad1.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbCiudad1.setText("€");
        lbCiudad1.setName("lbCiudad1"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbNumero)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addGap(248, 248, 248))
                    .addComponent(jLabel1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbCiudad)
                        .addGap(4, 4, 4)
                        .addComponent(tfPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbCiudad1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbNombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbApellidos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
                    .addComponent(lbNumero)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbNombre)
                            .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbApellidos)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbCiudad))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(5, 5, 5)
                            .addComponent(lbCiudad1))
                        .addComponent(tfPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(64, 64, 64)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        btActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Actualizar.png"))); // NOI18N
        btActualizar.setMnemonic('d');
        btActualizar.setText("Actualizar");
        btActualizar.setToolTipText("Actualizar los datos del sistema");
        btActualizar.setName("btActualizar"); // NOI18N
        btActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btActualizarActionPerformed(evt);
            }
        });

        btGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/guardar.png"))); // NOI18N
        btGuardar.setMnemonic('g');
        btGuardar.setText("Guardar");
        btGuardar.setToolTipText("Guardar los cambios en el sistema");
        btGuardar.setName("btGuardar"); // NOI18N
        btGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGuardarActionPerformed(evt);
            }
        });

        btBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/buscar.png"))); // NOI18N
        btBuscar.setMnemonic('b');
        btBuscar.setText("Buscar");
        btBuscar.setToolTipText("Buscar un articulo");
        btBuscar.setName("btBuscar"); // NOI18N
        btBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBuscarActionPerformed(evt);
            }
        });

        btModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/editar.png"))); // NOI18N
        btModificar.setMnemonic('m');
        btModificar.setText("Modificar");
        btModificar.setToolTipText("Modificar una accion");
        btModificar.setName("btModificar"); // NOI18N
        btModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btModificarActionPerformed(evt);
            }
        });

        btEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/1329144248_delete.png"))); // NOI18N
        btEliminar.setMnemonic('e');
        btEliminar.setText("Eliminar");
        btEliminar.setToolTipText("Eliminar una accion");
        btEliminar.setName("btEliminar"); // NOI18N
        btEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEliminarActionPerformed(evt);
            }
        });

        btAlta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/alta.png"))); // NOI18N
        btAlta.setMnemonic('n');
        btAlta.setText("Nuevo");
        btAlta.setToolTipText("Añadir una accion");
        btAlta.setName("btAlta"); // NOI18N
        btAlta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAltaActionPerformed(evt);
            }
        });

        btSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/salir2.png"))); // NOI18N
        btSalir.setMnemonic('s');
        btSalir.setText("Salir");
        btSalir.setToolTipText("Cerrar la ventana actual");
        btSalir.setMaximumSize(new java.awt.Dimension(105, 33));
        btSalir.setMinimumSize(new java.awt.Dimension(105, 33));
        btSalir.setName("btSalir"); // NOI18N
        btSalir.setPreferredSize(new java.awt.Dimension(105, 33));
        btSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSalirActionPerformed(evt);
            }
        });

        jSeparator1.setName("jSeparator1"); // NOI18N

        btCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/eliminar.png"))); // NOI18N
        btCancelar.setText("Cancelar");
        btCancelar.setToolTipText("Cancelar un alta o una modificacion");
        btCancelar.setMaximumSize(new java.awt.Dimension(93, 33));
        btCancelar.setMinimumSize(new java.awt.Dimension(93, 33));
        btCancelar.setName("btCancelar"); // NOI18N
        btCancelar.setPreferredSize(new java.awt.Dimension(93, 33));
        btCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelarActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName("jPanel4"); // NOI18N

        jProgressBar1.setName("jProgressBar1"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(787, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 892, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .addComponent(btEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .addComponent(btAlta, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .addComponent(btActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .addComponent(btGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .addComponent(btBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .addComponent(btSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(35, 35, 35))
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(187, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(btAlta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btEliminar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btModificar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btGuardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btActualizar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btSalir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap()))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btActualizarActionPerformed
        // TODO add your handling code here:
        Actualizar();
}//GEN-LAST:event_btActualizarActionPerformed

    private void btGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGuardarActionPerformed
        // TODO add your handling code here:
        Guardar();
}//GEN-LAST:event_btGuardarActionPerformed

    private void btBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBuscarActionPerformed
        // TODO add your handling code here:
        buscar();
}//GEN-LAST:event_btBuscarActionPerformed

    private void btModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btModificarActionPerformed
        // TODO add your handling code here:

        modificar();
}//GEN-LAST:event_btModificarActionPerformed

    private void btEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEliminarActionPerformed
        // TODO add your handling code here:
        Eliminar();
}//GEN-LAST:event_btEliminarActionPerformed

    private void btAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAltaActionPerformed
        nuevaAccion();
}//GEN-LAST:event_btAltaActionPerformed

    private void btSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSalirActionPerformed
        // TODO add your handling code here:
        Cerrar();
    }//GEN-LAST:event_btSalirActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        Cerrar();
    }//GEN-LAST:event_formWindowClosing

    private void btCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarActionPerformed
        // TODO add your handling code here:
        cancelar();
    }//GEN-LAST:event_btCancelarActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                gesAcciones dialog = new gesAcciones(new javax.swing.JFrame(), true);
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
    private javax.persistence.EntityManager ProyectoDAIPUEntityManager;
    private java.util.List<Acciones> accionesList;
    private javax.persistence.Query accionesQuery;
    private javax.swing.JButton btActualizar;
    private javax.swing.JButton btAlta;
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btCancelar;
    private javax.swing.JButton btEliminar;
    private javax.swing.JButton btGuardar;
    private javax.swing.JButton btModificar;
    private javax.swing.JButton btSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jListAcciones;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbApellidos;
    private javax.swing.JLabel lbCiudad;
    private javax.swing.JLabel lbCiudad1;
    private javax.swing.JLabel lbNombre;
    private javax.swing.JLabel lbNumero;
    private javax.swing.JLabel lbTitulo;
    private javax.swing.JTextArea taDescripcion;
    private javax.swing.JTextField tfNombre;
    private javax.swing.JTextField tfPrecio;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the necesitaGuardar
     */
    public boolean isNecesitaGuardar() {
        return necesitaGuardar;
    }

    /**
     * @param necesitaGuardar the necesitaGuardar to set
     */
    public void setNecesitaGuardar(boolean necesitaGuardar) {
        btGuardar.setEnabled(necesitaGuardar);
        this.necesitaGuardar = necesitaGuardar;
    }

    /**
     * @return the alta
     */
    public boolean isAlta() {
        return alta;
    }

    /**
     * @param alta the alta to set
     */
    public void setAlta(boolean alta) {
        this.alta = alta;
    }

}
