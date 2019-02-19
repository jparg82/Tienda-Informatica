/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Esta clase sera la encargada de contener el modelos de datos. La mayor parte del codigo es generada por el entorno de desarrollo.
 * Las clases del paquete del paquete modelo contendra las entidades. Una entidad no es más que una simple clase POJO de tipo JavaBean
 * cuyos campos representan el estado persistente asociado a la entidad.
 * Los valores almacenados en los campos de un objeto de una clase de entidad representan información existente en una base de datos.
 * Típicamente, una entidad se encuentra asociada a una tabla de la base de datos y define un campo por cada columna de la misma,
 * de manera que cada objeto de entidad contendrá los datos de un registro de dicha tabla
 * @author JuanPaulo
 */
@Embeddable
public class LineasDetallesPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "cod_linea")
    private int codLinea;
    @Basic(optional = false)
    @Column(name = "numero_compra")
    private int numeroCompra;

    public LineasDetallesPK() {
    }

    public LineasDetallesPK(int codLinea, int numeroCompra) {
        this.codLinea = codLinea;
        this.numeroCompra = numeroCompra;
    }

    public int getCodLinea() {
        return codLinea;
    }

    public void setCodLinea(int codLinea) {
        this.codLinea = codLinea;
    }

    public int getNumeroCompra() {
        return numeroCompra;
    }

    public void setNumeroCompra(int numeroCompra) {
        this.numeroCompra = numeroCompra;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) codLinea;
        hash += (int) numeroCompra;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LineasDetallesPK)) {
            return false;
        }
        LineasDetallesPK other = (LineasDetallesPK) object;
        if (this.codLinea != other.codLinea) {
            return false;
        }
        if (this.numeroCompra != other.numeroCompra) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.LineasDetallesPK[codLinea=" + codLinea + ", numeroCompra=" + numeroCompra + "]";
    }

}
