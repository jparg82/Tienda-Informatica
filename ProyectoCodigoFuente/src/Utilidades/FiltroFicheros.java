/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utilidades;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FiltroFicheros extends FileFilter {
    private String formato;

    /**Constructor de la clase al que le pasamos el formato por el que queremos filtrar
     * @param formato String
     */
    public FiltroFicheros(String formato) {
        this.formato = formato;
    }

    /**Devuelve true si la seleccion tiene la extension del filtro
     * @param f File
     * @return boolean
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            String extension = s.substring(i+1).toLowerCase();
            if (formato.equals(extension)) {
                    return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**Devuelve la descripciÃ³n del filtro
     * @return String
     */
    public String getDescription() {
        return "*." + formato;
    }
}
