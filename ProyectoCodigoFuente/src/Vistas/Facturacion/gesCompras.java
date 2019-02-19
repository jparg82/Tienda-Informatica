/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * gesCompras.java
 *
 * Created on 01-feb-2012, 17:26:31
 */

package Vistas.Facturacion;

import Controlador.ArticulosJpaController;
import Controlador.CompraJpaController;
import Controlador.LineasDetallesJpaController;
import Controlador.exceptions.IllegalOrphanException;
import Controlador.exceptions.NonexistentEntityException;
import Controlador.exceptions.PreexistingEntityException;
import Modelo.Articulos;
import Modelo.Compra;
import Modelo.LineasDetalles;
import Utilidades.Informes;
import Utilidades.ventanaInformes;
import Utilidades.vistaReporte;
import com.mysql.jdbc.Connection;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author weejdu01
 */

public class gesCompras extends javax.swing.JDialog {

    private LineasDetallesJpaController controladorLineas; //controlador que gestionara las transacciones y las operacion sdobre el entityManager
    private CompraJpaController controladorCompras; //controlador que gestionara las transacciones y las operacion sdobre el entityManager
    private boolean necesitaGuardar;// variable que se utilizara para saber si hay cambios que guardar
    private Query consultaCompras; // consulta que se utilizara para obtener la lista de compras
    private Query consultaArticulos; // consulta que se utilizara para obtener la lista de articulos
    private ArticulosJpaController controladorArticulos; //controlador que gestionara las transacciones y las operacion sdobre el entityManager

