/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Inicio.java
 *
 * Created on 22-dic-2011, 20:19:36
 */

package proyectodai;

import Modelo.Empleados;
import Utilidades.Login;
import Utilidades.Splash;
import Utilidades.acercaDe;
import Utilidades.cambiarClave;
import Vistas.Acciones.gesAcciones;
import Vistas.Articulos.gesArticulos;
import Vistas.Clientes.gesClientes;
import Vistas.Emplados.gesEmpleados;
import Vistas.Facturacion.gesCompras;
import Vistas.Facturacion.totalDiaVentas;
import Vistas.Facturacion.totalMesVentas;
import Vistas.Reparaciones.gesReparaciones;
import java.awt.Cursor;
import java.net.URL;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author JuanPaulo
 */
public class Inicio extends javax.swing.JFrame {

    /**
     * @return the empleado
     */
    public static Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param aEmpleado the empleado to set
     */
    public static void setEmpleado(Empleados aEmpleado) {
        empleado = aEmpleado;
    }

    /**
     * En todas las clases de la vista se ha creado una unidad de persistencia(que no se utilizara) con el editor grafico simplemente para poder
     * crear las listas con el editor y enlazarla con los componentes. Para todas las operacions se utiliza el entityManager de las clase controladora
     */
    private static Empleados empleado = null;
    private Login login = new Login(this,true);
    
    private Splash splash;
   
    /** Creates new form Inicio */
    public Inicio() {
        iniciarSplash();
        splash.dispose();
        Utilidades.Utilidades.centrar(login);   
        initComponents();
        this.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Splash1234.jpeg")).getImage());
        // se inicia la ventana de login
        login.setVisible(true);
        empleado = login.getEmpleado();
        
        Utilidades.Utilidades.centrar(this);

        /** se comprueab si se pulsado cancelar o cerrado la ventana de login, si cerro o se cancelo se saldra del
         sistema, en caso contrario se accedera a el*/
        if(login.isSalir()){
            login.dispose();
            System.exit(0);
        }

        comprobarUsuario();
        lbEmpleado.setText(empleado.getNombre()+" "+ empleado.getApellidos());
        if(empleado.getUsuario().getTipo()){
            lbTipo.setText("Administrador");
        }else{
            lbTipo.setText("Tecnico");
        }

        lbHora.setText(Utilidades.Utilidades.hora());
        activarComponentes();
        crearSistemaAyuda();
        //Utilidades.Utilidades.maximizar(this);
        setVisible(true);
    }

    /**
     * metodo que desactivara los componentes cuando se cierre la sesion
     */
    private void desactivarComponentes(){
        iniciarSesionMenu.setEnabled(true);
        cerrarSesionMenu.setEnabled(false);
        EmpleadoMenu.setEnabled(false);
        ClienteMenu.setEnabled(false);
        ComprasMenu.setEnabled(false);
        ReparacionesMenu.setEnabled(false);
        ArticulosMenu.setEnabled(false);
        AccionesMenu.setEnabled(false);
        btClientes.setEnabled(false);
        btCompras.setEnabled(false);
        btReparaciones.setEnabled(false);
        btEmpleados.setEnabled(false);
        cambiarMenu.setEnabled(false);
        menuFacturacionDia.setEnabled(false);
        menuFacturacionMes.setEnabled(false);

    }

    /**
     * metodo que activara los componentes una vez que se haya iniciado la sesion
     */
    private void activarComponentes(){
        iniciarSesionMenu.setEnabled(false);
        cerrarSesionMenu.setEnabled(true);
        EmpleadoMenu.setEnabled(true);
        ClienteMenu.setEnabled(true);
        ComprasMenu.setEnabled(true);
        ReparacionesMenu.setEnabled(true);
        ArticulosMenu.setEnabled(true);
        AccionesMenu.setEnabled(true);
        btClientes.setEnabled(true);
        btCompras.setEnabled(true);
        btReparaciones.setEnabled(true);
        btEmpleados.setEnabled(true);
        cambiarMenu.setEnabled(true);
        menuFacturacionDia.setEnabled(true);
        menuFacturacionMes.setEnabled(true);

    }

