/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * gesClientes.java
 *
 * Created on 27-ene-2012, 18:19:52
 */

package Vistas.Clientes;

import Controlador.ClientesJpaController;
import Controlador.exceptions.IllegalOrphanException;
import Controlador.exceptions.NonexistentEntityException;
import Modelo.Clientes;
import Modelo.Empleados;
import Modelo.Provincias;
import Modelo.Reparaciones;
import Utilidades.restringirTexfField;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.PropertyStateEvent;
import proyectodai.Inicio;

/**
 *
 * @author JuanPaulo
 */
public class gesClientes extends javax.swing.JDialog {

    private boolean necesitaGuardar; // variable que se utilizara para saber si hay cambios que guardar
    private boolean modificar; // varaiable para saber si se esta modificando un cliente
    private Query ConsultaListaClientes; // consulta que se utilizara para obtener la lista de clientes
    private ClientesJpaController controladorClientes; //controlador que gestionara las transacciones y las operacion sdobre el entityManager
    private Query ConsultaListaProvincias; // consulta que se utilizara para obtener la lista de provincias
 
    /** Creates new form gesClientes */
    public gesClientes(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        desactivarCampos();
        controladorClientes = new ClientesJpaController();
        activarBotones();

         // Se crea una variable restringirTexfField para restringir los caracteres del jTextField que contendra el dni
        restringirTexfField restringirTFDni = new restringirTexfField(tfDni);
        restringirTFDni.setLongitud(9);
        tfDni.addKeyListener(restringirTFDni);
        // Se crea una variable restringirTexfField para restringir los caracteres del jTextField que contendra el telefono
        restringirTexfField restringirTFtfno = new restringirTexfField(tfTelefono);
        restringirTFtfno.setLongitud(9);
        tfTelefono.addKeyListener(restringirTFtfno);

         // se crea la consulta con el entitymanager del controlador
        ConsultaListaProvincias = controladorClientes.getEm().createQuery("SELECT p FROM Provincias p");
        provinciasList.clear();  // se vacia la lista de provincias (esta lista se creo con el editor grafico y esta en lazada con el combobox cbProvincias)
        provinciasList.addAll(ConsultaListaProvincias.getResultList());  // se llena la lista con el resultado de la consulta

         // se crea la consulta con el entitymanager del controlador
        ConsultaListaClientes = controladorClientes.getEm().createQuery("SELECT c FROM Clientes c ORDER BY c.nombre,c.apellidos");
        Utilidades.Utilidades.centrar(this);
        listaClientes.clear();  // se vacia la lista de clientes (esta lista se creo con el editor grafico y esta en lazada con el jList jListClientes)
        listaClientes.addAll(ConsultaListaClientes.getResultList());  // se llena la lista con el resultado de la consulta

        /* Este listener es de modelo de la selección de nuestra lista. Cuando cambie el modelo de seleccion se tratara el evento y se comprobara
         si hay alguna fila seleccionada de la lista o no. si hay alguna fila seleccionada se activaran los botones modificar y eliminar, en caso contrario
         se desactivaran*/
        jListClientes.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    
                    if(jListClientes.getSelectedIndex()!=-1 || jListClientes.getSelectedValue()!=null){
                       
                        btEliminar.setEnabled(true);
                        btModificar.setEnabled(true);
                    }else{
                      
                        desactivarCampos();
                        btEliminar.setEnabled(false);
                        btModificar.setEnabled(false);
                      
                    }
                }
        });

        /* Este listener es de modelo de la selección de nuestra lista. Cuando cambie el modelo de seleccion se tratara el evento y se comprobara
         si hay alguna fila seleccionada de la lista o no.*/
        jTable2.getSelectionModel().addListSelectionListener(new ListSelectionListener () {
            public void valueChanged(ListSelectionEvent e) {
                if(!btModificar.isEnabled()){
                    if(jTable2.getSelectedRow()!=-1){
                        if(listaClientes.get(jListClientes.getSelectedIndex()).getReparacionesList().get(jTable2.getSelectedRow()).getEstado().equals("facturado")){
                            btEditar.setEnabled(false);
                        }else{
                            btEditar.setEnabled(true);
                        }
                    }else{
                        btEditar.setEnabled(false);
                    }
                }
                 
            }
        });

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

        comprobarUsusario();
    }

    /**
     * metodo que activara los botones del formularaio(activa algunos botones y desactiva otros)
     */
    private void activarBotones() {
        jListClientes.setEnabled(true);
        btAlta.setEnabled(true);
        btSalir.setEnabled(true);
        btActualizar.setEnabled(true);
        btBuscar.setEnabled(true);
        btCancelar.setEnabled(false);
        btModificar.setEnabled(false);
        btEliminar.setEnabled(false);
        btEditar.setEnabled(false);
        setNecesitaGuardar(false);
        setModificar(false);
    }

    /*
     * Metodo que desactivara los botones del formularaio
     */
    private void desactivarBotones (){
        btAlta.setEnabled(false);
        btEliminar.setEnabled(false);
        btModificar.setEnabled(false);
        btCancelar.setEnabled(false);
        btBuscar.setEnabled(false);
        btGuardar.setEnabled(false);
        btActualizar.setEnabled(false);
        btSalir.setEnabled(false);
        jListClientes.setEnabled(false);

    }
    /**
     * Metodo que comprabara si la modificacion es correcta, devuelve true si esta todo correcto y false en caso contrario
     * @return
     */
    private boolean comprobarModificacion(){
        boolean correcto = false; // devolvera true si los datos del cliente son correctos y false en caso contrario
        ClientesJpaController controladorAuxiliar = new ClientesJpaController(); // se utiliza un controlador auxiliar para realiziar la busqueda del cliente con dni pasado como parametros a la consulta
        Query Consulta; /* variable que tendra el resultado de la consulta "SELECT c FROM Clientes c WHERE c.dniCliente = :dniCliente"
        y se utilizara para comprobar si el cliente que se va a insertar no este en la BD*/
        String jpql = "SELECT c FROM Clientes c WHERE c.dniCliente = :dniCliente" ;
        String dni = this.tfDni.getText();
        String cp = this.tfCp.getText();
        boolean dnicorrecto;
        boolean cpcorrecto;

        /* Se obtiene el resultado de comprobar si el dni y el cp son correctos*/
        dnicorrecto=Utilidades.UtilidadesComprobar.comprobarNif(dni);
        cpcorrecto = Utilidades.UtilidadesComprobar.comprobarCodigoPostal(cp);
        /*Se utiliza el otro entitymanager distinto del controlador para comprobar*/
        
        /* Se comprueba que los compos obligatorios no esten vacios*/
        if(this.tfDni.getText().trim().equals("") || this.tfCp.getText().trim().equals("")|| this.tfNombre.getText().trim().equals("") ||
           this.tfApellidos.getText().trim().equals("") || this.tfTelefono.getText().trim().equals("")){
             JOptionPane.showMessageDialog(this, "Los campos con * son obligatorios", "Atencion", (int) CENTER_ALIGNMENT);
             correcto=false;
        }else{
             //Si los campos obligatorios estan correctos se comprueban que el campo
             // codigo postal sea correcto
            if(!cpcorrecto){
                    JOptionPane.showMessageDialog(this, "El Codigo postal deben ser 5 digitos", "Atencion", (int) CENTER_ALIGNMENT);
                    correcto=false;
            }else{
                /*Si los campos obligatorios y el campos cp son correctos se comprueba que el clienteAux que se
                va a insertar en la BD ya exista*/
                // si el resultado de la consulta devuelve null es porque el clienteAux no existe en la BD
                try{
                    int CodigoPostal = Integer.parseInt(tfCp.getText());
                    Consulta = controladorAuxiliar.getEm().createQuery("SELECT c FROM Clientes c WHERE c.dniCliente = :dniCliente");
                    Clientes clienteAux=null;
                    Consulta.setParameter("dniCliente", dni);
                    clienteAux=(Clientes)Consulta.getSingleResult();
                    /*si el clienteAux no existe se inserta en la BD si no se avisa de que ya existe,*/
                    if(clienteAux!=null && !listaClientes.get(jListClientes.getSelectedIndex()).equals(clienteAux)){
                        JOptionPane.showMessageDialog(this, "El cliente con DNI : "+dni+" ya existe", "Atencion", (int) CENTER_ALIGNMENT);
                        correcto=false;
                    }else{
                        correcto=true;
                    }

                    }catch(NumberFormatException ex){
                       JOptionPane.showMessageDialog(this, "El codigo postal debe ser un valor numerico", "Atencion", (int) CENTER_ALIGNMENT);
                       correcto=false;
                    }
            }
        }
        controladorAuxiliar=null;
        return correcto;
    }
    /**
     * metodo que comprueba el tipo de ususrio(administrador o empleado), si es empleado desactivara el boto eliminar
     */
    private void comprobarUsusario(){
        Empleados emp = Inicio.getEmpleado();
        if(!emp.getUsuario().getTipo()){
            btEliminar.setVisible(false);
        }
    }
    /**
     * Cierra la ventana de gestion de clientes
     */
    private void Cerrar(){
        if(isNecesitaGuardar()){
            int opcion= JOptionPane.showConfirmDialog(this, "Hay cambios sin guardar ¿desea salir?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(opcion==JOptionPane.YES_OPTION){
                //em.getTransaction().rollback();
                controladorClientes.Deshacer();
                this.dispose();
            }
        }else{
            this.dispose();
        }
    }

    /**
     * Metodo para buscar clientes
     */
    public void buscar(){
        buscarCliente busqueda = new buscarCliente(this,true);
        busqueda.setVisible(true);
        Query consultaBusqueda;
        int tipoBusqueda = busqueda.getSeleccionBusqueda();  // se obtiene el valor de la variable seleccion busqueda de la clase buscarCliente para saber el tipo de busqueda
        List<Clientes> lista = new ArrayList(); // esta lista contendra el resultado de la consulta segun el tipo de busqueda
        switch (tipoBusqueda){

            // si el tipoBusqueda es 0 se listaran todos los clientes
            case 0:
                listaClientes.clear(); // se vacia la lista de clientes
                listaClientes.addAll(ConsultaListaClientes.getResultList()); // se llena la lista de clientes
                busqueda.dispose();
                break;
            // si el tipoBusqueda es 1 se listaran todos los clientes con el id pasado como parametro en la consulta
            case 1:
                consultaBusqueda=controladorClientes.getEm().createQuery("SELECT c FROM Clientes c WHERE c.idCliente = :idCliente");
                int id= busqueda.getId();
                consultaBusqueda.setParameter("idCliente", id);
                lista.addAll(consultaBusqueda.getResultList());
                
                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No se encontro el cliente con id "+id, "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    listaClientes.clear();
                    listaClientes.addAll(lista);
                    jListClientes.setSelectedIndex(0);
                }
                busqueda.dispose();
                break;
             // si el tipoBusqueda es 2 se listaran todos los clientes con el dni pasado como parametro en la consulta
            case 2:
                consultaBusqueda=controladorClientes.getEm().createQuery("SELECT c FROM Clientes c WHERE c.dniCliente = :dniCliente");
                String dni= busqueda.getDni();
                consultaBusqueda.setParameter("dniCliente", dni);
                lista.addAll(consultaBusqueda.getResultList());

                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No se encontro el cliente con DNI "+dni, "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    listaClientes.clear();
                    listaClientes.addAll(lista);
                    jListClientes.setSelectedIndex(0);
             
                }
                busqueda.dispose();
                break;
             // si el tipoBusqueda es 3 se listaran todos los clientes con el apellido pasado como parametro en la consulta
            case 3:
                String apellidos= busqueda.getApellidos();
                consultaBusqueda=controladorClientes.getEm().createQuery("SELECT c FROM Clientes c WHERE c.apellidos LIKE :apellidos");
                
                consultaBusqueda.setParameter("apellidos", "%"+apellidos+"%");
                lista.addAll(consultaBusqueda.getResultList());

                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No se encontro el cliente con apellidos "+apellidos, "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    listaClientes.clear();
                    listaClientes.addAll(lista);
                    jListClientes.setSelectedIndex(0);
                    
                }
                busqueda.dispose();
                break;
            case 4:
                break;         
        }
    }

    /**
     * Metodo que activara los campos de edicion
     */
    private void activarCampos(){

        tfDni.setEditable(true);
        tfNombre.setEditable(true);
        tfApellidos.setEditable(true);
        tfCiudad.setEditable(true);
        cbProvincias.setEnabled(true);
        tfCp.setEditable(true);
        tfDireccion.setEditable(true);
        tfTelefono.setEditable(true);
        tfEmail.setEditable(true);      
    }

    /**
     * metodo que desactivara los campos de edicion
     */
    private void desactivarCampos(){

        tfDni.setEditable(false);
        tfNombre.setEditable(false);
        tfApellidos.setEditable(false);
        tfCiudad.setEditable(false);
        cbProvincias.setEnabled(false);
        tfCp.setEditable(false);
        tfDireccion.setEditable(false);
        tfTelefono.setEditable(false);
        tfEmail.setEditable(false);
        
    }

    /**
     * metodo que dara de alta un clientes
     */
    private void altaCliente(){
        // se crea un cliente
        Clientes c = new Clientes();
        AltaCliente1 alta = new AltaCliente1(this,true, c,controladorClientes.getEm());// se abre una ventana de alta que recibe como parametro un c y el entityManager
        alta.setVisible(true);
        // si se pulso el boton aceptar en la ventana de alta se persiste la entidad c en el entitymanager y se añade a la lista
       
        if(alta.isAcaptaAlta()){
            controladorClientes.create(c);
            listaClientes.add(c);
            jListClientes.setSelectedIndex(listaClientes.indexOf(c)); // se selecciona la aacion en la tabla
            btEliminar.setEnabled(false);
            btAlta.setEnabled(false);
            btModificar.setEnabled(false);
            btBuscar.setEnabled(false);
            jListClientes.setEnabled(false);
            btActualizar.setEnabled(false);
            btCancelar.setEnabled(true);

        }
        setNecesitaGuardar(alta.isAcaptaAlta());
        alta.dispose();
    }

    /**
     * metodo para guardar los cambios en la BD, se encargara de cerrar la transaccion con un commit y abrir una nueva con begin
     */
     public void Guardar() {
            if(this.isModificar()){
                if(comprobarModificacion()){
                    try{
                        Clientes c; // este cliente se utilizara en caso de que haya un cliente seleccionado en la lista para que una vez que se actualiza la lista se pueda volver a seleccionar
                        controladorClientes.Guardar(); // se guardan los cambios en la BD con el controlador
                        activarBotones();
                        desactivarCampos();
                        if(jListClientes.getSelectedIndex()!=-1){
                            c = listaClientes.get(jListClientes.getSelectedIndex());
                            jListClientes.clearSelection();
                            jListClientes.setSelectedIndex(listaClientes.indexOf(c));
                        }else{
                            jListClientes.clearSelection();
                        }
                        
                        
                         JOptionPane.showMessageDialog(this, "Los cambios se guardaron correctamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    } catch(RollbackException e){
                        controladorClientes.getEm().getTransaction().begin();
                        List<Clientes> lista = new ArrayList<Clientes>(listaClientes.size());
                        for(int i=0;i<listaClientes.size();i++){
                            lista.add(controladorClientes.getEm().merge(listaClientes.get(i)));
                        }
                        listaClientes.clear();
                        listaClientes.addAll(lista);
                    }
                }
            }else{
                 try{
                        Clientes c; // este cliente se utilizara en caso de que haya un cliente seleccionado en la lista para que una vez que se actualiza la lista se pueda volver a seleccionar
                        controladorClientes.Guardar(); // se guardan los cambios en la BD con el controlador
                         activarBotones();
                        desactivarCampos();
                        if(jListClientes.getSelectedIndex()!=-1){
                            c = listaClientes.get(jListClientes.getSelectedIndex());
                            jListClientes.clearSelection();
                            jListClientes.setSelectedIndex(listaClientes.indexOf(c));
                        }else{
                            jListClientes.clearSelection();
                        }
                        //activarBotones();
                        //desactivarCampos();
                        
                         JOptionPane.showMessageDialog(this, "Los cambios se guardaron correctamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    /*En caso de una excepción (la llamada a commit puede lanzar una RollbackException si la transacción ha sido deshecha anteriormente)
                    * “mezclamos” los datos de los clientes con los datos almacenados en la base de datos.*/
                    //controladorClientes.getEm().getTransaction().begin();
                    } catch(RollbackException e){
                        controladorClientes.getEm().getTransaction().begin();
                        List<Clientes> lista = new ArrayList<Clientes>(listaClientes.size());
                        for(int i=0;i<listaClientes.size();i++){
                            lista.add(controladorClientes.getEm().merge(listaClientes.get(i)));
                        }
                        listaClientes.clear();
                        listaClientes.addAll(lista);
                    }
            }
    }

     /**
     * metodo que activara los campos de edicion para modificar la cciones y desactivara los botones
     */
     public void modificar(){
         //btEliminar.setEnabled(false);
         btAlta.setEnabled(false);
         btModificar.setEnabled(false);
         btBuscar.setEnabled(false);
         jListClientes.setEnabled(false);
         jTable2.clearSelection();
         jTable1.clearSelection();
         activarCampos();
         btCancelar.setEnabled(true);
         btActualizar.setEnabled(false);
         setModificar(true);
     }

     /**
      * Metodo para cancelar un alta o una modificacion
      */
     private void cancelar(){
         // se comprueba que si hay algun cliente seleccionado
         if(jListClientes.getSelectedIndex()!=-1){
             // se obtiene el cliente seleccionado
             Clientes c = listaClientes.get(jListClientes.getSelectedIndex());
             activarBotones();
             // se borra la selecion
             jListClientes.clearSelection();
             // si el id es null es porque se ha creado un cliente nuevo y no se ha guardado en la BD
             if(c.getIdCliente()!=null){
                 // si el id es distindto de null se actualizan los datos con los de la BD y se selecciona el cliente
                 controladorClientes.getEm().refresh(c);
                 jListClientes.setSelectedIndex(listaClientes.indexOf(c));
             }else{
                 // si el id del cliente es null ,es porque todavia no se ha guardado en la BD, se elimina del entitymanager y de la lista de clientes
                 listaClientes.remove(c);
                try {
                    controladorClientes.destroy(c);
                    //jListClientes.setSelectedIndex(-);
                } catch (IllegalOrphanException ex) {
                    Logger.getLogger(gesClientes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(gesClientes.class.getName()).log(Level.SEVERE, null, ex);
                }
             }
         }
     }

     /**
     * Metodo para eliminar un cliente del sistema
     */
    public void Eliminar() {
         int opcion;
        // se comprueba si hay mas de un articulo seleccionado para mostrar el mensaje adecuado
        if(jListClientes.getSelectedIndices().length==1){
            opcion= JOptionPane.showConfirmDialog(this, "¿Desea eliminar el cliente seleccionado?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        }else{
            opcion= JOptionPane.showConfirmDialog(this, "¿Desea eliminar los clientes seleccionados?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        }

        if(opcion == JOptionPane.YES_OPTION){

            List paraEliminar = new ArrayList<Clientes> (); // lista que contendra los clientes para eliminar
            int [] seleccionados = jListClientes.getSelectedIndices(); // se otiene un array de los clientes seleccioneado con los indices donde se encuentran los clientes en la lista de clientes
            // se recorre el array con los clientes selecciondos y se llena la lista de clientes para eliminar
            for(int i=0;i<seleccionados.length;i++){
                // se van obteniendo uno a uno los clientes seleccionados y se añaden a la lista de clientes para eliminar y tambien se elimina del entitymanager
                Clientes cli = listaClientes.get(seleccionados[i]);

                try {
                    controladorClientes.destroy(cli);
                    paraEliminar.add(cli);
                } catch (IllegalOrphanException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Atencion", WIDTH);

                } catch (NonexistentEntityException ex) {
                   
                }
            }
            if(!paraEliminar.isEmpty()){
                activarBotones();
                setNecesitaGuardar(true);
                btAlta.setEnabled(false);
                btBuscar.setEnabled(false);
                // se eliminana los clientes de la lista
                listaClientes.removeAll(paraEliminar);
            }
            
        }
    }

    /**
     * Metodo para deshacer los cambio de la BD ignorando todo cambio que se haya hecho. Para ello es necesariodeshacer los cambios de la transacción e iniciar una
     * nueva. Luego se “refresca” la lista de clientes que tenemos con los datos almacenados en la base de datos, recuperándolos nuevamente volviendo a ejecutar la consulta del objeto Query
     */
    public void Actualizar() {
        // se deshacen los cambios en el entitymanager con el controlador

        Thread hebra = new Thread(new Runnable() {
            public void run() {
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    desactivarBotones();
                    jProgressBar1.setValue(10);
                    Thread.sleep(500);
                    controladorClientes.Deshacer();

                    //btModificar.setEnabled(true);
                    //jListClientes.setEnabled(true);
                    listaClientes.clear(); // se vacia la lista de clientes
                    listaClientes.addAll(ConsultaListaClientes.getResultList()); // se llena la lista de clientes
                    jProgressBar1.setValue(50);
                    Thread.sleep(500);
                    provinciasList.clear();// se vacia la lista de provincias
                    provinciasList.addAll(ConsultaListaProvincias.getResultList()); // se vuelve a llenar la lista de provincias
                    jProgressBar1.setValue(100);
                    Thread.sleep(500);
                    jProgressBar1.setValue(0);
                    Thread.sleep(500);
                    activarBotones();
                    desactivarCampos();
                    jListClientes.clearSelection();
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } catch (InterruptedException ex) {
                    Logger.getLogger(gesClientes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        hebra.start();
    }

    /**
     * metodo para editar una reparacion de un cliente
     */
    private void editarReparacion() {
        // se obtiene la reparacion seleccionada
        Reparaciones r = listaClientes.get(jListClientes.getSelectedIndex()).getReparacionesList().get(jTable2.getSelectedRow());
        // se crea una ventana para modificar la reparacion que recibe como parametro la reparacion seleccionada y el controlador
        modReparacion mod = new modReparacion(this, true, r, controladorClientes);
        mod.setVisible(true);
        // si la variable AceptaModificar de la clase modReparacion es true se modificara la reparacion
        if (!mod.isAceptaModificar()) {
            setNecesitaGuardar(false);
        } else {
            mod.modificarReparacion();
            Clientes c = listaClientes.get(jListClientes.getSelectedIndex());
            jListClientes.clearSelection();
            jListClientes.setSelectedIndex(listaClientes.indexOf(c));
            int fila= c.getReparacionesList().indexOf(r);
            jTable2.setRowSelectionInterval(fila, fila);
            setNecesitaGuardar(true);
        }
        mod.dispose();
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        entityManager1 = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("ProyectoDAIPU").createEntityManager();
        clientesQuery = java.beans.Beans.isDesignTime() ? null : entityManager1.createQuery("SELECT c FROM Clientes c");
        listaClientes = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(clientesQuery.getResultList());
        provinciasQuery = java.beans.Beans.isDesignTime() ? null : entityManager1.createQuery("SELECT p FROM Provincias p");
        provinciasList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : provinciasQuery.getResultList();
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btEditar = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListClientes = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        lbNumero = new javax.swing.JLabel();
        lbDni = new javax.swing.JLabel();
        lbNombre = new javax.swing.JLabel();
        lbApellidos = new javax.swing.JLabel();
        lbCiudad = new javax.swing.JLabel();
        lbProvincia = new javax.swing.JLabel();
        lbCp = new javax.swing.JLabel();
        lbTelefono = new javax.swing.JLabel();
        lbEmail = new javax.swing.JLabel();
        tfDni = new javax.swing.JTextField();
        tfNombre = new javax.swing.JTextField();
        tfApellidos = new javax.swing.JTextField();
        tfCiudad = new javax.swing.JTextField();
        cbProvincias = new javax.swing.JComboBox();
        tfCp = new javax.swing.JTextField();
        tfTelefono = new javax.swing.JTextField();
        tfEmail = new javax.swing.JTextField();
        lbDireccion = new javax.swing.JLabel();
        tfDireccion = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        btAlta = new javax.swing.JButton();
        btEliminar = new javax.swing.JButton();
        btModificar = new javax.swing.JButton();
        btBuscar = new javax.swing.JButton();
        btGuardar = new javax.swing.JButton();
        btActualizar = new javax.swing.JButton();
        btSalir = new javax.swing.JButton();
        btCancelar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lbTitulo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Gestion de Clientes");
        setMinimumSize(new java.awt.Dimension(1013, 668));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 51, 255))); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${selectedElement.compraList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, eLProperty, jTable1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${idCliente}"));
        columnBinding.setColumnName("Numero Cliente");
        columnBinding.setColumnClass(Modelo.Clientes.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${numeroCompra}"));
        columnBinding.setColumnName("Numero Compra");
        columnBinding.setColumnClass(Integer.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${formatoFecha}"));
        columnBinding.setColumnName(" Fecha");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalCompraIva} €"));
        columnBinding.setColumnName("Total €");
        columnBinding.setEditable(false);
        jTableBinding.setSourceUnreadableValue(java.util.Collections.emptyList());
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), jTable1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Compras", new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Compras.png")), jPanel4); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${selectedElement.reparacionesList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, eLProperty, jTable2);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${idReparacion}"));
        columnBinding.setColumnName("Id Reparacion");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${idCliente}"));
        columnBinding.setColumnName("Id Cliente");
        columnBinding.setColumnClass(Modelo.Clientes.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${formatoFechaEntrada}"));
        columnBinding.setColumnName("Fecha Entrada");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${formatoFechaSalida}"));
        columnBinding.setColumnName("Fecha Salida");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${estado}"));
        columnBinding.setColumnName("Estado");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalReparacionIva} €"));
        columnBinding.setColumnName("Total");
        columnBinding.setEditable(false);
        jTableBinding.setSourceUnreadableValue(java.util.Collections.emptyList());
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane3.setViewportView(jTable2);

        btEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/editar.png"))); // NOI18N
        btEditar.setMnemonic('e');
        btEditar.setText("Editar");
        btEditar.setToolTipText("Editar reparacion del cliente");
        btEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEditarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                    .addComponent(btEditar))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btEditar))
        );

        jTabbedPane1.addTab("Reparaciones", new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Reparaciones.png")), jPanel5); // NOI18N

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Clientes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 51, 255))); // NOI18N

        jListClientes.setToolTipText("Lista de Clientes");

        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listaClientes, jListClientes);
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create("${nombre} ${apellidos}"));
        bindingGroup.addBinding(jListBinding);

        jScrollPane1.setViewportView(jListClientes);

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Clientes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.dniCliente}"), tfDni, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), tfDni, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.nombre}"), tfNombre, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), tfNombre, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.apellidos}"), tfApellidos, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), tfApellidos, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ciudad}"), tfCiudad, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), tfCiudad, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, provinciasList, cbProvincias);
        jComboBoxBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.codProvincia}"), cbProvincias, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cp}"), tfCp, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), tfCp, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.telefono}"), tfTelefono, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), tfTelefono, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.mail}"), tfEmail, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), tfEmail, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lbDireccion.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDireccion.setText("Direccion:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.direccion}"), tfDireccion, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), tfDireccion, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 8));
        jLabel1.setText("Los campos con * son obligatorios");

        jLabel4.setForeground(new java.awt.Color(204, 0, 51));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idCliente}"), jLabel4, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbTelefono)
                        .addGap(4, 4, 4)
                        .addComponent(tfTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(lbEmail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbDireccion)
                        .addGap(4, 4, 4)
                        .addComponent(tfDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbCiudad)
                        .addGap(4, 4, 4)
                        .addComponent(tfCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(lbProvincia)
                        .addGap(4, 4, 4)
                        .addComponent(cbProvincias, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(lbCp)
                        .addGap(10, 10, 10)
                        .addComponent(tfCp, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(lbNumero)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(lbNombre)
                                .addGap(4, 4, 4)
                                .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(lbApellidos)
                                .addGap(4, 4, 4)
                                .addComponent(tfApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lbDni)
                                .addGap(4, 4, 4)
                                .addComponent(tfDni, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbNumero)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDni)
                    .addComponent(tfDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbNombre))
                    .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbApellidos))
                    .addComponent(tfApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(lbCiudad))
                            .addComponent(tfCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(lbProvincia))
                            .addComponent(cbProvincias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(lbCp)))
                        .addGap(25, 25, 25)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(lbDireccion))
                            .addComponent(tfDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(lbTelefono))
                            .addComponent(tfTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lbEmail)
                                    .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(tfCp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );

        jPanel8.setLayout(new java.awt.GridBagLayout());

        btAlta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/alta.png"))); // NOI18N
        btAlta.setMnemonic('n');
        btAlta.setText("Nuevo");
        btAlta.setToolTipText("Añadir un cliente");
        btAlta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAltaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel8.add(btAlta, gridBagConstraints);

        btEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/1329144248_delete.png"))); // NOI18N
        btEliminar.setMnemonic('e');
        btEliminar.setText("Eliminar");
        btEliminar.setToolTipText("Eliminar un Cliente");
        btEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEliminarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel8.add(btEliminar, gridBagConstraints);

        btModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/editar.png"))); // NOI18N
        btModificar.setMnemonic('m');
        btModificar.setText("Modificar");
        btModificar.setToolTipText("Modificar un cliente");
        btModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btModificarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel8.add(btModificar, gridBagConstraints);

        btBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/buscar.png"))); // NOI18N
        btBuscar.setMnemonic('b');
        btBuscar.setText("Buscar");
        btBuscar.setToolTipText("Buscar un cliente");
        btBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBuscarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel8.add(btBuscar, gridBagConstraints);

        btGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/guardar.png"))); // NOI18N
        btGuardar.setMnemonic('g');
        btGuardar.setText("Guardar");
        btGuardar.setToolTipText("Guardar los cambios en el sistema");
        btGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGuardarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel8.add(btGuardar, gridBagConstraints);

        btActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Actualizar.png"))); // NOI18N
        btActualizar.setMnemonic('d');
        btActualizar.setText("Actualizar");
        btActualizar.setToolTipText("Actualizar los datos del sistema");
        btActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btActualizarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel8.add(btActualizar, gridBagConstraints);

        btSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/salir2.png"))); // NOI18N
        btSalir.setMnemonic('s');
        btSalir.setText("Salir");
        btSalir.setToolTipText("Cerrar la ventana actual");
        btSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSalirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 45;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel8.add(btSalir, gridBagConstraints);

        btCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/eliminar.png"))); // NOI18N
        btCancelar.setText("Cancelar");
        btCancelar.setToolTipText("Cancelar un alta o una modificacion");
        btCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel8.add(btCancelar, gridBagConstraints);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 991, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(7, 7, 7)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionClientes.png"))); // NOI18N

        lbTitulo.setFont(new java.awt.Font("Tahoma 18 Negrita 18 Negrita", 1, 18));
        lbTitulo.setText("Gestion de Clientes");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Logo1.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(323, 323, 323)
                .addComponent(jLabel2)
                .addGap(5, 5, 5)
                .addComponent(lbTitulo)
                .addGap(964, 964, 964))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel2))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lbTitulo))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanel7.add(jProgressBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1029, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1029, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 1039, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAltaActionPerformed
        // TODO add your handling code here:
        altaCliente();
        
    }//GEN-LAST:event_btAltaActionPerformed

    private void btBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBuscarActionPerformed
        // TODO add your handling code here:
        buscar();
    }//GEN-LAST:event_btBuscarActionPerformed

    private void btModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btModificarActionPerformed
        // TODO add your handling code here:
        modificar();
    }//GEN-LAST:event_btModificarActionPerformed

    private void btGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGuardarActionPerformed
        // TODO add your handling code here:
        Guardar();
    }//GEN-LAST:event_btGuardarActionPerformed

    private void btActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btActualizarActionPerformed
        // TODO add your handling code here:
        Actualizar();
    }//GEN-LAST:event_btActualizarActionPerformed

    private void btEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEliminarActionPerformed
        // TODO add your handling code here:
        Eliminar();
    }//GEN-LAST:event_btEliminarActionPerformed

    private void btEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEditarActionPerformed
        editarReparacion();
    }//GEN-LAST:event_btEditarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        Cerrar();
    }//GEN-LAST:event_formWindowClosing

    private void btSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSalirActionPerformed
        // TODO add your handling code here:
        Cerrar();
    }//GEN-LAST:event_btSalirActionPerformed

    private void btCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarActionPerformed
        // TODO add your handling code here:
        cancelar();
    }//GEN-LAST:event_btCancelarActionPerformed
 
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                gesClientes dialog = new gesClientes(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btActualizar;
    private javax.swing.JButton btAlta;
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btCancelar;
    private javax.swing.JButton btEditar;
    private javax.swing.JButton btEliminar;
    private javax.swing.JButton btGuardar;
    private javax.swing.JButton btModificar;
    private javax.swing.JButton btSalir;
    private javax.swing.JComboBox cbProvincias;
    private javax.persistence.Query clientesQuery;
    private javax.persistence.EntityManager entityManager1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList jListClientes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
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
    private java.util.List<Clientes> listaClientes;
    private java.util.List<Provincias> provinciasList;
    private javax.persistence.Query provinciasQuery;
    private javax.swing.JTextField tfApellidos;
    private javax.swing.JTextField tfCiudad;
    private javax.swing.JTextField tfCp;
    private javax.swing.JTextField tfDireccion;
    private javax.swing.JTextField tfDni;
    private javax.swing.JTextField tfEmail;
    private javax.swing.JTextField tfNombre;
    private javax.swing.JTextField tfTelefono;
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
        this.necesitaGuardar = necesitaGuardar;
        btGuardar.setEnabled(necesitaGuardar);

    }

    /**
     * @return the modificar
     */
    public boolean isModificar() {
        return modificar;
    }

    /**
     * @param modificar the modificar to set
     */
    public void setModificar(boolean modificar) {
        this.modificar = modificar;
    }

}
