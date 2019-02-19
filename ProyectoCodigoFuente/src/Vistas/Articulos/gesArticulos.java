/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * gesArticulos.java
 *
 * Created on 06-feb-2012, 15:21:37
 */

package Vistas.Articulos;

import Controlador.ArticulosJpaController;
import Controlador.exceptions.NonexistentEntityException;
import Modelo.Articulos;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.PropertyStateEvent;

/**
 *
 * @author weejdu01
 */
public class gesArticulos extends javax.swing.JDialog {

    private ArticulosJpaController controladorArticulos; // consulta que se utilizara para obtener la lista de articulos
    private Query consultaArticulos;  //controlador que gestionara las transacciones y las operacion sdobre el entityManager
    private boolean necesitaGuardar; // variable que se utilizara para saber si hay cambios que guardar
    private boolean alta;  // variable para saber si se estadando de alta una accion

    /** Creates new form gesArticulos */
    public gesArticulos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        controladorArticulos = new ArticulosJpaController();
        // se crea la consulta con el entitymanager del controlador
        consultaArticulos = controladorArticulos.getEm().createQuery("SELECT a FROM Articulos a");
        articulosList.clear();  // se vacia la lista de articulos (esta lista se creo con el editor grafico y esta en lazada con el jList jListArticulos)
        articulosList.addAll(consultaArticulos.getResultList());  // se llena la lista con el resultado de la consulta
        setAlta(false);
        // se desactivan los campos de edicion, solo se activaran al modificar un articulo o la dar de alta uno nuevo
        desactivarCampos();
        // se desactivan los botones
        activarBotones();
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
        jListArticulos.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if(jListArticulos.getSelectedIndex()!=-1){

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

    private JDialog ventana(){
        return this;
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
        btGuardar.setEnabled(false);
        btActualizar.setEnabled(false);
        btSalir.setEnabled(false);
        jListArticulos.setEnabled(false);
    }

    /**
     * metodo que activara los botones del formularaio(activa algunos botones y desactiva otros)
     */
    private void activarBotones(){
        jListArticulos.setEnabled(true);
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
         if(jListArticulos.getSelectedIndex()!=-1){
             // se obtiene la accion seleccionada
             Articulos a = articulosList.get(jListArticulos.getSelectedIndex());
             desactivarCampos();
             activarBotones();
             // se borra la selecion
             jListArticulos.clearSelection();
             // si el id es null es porque se ha creado una accion nueva y no se ha guardado en la BD
             if(a.getIdarticulo()!=null){
                 // si el id es distindto de null se actualizan los datos con los de la BD y se selecciona la accion
                 controladorArticulos.getEm().refresh(a);
                 jListArticulos.setSelectedIndex(articulosList.indexOf(a));
             }else{
                   // si el id de la accion es null ,es porque todavia no se ha guardado en la BD, se elimina de la lista de acciones
                 articulosList.remove(a);
             }
         }
     }
     /**
      * metodo que cierra la ventana de gestion de articulos
      */
    private void Cerrar(){
        if(isNecesitaGuardar()){
            int opcion= JOptionPane.showConfirmDialog(this, "Hay cambios sin guardar ¿desea salir?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(opcion==JOptionPane.YES_OPTION){
                //em.getTransaction().rollback();
                controladorArticulos.Deshacer();
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
        jSpinner1.setEnabled(false);
        jListArticulos.setEnabled(true);
    }

    /**
     * Metodo que activara los campos de edicion
     */
    private void activarCampos(){
        tfNombre.setEditable(true);
        tfPrecio.setEditable(true);
        taDescripcion.setEditable(true);
        jSpinner1.setEnabled(true);
        jListArticulos.setEnabled(false);
    }

     /**
     * Metodo para eliminar un articulos del sistema
     */
    public void Eliminar() {
         int opcion;
        // se comprueba si hay mas de un articulo seleccionado para mostrar el mensaje adecuado
        if(jListArticulos.getSelectedIndices().length==1){
            opcion= JOptionPane.showConfirmDialog(this, "¿Desea eliminar el articulo seleccionado?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        }else{
            opcion= JOptionPane.showConfirmDialog(this, "¿Desea eliminar los articulos seleccionados?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        }

        if(opcion == JOptionPane.YES_OPTION){

            List paraEliminar = new ArrayList<Articulos> (); // lista que contendra los articulos para eliminar
            int [] seleccionados = jListArticulos.getSelectedIndices(); // se otiene un array de los articulos seleccioneado con los indices donde se encuentran los articulos en la lista de articulos
            // se recorre el array con los articulos selecciondos y se llena la lista de articulos para eliminar
            for(int i=0;i<seleccionados.length;i++){
                // se van obteniendo uno a uno los articulos seleccionados y se añaden a la lista de articulos para eliminar y tambien se elimina del entitymanager con el controlador
                Articulos a = articulosList.get(seleccionados[i]);
                try {
                    controladorArticulos.destroy(a.getIdarticulo());
                    paraEliminar.add(a);
                } catch (NonexistentEntityException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Atencion", WIDTH);
                }

            }
            // se eliminana los articulos de la lista
            activarBotones();
            articulosList.removeAll(paraEliminar);
            setNecesitaGuardar(true);
            btAlta.setEnabled(false);
            btBuscar.setEnabled(false);
        }
    }

    /**
     * metodo para actualizar la lista de articulos. Para ello se vacia la lista y vuelve a llenar con el resultado de la consulta consultaArticulos
     */
    public void Actualizar(){

        Thread hebra = new Thread(new Runnable() {
            public void run() {       
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    // se deshacen los cambios en el entitymanager con el controlador
                    desactivarBotones();
                    jProgressBar1.setValue(10);
                    controladorArticulos.Deshacer();
                    Thread.sleep(500);
                    // se vacia la lista de articulos
                    articulosList.clear();
                    // se vuelve a llenar la lista de articulos con el resultado de la consulta
                    articulosList.addAll(consultaArticulos.getResultList());
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
                    jListArticulos.setSelectedIndex(-1);
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } catch (InterruptedException ex) {
                    Logger.getLogger(gesArticulos.class.getName()).log(Level.SEVERE, null, ex);
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
        btActualizar.setEnabled(false);
        btCancelar.setEnabled(true);
    }

    /**
     * Metodo para buscar articulos
     */
    public void buscar(){
        buscarArticulo busqueda = new buscarArticulo(this,true);
        busqueda.setVisible(true);
        Query consultaBusqueda;
        int tipoBusqueda = busqueda.getSeleccionBusqueda();  // se obtiene el valor de la variable seleccion busqueda de la clase buscarArticulo para saber el tipo de busqueda
        List<Articulos> lista = new ArrayList(); // esta lista contendra el resultado de la consulta segun el tipo de busqueda

        switch (tipoBusqueda){

            // si el tipoBusqueda es 0 se listaran todos los articulos
            case 0:
                articulosList.clear(); // se vacia la lista de articulos
                articulosList.addAll(consultaArticulos.getResultList()); // se llena la lista de articulos
                busqueda.dispose();

                break;
            // si el tipoBusqueda es 1 se listaran todos los articulos con el id pasado como parametro en la consulta
            case 1:
                consultaBusqueda=controladorArticulos.getEm().createQuery("SELECT a FROM Articulos a WHERE a.idarticulo = :idarticulo");
                int id= busqueda.getId();
                consultaBusqueda.setParameter("idarticulo", id);
                lista.addAll(consultaBusqueda.getResultList());

                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No se encontro el articulo con id "+id, "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    articulosList.clear();
                    articulosList.addAll(lista);
                    jListArticulos.setSelectedIndex(0);
                }
                busqueda.dispose();
                break;
            case 2:
                break;
        }
    }

    /**
     * metodo que creara un articulo y activara los campos de edicion
     */
    private void nuevoArticulo(){
        Articulos articulo = new Articulos();
        articulo.setUnidades(0);
       
        activarCampos();
        // se añade la accion a la lista de acciones
        articulosList.add(articulo);
        jListArticulos.setSelectedIndex(articulosList.size()-1);
        jListArticulos.setEnabled(false);
        btAlta.setEnabled(false);
        btEliminar.setEnabled(false);
        btBuscar.setEnabled(false);
        btModificar.setEnabled(false);
        btCancelar.setEnabled(true);
        btActualizar.setEnabled(false);
        this.setAlta(true);
    }

    /**
     * metodo para guardar los cambios en la bd
     */
     public void Guardar() {
         
        try{
            // se cierra una transaccion del entity manager con un commit y se vuelve abrir una nueva con begin. todo ello lo hace el controlador
            // se comprueba si hay un articulo seleccionado, si la hay se obtendra el articulo seleccionado
            if(jListArticulos.getSelectedIndex()!=-1){
                if(tfNombre.getText().equals("") || tfPrecio.getText().equals("")){
                    JOptionPane.showMessageDialog(this, "Los campos con * son obligatorios", "Atencion", JOptionPane.WARNING_MESSAGE);
                }else{
                    Articulos ar = articulosList.get(jListArticulos.getSelectedIndex());
                    try{
                        float precio = Float.parseFloat(tfPrecio.getText());
                        // se comprueba si se ha dado de alta un articulo, si es verdadero se persiste en el entitymanager con el controlador
                        if(isAlta()){
                            controladorArticulos.create(ar);
                        }
                        // se guardan los cambios en la BD con el controlador
                        controladorArticulos.Guardar();
                        // se vuelve a comprobar si se hadado de alta el articulo, si es verdadero se vuelve a llenar la lista de articulos con el resultado
                        // de la consulta consultaArticulos para que los cambios se reflejen en la lista de articulos
                        if(isAlta()){
                            articulosList.clear();
                            articulosList.addAll(consultaArticulos.getResultList());
                        }
                        activarBotones();
                        jListArticulos.clearSelection();
                        jListArticulos.setSelectedIndex(articulosList.indexOf(ar));
                        desactivarCampos();
                        setAlta(false);
                        JOptionPane.showMessageDialog(ventana(), "Los cambios se guardaron correctamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    }catch(NumberFormatException nfe){
                        JOptionPane.showMessageDialog(ventana(), "El precio debe se un valor numerico", "Atencion", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }else{
                desactivarBotones();
                controladorArticulos.Guardar();
                desactivarCampos();
                activarBotones();
                setAlta(false);
                jListArticulos.clearSelection();
                JOptionPane.showMessageDialog(ventana(), "Los cambios se guardaron correctamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }

        /*En caso de una excepción (la llamada a commit puede lanzar una RollbackException si la transacción ha sido deshecha anteriormente)
        * “mezclamos” los datos de los clientes con los datos almacenados en la base de datos.*/
        //controladorEmpleados.getEm().getTransaction().begin();
        } catch(RollbackException e){
            controladorArticulos.getEm().getTransaction().begin();
            List<Articulos> lista = new ArrayList<Articulos>(articulosList.size());
            for(int i=0;i<articulosList.size();i++){
                lista.add(controladorArticulos.getEm().merge(articulosList.get(i)));
            }
            articulosList.clear();
            articulosList.addAll(lista);
            setAlta(false);
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
        articulosQuery = java.beans.Beans.isDesignTime() ? null : ProyectoDAIPUEntityManager.createQuery("SELECT a FROM Articulos a");
        articulosList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(articulosQuery.getResultList());
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lbTitulo = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListArticulos = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        jSpinner1 = new javax.swing.JSpinner();
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
        lbCiudad2 = new javax.swing.JLabel();
        btEliminar = new javax.swing.JButton();
        btModificar = new javax.swing.JButton();
        btCancelar = new javax.swing.JButton();
        btBuscar = new javax.swing.JButton();
        btGuardar = new javax.swing.JButton();
        btActualizar = new javax.swing.JButton();
        btAlta = new javax.swing.JButton();
        btSalir = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Gestion de articulos");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionArticulos.png"))); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        lbTitulo.setFont(new java.awt.Font("Tahoma 18 Negrita 18 Negrita", 1, 18));
        lbTitulo.setText("Gestion de Articulos");
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
                .addGap(292, 292, 292)
                .addComponent(jLabel3)
                .addGap(5, 5, 5)
                .addComponent(lbTitulo))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel3))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lbTitulo))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setName("jPanel2"); // NOI18N

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Articulos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 51, 255))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jListArticulos.setToolTipText("Lista de Articulos(muestra el numero de articulo)");
        jListArticulos.setName("jListArticulos"); // NOI18N

        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, articulosList, jListArticulos);
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create("${idarticulo}"));
        bindingGroup.addBinding(jListBinding);

        jScrollPane1.setViewportView(jListArticulos);

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Articulo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        jSpinner1.setName("jSpinner1"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListArticulos, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.unidades}"), jSpinner1, org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        lbNumero.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNumero.setText("Numero de Articulo:");
        lbNumero.setName("lbNumero"); // NOI18N

        lbNombre.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNombre.setText("*Nombre:");
        lbNombre.setName("lbNombre"); // NOI18N

        lbApellidos.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbApellidos.setText("Descripcion:");
        lbApellidos.setName("lbApellidos"); // NOI18N

        lbCiudad.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbCiudad.setText("*Precio:");
        lbCiudad.setName("lbCiudad"); // NOI18N

        tfNombre.setName("tfNombre"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListArticulos, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.nombre}"), tfNombre, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        tfPrecio.setName("tfPrecio"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListArticulos, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.precio}"), tfPrecio, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 8));
        jLabel1.setText("Los campos con * son obligatorios");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setForeground(new java.awt.Color(204, 0, 51));
        jLabel2.setName("jLabel2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListArticulos, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idarticulo}"), jLabel2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        taDescripcion.setColumns(20);
        taDescripcion.setLineWrap(true);
        taDescripcion.setRows(5);
        taDescripcion.setName("taDescripcion"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListArticulos, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.descripcion}"), taDescripcion, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(taDescripcion);

        lbCiudad1.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbCiudad1.setText("€");
        lbCiudad1.setName("lbCiudad1"); // NOI18N

        lbCiudad2.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbCiudad2.setText("Unidades:");
        lbCiudad2.setName("lbCiudad2"); // NOI18N

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
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                        .addGap(248, 248, 248))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbCiudad)
                        .addGap(4, 4, 4)
                        .addComponent(tfPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbCiudad1)
                        .addGap(65, 65, 65)
                        .addComponent(lbCiudad2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbNombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(lbApellidos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
                    .addComponent(lbNumero)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lbNombre)
                                .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lbApellidos))))
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbCiudad2)
                    .addComponent(tfPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCiudad)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCiudad1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );

        btEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/1329144248_delete.png"))); // NOI18N
        btEliminar.setMnemonic('e');
        btEliminar.setText("Eliminar");
        btEliminar.setToolTipText("Eliminar un articulo");
        btEliminar.setName("btEliminar"); // NOI18N
        btEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEliminarActionPerformed(evt);
            }
        });

        btModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/editar.png"))); // NOI18N
        btModificar.setMnemonic('m');
        btModificar.setText("Modificar");
        btModificar.setToolTipText("Modificar un articulo");
        btModificar.setName("btModificar"); // NOI18N
        btModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btModificarActionPerformed(evt);
            }
        });

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

        btAlta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/alta.png"))); // NOI18N
        btAlta.setMnemonic('n');
        btAlta.setText("Nuevo");
        btAlta.setToolTipText("Añadir un articulo");
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
        btSalir.setName("btSalir"); // NOI18N
        btSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSalirActionPerformed(evt);
            }
        });

        jSeparator1.setName("jSeparator1"); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName("jPanel4"); // NOI18N

        jProgressBar1.setName("jProgressBar1"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(801, Short.MAX_VALUE))
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 919, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                                .addComponent(btEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                                .addComponent(btAlta, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                                .addComponent(btCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .addComponent(btGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .addComponent(btSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(22, 22, 22))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(180, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addComponent(btSalir)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(29, 29, 29)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
                    .addContainerGap(103, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEliminarActionPerformed
        // TODO add your handling code here:
        Eliminar();
}//GEN-LAST:event_btEliminarActionPerformed

    private void btModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btModificarActionPerformed
        // TODO add your handling code here:

        modificar();
}//GEN-LAST:event_btModificarActionPerformed

    private void btBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBuscarActionPerformed
        // TODO add your handling code here:
        buscar();
}//GEN-LAST:event_btBuscarActionPerformed

    private void btGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGuardarActionPerformed
        // TODO add your handling code here:
        Guardar();
}//GEN-LAST:event_btGuardarActionPerformed

    private void btActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btActualizarActionPerformed
        // TODO add your handling code here:
        Actualizar();
}//GEN-LAST:event_btActualizarActionPerformed

    private void btAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAltaActionPerformed
        nuevoArticulo();
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
                gesArticulos dialog = new gesArticulos(new javax.swing.JFrame(), true);
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
    private java.util.List<Modelo.Articulos> articulosList;
    private javax.persistence.Query articulosQuery;
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
    private javax.swing.JList jListArticulos;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JLabel lbApellidos;
    private javax.swing.JLabel lbCiudad;
    private javax.swing.JLabel lbCiudad1;
    private javax.swing.JLabel lbCiudad2;
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
