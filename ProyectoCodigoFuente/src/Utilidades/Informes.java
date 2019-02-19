/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utilidades;


import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;

/**
 *  Clase que generara los informes
 * @author JuanPaulo
 */
public class Informes {

    public Informes(String nombre, Connection conexionBD) {
        this.nombre = nombre;
        this.conexionBD = conexionBD;
        guardado = false;
    }

    private String nombre = null;
    private JasperDesign informeFuente = null;
    private JasperReport informeCompilado = null;
    private JasperPrint informe = null;
    private Connection conexionBD = null;
    private boolean guardado;

    private static final String pathInformes = "/Recursos/Reportes/";

        /**Carga un fichero .jrxml para obtener un objeto JasperDesign*/
    public void cargarFuente() {
        String recurso = pathInformes + nombre + ".jrxml";
        InputStream streamInforme = this.getClass().getResourceAsStream(recurso);
        try {
            informeFuente = JRXmlLoader.load(streamInforme);
        } catch (JRException jre) {
            System.out.println("No puedo cargar el fichero JRXML: " + jre.getMessage());
        }
    }

        /**Compila el objeto JasperDesign y obtiene un objeto JasperReport*/
    public void compilar() {
        if (informeFuente != null) {
            long inicio = System.currentTimeMillis();
            try {
                informeCompilado = JasperCompileManager.compileReport(informeFuente);
                long fin = System.currentTimeMillis();
                System.out.println("Tiempo de compilaciÃ³n: " + (fin - inicio) + " ms.");
            } catch (JRException jre) {
                System.out.println("No puedo compilar el informe: " + jre.getMessage());
            }
        } else {
            System.out.println("El informe fuente no puedes ser null");
        }

    }

        /**Carga un fichero .jasper para obtener un objeto JasperReport*/
    public void cargarCompilado() {
        String recurso = pathInformes + nombre + ".jasper";
        URL urlInforme = this.getClass().getResource(recurso);
        try {
            informeCompilado = (JasperReport)JRLoader.loadObject(urlInforme);
        } catch (JRException jre) {
            System.out.println("No puedo cargar el fichero JASPER: " + jre.getMessage());
        }
    }

        /**Rellena el objeto JasperReport con los parÃ¡metros pasados y nuestra
     * conexiÃ³n a la BD para obtener un objeto JasperPrint.
     * @param parametros Map
     */
    public void rellenar(Map parametros) {
        if (informeCompilado == null)
            System.out.println("El informe compilado no puede ser null");
        else if (conexionBD == null)
            System.out.println("La conexion a la BD no puede ser nula");
        else {
            try {
                informe = JasperFillManager.fillReport(informeCompilado, parametros, conexionBD);
            } catch (JRException jre) {
                System.out.println("No puedo rellenar el informe: " + jre.getMessage());
            }
        }
    }

        /**Exporta el objeto JasperPrint a PDF.
     * @param ficheroSalida String
     */
    public void exportar(String ficheroSalida) {
        if (getInforme() == null)
            System.out.println("El informe relleno no puede ser null");
        else {
            long inicio = System.currentTimeMillis();

                //Para exportarlo a PDF
                try {
                    JasperExportManager.exportReportToPdfFile(getInforme(),ficheroSalida);
                    guardado=true;
                    long fin = System.currentTimeMillis();
                    System.out.println("Tiempo de exportaciÃ³n: " + (fin - inicio) + " ms.");


                } catch (JRException jre) {
                    System.out.println("Error al exportar a PDF: " + jre.getMessage());
                     guardado=false;
                }
        }


    }

        /**Visualiza el objeto JasperPrint*/
    public JRViewer visualizar() {
            //PodrÃ­amos haber utilizado la llamada JasperViewer.viewReport(informe, false)
            //que visualiza el informe, pero entonces no podrÃ­amos haberle dado
            //el foco para que aparezca encima de nuestra GUI.
            JasperViewer jasperViewer = new JasperViewer(getInforme(), true);
            JRViewer jrv = new JRViewer(getInforme());
            //jasperViewer.setVisible(true);
            //jasperViewer.requestFocus();


        return jrv;
    }

    /**
     * @return the informe
     */
    public JasperPrint getInforme() {
        return informe;
    }

    /**
     * @return the guardado
     */
    public boolean isGuardado() {
        return guardado;
    }

    /**
     * @param guardado the guardado to set
     */
    public void setGuardado(boolean guardado) {
        this.guardado = guardado;
    }
}