    /** Creates new form gesCompras */
    public gesCompras(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        controladorCompras= new CompraJpaController();
        controladorLineas= new LineasDetallesJpaController();
        controladorArticulos = new ArticulosJpaController();
        lineasDetallesList.clear(); // lista que contendra las lineas de detalles de cada compra (esta lista se creo con el editor grafico y esta en lazada a la tabla jtLineas)
        consultaCompras = controladorCompras.getEm().createQuery("SELECT c FROM Compra c");
        consultaArticulos = controladorArticulos.getEm().createQuery("SELECT a FROM Articulos a ");
        compraList.clear();  // se vacia la lista de compras (esta lista se creo con el editor grafico y esta en lazada con el jList jListCompras)
        compraList.addAll(consultaCompras.getResultList());// se llena la lista con el resultado de la consulta
        articulosList.clear(); // se vacia la lista de articulos (esta lista se creo con el editor grafico y esta en lazada con el combobox cbArticulos)
        articulosList.addAll(consultaArticulos.getResultList());// se llena la lista con el resultado de la consulta
        activarBotones();
        // crea el modelo del Spinner poniendo como avlor maximo la cantidad de articulos
        SpinnerNumberModel modeloSpinner = new SpinnerNumberModel();
        modeloSpinner.setMaximum(articulosList.get(cbArticulos.getSelectedIndex()).getUnidades());
        modeloSpinner.setMinimum(0);
        jSpinner1.setModel(modeloSpinner);

        /* Este listener es de modelo de la selección de nuestra lista. Cuando cambie el modelo de seleccion se tratara el evento y se comprobara
         si hay alguna fila seleccionada de la lista o no. si hay alguna fila seleccionada se activaran los botones modificar y eliminar, en caso contrario
         se desactivaran. Tambien  se vaciara la lista lineasDetallesList y se llenara con las lineas de la compra seleccionada, si no hay
         ninguna compra seleccionada se vaciara la lista lineasDetallesList*/
        jListCompras.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {

                    if(jListCompras.getSelectedIndex()!=-1 || jListCompras.getSelectedValue()!=null){

                        lineasDetallesList.clear();
                        lineasDetallesList.addAll(compraList.get(jListCompras.getSelectedIndex()).getLineasDetallesList());
                        lbCompraSubtotalValor.setText(String.valueOf(compraList.get(jListCompras.getSelectedIndex()).getTotalCompra()) + " €");
                        lbCompraTotalValor.setText(String.valueOf(compraList.get(jListCompras.getSelectedIndex()).getTotalCompraIva())+" €");
                        btImprimir.setEnabled(true);
                    }else{

                        lineasDetallesList.clear();
                        btImprimir.setEnabled(false);
                    }
                }
            });

            // se añade el listiner al combobox de articulos para que cada vez que se seleccione un articulo se cambie el modelo del spiner con los datos del articulo seleccionado
        cbArticulos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!articulosList.isEmpty()){
                    SpinnerNumberModel modeloSpinner = new SpinnerNumberModel();
                    modeloSpinner.setMaximum(articulosList.get(cbArticulos.getSelectedIndex()).getUnidades());
                    modeloSpinner.setMinimum(0);
                    jSpinner1.setModel(modeloSpinner);
                }
                
            }
        });

        /* Este listener es de modelo de la selección del jTable que muestra las lineas de la compra. Cuando cambie el modelo de seleccion se tratara el evento.
         * Si hay hay una fila selecciojada del jtArticulos se activara el boton eliminar, en caso contrario se desactiva.*/
         jtLineas.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if(!jListCompras.isEnabled()){
                        if(jtLineas.getSelectedRow()==-1){
                            btEliminarArticulo.setEnabled(false);
                        }else{
                            btEliminarArticulo.setEnabled(true);
                        }
                    }
                    
                }
        });

        Utilidades.Utilidades.centrar(this);
    }

    /**
     * metodo que activara los botones del formularaio(activa algunos botones y desactiva otros)
     */
    private void activarBotones() {
        btBuscar.setEnabled(true);
        btAlta.setEnabled(true);
        btActualizar.setEnabled(true);
        btCancelar.setEnabled(false);
        btSalir.setEnabled(true);
        jListCompras.setEnabled(true);
        setNecesitaGuardar(false);
        btAnadir.setEnabled(false);
        btImprimir.setEnabled(false);
        btEliminarArticulo.setEnabled(false);
        cbArticulos.setEnabled(false);
        jSpinner1.setEnabled(false);
        cbClientes.setEnabled(false);
    }

    /**
     * Metodo que desactivara los botones
     */
    private void desactivarBotones (){
        //desactivarCampos();
        btAlta.setEnabled(false);
        btCancelar.setEnabled(false);
        btBuscar.setEnabled(false);
        btActualizar.setEnabled(false);
        btSalir.setEnabled(false);
        jListCompras.setEnabled(false);
        setNecesitaGuardar(false);
    }

    /**
      * Metodo para cancelar un alta o una modificacion
      */
     private void cancelar(){
         // se comprueba que si hay algun cliente seleccionado
         if(jListCompras.getSelectedIndex()!=-1){
             // se obtiene el cliente seleccionado
             Compra c = compraList.get(jListCompras.getSelectedIndex());
             activarBotones();
             // se borra la selecion
             jListCompras.clearSelection();
             // si el id es null es porque se ha creado un cliente nuevo y no se ha guardado en la BD
             controladorArticulos.Deshacer();
             
             if(c.getNumeroCompra()!=null){
                 // si el id es distindto de null se actualizan los datos con los de la BD y se selecciona el cliente
                 controladorCompras.getEm().refresh(c);
                 jListCompras.setSelectedIndex(compraList.indexOf(c));
                 articulosList.clear();
                 articulosList.addAll(consultaArticulos.getResultList());
             }else{
                 c.setLineasDetallesList(null);
                 // si el id del cliente es null ,es porque todavia no se ha guardado en la BD, se elimina del entitymanager y de la lista de clientes
                 compraList.remove(c);
                try {
                    controladorCompras.destroy(c);
                } catch (IllegalOrphanException ex) {
                    Logger.getLogger(gesCompras.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(gesCompras.class.getName()).log(Level.SEVERE, null, ex);
                }
                 articulosList.clear();
                 articulosList.addAll(consultaArticulos.getResultList());
   
             }
         }
     }

    /**
     * Metodo que cierra la ventana de gestion de compras
     */
    private void Cerrar(){
        if(isNecesitaGuardar()){
            int opcion= JOptionPane.showConfirmDialog(this, "Hay cambios sin guardar ¿desea salir?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(opcion==JOptionPane.YES_OPTION){
                //em.getTransaction().rollback();
                controladorLineas.Deshacer();
                controladorCompras.Deshacer();
                controladorArticulos.Deshacer();
                this.dispose();
            }
        }else{
            this.dispose();
        }
    }

    /**
     * Metodo para añadir un articulo a la linea de detalles
     */
    private void anadirArticulo() {
    
        // solo se añadira el articulo si la cantidad es distinto de 0
        if ((Integer) jSpinner1.getValue() != 0) {
            // se obtiene el articulo seleccionado
            Articulos art = articulosList.get(cbArticulos.getSelectedIndex());
            // se crea uan nueva linea de detalle
            LineasDetalles linea = new LineasDetalles();
            // se añade la cantidad a la linea de detalle
            linea.setCantidad((Integer) jSpinner1.getValue());
            // se añade el articulo a la linea
            linea.setIdarticulo(art);
            // se modifica la cantidad del articulo
            art.setUnidades(art.getUnidades() - (Integer) jSpinner1.getValue());
            // se añade la linea a la lista de lineas de detalles lineasDetallesList, esta lista contendra la lista de lineas de detalles que se añadira a la compra
            //la lista esta enlazada con la tabla jTlineas,la lista se ha creado desde el editor grafico(se encuntra en otros componentes)
            lineasDetallesList.add(linea);
            // se añade la linea a la lista de lineas de la compra
            compraList.get(jListCompras.getSelectedIndex()).getLineasDetallesList().add(linea);

            // se actualiza el total y subtotal de la compra
            this.lbCompraSubtotalValor.setText(String.valueOf(compraList.get(jListCompras.getSelectedIndex()).getTotalCompra()) + " €");
            this.lbCompraTotalValor.setText(String.valueOf(compraList.get(jListCompras.getSelectedIndex()).getTotalCompraIva())+" €");
            
            // cada vez que se añada un articulo se cambiara el modelo del spiner con la cantidad actualizada del articulo
            SpinnerNumberModel modeloSpinner = new SpinnerNumberModel();
            modeloSpinner.setMaximum(articulosList.get(cbArticulos.getSelectedIndex()).getUnidades());
            modeloSpinner.setMinimum(0);
            jSpinner1.setModel(modeloSpinner);
        }
    }

    /**
     * metodo para eliminar una linea de detalle
     */
    private void eliminarLinea() {
        // TODO add your handling code here:
        Articulos art = articulosList.get(cbArticulos.getSelectedIndex());
        art.setUnidades(art.getUnidades() + lineasDetallesList.get(jtLineas.getSelectedRow()).getCantidad());
        int lineaBorrar = jtLineas.getSelectedRow();
        // se borra la linea a la lista de lineas de detalles lineasDetallesList, esta lista contendra la lista de lineas de detalles que se añadira a la compra
        //la lista esta enlazada con la tabla jTlineas,la lista se ha creado desde el editor grafico(se encuntra en otros componentes)
        lineasDetallesList.remove(lineaBorrar);
         // se elimina la linea a la lista de lineas de la compra
        compraList.get(jListCompras.getSelectedIndex()).getLineasDetallesList().remove(lineaBorrar);
         // se actualiza el total y subtotal de la compra
        this.lbCompraSubtotalValor.setText(String.valueOf(compraList.get(jListCompras.getSelectedIndex()).getTotalCompra()) + " €");
        this.lbCompraTotalValor.setText(String.valueOf(compraList.get(jListCompras.getSelectedIndex()).getTotalCompraIva())+" €");
        // cada vez que se elimine un articulo se cambiara el modelo del spiner con la cantidad actualizada del articulo
        SpinnerNumberModel modeloSpinner = new SpinnerNumberModel();
        modeloSpinner.setMaximum(articulosList.get(cbArticulos.getSelectedIndex()).getUnidades());
        modeloSpinner.setMinimum(0);
        jSpinner1.setModel(modeloSpinner);
    }

    /**
     * metodo que activara los componetes para poder añadir una compra, tambien añadira la compra a la lista de compras
     */
    private void nuevaCompra(){
        Compra compra = new Compra();
        compra.setLineasDetallesList(new ArrayList<LineasDetalles>());
        compraList.add(compra);
        jListCompras.setSelectedIndex(compraList.size()-1);
        cbClientes.setSelectedIndex(0);
        // se activan los botones para añadir y eliminar a rticulos a la compra. tambien se desactiva la lista de compras
        jListCompras.setEnabled(false);
        btAnadir.setEnabled(true);
        setNecesitaGuardar(true);
        //btEliminarArticulo.setEnabled(true);
        btImprimir.setEnabled(false);
        cbArticulos.setEnabled(true);
        jSpinner1.setEnabled(true);
        cbClientes.setEnabled(true);
        btAlta.setEnabled(false);
        btGuardar.setEnabled(true);
        btActualizar.setEnabled(false);
        btCancelar.setEnabled(true);
        btBuscar.setEnabled(false);

    }

    /**
     * Metodo que guardara la compra en la BD
     */
    private void facturar() {
        // TODO add your handling code here:
        // TODO mirar error al facturar sin linas de detalles
        if(lineasDetallesList.isEmpty()){
            JOptionPane.showMessageDialog(this, "No se pueden realizar facturas que no tengan articulos", "Atencion", WIDTH);
        }else{
            //se obtien la nueva compra de la lista de compras
            Compra c = compraList.get(jListCompras.getSelectedIndex());
            c.getLineasDetallesList().clear();        
            // se añade el cliente a la compra
            c.setIdCliente(clientesList.get(cbClientes.getSelectedIndex()));
            // se crea la compra en el entityManger con el controlador
            controladorCompras.create(c);
            // se guarda la compra en la bd
            controladorCompras.Guardar();
            /*se recorre la lista de lineas de detalles y se va creando cada linea en el entitymanager con el controlador*/
            for (int i = 0; i < lineasDetallesList.size(); i++) {                
                    // Se modifica la propieda compra de la linea con la nueva compra
                    lineasDetallesList.get(i).setCompra(c);
                try {
                    // se crea la linea con el entityManager desde el controlador
                    controladorLineas.create(lineasDetallesList.get(i));
                } catch (PreexistingEntityException ex) {
                    Logger.getLogger(gesCompras.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(gesCompras.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
            }
            // se guardan los cambios en la BD con el controlador
            controladorLineas.Guardar();
            controladorCompras.Guardar();
            controladorArticulos.Guardar();
            // se actualizan los datos de la compra con los de la BD
            controladorCompras.getEm().refresh(c);
            // se vuelve a cargar la lista de compras
            compraList.clear();
            compraList.addAll(consultaCompras.getResultList());
            // se selecciona la compra nueva para que se muestre por pantalla
            activarBotones();
            jListCompras.setSelectedIndex(compraList.indexOf(c));
             JOptionPane.showMessageDialog(this, "Los cambios se guardaron correctamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    /**
     * Metodo para deshacer los cambio de la BD ignorando todo cambio que se haya hecho. Para ello es necesariodeshacer los cambios de la transacción e iniciar una
     * nueva. Luego se “refresca” la lista de clientes que tenemos con los datos almacenados en la base de datos, recuperándolos nuevamente volviendo a ejecutar la consulta del objeto Query
     */
    public void Actualizar(){

        Thread hebra = new Thread(new Runnable() {
            public void run() {
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    // se deshacen los cambios en el entitymanager con el controlador
                    desactivarBotones();
                    jProgressBar1.setValue(10);
                    // se deshacen los cambios con el controlador
                    // se deshacen los cambios en el entitymanager con el controlador
                    controladorCompras.Deshacer();
                    controladorArticulos.Deshacer();
                    controladorLineas.Deshacer();
                    Thread.sleep(500);
                    // se vacian las listas y se vuelve a llenar actualizadas
                    compraList.clear();
                    compraList.addAll(consultaCompras.getResultList());
                    articulosList.clear();
                    articulosList.addAll(consultaArticulos.getResultList());
                    jProgressBar1.setValue(50);
                    Thread.sleep(500);
                    //desactivarCampos();

                    jProgressBar1.setValue(100);
                    Thread.sleep(500);

                    Thread.sleep(500);
                    jProgressBar1.setValue(0);
                    activarBotones();
                    jListCompras.setSelectedIndex(-1);
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } catch (InterruptedException ex) {

                }
            }
        });
        hebra.start();
    }

    /**
     * Metodo para buscar clientes
     */
    public void buscar(){
        buscarCompra busqueda = new buscarCompra(this,true);
        busqueda.setVisible(true);
        Query consultaBusqueda;
        int tipoBusqueda = busqueda.getSeleccionBusqueda();  // se obtiene el valor de la variable seleccion busqueda de la clase buscarCompra para saber el tipo de busqueda
        List<Compra> lista = new ArrayList();// esta lista contendra el resultado de la consulta segun el tipo de busqueda
        
        switch (tipoBusqueda){
            // si el tipoBusqueda es 0 se listaran todos las compras
            case 0:
                compraList.clear(); // se vacia la lista de clientes
                compraList.addAll(consultaCompras.getResultList()); // se llena la lista de clientes
                busqueda.dispose();
                break;
            // si el tipoBusqueda es 1 se listaran todos las compras con el id pasado como parametro en la consulta
            case 1:
                consultaBusqueda=controladorCompras.getEm().createQuery("SELECT c FROM Compra c WHERE c.numeroCompra = :numeroCompra");
                int id= busqueda.getId();
                consultaBusqueda.setParameter("numeroCompra", id);
                lista.addAll(consultaBusqueda.getResultList());

                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No se encontro la compra con id "+id, "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    compraList.clear();
                    compraList.addAll(lista);
                    jListCompras.setSelectedIndex(0);

                }
                busqueda.dispose();
                break;
            case 2:
                consultaBusqueda=controladorCompras.getEm().createQuery("SELECT c FROM Compra c WHERE c.idCliente.dniCliente = :dni");
                String dni=busqueda.getDni();
                consultaBusqueda.setParameter("dni", dni);
                lista.addAll(consultaBusqueda.getResultList());
                if(lista.isEmpty()){
                    JOptionPane.showMessageDialog(this, "El cliente con dni "+dni+" no tiene Compras", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    compraList.clear();
                    compraList.addAll(lista);
                }
        }
    }
    /**
     * metodo que sellamara al pulsar el boton factura
     */
    private void imprimir(){
        final Map parametros = getParametrosInforme();
        verExportarInforme(parametros);
    }

        /**Pregunta al usuario por los parametros a introducir dependiendo del tipo de Informe
     * @param tipoInforme String
     * @return HashMap
     * @throws EntradaCancelada
     */
    private HashMap getParametrosInforme()  {
        HashMap parametros = new HashMap();
        Compra c = compraList.get(jListCompras.getSelectedIndex());
        //Integer[] compra = null;
        int numeroCompra = c.getNumeroCompra();
        parametros.put("NCompra", numeroCompra);
        return parametros;
    }

        /**MÃ©todo que se encarga de ver el informe, realizando los pasos
     * intermedios que son necesarios (cargar, compilar, rellenar, visualizar).
     * @param tipoInforme String
     * @param parametros Map
     * @param exportar boolean
     * @param ficheroSalida String
     */
    private void verExportarInforme( Map parametros) {
        try {
            //UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)((JpaEntityManager)controladorCompras.getEm()..getDelegate()).getActiveSession();
            //unitOfWork.beginEarlyTransaction();
            //Accessor accessor = unitOfWork.getAccessor();
            //accessor.incrementCallCount(unitOfWork.getParent());
            //accessor.decrementCallCount();
            //java.sql.Connection connection = accessor.getConnection();
            Connection Conexion = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/tiendainformatica", "root", "root");
             Informes informe = new Informes("Compras1",Conexion);
        informe.cargarFuente();
        informe.compilar();
        informe.rellenar(parametros);
        //JRViewer jasperViewer=informe.visualizar();
        vistaReporte v= new vistaReporte(informe.getInforme(), informe);
        JDialog viewer = new JDialog(this," Factura", true);
        ventanaInformes ventana = new ventanaInformes(this,"factura",true,viewer.getContentPane());
        ventana.getContentPane().add(v);
        ventana.setVisible(true);
        ventana.setSize(800,600);
        } catch (SQLException ex) {
            Logger.getLogger(gesCompras.class.getName()).log(Level.SEVERE, null, ex);
        }

       
        //viewer.getContentPane().add(jasperViewer);
        //viewer.setSize(800,600);
        //viewer.setVisible(true);
        //informe.exportar(ficheroSalida, getFormato());
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
        compraQuery = java.beans.Beans.isDesignTime() ? null : entityManager1.createQuery("SELECT c FROM Compra c");
        compraList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(compraQuery.getResultList());
        clientesQuery = java.beans.Beans.isDesignTime() ? null : entityManager1.createQuery("SELECT c FROM Clientes c");
        clientesList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(clientesQuery.getResultList());
        articulosQuery = java.beans.Beans.isDesignTime() ? null : entityManager1.createQuery("SELECT a FROM Articulos a");
        articulosList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(articulosQuery.getResultList());
        lineasDetallesQuery = java.beans.Beans.isDesignTime() ? null : entityManager1.createQuery("SELECT l FROM LineasDetalles l");
        lineasDetallesList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(lineasDetallesQuery.getResultList());
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btAlta = new javax.swing.JButton();
        btBuscar = new javax.swing.JButton();
        btGuardar = new javax.swing.JButton();
        btActualizar = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListCompras = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        lbNumero = new javax.swing.JLabel();
        lbDni = new javax.swing.JLabel();
        lbNombre = new javax.swing.JLabel();
        lbDireccion = new javax.swing.JLabel();
        lbApellidos1 = new javax.swing.JLabel();
        cbClientes = new javax.swing.JComboBox();
        lbIdReparacion = new javax.swing.JLabel();
        lbFechaSalida = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbDni1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lbDireccion1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbDireccion2 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbArticulos = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        btAnadir = new javax.swing.JButton();
        btEliminarArticulo = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtLineas = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbCompraSubtotal = new javax.swing.JLabel();
        lbCompraSubtotalValor = new javax.swing.JLabel();
        lbIvaCompra = new javax.swing.JLabel();
        lbCompraTotal = new javax.swing.JLabel();
        lbCompraTotalValor = new javax.swing.JLabel();
        lbCompraIvaValor = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        btSalir = new javax.swing.JButton();
        btImprimir = new javax.swing.JButton();
        btCancelar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        lbTitulo = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Gestion de Compras");
        setMinimumSize(new java.awt.Dimension(1022, 680));
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
        btAlta.setText("Nueva compra");
        btAlta.setToolTipText("Añadir una compra");
        btAlta.setName("btAlta"); // NOI18N
        btAlta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAltaActionPerformed(evt);
            }
        });

        btBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/buscar.png"))); // NOI18N
        btBuscar.setMnemonic('b');
        btBuscar.setText("Buscar");
        btBuscar.setToolTipText("Buscar una compra");
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

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Compras", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 51, 255))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jListCompras.setToolTipText("Lista de Compras(muestra el numero de compra)");
        jListCompras.setName("jListCompras"); // NOI18N

        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, compraList, jListCompras);
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create("${numeroCompra}"));
        jListBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jListBinding);

        jScrollPane1.setViewportView(jListCompras);

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Compra", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 51, 255))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(545, 271));

        lbNumero.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNumero.setText("Cliente:");
        lbNumero.setName("lbNumero"); // NOI18N

        lbDni.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDni.setText("Nombre:");
        lbDni.setName("lbDni"); // NOI18N

        lbNombre.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbNombre.setText("Fecha:");
        lbNombre.setName("lbNombre"); // NOI18N

        lbDireccion.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDireccion.setText("Direccion:");
        lbDireccion.setName("lbDireccion"); // NOI18N

        lbApellidos1.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbApellidos1.setText("Apellidos:");
        lbApellidos1.setName("lbApellidos1"); // NOI18N

        cbClientes.setName("cbClientes"); // NOI18N

        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, clientesList, cbClientes);
        jComboBoxBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListCompras, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idCliente}"), cbClientes, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        lbIdReparacion.setName("lbIdReparacion"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListCompras, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idCliente.nombre}"), lbIdReparacion, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        lbFechaSalida.setName("lbFechaSalida"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListCompras, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idCliente.apellidos}"), lbFechaSalida, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jLabel5.setName("jLabel5"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListCompras, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.formatoFecha}"), jLabel5, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        lbDni1.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDni1.setText("NºCompra:");
        lbDni1.setName("lbDni1"); // NOI18N

        jLabel6.setForeground(new java.awt.Color(204, 0, 51));
        jLabel6.setName("jLabel6"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListCompras, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.numeroCompra}"), jLabel6, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jLabel7.setName("jLabel7"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListCompras, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idCliente.direccion}"), jLabel7, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        lbDireccion1.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDireccion1.setText("Provincia:");
        lbDireccion1.setName("lbDireccion1"); // NOI18N

        jLabel10.setName("jLabel10"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListCompras, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idCliente.codProvincia.provincia}"), jLabel10, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        lbDireccion2.setFont(new java.awt.Font("Tahoma", 1, 12));
        lbDireccion2.setText("Telefono:");
        lbDireccion2.setName("lbDireccion2"); // NOI18N

        jLabel11.setName("jLabel11"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jListCompras, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.idCliente.telefono}"), jLabel11, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lbDireccion2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(407, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lbDireccion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbDireccion1, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addGap(88, 88, 88))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lbDni, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbIdReparacion, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbApellidos1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbFechaSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(179, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbDni1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(lbNumero, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbClientes, 0, 158, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbNumero)
                    .addComponent(lbDni1)
                    .addComponent(lbNombre)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbDni)
                    .addComponent(lbApellidos1)
                    .addComponent(lbIdReparacion, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbFechaSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lbDireccion)
                                .addComponent(lbDireccion1))
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbDireccion2)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 51, 255))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Añadir Articulo"));
        jPanel9.setName("jPanel9"); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Articulos.png"))); // NOI18N
        jLabel1.setText("Articulo:");
        jLabel1.setName("jLabel1"); // NOI18N

        cbArticulos.setName("cbArticulos"); // NOI18N

        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, articulosList, cbArticulos);
        jComboBoxBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jComboBoxBinding);

        jLabel12.setText("unidades:");
        jLabel12.setName("jLabel12"); // NOI18N

        jSpinner1.setName("jSpinner1"); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        btAnadir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/alta1.png"))); // NOI18N
        btAnadir.setText("Añadir");
        btAnadir.setName("btAnadir"); // NOI18N
        btAnadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAnadirActionPerformed(evt);
            }
        });

        btEliminarArticulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/eliminar1.png"))); // NOI18N
        btEliminarArticulo.setText("Eliminar");
        btEliminarArticulo.setName("btEliminarArticulo"); // NOI18N
        btEliminarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEliminarArticuloActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Detalles"), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jtLineas.setName("jtLineas"); // NOI18N

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lineasDetallesList, jtLineas);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${idarticulo.nombre}"));
        columnBinding.setColumnName("Articulo");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${idarticulo.precio} €"));
        columnBinding.setColumnName("Precio €");
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${cantidad}"));
        columnBinding.setColumnName("Cantidad");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${precioArticulos} €"));
        columnBinding.setColumnName("Total Articulos €");
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane2.setViewportView(jtLineas);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalles Articulo"));
        jPanel5.setName("jPanel5"); // NOI18N

        jLabel2.setText("Id Articulo:");
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbArticulos, org.jdesktop.beansbinding.ELProperty.create("${selectedItem.descripcion}"), jTextArea3, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(jTextArea3);

        jLabel8.setName("jLabel8"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbArticulos, org.jdesktop.beansbinding.ELProperty.create("${selectedItem.idarticulo}"), jLabel8, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel9.setName("jLabel9"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbArticulos, org.jdesktop.beansbinding.ELProperty.create("${selectedItem.precio} €"), jLabel9, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel4)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbCompraSubtotal.setFont(new java.awt.Font("Tahoma", 1, 11));
        lbCompraSubtotal.setText("Subtotal:");
        lbCompraSubtotal.setName("lbCompraSubtotal"); // NOI18N

        lbCompraSubtotalValor.setName("lbCompraSubtotalValor"); // NOI18N

        lbIvaCompra.setFont(new java.awt.Font("Tahoma", 1, 11));
        lbIvaCompra.setText("IVA");
        lbIvaCompra.setName("lbIvaCompra"); // NOI18N

        lbCompraTotal.setFont(new java.awt.Font("Tahoma", 1, 11));
        lbCompraTotal.setText("Total:");
        lbCompraTotal.setName("lbCompraTotal"); // NOI18N

        lbCompraTotalValor.setName("lbCompraTotalValor"); // NOI18N

        lbCompraIvaValor.setText("16%");
        lbCompraIvaValor.setName("lbCompraIvaValor"); // NOI18N

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/total1.png"))); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btAnadir)
                        .addGap(18, 18, 18)
                        .addComponent(btEliminarArticulo)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                        .addGap(48, 48, 48)))
                .addGap(20, 20, 20))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
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
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btAnadir)
                            .addComponent(btEliminarArticulo))
                        .addGap(26, 26, 26)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(207, 207, 207)
                        .addComponent(lbCompraSubtotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)))
                .addContainerGap())
        );

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

        btImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/factura.png"))); // NOI18N
        btImprimir.setText("Factura");
        btImprimir.setToolTipText("Muestra la factura");
        btImprimir.setName("btImprimir"); // NOI18N
        btImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btImprimirActionPerformed(evt);
            }
        });

        btCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/eliminar.png"))); // NOI18N
        btCancelar.setText("Cancelar");
        btCancelar.setToolTipText("Cancelar un alta");
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1019, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE))
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 864, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                .addComponent(btImprimir, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                .addComponent(btSalir, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                .addComponent(btGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btAlta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(8, 8, 8)))
                .addGap(20, 20, 20))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(btAlta)
                        .addGap(18, 18, 18)
                        .addComponent(btBuscar)
                        .addGap(18, 18, 18)
                        .addComponent(btCancelar)
                        .addGap(18, 18, 18)
                        .addComponent(btGuardar)
                        .addGap(18, 18, 18)
                        .addComponent(btActualizar)
                        .addGap(18, 18, 18)
                        .addComponent(btImprimir)
                        .addGap(18, 18, 18)
                        .addComponent(btSalir)))
                .addContainerGap())
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionCompras.png"))); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        lbTitulo.setFont(new java.awt.Font("Tahoma 18 Negrita 18 Negrita", 1, 18));
        lbTitulo.setText("Gestion de Compras");
        lbTitulo.setName("lbTitulo"); // NOI18N

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Logo1.png"))); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addGap(327, 327, 327)
                .addComponent(jLabel13)
                .addGap(5, 5, 5)
                .addComponent(lbTitulo))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel13))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lbTitulo))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel14)
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
        // TODO add your handling code here:
        nuevaCompra();
}//GEN-LAST:event_btAltaActionPerformed

    private void btBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBuscarActionPerformed
        // TODO add your handling code here:
        buscar();
}//GEN-LAST:event_btBuscarActionPerformed

    private void btGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGuardarActionPerformed
        // TODO add your handling code here:
        facturar();
}//GEN-LAST:event_btGuardarActionPerformed

    private void btActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btActualizarActionPerformed
        // TODO add your handling code here:
        Actualizar();
}//GEN-LAST:event_btActualizarActionPerformed

    private void btAnadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAnadirActionPerformed
        // TODO add your handling code here:
        anadirArticulo();

}//GEN-LAST:event_btAnadirActionPerformed

    private void btEliminarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEliminarArticuloActionPerformed
        // TODO add your handling code here:
        eliminarLinea();
}//GEN-LAST:event_btEliminarArticuloActionPerformed

    private void btSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSalirActionPerformed
        // TODO add your handling code here:
        Cerrar();
    }//GEN-LAST:event_btSalirActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        Cerrar();
    }//GEN-LAST:event_formWindowClosing

    private void btImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btImprimirActionPerformed
        // TODO add your handling code here:
        imprimir();
    }//GEN-LAST:event_btImprimirActionPerformed

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
                gesCompras dialog = new gesCompras(new javax.swing.JFrame(), true);
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
    private java.util.List<Modelo.Articulos> articulosList;
    private javax.persistence.Query articulosQuery;
    private javax.swing.JButton btActualizar;
    private javax.swing.JButton btAlta;
    private javax.swing.JButton btAnadir;
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btCancelar;
    private javax.swing.JButton btEliminarArticulo;
    private javax.swing.JButton btGuardar;
    private javax.swing.JButton btImprimir;
    private javax.swing.JButton btSalir;
    private javax.swing.JComboBox cbArticulos;
    private javax.swing.JComboBox cbClientes;
    private java.util.List<Modelo.Clientes> clientesList;
    private javax.persistence.Query clientesQuery;
    private java.util.List<Compra> compraList;
    private javax.persistence.Query compraQuery;
    private javax.persistence.EntityManager entityManager1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jListCompras;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTable jtLineas;
    private javax.swing.JLabel lbApellidos1;
    private javax.swing.JLabel lbCompraIvaValor;
    private javax.swing.JLabel lbCompraSubtotal;
    private javax.swing.JLabel lbCompraSubtotalValor;
    private javax.swing.JLabel lbCompraTotal;
    private javax.swing.JLabel lbCompraTotalValor;
    private javax.swing.JLabel lbDireccion;
    private javax.swing.JLabel lbDireccion1;
    private javax.swing.JLabel lbDireccion2;
    private javax.swing.JLabel lbDni;
    private javax.swing.JLabel lbDni1;
    private javax.swing.JLabel lbFechaSalida;
    private javax.swing.JLabel lbIdReparacion;
    private javax.swing.JLabel lbIvaCompra;
    private javax.swing.JLabel lbNombre;
    private javax.swing.JLabel lbNumero;
    private javax.swing.JLabel lbTitulo;
    private java.util.List<Modelo.LineasDetalles> lineasDetallesList;
    private javax.persistence.Query lineasDetallesQuery;
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

}
