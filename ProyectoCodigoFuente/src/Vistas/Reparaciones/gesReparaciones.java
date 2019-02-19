/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * gesReparaciones.java
 *
 * Created on 31-ene-2012, 17:36:49
 */

package Vistas.Reparaciones;

import Controlador.ReparacionesJpaController;
import Controlador.exceptions.NonexistentEntityException;
import Modelo.Acciones;
import Modelo.Empleados;
import Modelo.Reparaciones;
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
import proyectodai.Inicio;

/**
 *
 * @author weejdu01
 */
public class gesReparaciones extends javax.swing.JDialog {

    /** Creates new form gesReparaciones */
    private List<String> listaEstados; // lista que contendra los estados de reparacion
    private boolean necesitaGuardar; // variable para saber si hay cambios sin guardar
    private ReparacionesJpaController  controladorReparaciones; // controlador que gestionara las transacciones y la persistencia de datos
    private Query consultaReparaciones; // consulta que obtendra la lista de todas las reparaciones
    private Query consultaClientes;// consulta que obtendra la lista de todos los clientes
    private Query consultaEmpleados; // consulta que obtendra la lista de todos los empleados

    public gesReparaciones(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        listaEstados = new ArrayList<String>();
        listaEstados.add("reparado");
        listaEstados.add("reparacion");
        listaEstados.add("facturado");

        initComponents();
        activarBotones();
        list1.clear();// la lista list1 contendra las acciones de cada reparacion(), esta lista esta enlazada con la tabla que muestra las acciones(se creado y enlazado desde el editor grafico)
        controladorReparaciones= new ReparacionesJpaController();
        consultaReparaciones = controladorReparaciones.getEm().createQuery("SELECT r FROM Reparaciones r");
        consultaClientes = controladorReparaciones.getEm().createQuery("SELECT c FROM Clientes c");
        consultaEmpleados= controladorReparaciones.getEm().createQuery("SELECT e FROM Empleados e");
        // se vacian las listas y se vuelve a llenar con losresultados de las consultas
        reparacionesList.clear();  // se vacia la lista de reparaciones (esta lista se creo con el editor grafico y esta en lazada con el jList jListReparaciones)
        reparacionesList.addAll(consultaReparaciones.getResultList());
        clientesList.clear();  // se vacia la lista de clientes (esta lista se creo con el editor grafico y esta en lazada con el combobox cbClientes)
        clientesList.addAll(consultaClientes.getResultList());
        empleadosList.clear();  // se vacia la lista de empleados (esta lista se creo con el editor grafico y esta en lazada con el combobox cbEmpleados)
        empleadosList.addAll(consultaEmpleados.getResultList());
        desactivarCampos();
        Utilidades.Utilidades.centrar(this);

        /* Este listener es de modelo de la selección de nuestra lista. Cuando cambie el modelo de seleccion se tratara el evento y se comprobara
         si hay alguna fila seleccionada de la lista o no. si hay alguna fila seleccionada se activaran los botones modificar y eliminar, en caso contrario
         se desactivaran. Tambien si hay una reparacion seleccionada se vaciara la lista list1 y se llenara con las acciones de la reparacion seleccionada, si no hay
         ninguna reparacion seleccionada se vaciara la lista list1*/
        jListClientes.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {

                    if(jListClientes.getSelectedIndex()!=-1 || jListClientes.getSelectedValue()!=null){
                        if(reparacionesList.get(jListClientes.getSelectedIndex()).getEstado().equals("facturado")){
                             btEliminar.setEnabled(false);
                             btModificar.setEnabled(false);
                             btAnadir.setEnabled(false);
                        }else{
                            btEliminar.setEnabled(true);
                            btModificar.setEnabled(true);
                        }
                        list1.clear();
                        list1.addAll(reparacionesList.get(jListClientes.getSelectedIndex()).getAccionesList());
                        lbCompraSubtotalValor.setText(String.valueOf(reparacionesList.get(jListClientes.getSelectedIndex()).getTotalReparacion()) + " €");
                        lbCompraTotalValor.setText(String.valueOf(reparacionesList.get(jListClientes.getSelectedIndex()).getTotalReparacionIva())+" €");
                    }else{
                        btEliminar.setEnabled(false);
                        btModificar.setEnabled(false);
                        btAnadir.setEnabled(false);
                        list1.clear();
                    }
                }
            });

            /*
            bindingGroup.addBindingListener(new AbstractBindingListener() {
            public void targetChanged(Binding binding, PropertyStateEvent event) {
                    setNecesitaGuardar(true);
            }});*/


            /* Este listener es de modelo de la selección del jTable que muestra las acciones de la reparacion. Cuando cambie el modelo de seleccion se tratara el evento.
         * Si hay hay una fila selecciojada del jtAcciones se activara el boton eliminar, en caso contrario se desactiva.*/
         jtAcciones.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if(!jListClientes.isEnabled()){
                        if(jtAcciones.getSelectedRow()==-1){
                            btEliminarAccion.setEnabled(false);
                        }else{
                            btEliminarAccion.setEnabled(true);
                        }
                    }
                    
                }
        });

        comprobarUsusario();
    }

     /**
      * Metodo para cancelar un alta o una modificacion
      */
     private void cancelar(){
         // se comprueba que si hay algun cliente seleccionado
         if(jListClientes.getSelectedIndex()!=-1){
             // se obtiene el cliente seleccionado
             Reparaciones c = reparacionesList.get(jListClientes.getSelectedIndex());
             activarBotones();
             // se borra la selecion
             jListClientes.clearSelection();
             // si el id es null es porque se ha creado un cliente nuevo y no se ha guardado en la BD
             if(c.getIdReparacion()!=null){
                 // si el id es distindto de null se actualizan los datos con los de la BD y se selecciona el cliente
                 controladorReparaciones.getEm().refresh(c);
                 jListClientes.setSelectedIndex(reparacionesList.indexOf(c));
             }else{
                 // si el id del cliente es null ,es porque todavia no se ha guardado en la BD, se elimina del entitymanager y de la lista de clientes
                 reparacionesList.remove(c);
                try {
                    controladorReparaciones.destroy(c);
                    //jListClientes.setSelectedIndex(-);
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(gesReparaciones.class.getName()).log(Level.SEVERE, null, ex);
                }

             }
             desactivarCampos();
         }
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
     * Cierra la ventana de gedtion de reparaciones
     */
    private void Cerrar(){
        if(isNecesitaGuardar()){
            int opcion= JOptionPane.showConfirmDialog(this, "Hay cambios sin guardar ¿desea salir?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(opcion==JOptionPane.YES_OPTION){
                //em.getTransaction().rollback();
                controladorReparaciones.Deshacer();
                this.dispose();
            }
        }else{
            this.dispose();
        }
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
        btEliminarAccion.setEnabled(false);
        setNecesitaGuardar(false);
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
        jListClientes.setEnabled(false);
        setNecesitaGuardar(false);

    }

    /**
     * Metodo para desactivar los campos de edicion
     */
    private void desactivarCampos(){
        cbClientes.setEnabled(false);
        cbEmpleados.setEnabled(false);
        cbEstados.setEnabled(false);
        cbAcciones.setEnabled(false);
        taObservaciones.setEnabled(false);
        taDiagnostico.setEnabled(false);
        btAnadir.setEnabled(false);

    }

    /**
     * Metodo para activar los campos de edicion
     */
    private void activarCampos(){
        cbClientes.setEnabled(true);
        cbEmpleados.setEnabled(true);
        cbEstados.setEnabled(true);
        cbAcciones.setEnabled(true);
        taObservaciones.setEnabled(true);
        taDiagnostico.setEnabled(true);
        btAnadir.setEnabled(true);
    }

    /**
     * Metodo que se ejecutara al presionar el boton modificar y activara los compnentes de edicio y desactivara todos los botones menos los de
     * guardar y deshacer
     */
    private void modificar(){
        activarCampos();
        btAlta.setEnabled(false);
        btEliminar.setEnabled(false);
        btBuscar.setEnabled(false);
        btModificar.setEnabled(false);
        btCancelar.setEnabled(true);
         btActualizar.setEnabled(false);
        setNecesitaGuardar(true);
        jListClientes.setEnabled(false);
    }

    /**
     * Metodo que añadira una accion a la lista de acciones
     */
    private void anadirAccion(){
        // se obtiene la accion seleccionada
        Acciones a = (Acciones) cbAcciones.getSelectedItem();
        boolean encontrado=false;
        // se comprobata si ya esta añadida esa accion a la lista
        for(int i=0;i<reparacionesList.get(jListClientes.getSelectedIndex()).getAccionesList().size() && !encontrado;i++){
            if(reparacionesList.get(jListClientes.getSelectedIndex()).getAccionesList().get(i).equals(a)){
                encontrado=true;
            }
        }
        if(!encontrado){
            // se añade la laccion a la lista de acciones y a la lista de acciones de la reparacion
            list1.add(a);
            reparacionesList.get(jListClientes.getSelectedIndex()).getAccionesList().add(a);
            // se actualiza el total y subtotal de la compra
            this.lbCompraSubtotalValor.setText(String.valueOf(reparacionesList.get(jListClientes.getSelectedIndex()).getTotalReparacion()) + " €");
            this.lbCompraTotalValor.setText(String.valueOf(reparacionesList.get(jListClientes.getSelectedIndex()).getTotalReparacionIva())+" €");
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
            Acciones ac = list1.get(seleccionados[i]);
            paraEliminar.add(ac);

        }
        // se eliminana las acciones de la lista de acciones y de la lista de acciones de la reparacion
        list1.removeAll(paraEliminar);
        reparacionesList.get(jListClientes.getSelectedIndex()).getAccionesList().removeAll(paraEliminar);
        // se actualiza el total y subtotal de la compra
        this.lbCompraSubtotalValor.setText(String.valueOf(reparacionesList.get(jListClientes.getSelectedIndex()).getTotalReparacion()) + " €");
        this.lbCompraTotalValor.setText(String.valueOf(reparacionesList.get(jListClientes.getSelectedIndex()).getTotalReparacionIva())+" €");
    }

    /**
     * Metodo para buscar clientes
     */
    public void buscar(){
        buscarReparacion busqueda = new buscarReparacion(this,true);
        busqueda.setVisible(true);
        Query consultaBusqueda;
        int tipoBusqueda = busqueda.getSeleccionBusqueda(); // se el valor de la bariable seleccion busqueda de la clase buscarReparacion para saber el tipo de busqueda
        List<Reparaciones> lista = new ArrayList(); // esta lista contendra el resultado de la consulta segun el tipo de busqueda

        switch (tipoBusqueda){

            // si el tipoBusqueda es 0 se listaran todos las reparaciones
            case 0:
                reparacionesList.clear(); // se vacia la lista de reparaciones
                reparacionesList.addAll(consultaReparaciones.getResultList()); // se llena la lista de reparaciones
                busqueda.dispose();

                break;
            // si el tipoBusqueda es 1 se listaran todos las reparaciones con el id pasado como parametro en la consulta
            case 1:
                consultaBusqueda=controladorReparaciones.getEm().createQuery("SELECT r FROM Reparaciones r WHERE r.idReparacion = :idReparacion");
                int id= busqueda.getId();
                consultaBusqueda.setParameter("idReparacion", id);
                lista.addAll(consultaBusqueda.getResultList());

                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No se encontro la reparacion con id "+id, "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    reparacionesList.clear();
                    reparacionesList.addAll(lista);
                }
                busqueda.dispose();
                break;
            case 2:
                consultaBusqueda=controladorReparaciones.getEm().createQuery("SELECT r FROM Reparaciones r WHERE r.idCliente.dniCliente = :dni");
                String dni=busqueda.getDni();
                consultaBusqueda.setParameter("dni", dni);
                lista.addAll(consultaBusqueda.getResultList());
                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "El cliente con dni "+dni+" no tiene reparaciones", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    reparacionesList.clear();
                    reparacionesList.addAll(lista);
                }
            case 3:
                busqueda.dispose();
                break;
                

        }
    }

    /**
     * Metodo para eliminar una reparacion del sistema
     */
    public void Eliminar() {
         int opcion;
        // se comprueba si hay mas de una reparacion seleccionada para mostrar el mensaje adecuado
        if(jListClientes.getSelectedIndices().length==1){
            opcion= JOptionPane.showConfirmDialog(this, "¿Desea eliminar la reparacion seleccionada?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        }else{
            opcion= JOptionPane.showConfirmDialog(this, "¿Desea eliminar las reparaciones seleccionados?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        }

        if(opcion == JOptionPane.YES_OPTION){

            List paraEliminar = new ArrayList<Reparaciones> (); // lista que contendra las reparaciones para eliminar
            int [] seleccionados = jListClientes.getSelectedIndices(); // se otiene un array de las reparaciones seleccioneadas con los indices donde se encuentran las reparaciones en la lista de reparaciones
            // se recorre el array con las reparaciones selecciondas y se llena la lista de reparaciones para eliminar
            for(int i=0;i<seleccionados.length;i++){
                // se van obteniendo una a una las reparaciones seleccionados y se añaden a la lista de reparaciones para eliminar y tambien se elimina del entitymanager con el controlador
                Reparaciones rep = reparacionesList.get(seleccionados[i]);
                paraEliminar.add(rep);
                try {
                    controladorReparaciones.destroy(rep);
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(gesReparaciones.class.getName()).log(Level.SEVERE, null, ex);
                }
                    // se eliminan las reparaciones de la lista
                    reparacionesList.removeAll(paraEliminar);   
            }
            if(!paraEliminar.isEmpty()){
                activarBotones();
                setNecesitaGuardar(true);
                btAlta.setEnabled(false);
                btBuscar.setEnabled(false);
            }
            
        }
    }

    /**
     * Metodo para dar de alta una reparacion
     */
    public void Alta() {
        Reparaciones reparacion = new Reparaciones();
        AltaReparacion1 alta = new AltaReparacion1(this,true, reparacion);// se abre una ventana de alta que recibe como parametro una nueva reparacion
        alta.setVisible(true);
        // si se pulso el boton aceptar en la ventana de alta se persiste la entidad reparacion en el entitymanager con el controlador y se añade a la lista
            if(alta.isAcaptaAlta()){
                controladorReparaciones.create(reparacion);
                reparacionesList.add(reparacion);
                jListClientes.setSelectedIndex(reparacionesList.indexOf(reparacion));
                jListClientes.setEnabled(false);
                btAlta.setEnabled(false);
                btBuscar.setEnabled(false);
                btModificar.setEnabled(false);
                btEliminar.setEnabled(false);
                btCancelar.setEnabled(true);
                btActualizar.setEnabled(false);
                setNecesitaGuardar(true);
            }   
    }

    /**
     * metodo para guardar los cambios en la BD
     */
     public void Guardar() {
        try{
            // se cierra una transaccion del entity manager con un commit y se vuelve abrir una nueva con begin desde el controlador
            controladorReparaciones.Guardar();
            List lista = consultaReparaciones.getResultList(); // se ejecuta nuevamente la consulta para obtener la lista de reparaciones
            activarBotones();
            if(jListClientes.getSelectedIndex()!=-1){
                // se obtiene la reparacion seleccionda para volver a seleccionarlo una ves que se vuelva a cargar la lista
                Reparaciones r = reparacionesList.get(jListClientes.getSelectedIndex());
                reparacionesList.clear(); // se vacia la lista de clientes
                reparacionesList.addAll(lista); // se llena la lista de clientes
                jListClientes.clearSelection();
                controladorReparaciones.getEm().refresh(r);
                jListClientes.setSelectedIndex(reparacionesList.indexOf(r));
            }else{
                reparacionesList.clear(); // se vacia la lista de clientes
                reparacionesList.addAll(lista); // se llena la lista de clientes
            }

            desactivarCampos();
            JOptionPane.showMessageDialog(this, "Los cambios se guardaron correctamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            /*En caso de una excepción (la llamada a commit puede lanzar una RollbackException si la transacción ha sido deshecha anteriormente)
             * “mezclamos” los datos de las reparaciones con los datos almacenados en la base de datos.*/
        }catch(RollbackException e){
            //em.getTransaction().begin();
            List<Reparaciones> lista = new ArrayList<Reparaciones>(reparacionesList.size());
            for(int i=0;i<reparacionesList.size();i++){
                //lista.add(em.merge(listaReparaciones.get(i)));
            }
            reparacionesList.clear();
            reparacionesList.addAll(lista);
            String msg=e.getMessage();
            JOptionPane.showMessageDialog(this, msg, "Atencion", WIDTH);
        }
    }

      /**
     * Metodo para deshacer los cambio de la BD ignorando todo cambio que se haya hecho. Para ello es necesariodeshacer los cambios de la transacción e iniciar una
     * nueva. Luego se “refresca” la lista de reparaciones que tenemos con los datos almacenados en la base de datos, recuperándolos nuevamente volviendo a ejecutar la consulta del objeto Query
     * Este objeto query se ha creado desde el editor grfico (lo podemos ver en el editor grafico en otros componentes)
     */
    public void Actualizar() {

        Thread hebra = new Thread(new Runnable() {
            public void run() {
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    // se deshacen los cambios en el entitymanager con el controlador
                    desactivarBotones();
                    jProgressBar1.setValue(10);
                   // se deshacen los cambios con el controlador
                    controladorReparaciones.Deshacer();
                    Thread.sleep(500);
                    // se vacia la lista de acciones
                    List lista = consultaReparaciones.getResultList(); // se ejecuta nuevamente la consulta para obtener la lista de reparaciones
                    reparacionesList.clear(); // se vacia la lista de reparaciones
                    reparacionesList.addAll(lista); // se llena la lista de reparaciones
                    clientesList.clear();// se vacia la lista de clientes
                    clientesList.addAll(consultaClientes.getResultList());// se llena la lista de clientes
                    empleadosList.clear(); // se vacia la lista de empleados
                    empleadosList.addAll(consultaEmpleados.getResultList());// se llena la lista de empleados

                    jProgressBar1.setValue(50);
                    Thread.sleep(500);
                    desactivarCampos();
                 
                    jProgressBar1.setValue(100);
                    Thread.sleep(500);
                
                    Thread.sleep(500);
                    jProgressBar1.setValue(0);
                    activarBotones();
                    jListClientes.setSelectedIndex(-1);
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } catch (InterruptedException ex) {

                }
            }
        });
        hebra.start();
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

        ProyectoDAIPUEntityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("ProyectoDAIPU").createEntityManager();
        reparacionesQuery = java.beans.Beans.isDesignTime() ? null : ProyectoDAIPUEntityManager.createQuery("SELECT r FROM Reparaciones r");
        reparacionesList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(reparacionesQuery.getResultList());
        clientesQuery = java.beans.Beans.isDesignTime() ? null : ProyectoDAIPUEntityManager.createQuery("SELECT c FROM Clientes c");
        clientesList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(clientesQuery.getResultList());
        empleadosQuery = java.beans.Beans.isDesignTime() ? null : ProyectoDAIPUEntityManager.createQuery("SELECT e FROM Empleados e");
        empleadosList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(empleadosQuery.getResultList());
        accionesQuery = java.beans.Beans.isDesignTime() ? null : ProyectoDAIPUEntityManager.createQuery("SELECT a FROM Acciones a");
        accionesList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(accionesQuery.getResultList());
        list1 = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(accionesQuery.getResultList());
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListClientes = new javax.swing.JList();
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
        jLabel1 = new javax.swing.JLabel();
        cbAcciones = new javax.swing.JComboBox();
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
        jLabel15 = new javax.swing.JLabel();
        lbCompraSubtotal = new javax.swing.JLabel();
        lbIvaCompra = new javax.swing.JLabel();
        lbCompraTotal = new javax.swing.JLabel();
        lbCompraTotalValor = new javax.swing.JLabel();
        lbCompraIvaValor = new javax.swing.JLabel();
        lbCompraSubtotalValor = new javax.swing.JLabel();
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
        jLabel5 = new javax.swing.JLabel();
        lbTitulo = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Gestion de Reparaciones");
        setMinimumSize(new java.awt.Dimension(998, 678));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setName("jPanel2"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Reparaciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 51, 255))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jListClientes.setToolTipText("Lista de Reparaciones(muestra el numero de reparacion)");
        jListClientes.setName("jListClientes"); // NOI18N

        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, reparacionesList, jListClientes);
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create("${idReparacion}"));
        jListBinding.setSourceUnreadableValue(null);
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addContainerGap())
        );

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
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idEmpleado}"), cbEmpleados, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        cbClientes.setName("cbClientes"); // NOI18N

        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, clientesList, cbClientes);
        jComboBoxBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idCliente}"), cbClientes, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        cbEstados.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbEstados.setName("cbEstados"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${listaEstados}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, cbEstados);
        jComboBoxBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.estado}"), cbEstados, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        lbApellidos2.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbApellidos2.setText("Observaciones:");
        lbApellidos2.setName("lbApellidos2"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        taObservaciones.setColumns(20);
        taObservaciones.setLineWrap(true);
        taObservaciones.setRows(5);
        taObservaciones.setName("taObservaciones"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.observaciones}"), taObservaciones, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jScrollPane4.setViewportView(taObservaciones);

        lbApellidos3.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbApellidos3.setText("Diagnostico:");
        lbApellidos3.setName("lbApellidos3"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        taDiagnostico.setColumns(20);
        taDiagnostico.setLineWrap(true);
        taDiagnostico.setRows(5);
        taDiagnostico.setName("taDiagnostico"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.diagnostico}"), taDiagnostico, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jScrollPane5.setViewportView(taDiagnostico);

        lbIdReparacion.setForeground(new java.awt.Color(204, 0, 51));
        lbIdReparacion.setName("lbIdReparacion"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idReparacion}"), lbIdReparacion, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        lbFechaEntrada.setName("lbFechaEntrada"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.formatoFechaEntrada}"), lbFechaEntrada, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        lbFechaSalida.setName("lbFechaSalida"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListClientes, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.formatoFechaSalida}"), lbFechaSalida, org.jdesktop.beansbinding.BeanProperty.create("text"));
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
                        .addComponent(lbDni)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbIdReparacion, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbApellidos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbFechaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbApellidos1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbFechaSalida, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbDireccion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbEstados, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(lbApellidos2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                        .addComponent(lbApellidos3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbNumero)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                        .addComponent(lbNombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbNumero)
                    .addComponent(cbClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbNombre)
                    .addComponent(cbEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbDni)
                    .addComponent(lbIdReparacion, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbApellidos)
                    .addComponent(lbFechaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbApellidos1)
                    .addComponent(lbFechaSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbDireccion)
                    .addComponent(cbEstados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(lbApellidos3))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(lbApellidos2))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Añadir Accion"));
        jPanel9.setName("jPanel9"); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Accion.png"))); // NOI18N
        jLabel1.setText("Accion:");
        jLabel1.setName("jLabel1"); // NOI18N

        cbAcciones.setName("cbAcciones"); // NOI18N

        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, accionesList, cbAcciones);
        bindingGroup.addBinding(jComboBoxBinding);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(155, Short.MAX_VALUE))
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

        btAnadir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/alta1.png"))); // NOI18N
        btAnadir.setText("Añadir");
        btAnadir.setName("btAnadir"); // NOI18N
        btAnadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAnadirActionPerformed(evt);
            }
        });

        btEliminarAccion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/eliminar1.png"))); // NOI18N
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

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, list1, jtAcciones);
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
        jTableBinding.setSourceUnreadableValue(null);
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
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jLabel9.setName("jLabel9"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbAcciones, org.jdesktop.beansbinding.ELProperty.create("${selectedItem.precio} €"), jLabel9, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel4)))
                .addContainerGap())
        );

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/total1.png"))); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        lbCompraSubtotal.setFont(new java.awt.Font("Tahoma", 1, 11));
        lbCompraSubtotal.setText("Subtotal:");
        lbCompraSubtotal.setName("lbCompraSubtotal"); // NOI18N

        lbIvaCompra.setFont(new java.awt.Font("Tahoma", 1, 11));
        lbIvaCompra.setText("IVA");
        lbIvaCompra.setName("lbIvaCompra"); // NOI18N

        lbCompraTotal.setFont(new java.awt.Font("Tahoma", 1, 11));
        lbCompraTotal.setText("Total:");
        lbCompraTotal.setName("lbCompraTotal"); // NOI18N

        lbCompraTotalValor.setName("lbCompraTotalValor"); // NOI18N

        lbCompraIvaValor.setText("16%");
        lbCompraIvaValor.setName("lbCompraIvaValor"); // NOI18N

        lbCompraSubtotalValor.setName("lbCompraSubtotalValor"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btAnadir)
                        .addGap(18, 18, 18)
                        .addComponent(btEliminarAccion))
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                        .addComponent(jLabel15)
                        .addGap(36, 36, 36)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbCompraSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbCompraTotal)
                            .addComponent(lbIvaCompra))
                        .addGap(19, 19, 19)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbCompraTotalValor, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lbCompraIvaValor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE))
                            .addComponent(lbCompraSubtotalValor, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(57, 57, 57))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btAnadir)
                    .addComponent(btEliminarAccion))
                .addGap(8, 8, 8)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lbCompraSubtotalValor, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
                        .addGap(15, 15, 15)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbIvaCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbCompraIvaValor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(lbCompraTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(lbCompraTotalValor, javax.swing.GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE))))
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lbCompraSubtotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)))
                .addGap(7, 7, 7))
        );

        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.GridBagLayout());

        btAlta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/alta.png"))); // NOI18N
        btAlta.setMnemonic('n');
        btAlta.setText("Nuevo");
        btAlta.setToolTipText("Añadir una reparacion");
        btAlta.setName("btAlta"); // NOI18N
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
        btEliminar.setToolTipText("Eliminar una reparacion");
        btEliminar.setName("btEliminar"); // NOI18N
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
        btModificar.setToolTipText("Modificar una raparacion");
        btModificar.setName("btModificar"); // NOI18N
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
        btBuscar.setToolTipText("Buscar una reparacion");
        btBuscar.setName("btBuscar"); // NOI18N
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
        btGuardar.setName("btGuardar"); // NOI18N
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
        btActualizar.setName("btActualizar"); // NOI18N
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
        btSalir.setName("btSalir"); // NOI18N
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
        gridBagConstraints.ipadx = 45;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel8.add(btSalir, gridBagConstraints);

        btCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/eliminar.png"))); // NOI18N
        btCancelar.setText("Cancelar");
        btCancelar.setToolTipText("Cancelar un alta o una modificacion");
        btCancelar.setName("btCancelar"); // NOI18N
        btCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel8.add(btCancelar, gridBagConstraints);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionReparaciones.png"))); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        lbTitulo.setFont(new java.awt.Font("Tahoma 18 Negrita 18 Negrita", 1, 18));
        lbTitulo.setText("Gestion de Reparaciones");
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
                .addGap(299, 299, 299)
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

    private void btAnadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAnadirActionPerformed
        // TODO add your handling code here:
        anadirAccion();
    }//GEN-LAST:event_btAnadirActionPerformed

    private void btEliminarAccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEliminarAccionActionPerformed
        // TODO add your handling code here:
      eliminarAccion();
}//GEN-LAST:event_btEliminarAccionActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        Cerrar();
    }//GEN-LAST:event_formWindowClosing

    private void btAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAltaActionPerformed
        // TODO add your handling code here:
        Alta();
    }//GEN-LAST:event_btAltaActionPerformed

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
                gesReparaciones dialog = new gesReparaciones(new javax.swing.JFrame(), true);
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
    private java.util.List<Modelo.Acciones> accionesList;
    private javax.persistence.Query accionesQuery;
    private javax.swing.JButton btActualizar;
    private javax.swing.JButton btAlta;
    private javax.swing.JButton btAnadir;
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btCancelar;
    private javax.swing.JButton btEliminar;
    private javax.swing.JButton btEliminarAccion;
    private javax.swing.JButton btGuardar;
    private javax.swing.JButton btModificar;
    private javax.swing.JButton btSalir;
    private javax.swing.JComboBox cbAcciones;
    private javax.swing.JComboBox cbClientes;
    private javax.swing.JComboBox cbEmpleados;
    private javax.swing.JComboBox cbEstados;
    private java.util.List<Modelo.Clientes> clientesList;
    private javax.persistence.Query clientesQuery;
    private java.util.List<Modelo.Empleados> empleadosList;
    private javax.persistence.Query empleadosQuery;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jListClientes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
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
    private java.util.List<Acciones> list1;
    private java.util.List<Reparaciones> reparacionesList;
    private javax.persistence.Query reparacionesQuery;
    private javax.swing.JTextArea taDiagnostico;
    private javax.swing.JTextArea taObservaciones;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

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

}
