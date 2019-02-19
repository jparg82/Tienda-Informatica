/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Esta clase sera la encargada de contener el modelos de datos. La mayor parte del codigo es generada por el entorno de desarrollo.
 * Las clases del paquete del paquete modelo contendra las entidades. Una entidad no es más que una simple clase POJO de tipo JavaBean
 * cuyos campos representan el estado persistente asociado a la entidad.
 * Los valores almacenados en los campos de un objeto de una clase de entidad representan información existente en una base de datos.
 * Típicamente, una entidad se encuentra asociada a una tabla de la base de datos y define un campo por cada columna de la misma,
 * de manera que cada objeto de entidad contendrá los datos de un registro de dicha tabla
 * @author JuanPaulo
 */
@Entity
@Table(name = "lineas_detalles")
@NamedQueries({
    @NamedQuery(name = "LineasDetalles.findAll", query = "SELECT l FROM LineasDetalles l"),
    @NamedQuery(name = "LineasDetalles.findByCodLinea", query = "SELECT l FROM LineasDetalles l WHERE l.lineasDetallesPK.codLinea = :codLinea"),
    @NamedQuery(name = "LineasDetalles.findByCantidad", query = "SELECT l FROM LineasDetalles l WHERE l.cantidad = :cantidad"),
    @NamedQuery(name = "LineasDetalles.findByNumeroCompra", query = "SELECT l FROM LineasDetalles l WHERE l.lineasDetallesPK.numeroCompra = :numeroCompra")})
public class LineasDetalles implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected LineasDetallesPK lineasDetallesPK;
    @Column(name = "cantidad")
    private Integer cantidad;
    @JoinColumn(name = "numero_compra", referencedColumnName = "numero_compra", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Compra compra;
    @JoinColumn(name = "Id_articulo", referencedColumnName = "Id_articulo")
    @ManyToOne
    private Articulos idarticulo;

    @Transient
    private float PrecioArticulos;// variable que almacenara el el precio del articulo por la cantidad

    public LineasDetalles() {
    }

    public LineasDetalles(LineasDetallesPK lineasDetallesPK) {
        this.lineasDetallesPK = lineasDetallesPK;
    }

    public LineasDetalles(int codLinea, int numeroCompra) {
        this.lineasDetallesPK = new LineasDetallesPK(codLinea, numeroCompra);
    }

    public LineasDetallesPK getLineasDetallesPK() {
        return lineasDetallesPK;
    }

    public void setLineasDetallesPK(LineasDetallesPK lineasDetallesPK) {
        this.lineasDetallesPK = lineasDetallesPK;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        Integer oldCantidad = this.cantidad;
        this.cantidad = cantidad;
        changeSupport.firePropertyChange("cantidad", oldCantidad, cantidad);
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        Compra oldCompra = this.compra;
        this.compra = compra;
        changeSupport.firePropertyChange("compra", oldCompra, compra);
    }

    public Articulos getIdarticulo() {
        return idarticulo;
    }

    public void setIdarticulo(Articulos idarticulo) {
        Articulos oldIdarticulo = this.idarticulo;
        this.idarticulo = idarticulo;
        changeSupport.firePropertyChange("idarticulo", oldIdarticulo, idarticulo);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lineasDetallesPK != null ? lineasDetallesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LineasDetalles)) {
            return false;
        }
        LineasDetalles other = (LineasDetalles) object;
        if ((this.lineasDetallesPK == null && other.lineasDetallesPK != null) || (this.lineasDetallesPK != null && !this.lineasDetallesPK.equals(other.lineasDetallesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.LineasDetalles[lineasDetallesPK=" + lineasDetallesPK + "]";
    }

    /**
     * Se calculacula en timepo de ejecucion el precio del articulo por la cantidad
     * @return the PrecioArticulos
     */
    public float getPrecioArticulos() {
        PrecioArticulos=idarticulo.getPrecio()*cantidad;
        return PrecioArticulos;
    }

    /**
     * @param PrecioArticulos the PrecioArticulos to set
     */
    public void setPrecioArticulos(float PrecioArticulos) {
        this.PrecioArticulos = PrecioArticulos;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }


}
