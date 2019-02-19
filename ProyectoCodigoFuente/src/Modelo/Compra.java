/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "compra")
@NamedQueries({
    @NamedQuery(name = "Compra.findAll", query = "SELECT c FROM Compra c"),
    @NamedQuery(name = "Compra.findByNumeroCompra", query = "SELECT c FROM Compra c WHERE c.numeroCompra = :numeroCompra"),
    @NamedQuery(name = "Compra.findByFecha", query = "SELECT c FROM Compra c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Compra.findByIva", query = "SELECT c FROM Compra c WHERE c.iva = :iva")})
public class Compra implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "numero_compra")
    private Integer numeroCompra;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "iva")
    private Float iva;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compra")
    private List<LineasDetalles> lineasDetallesList;
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente")
    @ManyToOne(optional = false)
    private Clientes idCliente;

    @Transient
    private String formatoFecha;// variable que no esta asocioada a la BD que se utilizara para mostrar la fecha en String en tiempo de ejecucion

    @Transient
    private float totalCompra; // variable que no esta asocioada a la BD que se utilizara para calcular el total de la compra en tiempo de ejecucion

     @Transient
    private float totalCompraIva;// variable que no esta asocioada a la BD que se utilizara para calcular el total de la compra con iva en tiempo de ejecucion

    public Compra() {
    }

    public Compra(Integer numeroCompra) {
        this.numeroCompra = numeroCompra;
    }

    public Integer getNumeroCompra() {
        return numeroCompra;
    }

    public void setNumeroCompra(Integer numeroCompra) {
        Integer oldNumeroCompra = this.numeroCompra;
        this.numeroCompra = numeroCompra;
        changeSupport.firePropertyChange("numeroCompra", oldNumeroCompra, numeroCompra);
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        Date oldFecha = this.fecha;
        this.fecha = fecha;
        changeSupport.firePropertyChange("fecha", oldFecha, fecha);
    }

    public Float getIva() {
        return iva;
    }

    public void setIva(Float iva) {
        Float oldIva = this.iva;
        this.iva = iva;
        changeSupport.firePropertyChange("iva", oldIva, iva);
    }

    public List<LineasDetalles> getLineasDetallesList() {
        return lineasDetallesList;
    }

    public void setLineasDetallesList(List<LineasDetalles> lineasDetallesList) {
        this.lineasDetallesList = lineasDetallesList;
    }

    public Clientes getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Clientes idCliente) {
        Clientes oldIdCliente = this.idCliente;
        this.idCliente = idCliente;
        changeSupport.firePropertyChange("idCliente", oldIdCliente, idCliente);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numeroCompra != null ? numeroCompra.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Compra)) {
            return false;
        }
        Compra other = (Compra) object;
        if ((this.numeroCompra == null && other.numeroCompra != null) || (this.numeroCompra != null && !this.numeroCompra.equals(other.numeroCompra))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.Compra[numeroCompra=" + numeroCompra + "]";
    }

    /**
     * Este metodo devolvera la variable formatoFecha, antes de devolverla se formateara la fecha de la variable fecha de tipo Date a String
     * @return the formatoFecha 
     */
    public String getFormatoFecha() {
        if(getFecha()!=null){
            SimpleDateFormat sdf=new java.text.SimpleDateFormat("dd/MM/yyyy");
            String fecha = sdf.format(getFecha());
            formatoFecha=fecha;
        }
        return formatoFecha;
    }

    /**
     * @param formatoFecha the formatoFecha to set
     */
    public void setFormatoFecha(String formatoFecha) {
        this.formatoFecha = formatoFecha;
    }

    /**
     * Este metodo calculara el total de la compra en tiempo de ejecucion
     * @return the totalCompra
     */
    public float getTotalCompra() {
         float total = 0;
         totalCompraIva=0;
         if(!lineasDetallesList.isEmpty() || lineasDetallesList==null){

            for(int i=0;i<lineasDetallesList.size();i++){

                total+=lineasDetallesList.get(i).getCantidad()*lineasDetallesList.get(i).getIdarticulo().getPrecio();
            }

            totalCompra=total;
            totalCompraIva=(float) (totalCompra * 1.16);

        }
        return totalCompra;
    }

    /**
     * @param totalCompra the totalCompra to set
     */
    public void setTotalCompra(float totalCompra) {
        this.totalCompra = totalCompra;
    }

    /**
     * Este metodo calculara el total de la compra con iva en tiempo de ejecucion
     * @return the totalCompraIva
     */
    public float getTotalCompraIva() {

        float total = 0;
         totalCompraIva=0;
         if(!lineasDetallesList.isEmpty() || lineasDetallesList==null){

            for(int i=0;i<lineasDetallesList.size();i++){

                total+=lineasDetallesList.get(i).getCantidad()*lineasDetallesList.get(i).getIdarticulo().getPrecio();
            }

            //totalCompra=total;
            totalCompraIva=(float) (total * 1.16);

        }

        return totalCompraIva;
    }

    /**
     * @param totalCompraIva the totalCompraIva to set
     */
    public void setTotalCompraIva(float totalCompraIva) {
        this.totalCompraIva = totalCompraIva;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

}
