/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utilidades;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRViewer;


/**
 * Esta clase extiene de JRViewer, simplemente se utilizar para desactivar los botones del visor y añadir un boton para exportar a pdf
 * el constructor recibe como parametro un objeto JasperPrint
 * @author weejdu01
 */
public class vistaReporte extends JRViewer {

    private Informes informe;
    private JFileChooser jFC_Fichero = new JFileChooser();
    FiltroFicheros ff = null;
    String formato = "pdf";

    public vistaReporte(JasperPrint jrPrint, Informes informe){
        super(jrPrint);
        this.informe=informe;
        JButton btExportar = new JButton();
        btExportar.setText("Exportar a PDF");
        tlbToolBar.add(btExportar);
        this.btnActualSize.setVisible(false);
        this.btnFirst.setVisible(false);
        this.btnFitPage.setVisible(false);
        this.btnFitWidth.setVisible(false);
        this.btnLast.setVisible(false);
        this.btnNext.setVisible(false);
        this.btnPrevious.setVisible(false);
        this.btnReload.setVisible(false);
        this.btnSave.setVisible(false);
        this.btnZoomIn.setVisible(false);
        this.btnZoomOut.setVisible(false);
        this.cmbZoom.setVisible(false);

        //ImageIcon icobtExportar = new ImageIcon(this.getClass().getResource("/recursos/Imagenes/pdf1.png"));
        btExportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Imagenes/pdf1.png")));
        btExportar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportar();
            }
        });
        ff = new FiltroFicheros(formato);
        //bar.add(btExportar);
    }

    /**
     * metodo que abrira un JFileChooser y llamara al metodo exportar de la clase Informes para exportar la factura a PDF
     */
    private void exportar(){
        boolean seguir = false;
        
        String camino = null;
        File seleccionado;
        jFC_Fichero.addChoosableFileFilter(ff);
        seleccionado=jFC_Fichero.getSelectedFile();
        int valorDevuelto = jFC_Fichero.showSaveDialog(this);
        seleccionado=jFC_Fichero.getSelectedFile();
        if (valorDevuelto == JFileChooser.APPROVE_OPTION) {
            camino = jFC_Fichero.getSelectedFile().getPath();
            // Se comprueba que el nombre del archivo no exista, si existe preguntara si se desea reemplazarlo.
            if(new File(seleccionado.getAbsolutePath()+"."+formato).exists() || seleccionado.exists()){
                int opcion=  JOptionPane.showConfirmDialog(null, " El archivo ya existe, ¿Desea reemplazarlo? ", "Informacion",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                 if(opcion==JOptionPane.YES_OPTION){

                    if (camino.lastIndexOf("." + formato) == -1){
                        camino = camino + "." + formato;
                        seguir=true;
                    }else{
                        seguir = true;
                        jFC_Fichero.removeChoosableFileFilter(ff);
                    }
                 }
            }else{
                camino = jFC_Fichero.getSelectedFile().getPath();
                if (camino.lastIndexOf("." + formato) == -1)
                    camino = camino + "." + formato;
                    seguir = true;
            }
        }

        if (seguir) {
            final String fCamino = camino;
            informe.exportar(fCamino);
            if(informe.isGuardado()){
                JOptionPane.showMessageDialog(this,  "El archivo "+camino+" se guardo correctamente","informacion", JOptionPane.INFORMATION_MESSAGE);

            }else{
                JOptionPane.showMessageDialog(this,  "Erro al guardar el archivo "+camino,"informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
