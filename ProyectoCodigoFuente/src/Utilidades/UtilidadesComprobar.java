/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utilidades;

/**
 *  Clase que hara las comprobaciones del Dni y del CP
 * @author JuanPaulo
 */
public class UtilidadesComprobar {
    /**
     * metodo para comprobar que el dni sea correcto, devuelve true si el dni es correcto y false en caso contrario
     * @param nif
     * @return
     */
    public static boolean comprobarNif(String nif){
           String dni="";
           Integer numDni=0;
           char letra;
           char letraCorrecta;

           if (nif.length()!=9){
               return false;
           }
           else{
               try{
                  dni=nif.substring(0, 8);
               }catch (IndexOutOfBoundsException ioobe){
                   System.out.println("El índice estaba de los límites de la cadena");
                   return false;
               }

               try {
                   numDni=Integer.parseInt(dni);
               }catch (NumberFormatException nfe){
                   System.out.println("El dni no puede contener caracteres no numéricos");
                   return false;
               }

               String letraString = nif.substring(8).toUpperCase();
               letra = letraString.charAt(0);

               letraCorrecta=("TRWAGMYFPDXBNJZSQVHLCKET".charAt(numDni % 23));

               return (letra == letraCorrecta);
           }
    }

    /**
     * metodo para comprobar que el codigo postal sea correcto, devuelve true si el codigo postal es correcto y false en caso contrario
     * solamente comprobara que el codigo postal sean 5 digitos
     * @param codigoPostal
     * @return
     */
    public static boolean comprobarCodigoPostal(String codigoPostal){
            if(codigoPostal.length()==5){
                return true;
            }else{
                return false;
            }
    }

    public static String mesString(int mes){
        String mesString = null;
        int mesInt=mes;

        switch (mesInt) {
            case 0:
                mesString = "Enero";
                break;
            case 1:
                mesString = "Febrero";
                break;
            case 2:
                mesString = "Marzo";
                break;
            case 3:
                mesString = "Abril";
                break;
            case 4:
                mesString = "Mayo";
                break;
            case 5:
                mesString = "Junio";
                break;
            case 6:
                mesString = "Julio";
                break;
            case 7:
                mesString = "Agosto";
                break;
            case 8:
                mesString = "Septimbre";
                break;
            case 9:
                mesString = "Octubre";
                break;
            case 10:
                mesString = "Noviembre";
                break;
            case 11:
                mesString = "Diciembre";
                break;
        }
        return mesString;
    }
}
