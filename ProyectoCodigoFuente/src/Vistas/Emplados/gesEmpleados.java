/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * gesEmpleados.java
 *
 * Created on 30-ene-2012, 12:47:08
 */

package Vistas.Emplados;

import Controlador.EmpleadosJpaController;
import Controlador.exceptions.IllegalOrphanException;
import Controlador.exceptions.NonexistentEntityException;
import Modelo.Empleados;
import Modelo.Provincias;
import Modelo.Usuarios;
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

/**
 *
 * @author weejdu01
 */
public class gesEmpleados extends javax.swing.JDialog {

   
    private boolean necesitaGuardar; // variable que se utilizara para saber si hay cambios que guardar
    private Query ConsultaListaEmpleados; // consulta que se utilizara para obtener la lista de empleados
    private Query consultaListaProvincias;  // consulta que se utilizara para obtener la lista de provincias
    private EmpleadosJpaController controladorEmpleados; //controlador que gestionara las transacciones y las operacion sdobre el entityManager
    private boolean modificar; // varaiable para saber si se esta modificando un empleado

    /** Creates new form gesEmpleados */
    public gesEmpleados(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        activarBotones();
        desactivarCampos();
        controladorEmpleados = new EmpleadosJpaController();// controlador que gestionara las transacciones y la persistencia de datos

        // Se crea una variable restringirTexfField para restringir los caracteres del jTextField que contendra el dni
        restringirTexfField restringirTFDni = new restringirTexfField(tfDni);
        restringirTFDni.setLongitud(9);
        tfDni.addKeyListener(restringirTFDni);
        // Se crea una variable restringirTexfField para restringir los caracteres del jTextField que contendra el telefono
        restringirTexfField restringirTFtfno = new restringirTexfField(tfTelefono);
        restringirTFtfno.setLongitud(9);
        tfTelefono.addKeyListener(restringirTFtfno);

        // consulta que obtendra la lista de todos los empleados ordenados por nombre y apellidos
        ConsultaListaEmpleados = controladorEmpleados.getEm().createQuery("SELECT e FROM Empleados e ORDER BY e.nombre,e.apellidos");
        consultaListaProvincias = controladorEmpleados.getEm().createQuery("SELECT p FROM Provincias p");
        provinciasList.clear();  // se vacia la lista de provincias (esta lista se creo con el editor grafico y esta en lazada con el combobox cbProvincias)
        provinciasList.addAll(consultaListaProvincias.getResultList());// se llena la lista con el resultado de la consulta
       
        Utilidades.Utilidades.centrar(this);
        listaEmpleados.clear();  // se vacia la lista de empleados (esta lista se creo con el editor grafico y esta en lazada con el jList jListEmpleados)
        listaEmpleados.addAll(ConsultaListaEmpleados.getResultList()); // se llena la lista con el resultado de la consulta

        /* Este listener es de modelo de la selección de nuestra lista. Cuando cambie el modelo de seleccion se tratara el evento y se comprobara
         si hay alguna fila seleccionada de la lista o no. si hay alguna fila seleccionada se activaran los botones modificar y eliminar, en caso contrario
         se desactivaran*/
        jListEmpleados.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {

                    if(jListEmpleados.getSelectedIndex()!=-1){
                        
                        btEliminar.setEnabled(true);
                        btModificar.setEnabled(true);
                        //jchbUsuario.setEnabled(false);
                    }else{
                        btEliminar.setEnabled(false);
                        btModificar.setEnabled(false);
                        //jchbUsuario.setEnabled(false);
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
        setModificar(false);
    }
    
    /**
     * metodo que activara los botones del formularaio(activa algunos botones y desactiva otros)
     */
    private void activarBotones() {
        jListEmpleados.setEnabled(true);
        btAlta.setEnabled(true);
        btSalir.setEnabled(true);
        btActualizar.setEnabled(true);
        btBuscar.setEnabled(true);
        btCancelar.setEnabled(false);
        btModificar.setEnabled(false);
        btEliminar.setEnabled(false);
        setNecesitaGuardar(false);
        setModificar(false);
    }
    /**
     * Metodo que deactiva todos los botones
     */
    private void desactivarBotones (){
        //desactivarCampos();
        btAlta.setEnabled(false);
        btEliminar.setEnabled(false);
        btModificar.setEnabled(false);
        btCancelar.setEnabled(false);
        btBuscar.setEnabled(false);      
        btActualizar.setEnabled(false);
        btSalir.setEnabled(false);
        jListEmpleados.setEnabled(false);
        setNecesitaGuardar(false);

    }

    /**
     * Metodo que comprabara si la modificacion es correcta, devuelve true si esta todo correcto y false en caso contrario
     * @return
     */
    private boolean comprobarModificacion(){
        boolean correcto = false; // devolvera true si los datos del cliente son correctos y false en caso contrario
        EmpleadosJpaController controladorAuxiliar = new EmpleadosJpaController(); // se utiliza un controlador auxiliar para realiziar la busqueda del cliente con dni pasado como parametros a la consulta
        Query Consulta; /* variable que tendra el resultado de la consulta "SELECT c FROM Clientes c WHERE c.dniCliente = :dniCliente"
        y se utilizara para comprobar si el cliente que se va a insertar no este en la BD*/
        String jpql = "SELECT e FROM Empleados e WHERE e.dniEmpleado = :dniEmpleado" ;
        String dni = this.tfDni.getText();
        boolean dnicorrecto;
        boolean cpcorrecto;

        /* Se obtiene el resultado de comprobar si el dni y el cp son correctos*/
        dnicorrecto=Utilidades.UtilidadesComprobar.comprobarNif(dni);
        /*Se utiliza el otro entitymanager distinto del controlador para comprobar*/

        /* Se comprueba que los compos obligatorios no esten vacios*/
        if(this.tfDni.getText().trim().equals("")|| this.tfNombre.getText().trim().equals("") ||
           this.tfApellidos.getText().trim().equals("") || this.tfTelefono.getText().trim().equals("")){
             JOptionPane.showMessageDialog(this, "Los campos con * son obligatorios", "Atencion", (int) CENTER_ALIGNMENT);
             correcto=false;
        }else{  
              /*Si los campos obligatorios y el campos cp son correctos se comprueba que el clienteAux que se
                va a insertar en la BD ya exista*/
                // si el resultado de la consulta devuelve null es porque el clienteAux no existe en la BD
                    Consulta = controladorAuxiliar.getEm().createQuery(jpql);
                    Empleados empleadoAux=null;
                    Consulta.setParameter("dniEmpleado", dni);
                    empleadoAux=(Empleados)Consulta.getSingleResult();
                    /*si el clienteAux no existe se inserta en la BD si no se avisa de que ya existe,*/
                    if(empleadoAux!=null && !listaEmpleados.get(jListEmpleados.getSelectedIndex()).equals(empleadoAux)){
                        JOptionPane.showMessageDialog(this, "El Empleado con DNI : "+dni+" ya existe", "Atencion", (int) CENTER_ALIGNMENT);
                        correcto=false;
                    }else{
                        correcto=true;
                    }

        }
        controladorAuxiliar=null;
        return correcto;
    }

    /**
      * Metodo para cancelar un alta o una modificacion
      */
     private void cancelar(){
         // se comprueba que si hay algun cliente seleccionado
         if(jListEmpleados.getSelectedIndex()!=-1){
             // se obtiene el cliente seleccionado
             Empleados e = listaEmpleados.get(jListEmpleados.getSelectedIndex());
             activarBotones();
             desactivarCampos();
             // se borra la selecion
             jListEmpleados.clearSelection();
             // si el id es null es porque se ha creado un cliente nuevo y no se ha guardado en la BD
             if(e.getIdEmpleado()!=null){
                 // si el id es distindto de null se actualizan los datos con los de la BD y se selecciona el cliente
                 controladorEmpleados.getEm().refresh(e);
                 jListEmpleados.setSelectedIndex(listaEmpleados.indexOf(e));
             }else{
                 // si el id del cliente es null ,es porque todavia no se ha guardado en la BD, se elimina del entitymanager y de la lista de clientes
                 listaEmpleados.remove(e);
                try {
                    Usuarios u = e.getUsuario();
                    e.setUsuario(null);
                    controladorEmpleados.getEm().remove(u);
                    controladorEmpleados.destroy(e);
                } catch (IllegalOrphanException ex) {
                    Logger.getLogger(gesEmpleados.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(gesEmpleados.class.getName()).log(Level.SEVERE, null, ex);
                }
              
             }
             
         }
     }

    /**
     * Metodo que cierra la ventana de gestion de empleados
     */
    private void Cerrar(){
        if(isNecesitaGuardar()){
            int opcion= JOptionPane.showConfirmDialog(this, "Hay cambios sin guardar ¿desea salir?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(opcion==JOptionPane.YES_OPTION){
                //em.getTransaction().rollback();
                controladorEmpleados.Deshacer();
                this.dispose();
            }
        }else{
            this.dispose();
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
        tfDireccion.setEditable(true);
        tfTelefono.setEditable(true);
        jchbUsuario.setEnabled(true);
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
        tfDireccion.setEditable(false);
        tfTelefono.setEditable(false);
        jchbUsuario.setEnabled(false);
    }

    /**
     * Metodo para buscar empleados
     */
    public void buscar(){
        buscarEmpleados busqueda = new buscarEmpleados(this,true);
        busqueda.setVisible(true);
        Query consultaBusqueda;
        int tipoBusqueda = busqueda.getSeleccionBusqueda();  // se obtiene el valor de la variable seleccion busqueda de la clase buscarEmpleados para saber el tipo de busqueda
        List<Empleados> lista = new ArrayList();
        switch (tipoBusqueda){
            // si el tipoBusqueda es 0 se listaran todos los empleados
            case 0:
                listaEmpleados.clear(); // se vacia la lista de empleados
                listaEmpleados.addAll(ConsultaListaEmpleados.getResultList()); // se llena la lista de empleados
                busqueda.dispose();
                break;
            // si el tipoBusqueda es 1 se listaran todos los empleados con el id pasado como parametro en la consulta
            case 1:
                consultaBusqueda=controladorEmpleados.getEm().createQuery("SELECT e FROM Empleados e WHERE e.idEmpleado = :idEmpleado");
                int id= busqueda.getId();
                consultaBusqueda.setParameter("idEmpleado", id);
                lista.addAll(consultaBusqueda.getResultList());

                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No se encontro el Empleado con id "+id, "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    listaEmpleados.clear();
                    listaEmpleados.addAll(lista);
                    jListEmpleados.setSelectedIndex(0);

                }
                busqueda.dispose();
                break;
             // si el tipoBusqueda es 2 se listaran todos los empleados con el dni pasado como parametro en la consulta
            case 2:
                consultaBusqueda=controladorEmpleados.getEm().createQuery("SELECT e FROM Empleados e WHERE e.dniEmpleado = :dniEmpleado");
                String dni= busqueda.getDni();
                consultaBusqueda.setParameter("dniEmpleado", dni);
                lista.addAll(consultaBusqueda.getResultList());

                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No se encontro el empleado con DNI "+dni, "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    listaEmpleados.clear();
                    listaEmpleados.addAll(lista);
                    jListEmpleados.setSelectedIndex(0);

                }
                busqueda.dispose();
                break;
            // si el tipoBusqueda es 3 se listaran todos los empleados con el apellido pasado como parametro en la consulta
            case 3:
                String apellidos= busqueda.getApellidos();
                consultaBusqueda=controladorEmpleados.getEm().createQuery("SELECT e FROM Empleados e WHERE e.apellidos LIKE :apellidos");

                consultaBusqueda.setParameter("apellidos", "%"+apellidos+"%");
                lista.addAll(consultaBusqueda.getResultList());

                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No se encontro el empelado con apellidos "+apellidos, "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    listaEmpleados.clear();
                    listaEmpleados.addAll(lista);
                    jListEmpleados.setSelectedIndex(0);

                }
                busqueda.dispose();
                break;

            case 4:
                break;
        }
    }

    /**
     * metodo para guardar los cambios en la BD, se encargara de cerrar la transaccion con un commit y abrir una nueva con begin.Todo ello lo hace el controlador
     */
     public void Guardar() {
         if(this.isModificar()){
             if(comprobarModificacion()){
                 try{
                    // se cierra una transaccion del entity manager con un commit y se vuelve abrir una nueva con begin. todo ello lo hace el controlador
                    Empleados emp;  // este empleado se utilizara en caso de que haya un cliente seleccionado en la lista para que una vez que se actualiza la lista se pueda volver a seleccionar

                    controladorEmpleados.Guardar();// se guardan los cambios en la BD con el controlador
                    activarBotones();
                    if(jListEmpleados.getSelectedIndex()!=-1){
                        emp = listaEmpleados.get(jListEmpleados.getSelectedIndex());
                        jListEmpleados.clearSelection();
                        jListEmpleados.setSelectedIndex(listaEmpleados.indexOf(emp));
                    }else{
                        jListEmpleados.clearSelection();
                    }

                    //EdicionClientes.setNecesitaGuardar(isNecesitaGuardar());
                    //AltaCliente.setNecesitaGuardar(isNecesitaGuardar());
                    // EdicionReparacionCliente.setNecesitaGuardar(isNecesitaGuardar());
                    desactivarCampos();
                     JOptionPane.showMessageDialog(this, "Los cambios se guardaron correctamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    /*En caso de una excepción (la llamada a commit puede lanzar una RollbackException si la transacción ha sido deshecha anteriormente)
                    * “mezclamos” los datos de los clientes con los datos almacenados en la base de datos.*/
                    //controladorEmpleados.getEm().getTransaction().begin();
                } catch(RollbackException e){
                    controladorEmpleados.getEm().getTransaction().begin();
                    List<Empleados> lista = new ArrayList<Empleados>(listaEmpleados.size());
                    for(int i=0;i<listaEmpleados.size();i++){
                        lista.add(controladorEmpleados.getEm().merge(listaEmpleados.get(i)));
                    }
                    listaEmpleados.clear();
                    listaEmpleados.addAll(lista);
                }
            }
         }else{
             try{
                // se cierra una transaccion del entity manager con un commit y se vuelve abrir una nueva con begin. todo ello lo hace el controlador

                Empleados emp;  // este empleado se utilizara en caso de que haya un cliente seleccionado en la lista para que una vez que se actualiza la lista se pueda volver a seleccionar

                controladorEmpleados.Guardar();// se guardan los cambios en la BD con el controlador
                activarBotones();
                if(jListEmpleados.getSelectedIndex()!=-1){
                    emp = listaEmpleados.get(jListEmpleados.getSelectedIndex());
                    jListEmpleados.clearSelection();
                    jListEmpleados.setSelectedIndex(listaEmpleados.indexOf(emp));
                }else{
                    jListEmpleados.clearSelection();
                }
                
                //EdicionClientes.setNecesitaGuardar(isNecesitaGuardar());
                //AltaCliente.setNecesitaGuardar(isNecesitaGuardar());
                // EdicionReparacionCliente.setNecesitaGuardar(isNecesitaGuardar());
                desactivarCampos();
                 JOptionPane.showMessageDialog(this, "Los cambios se guardaron correctamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                /*En caso de una excepción (la llamada a commit puede lanzar una RollbackException si la transacción ha sido deshecha anteriormente)
                * “mezclamos” los datos de los clientes con los datos almacenados en la base de datos.*/
                //controladorEmpleados.getEm().getTransaction().begin();
            } catch(RollbackException e){
                controladorEmpleados.getEm().getTransaction().begin();
                List<Empleados> lista = new ArrayList<Empleados>(listaEmpleados.size());
                for(int i=0;i<listaEmpleados.size();i++){
                    lista.add(controladorEmpleados.getEm().merge(listaEmpleados.get(i)));
                }
                listaEmpleados.clear();
                listaEmpleados.addAll(lista);
            }
         }
        
    }

     /**
     * metodo que activara los campos de edicion para modificar la cciones y desactivara los botones
     */
     public void modificar(){
         btEliminar.setEnabled(false);
         btAlta.setEnabled(false);
         btModificar.setEnabled(false);
         btBuscar.setEnabled(false);
         jListEmpleados.setEnabled(false);
         btCancelar.setEnabled(true);
         btActualizar.setEnabled(false);
         setModificar(true);
         activarCampos();
     }

    /**
     * Metodo para eliminar un empleado del sistema
     */
    public void Eliminar() {
         int opcion;
        // se comprueba si hay mas de un empleado seleccionado para mostrar el mensaje adecuado
        if(jListEmpleados.getSelectedIndices().length==1){
            opcion= JOptionPane.showConfirmDialog(this, "¿Desea eliminar el empleado seleccionado?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        }else{
            opcion= JOptionPane.showConfirmDialog(this, "¿Desea eliminar los clientes seleccionados?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        }

        if(opcion == JOptionPane.YES_OPTION){

            List paraEliminar = new ArrayList<Empleados> (); // lista que contendra los empleados para eliminar
            int [] seleccionados = jListEmpleados.getSelectedIndices(); // se otiene un array de los empleados seleccioneado con los indices donde se encuentran los empleados en la lista de cliempleadosentes
            // se recorre el array con los empleados selecciondos y se llena la lista de empleados para eliminar
            for(int i=0;i<seleccionados.length;i++){
                // se van obteniendo uno a uno los empleados seleccionados y se añaden a la lista de empleados para eliminar y tambien se elimina del entitymanager desde el controlador
                Empleados emp = listaEmpleados.get(seleccionados[i]);
                try {
                    controladorEmpleados.destroy(emp);
                    paraEliminar.add(emp);

                } catch (IllegalOrphanException ex) {

                     JOptionPane.showMessageDialog(this, ex.getMessage(), "Atencion", WIDTH);
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(gesEmpleados.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
               
            }
            if(paraEliminar.isEmpty()){
                this.setNecesitaGuardar(false);
            }else{
                activarBotones();
                setNecesitaGuardar(true);
                btAlta.setEnabled(false);
                btBuscar.setEnabled(false);
                // se eliminana los empleados de la lista
                listaEmpleados.removeAll(paraEliminar);
             
            }
        }
    }

    /**
     * Metodo para deshacer los cambio de la BD ignorando todo cambio que se haya hecho. Para ello es necesariodeshacer los cambios de la transacción e iniciar una
     * nueva. Luego se “refresca” la lista de empleados que tenemos con los datos almacenados en la base de datos, recuperándolos nuevamente volviendo a ejecutar la consulta del objeto Query.
     */
    public void Actualizar() {

        Thread hebra = new Thread(new Runnable() {
            public void run() {
                try {
                    desactivarBotones();
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    jProgressBar1.setValue(10);
                    Thread.sleep(500);          
                      // se deshacen los cambios en el entitymanager con el controlador
                    controladorEmpleados.Deshacer();
                    listaEmpleados.clear(); // se vacia la lista de empleados
                    listaEmpleados.addAll(ConsultaListaEmpleados.getResultList()); // se llena la lista de empleados con la lista devuelta por la consulta
                    provinciasList.clear();// se vacia la lista de provincias
                    provinciasList.addAll(consultaListaProvincias.getResultList()); // se vuelve a llenar la lista de provincias
                    
                    jProgressBar1.setValue(50);
                    Thread.sleep(500);
                    //desactivarBotones();
                    jProgressBar1.setValue(100);
                    jProgressBar1.setValue(0);
                    Thread.sleep(500);
                    activarBotones();
                    desactivarCampos();
                    jListEmpleados.setSelectedIndex(-1);     
                    //activarBotones();
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } catch (InterruptedException ex) {
                    
                }
            }
        });
        hebra.start();
  
    }

    /**
     * Metodo para dar de alta un empleado
     */
    private void altaEmpleado(){

        Empleados emp = new Empleados();
        Usuarios us = new Usuarios();
        AltaEmpleado1 alta = new AltaEmpleado1(this,true, emp,us,controladorEmpleados.getEm());// se abre una ventana de alta que recibe como parametro un empleado y el entityManager del controlador
        alta.setVisible(true);
        // si se pulso el boton aceptar en la ventana de alta se persiste la entidad Empleados en el entitymanager con el controlador y se añade a la lista
        if(alta.isAcaptaAlta()){
            try {
                controladorEmpleados.getEm().persist(us);
                controladorEmpleados.create(emp);
                listaEmpleados.add(emp);
                jListEmpleados.setSelectedIndex(listaEmpleados.size()-1); // se selecciona la aacion en la tabla
            }catch (Exception ex) {
                Logger.getLogger(gesEmpleados.class.getName()).log(Level.SEVERE, null, ex);
            }               
            btEliminar.setEnabled(false);
            btAlta.setEnabled(false);
            btModificar.setEnabled(false);
            btBuscar.setEnabled(false);
            jListEmpleados.setEnabled(false);
            btActualizar.setEnabled(false);
            btCancelar.setEnabled(true);

        }
        setNecesitaGuardar(alta.isAcaptaAlta());
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
        provinciasQuery = java.beans.Beans.isDesignTime() ? null : ProyectoDAIPUEntityManager.createQuery("SELECT p FROM Provincias p");
        provinciasList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : provinciasQuery.getResultList();
        empleadosQuery = java.beans.Beans.isDesignTime() ? null : ProyectoDAIPUEntityManager.createQuery("SELECT e FROM Empleados e");
        listaEmpleados = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(empleadosQuery.getResultList());
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btAlta = new javax.swing.JButton();
        btBuscar = new javax.swing.JButton();
        btEliminar = new javax.swing.JButton();
        btModificar = new javax.swing.JButton();
        btGuardar = new javax.swing.JButton();
        btActualizar = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtReparaciones = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListEmpleados = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        lbNumero = new javax.swing.JLabel();
        lbDni = new javax.swing.JLabel();
        lbNombre = new javax.swing.JLabel();
        lbApellidos = new javax.swing.JLabel();
        lbCiudad = new javax.swing.JLabel();
        lbProvincia = new javax.swing.JLabel();
        lbTelefono = new javax.swing.JLabel();
        tfDni = new javax.swing.JTextField();
        tfNombre = new javax.swing.JTextField();
        tfApellidos = new javax.swing.JTextField();
        tfCiudad = new javax.swing.JTextField();
        cbProvincias = new javax.swing.JComboBox();
        tfTelefono = new javax.swing.JTextField();
        lbDireccion = new javax.swing.JLabel();
        tfDireccion = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jchbUsuario = new javax.swing.JCheckBox();
        lbUsuario = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btSalir = new javax.swing.JButton();
        btCancelar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lbTitulo = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Gestion de Empleados");
        setMinimumSize(new java.awt.Dimension(969, 691));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setName("jPanel2"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        btAlta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/alta.png"))); // NOI18N
        btAlta.setMnemonic('n');
        btAlta.setText("Nuevo");
        btAlta.setToolTipText("Añadir un empleado");
        btAlta.setName("btAlta"); // NOI18N
        btAlta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAltaActionPerformed(evt);
            }
        });

        btBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/buscar.png"))); // NOI18N
        btBuscar.setMnemonic('n');
        btBuscar.setText("Buscar");
        btBuscar.setToolTipText("Buscar un empleado");
        btBuscar.setName("btBuscar"); // NOI18N
        btBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBuscarActionPerformed(evt);
            }
        });

        btEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/1329144248_delete.png"))); // NOI18N
        btEliminar.setMnemonic('e');
        btEliminar.setText("Eliminar");
        btEliminar.setToolTipText("Eliminar un empleado");
        btEliminar.setName("btEliminar"); // NOI18N
        btEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEliminarActionPerformed(evt);
            }
        });

        btModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/editar.png"))); // NOI18N
        btModificar.setMnemonic('m');
        btModificar.setText("Modificar");
        btModificar.setToolTipText("Modificar un empleado");
        btModificar.setName("btModificar"); // NOI18N
        btModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btModificarActionPerformed(evt);
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

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 51, 255))); // NOI18N
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jtReparaciones.setName("jtReparaciones"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${selectedElement.reparacionesList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListEmpleados, eLProperty, jtReparaciones);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${idReparacion}"));
        columnBinding.setColumnName("Id Reparacion");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${idCliente.nombre} ${idCliente.apellidos}"));
        columnBinding.setColumnName("Cliente");
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${formatoFechaEntrada}"));
        columnBinding.setColumnName("Fecha de Entrada");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${formatoFechaSalida}"));
        columnBinding.setColumnName("Fecha de Salida");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${estado}"));
        columnBinding.setColumnName("Estado");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalReparacionIva} €"));
        columnBinding.setColumnName("Total €");
        columnBinding.setEditable(false);
        jTableBinding.setSourceUnreadableValue(java.util.Collections.emptyList());
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane3.setViewportView(jtReparaciones);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Reparaciones", new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Reparaciones.png")), jPanel5); // NOI18N

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Empleados", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 51, 255))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jListEmpleados.setToolTipText("Lista de Empleados");
        jListEmpleados.setName("jListEmpleados"); // NOI18N

        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listaEmpleados, jListEmpleados);
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create("${nombre} ${apellidos}"));
        bindingGroup.addBinding(jListBinding);

        jScrollPane1.setViewportView(jListEmpleados);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Empleado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        lbNumero.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNumero.setText("Numero de Empleado:");
        lbNumero.setName("lbNumero"); // NOI18N

        lbDni.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDni.setText("*DNI:");
        lbDni.setName("lbDni"); // NOI18N

        lbNombre.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNombre.setText("*Nombre:");
        lbNombre.setName("lbNombre"); // NOI18N

        lbApellidos.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbApellidos.setText("*Apellidos:");
        lbApellidos.setName("lbApellidos"); // NOI18N

        lbCiudad.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbCiudad.setText("Ciudad:");
        lbCiudad.setName("lbCiudad"); // NOI18N

        lbProvincia.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbProvincia.setText("Provincia:");
        lbProvincia.setName("lbProvincia"); // NOI18N

        lbTelefono.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbTelefono.setText("*Telefono:");
        lbTelefono.setName("lbTelefono"); // NOI18N

        tfDni.setName("tfDni"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListEmpleados, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.dniEmpleado}"), tfDni, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        tfNombre.setName("tfNombre"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListEmpleados, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.nombre}"), tfNombre, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        tfApellidos.setName("tfApellidos"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListEmpleados, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.apellidos}"), tfApellidos, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        tfCiudad.setName("tfCiudad"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListEmpleados, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ciudad}"), tfCiudad, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        cbProvincias.setName("cbProvincias"); // NOI18N

        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, provinciasList, cbProvincias);
        jComboBoxBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListEmpleados, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.codProvincia}"), cbProvincias, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        tfTelefono.setName("tfTelefono"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListEmpleados, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.telefono}"), tfTelefono, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        lbDireccion.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDireccion.setText("Direccion:");
        lbDireccion.setName("lbDireccion"); // NOI18N

        tfDireccion.setName("tfDireccion"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListEmpleados, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.direccion}"), tfDireccion, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 8));
        jLabel1.setText("Los campos con * son obligatorios");
        jLabel1.setName("jLabel1"); // NOI18N

        jchbUsuario.setText("Administrador");
        jchbUsuario.setName("jchbUsuario"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListEmpleados, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.usuario.tipo}"), jchbUsuario, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceUnreadableValue(true);
        bindingGroup.addBinding(binding);

        lbUsuario.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbUsuario.setText("Tipo de usuario");
        lbUsuario.setName("lbUsuario"); // NOI18N

        jLabel3.setForeground(new java.awt.Color(204, 0, 51));
        jLabel3.setName("jLabel3"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListEmpleados, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idEmpleado}"), jLabel3, org.jdesktop.beansbinding.BeanProperty.create("text"));
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
                        .addGap(52, 52, 52)
                        .addComponent(lbUsuario)
                        .addGap(7, 7, 7)
                        .addComponent(jchbUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addComponent(cbProvincias, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbNombre)
                        .addGap(4, 4, 4)
                        .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(lbApellidos)
                        .addGap(4, 4, 4)
                        .addComponent(tfApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbNumero)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(lbDni)
                        .addGap(4, 4, 4)
                        .addComponent(tfDni, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbNumero)
                    .addComponent(lbDni)
                    .addComponent(tfDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbCiudad))
                    .addComponent(tfCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbProvincia))
                    .addComponent(cbProvincias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbDireccion))
                    .addComponent(tfDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(lbTelefono))
                            .addComponent(tfTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addComponent(jLabel1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(lbUsuario))
                    .addComponent(jchbUsuario))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        btCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/eliminar.png"))); // NOI18N
        btCancelar.setText("Cancelar");
        btCancelar.setToolTipText("Cancelar un alta o una modificacion");
        btCancelar.setName("btCancelar"); // NOI18N
        btCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 927, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(btAlta)
                        .addGap(18, 18, 18)
                        .addComponent(btEliminar)
                        .addGap(18, 18, 18)
                        .addComponent(btModificar)
                        .addGap(18, 18, 18)
                        .addComponent(btCancelar)
                        .addGap(18, 18, 18)
                        .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionEmpleados.png"))); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        lbTitulo.setFont(new java.awt.Font("Tahoma 18 Negrita 18 Negrita", 1, 18));
        lbTitulo.setText("Gestion de Empleados");
        lbTitulo.setName("lbTitulo"); // NOI18N

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Logo1.png"))); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel6)
                .addGap(294, 294, 294)
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
                .addGap(25, 25, 25)
                .addComponent(lbTitulo))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jProgressBar1.setName("jProgressBar1"); // NOI18N
        jPanel7.add(jProgressBar1);

        getContentPane().add(jPanel7, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAltaActionPerformed
       
        //altaCliente();
        altaEmpleado();
    }//GEN-LAST:event_btAltaActionPerformed

    private void btBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBuscarActionPerformed
        // TODO add your handling code here:
       buscar();
}//GEN-LAST:event_btBuscarActionPerformed

    private void btEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEliminarActionPerformed
        // TODO add your handling code here:
        Eliminar();
}//GEN-LAST:event_btEliminarActionPerformed

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

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                gesEmpleados dialog = new gesEmpleados(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btActualizar;
    private javax.swing.JButton btAlta;
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btCancelar;
    private javax.swing.JButton btEliminar;
    private javax.swing.JButton btGuardar;
    private javax.swing.JButton btModificar;
    private javax.swing.JButton btSalir;
    private javax.swing.JComboBox cbProvincias;
    private javax.persistence.Query empleadosQuery;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jListEmpleados;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JCheckBox jchbUsuario;
    private javax.swing.JTable jtReparaciones;
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
    private java.util.List<Empleados> listaEmpleados;
    private java.util.List<Provincias> provinciasList;
    private javax.persistence.Query provinciasQuery;
    private javax.swing.JTextField tfApellidos;
    private javax.swing.JTextField tfCiudad;
    private javax.swing.JTextField tfDireccion;
    private javax.swing.JTextField tfDni;
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
        btGuardar.setEnabled(necesitaGuardar);
        this.necesitaGuardar = necesitaGuardar;
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
