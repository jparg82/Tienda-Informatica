/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "articulos")
@NamedQueries({
    @NamedQuery(name = "Articulos.findAll", query = "SELECT a FROM Articulos a"),
    @NamedQuery(name = "Articulos.findByIdarticulo", query = "SELECT a FROM Articulos a WHERE a.idarticulo = :idarticulo"),
    @NamedQuery(name = "Articulos.findByNombre", query = "SELECT a FROM Articulos a WHERE a.nombre = :nombre"),
    @NamedQuery(name = "Articulos.findByDescripcion", query = "SELECT a FROM Articulos a WHERE a.descripcion = :descripcion"),
    @NamedQuery(name = "Articulos.findByUnidades", query = "SELECT a FROM Articulos a WHERE a.unidades = :unidades"),
    @NamedQuery(name = "Articulos.findByPrecio", query = "SELECT a FROM Articulos a WHERE a.precio = :precio")})
public class Articulos implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id_articulo")
    private Integer idarticulo;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "unidades")
    private Integer unidades;
    @Column(name = "precio")
    private Float precio;
    @OneToMany(mappedBy = "idarticulo")
    private List<LineasDetalles> lineasDetallesList;

    public Articulos() {
        
    }

    public Articulos(Integer idarticulo) {
        
        this.idarticulo = idarticulo;
    }

    public Integer getIdarticulo() {
        return idarticulo;
    }

    public void setIdarticulo(Integer idarticulo) {
        Integer oldIdarticulo = this.idarticulo;
        this.idarticulo = idarticulo;
        changeSupport.firePropertyChange("idarticulo", oldIdarticulo, idarticulo);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        String oldNombre = this.nombre;
        this.nombre = nombre;
        changeSupport.firePropertyChange("nombre", oldNombre, nombre);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        String oldDescripcion = this.descripcion;
        this.descripcion = descripcion;
        changeSupport.firePropertyChange("descripcion", oldDescripcion, descripcion);
    }

    public Integer getUnidades() {
        return unidades;
    }

    public void setUnidades(Integer unidades) {

        Integer oldUnidades = this.unidades;
        this.unidades = unidades;
        changeSupport.firePropertyChange("unidades", oldUnidades, unidades);
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        Float oldPrecio = this.precio;
        this.precio = precio;
        changeSupport.firePropertyChange("precio", oldPrecio, precio);
    }

    public List<LineasDetalles> getLineasDetallesList() {
        return lineasDetallesList;
    }

    public void setLineasDetallesList(List<LineasDetalles> lineasDetallesList) {
        this.lineasDetallesList = lineasDetallesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idarticulo != null ? idarticulo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Articulos)) {
            return false;
        }
        Articulos other = (Articulos) object;
        if ((this.idarticulo == null && other.idarticulo != null) || (this.idarticulo != null && !this.idarticulo.equals(other.idarticulo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre ;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

}