    /**
     *  metodo que iniciara la sesion en el sistema
     */
    private void iniciarSesion(){
        final Login login = new Login(this,true);
        login.setVisible(true);
        Utilidades.Utilidades.centrar(login);
        if(!login.isSalir()){
          
            Thread hebra = new Thread(new Runnable() {
                public void run() {
                    try {
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        setEmpleado(login.getEmpleado());
                        jProgressBar1.setValue(10);
                        Thread.sleep(500);
                        jProgressBar1.setValue(50);
                        Thread.sleep(500);
                        jProgressBar1.setValue(100);
                        Thread.sleep(500);
                        activarComponentes();
                        comprobarUsuario();
                        jProgressBar1.setValue(0);
                        lbEmpleado.setText(empleado.getNombre()+" "+ empleado.getApellidos());
                        if(empleado.getUsuario().getTipo()){
                            lbTipo.setText("Administrador");
                        }else{
                            lbTipo.setText("Tecnico");
                        }
                        lbHora.setText(Utilidades.Utilidades.hora());
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    } catch (InterruptedException ex) {

                    }
                }
            });
            hebra.start();
        }
    }

    /**
     * Metodo que cerrara la sesion en el sistema
     */
    private void cerrarSesion(){
        int opcion= JOptionPane.showConfirmDialog(this, "¿Desea cerrar la sesion?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if(opcion==JOptionPane.YES_OPTION){
            Thread hebra = new Thread(new Runnable() {
                public void run() {
                    try {
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        desactivarComponentes();
                        jProgressBar1.setValue(10);
                        Thread.sleep(500);
                        jProgressBar1.setValue(50);
                        Thread.sleep(500);
                        empleado=null;
                        jProgressBar1.setValue(100);
                        Thread.sleep(500);
                        jProgressBar1.setValue(0);
                        if(empleado==null){
                           lbEmpleado.setText("");
                           lbTipo.setText("");
                        }else{
                            lbEmpleado.setText(empleado.getNombre()+" "+ empleado.getApellidos());
                            if(empleado.getUsuario().getTipo()){
                                lbTipo.setText("Administrador");
                            }else{
                                lbTipo.setText("Tecnico");
                            }
                        }
                        lbHora.setText("");                        
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    } catch (InterruptedException ex) {

                    }
                }
            });
            hebra.start();
            
        }
    }
    
    private void cambiar(){
        cambiarClave cambiar= new cambiarClave(this,true);
        cambiar.setVisible(true);
        if(!cambiar.isSalir()){
            cambiar.dispose();
        }
    }

    /**
     * Metodo que comprueba si el usuario que inicio la sesion en el sistema es administrador o no.
     * en caso de que sea administrador se hace visible el menu empleado, en caso contrario se desactiva.
     */
    private void comprobarUsuario(){

        if(getEmpleado().getUsuario().getTipo()){
            menuEmpleado.setVisible(true);
            menuAccion.setVisible(true);
            menuArticulo.setVisible(true);
            btEmpleados.setVisible(true);
            jSeparator4.setVisible(true);
            menuFacturacion.setVisible(true);
        }else{
            menuEmpleado.setVisible(false);
            menuAccion.setVisible(false);
            menuArticulo.setVisible(false);
            btEmpleados.setVisible(false);
            jSeparator4.setVisible(false);
            menuFacturacion.setVisible(false);
        }
    }

    /**
     * metodo para salir del sistema
     */
    public void Cerrar(){
        int opcion= JOptionPane.showConfirmDialog(this, "¿Desea salir del sistema?", "Atencion", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);

        if(opcion==JOptionPane.YES_OPTION){
            entityManager1.close();
            System.exit(0);
        }
    }

        /**Creamos el Sistema de Ayuda de nuestra apliaciÃ³n*/
    private void crearSistemaAyuda() {
        HelpSet hs = null;
        HelpBroker hb;
        // Buscamos el fichero .hs
        String helpHS = "Recursos/ayuda/ayuda.hs";
        ClassLoader cl = this.getClass().getClassLoader();
        try {
            URL hsURL = HelpSet.findHelpSet(cl, helpHS);
            hs = new HelpSet(cl, hsURL);
        } catch (Exception e) {
            System.out.println("HelpSet " + e.getMessage());
        }
        
        // Creamos el objeto HelpBroker
        hb = hs.createHelpBroker();
        //Lanzamos la ayuda al pulsar sobre el MenÃº Ayuda
        hb.enableHelpOnButton(ayudaMenu, "index", hs);
         //Habilitamos la tecla F1 para nuestra GUI
        hb.enableHelpKey(getRootPane(), "index", hs);
        //Lanzamos la ayuda despuÃ©s de elegir el componente sobre el que queremos ayuda
        jMI_QueEs.addActionListener(new CSH.DisplayHelpAfterTracking(hb));
        //Definimos la ayuda contextual
        hb.enableHelp(btClientes, "gestionClientes", hs);
        hb.enableHelp(btCompras, "gestionCompras", hs);
        hb.enableHelp(btEmpleados, "gestionEmpleados", hs);
        hb.enableHelp(btReparaciones, "gestionReparaciones", hs);
    }

    private void iniciarSplash (){
        splash = new Splash(new javax.swing.JFrame(), false);
        for(int i=1;i<=100;i++)
        {
            splash.actualizaEstado(i, String.valueOf(i));
            //progreso.setValue(i);

            Utilidades.Utilidades.pausa(100);
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

        entityManager1 = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("ProyectoDAIPU").createEntityManager();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        lbEmpleado = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbTipo = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lbHora = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btClientes = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btCompras = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btReparaciones = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btEmpleados = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btSalir = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        javax.swing.JMenu menuArchivo = new javax.swing.JMenu();
        iniciarSesionMenu = new javax.swing.JMenuItem();
        cerrarSesionMenu = new javax.swing.JMenuItem();
        usuarioMenu = new javax.swing.JMenu();
        cambiarMenu = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        SalirMenu = new javax.swing.JMenuItem();
        menuEmpleado = new javax.swing.JMenu();
        EmpleadoMenu = new javax.swing.JMenuItem();
        menuCliente = new javax.swing.JMenu();
        ClienteMenu = new javax.swing.JMenuItem();
        menuFacturacion = new javax.swing.JMenu();
        menuFacturacionDia = new javax.swing.JMenuItem();
        menuFacturacionMes = new javax.swing.JMenuItem();
        menuCompras = new javax.swing.JMenu();
        ComprasMenu = new javax.swing.JMenuItem();
        menuReparaciones = new javax.swing.JMenu();
        ReparacionesMenu = new javax.swing.JMenuItem();
        menuArticulo = new javax.swing.JMenu();
        ArticulosMenu = new javax.swing.JMenuItem();
        menuAccion = new javax.swing.JMenu();
        AccionesMenu = new javax.swing.JMenuItem();
        menuAspecto = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JMenu menuAyuda = new javax.swing.JMenu();
        ayudaMenu = new javax.swing.JMenuItem();
        jMI_QueEs = new javax.swing.JMenuItem();
        acercaDeMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("CompuSoft");
        setMinimumSize(new java.awt.Dimension(835, 577));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setLayout(new java.awt.GridBagLayout());
        jPanel1.add(jPanel8);

        getContentPane().add(jPanel1, java.awt.BorderLayout.EAST);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 896, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 414, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbEmpleado.setText("jLabel2");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Empleado:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel5.setText("Tipo de usuario:");

        lbTipo.setText("jLabel6");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel7.setText("Hora de Inicio:");

        lbHora.setText("jLabel6");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(jLabel1)
                .addGap(5, 5, 5)
                .addComponent(lbEmpleado)
                .addGap(114, 114, 114)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbTipo)
                .addGap(82, 82, 82)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbHora)
                .addContainerGap(121, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(lbEmpleado)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(lbTipo)
                            .addComponent(jLabel7)
                            .addComponent(lbHora)))))
        );

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        jToolBar1.setRollover(true);

        btClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionClientesInicio.png"))); // NOI18N
        btClientes.setText("Clientes");
        btClientes.setToolTipText("Gestion de Clientes");
        btClientes.setActionCommand("Gestion de Clientes");
        btClientes.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btClientes.setFocusable(false);
        btClientes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btClientes.setMaximumSize(new java.awt.Dimension(69, 55));
        btClientes.setMinimumSize(new java.awt.Dimension(69, 55));
        btClientes.setPreferredSize(new java.awt.Dimension(69, 55));
        btClientes.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btClientesActionPerformed(evt);
            }
        });
        jToolBar1.add(btClientes);
        jToolBar1.add(jSeparator1);

        btCompras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionComprasInicio.png"))); // NOI18N
        btCompras.setText("Compras");
        btCompras.setToolTipText("Gestion de Compras");
        btCompras.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btCompras.setFocusable(false);
        btCompras.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btCompras.setMaximumSize(new java.awt.Dimension(69, 55));
        btCompras.setMinimumSize(new java.awt.Dimension(69, 55));
        btCompras.setPreferredSize(new java.awt.Dimension(69, 55));
        btCompras.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btCompras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btComprasActionPerformed(evt);
            }
        });
        jToolBar1.add(btCompras);
        jToolBar1.add(jSeparator2);

        btReparaciones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionReparacionesInicio.png"))); // NOI18N
        btReparaciones.setText("Reparaciones");
        btReparaciones.setToolTipText("Gestion de Reparaciones");
        btReparaciones.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btReparaciones.setFocusable(false);
        btReparaciones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btReparaciones.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btReparaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btReparacionesActionPerformed(evt);
            }
        });
        jToolBar1.add(btReparaciones);
        jToolBar1.add(jSeparator3);

        btEmpleados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/gestionEmpleadosInicio.png"))); // NOI18N
        btEmpleados.setText("Empleados");
        btEmpleados.setToolTipText("Gestion de Empleados");
        btEmpleados.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btEmpleados.setFocusable(false);
        btEmpleados.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btEmpleados.setMaximumSize(new java.awt.Dimension(69, 55));
        btEmpleados.setMinimumSize(new java.awt.Dimension(69, 55));
        btEmpleados.setPreferredSize(new java.awt.Dimension(69, 55));
        btEmpleados.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btEmpleados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEmpleadosActionPerformed(evt);
            }
        });
        jToolBar1.add(btEmpleados);
        jToolBar1.add(jSeparator4);

        btSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/salirInicio3.png"))); // NOI18N
        btSalir.setText("Salir");
        btSalir.setToolTipText("Salir del sistema");
        btSalir.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btSalir.setFocusable(false);
        btSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btSalir.setMaximumSize(new java.awt.Dimension(69, 55));
        btSalir.setMinimumSize(new java.awt.Dimension(69, 55));
        btSalir.setPreferredSize(new java.awt.Dimension(69, 55));
        btSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSalirActionPerformed(evt);
            }
        });
        jToolBar1.add(btSalir);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/Logo1.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 358, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

        menuArchivo.setMnemonic('a');
        menuArchivo.setText("Archivo");

        iniciarSesionMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        iniciarSesionMenu.setText("Iniciar Sesion");
        iniciarSesionMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iniciarSesionMenuActionPerformed(evt);
            }
        });
        menuArchivo.add(iniciarSesionMenu);

        cerrarSesionMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        cerrarSesionMenu.setText("Cerrar Sesion");
        cerrarSesionMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarSesionMenuActionPerformed(evt);
            }
        });
        menuArchivo.add(cerrarSesionMenu);

        usuarioMenu.setMnemonic('u');
        usuarioMenu.setText("Usuario");

        cambiarMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        cambiarMenu.setMnemonic('b');
        cambiarMenu.setText("Cambiar Contraseña");
        cambiarMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambiarMenuActionPerformed(evt);
            }
        });
        usuarioMenu.add(cambiarMenu);

        menuArchivo.add(usuarioMenu);
        menuArchivo.add(jSeparator5);

        SalirMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        SalirMenu.setMnemonic('s');
        SalirMenu.setText("Salir");
        SalirMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalirMenuActionPerformed(evt);
            }
        });
        menuArchivo.add(SalirMenu);

        jMenuBar1.add(menuArchivo);

        menuEmpleado.setMnemonic('e');
        menuEmpleado.setText("Empleados");

        EmpleadoMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        EmpleadoMenu.setText("Gestion de empleados");
        EmpleadoMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EmpleadoMenuActionPerformed(evt);
            }
        });
        menuEmpleado.add(EmpleadoMenu);

        jMenuBar1.add(menuEmpleado);

        menuCliente.setMnemonic('c');
        menuCliente.setText("Clientes");

        ClienteMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        ClienteMenu.setText("Gestion de Clientes");
        ClienteMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClienteMenuActionPerformed(evt);
            }
        });
        menuCliente.add(ClienteMenu);

        jMenuBar1.add(menuCliente);

        menuFacturacion.setMnemonic('f');
        menuFacturacion.setText("Facturacion");

        menuFacturacionDia.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        menuFacturacionDia.setText("Ver facturacion del dia");
        menuFacturacionDia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFacturacionDiaActionPerformed(evt);
            }
        });
        menuFacturacion.add(menuFacturacionDia);

        menuFacturacionMes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        menuFacturacionMes.setText("Ver facturacion del mes");
        menuFacturacionMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFacturacionMesActionPerformed(evt);
            }
        });
        menuFacturacion.add(menuFacturacionMes);

        jMenuBar1.add(menuFacturacion);

        menuCompras.setMnemonic('p');
        menuCompras.setText("Compras");

        ComprasMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        ComprasMenu.setText("Gestion de compras");
        ComprasMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComprasMenuActionPerformed(evt);
            }
        });
        menuCompras.add(ComprasMenu);

        jMenuBar1.add(menuCompras);

        menuReparaciones.setMnemonic('r');
        menuReparaciones.setText("Reparaciones");

        ReparacionesMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        ReparacionesMenu.setText("Gestion de Reparaciones");
        ReparacionesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReparacionesMenuActionPerformed(evt);
            }
        });
        menuReparaciones.add(ReparacionesMenu);

        jMenuBar1.add(menuReparaciones);

        menuArticulo.setMnemonic('t');
        menuArticulo.setText("Articulos");

        ArticulosMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        ArticulosMenu.setText("Gestionar Articulos");
        ArticulosMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ArticulosMenuActionPerformed(evt);
            }
        });
        menuArticulo.add(ArticulosMenu);

        jMenuBar1.add(menuArticulo);

        menuAccion.setMnemonic('o');
        menuAccion.setText("Acciones");

        AccionesMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        AccionesMenu.setText("Gestionar Acciones");
        AccionesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AccionesMenuActionPerformed(evt);
            }
        });
        menuAccion.add(AccionesMenu);

        jMenuBar1.add(menuAccion);

        menuAspecto.setText("Aspecto");

        buttonGroup1.add(jRadioButtonMenuItem1);
        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("Sistema");
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        menuAspecto.add(jRadioButtonMenuItem1);

        buttonGroup1.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem2.setText("Multiplataforma");
        jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem2ActionPerformed(evt);
            }
        });
        menuAspecto.add(jRadioButtonMenuItem2);

        jMenuBar1.add(menuAspecto);

        menuAyuda.setMnemonic('y');
        menuAyuda.setText("Ayuda");

        ayudaMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        ayudaMenu.setText("Ayuda");
        menuAyuda.add(ayudaMenu);

        jMI_QueEs.setText("¿Que es...?");
        menuAyuda.add(jMI_QueEs);

        acercaDeMenu.setText("Acerca de");
        acercaDeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acercaDeMenuActionPerformed(evt);
            }
        });
        menuAyuda.add(acercaDeMenu);

        jMenuBar1.add(menuAyuda);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        // TODO add your handling code here:
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            /* Para que vuelva a dibujar la ventana con el nuevo aspecto, la hacemos invisible y luego la volvemos a mostrar.*/
            SwingUtilities.updateComponentTreeUI(this );
            this.setVisible(false);
            this.setVisible(true);
        }catch (Exception e){

        }
}//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        // TODO add your handling code here:
        try{
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        /* Hacemos desaparecer la ventana para volver a mostrarla. Con ello conseguimos que se dibuje con el nuevo aspecto
         * multiplataforma. */
            SwingUtilities.updateComponentTreeUI(this );
            this.setVisible(false);
            this.setVisible(true);
        }catch (Exception e){

        }
}//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void EmpleadoMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EmpleadoMenuActionPerformed
        // TODO add your handling code here:
        gesEmpleados emp = new gesEmpleados(this,true);
        emp.setVisible(true);
    }//GEN-LAST:event_EmpleadoMenuActionPerformed

    private void ClienteMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClienteMenuActionPerformed
        // TODO add your handling code here:
        gesClientes clie = new gesClientes(this,true);
        clie.setVisible(true);
    }//GEN-LAST:event_ClienteMenuActionPerformed

    private void ArticulosMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ArticulosMenuActionPerformed
        // TODO add your handling code here:
         gesArticulos art = new gesArticulos(this,true);
         art.setVisible(true);

        
    }//GEN-LAST:event_ArticulosMenuActionPerformed

    private void AccionesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccionesMenuActionPerformed
        // TODO add your handling code here:
        gesAcciones gestion = new gesAcciones(this,true);
        Utilidades.Utilidades.centrar(gestion);
        gestion.setVisible(true);
    }//GEN-LAST:event_AccionesMenuActionPerformed

    private void ReparacionesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReparacionesMenuActionPerformed
        // TODO add your handling code here:
        gesReparaciones reparacion= new gesReparaciones(this,true);
        reparacion.setVisible(true);
    }//GEN-LAST:event_ReparacionesMenuActionPerformed

    private void ComprasMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComprasMenuActionPerformed
        // TODO add your handling code here:
        gesCompras compra = new gesCompras(this,true);
        compra.setVisible(true);
    }//GEN-LAST:event_ComprasMenuActionPerformed

    private void SalirMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalirMenuActionPerformed
        // TODO add your handling code here:
       Cerrar();
    }//GEN-LAST:event_SalirMenuActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        Cerrar();
    }//GEN-LAST:event_formWindowClosing

    private void btEmpleadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEmpleadosActionPerformed
        // TODO add your handling code here:
        gesEmpleados emp = new gesEmpleados(this,true);
        emp.setVisible(true);
    }//GEN-LAST:event_btEmpleadosActionPerformed

    private void btComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btComprasActionPerformed
        // TODO add your handling code here:
        gesCompras compra = new gesCompras(this,true);
        compra.setVisible(true);
    }//GEN-LAST:event_btComprasActionPerformed

    private void btClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btClientesActionPerformed
        // TODO add your handling code here:
        gesClientes clie = new gesClientes(this,true);
        clie.setVisible(true);
    }//GEN-LAST:event_btClientesActionPerformed

    private void btSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSalirActionPerformed
        // TODO add your handling code here:
        Cerrar();
    }//GEN-LAST:event_btSalirActionPerformed

    private void iniciarSesionMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iniciarSesionMenuActionPerformed
        // TODO add your handling code here:
        iniciarSesion();
    }//GEN-LAST:event_iniciarSesionMenuActionPerformed

    private void cerrarSesionMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarSesionMenuActionPerformed
        // TODO add your handling code here:
        cerrarSesion();
    }//GEN-LAST:event_cerrarSesionMenuActionPerformed

    private void cambiarMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cambiarMenuActionPerformed
        // TODO add your handling code here:
        cambiar();
    }//GEN-LAST:event_cambiarMenuActionPerformed

    private void acercaDeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acercaDeMenuActionPerformed
        // TODO add your handling code here:
        acercaDe acerca = new acercaDe(this,true);
        acerca.setVisible(true);
    }//GEN-LAST:event_acercaDeMenuActionPerformed

    private void menuFacturacionDiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFacturacionDiaActionPerformed
        // TODO add your handling code here:
        totalDiaVentas ventas = new totalDiaVentas(this,true);
        ventas.setVisible(true);
    }//GEN-LAST:event_menuFacturacionDiaActionPerformed

    private void menuFacturacionMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFacturacionMesActionPerformed
        // TODO add your handling code here:
        totalMesVentas ventas = new totalMesVentas(this,true);
        ventas.setVisible(true);
    }//GEN-LAST:event_menuFacturacionMesActionPerformed

    private void btReparacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btReparacionesActionPerformed
        // TODO add your handling code here:
        gesReparaciones reparacion= new gesReparaciones(this,true);
        reparacion.setVisible(true);
    }//GEN-LAST:event_btReparacionesActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            
        }catch (Exception e){

        }
        new Inicio();
        /*java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try{
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    
                    new Inicio().setVisible(true);
                }catch (Exception e){

                }
                
            }
        });*/
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AccionesMenu;
    private javax.swing.JMenuItem ArticulosMenu;
    private javax.swing.JMenuItem ClienteMenu;
    private javax.swing.JMenuItem ComprasMenu;
    private javax.swing.JMenuItem EmpleadoMenu;
    private javax.swing.JMenuItem ReparacionesMenu;
    private javax.swing.JMenuItem SalirMenu;
    private javax.swing.JMenuItem acercaDeMenu;
    private javax.swing.JMenuItem ayudaMenu;
    private javax.swing.JButton btClientes;
    private javax.swing.JButton btCompras;
    private javax.swing.JButton btEmpleados;
    private javax.swing.JButton btReparaciones;
    private javax.swing.JButton btSalir;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem cambiarMenu;
    private javax.swing.JMenuItem cerrarSesionMenu;
    private javax.persistence.EntityManager entityManager1;
    private javax.swing.JMenuItem iniciarSesionMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenuItem jMI_QueEs;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbEmpleado;
    private javax.swing.JLabel lbHora;
    private javax.swing.JLabel lbTipo;
    private javax.swing.JMenu menuAccion;
    private javax.swing.JMenu menuArticulo;
    private javax.swing.JMenu menuAspecto;
    private javax.swing.JMenu menuCliente;
    private javax.swing.JMenu menuCompras;
    private javax.swing.JMenu menuEmpleado;
    private javax.swing.JMenu menuFacturacion;
    private javax.swing.JMenuItem menuFacturacionDia;
    private javax.swing.JMenuItem menuFacturacionMes;
    private javax.swing.JMenu menuReparaciones;
    private javax.swing.JMenu usuarioMenu;
    // End of variables declaration//GEN-END:variables

  

}
