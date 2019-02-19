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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "reparaciones")
@NamedQueries({
    @NamedQuery(name = "Reparaciones.findAll", query = "SELECT r FROM Reparaciones r"),
    @NamedQuery(name = "Reparaciones.findByIdReparacion", query = "SELECT r FROM Reparaciones r WHERE r.idReparacion = :idReparacion"),
    @NamedQuery(name = "Reparaciones.findByDiagnostico", query = "SELECT r FROM Reparaciones r WHERE r.diagnostico = :diagnostico"),
    @NamedQuery(name = "Reparaciones.findByFechaEnt", query = "SELECT r FROM Reparaciones r WHERE r.fechaEnt = :fechaEnt"),
    @NamedQuery(name = "Reparaciones.findByFechaSal", query = "SELECT r FROM Reparaciones r WHERE r.fechaSal = :fechaSal"),
    @NamedQuery(name = "Reparaciones.findByObservaciones", query = "SELECT r FROM Reparaciones r WHERE r.observaciones = :observaciones"),
    @NamedQuery(name = "Reparaciones.findByEstado", query = "SELECT r FROM Reparaciones r WHERE r.estado = :estado")})
public class Reparaciones implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_reparacion")
    private Integer idReparacion;
    @Column(name = "diagnostico")
    private String diagnostico;
    @Column(name = "fecha_ent")
    @Temporal(TemporalType.DATE)
    private Date fechaEnt;
    @Column(name = "fecha_sal")
    @Temporal(TemporalType.DATE)
    private Date fechaSal;
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "estado")
    private String estado;
    @JoinTable(name = "rep_accion", joinColumns = {
        @JoinColumn(name = "id_reparacion", referencedColumnName = "id_reparacion")}, inverseJoinColumns = {
        @JoinColumn(name = "id_accion", referencedColumnName = "id_accion")})
    @ManyToMany
    private List<Acciones> accionesList;
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente")
    @ManyToOne(optional = false)
    private Clientes idCliente;
    @JoinColumn(name = "id_empleado", referencedColumnName = "id_empleado")
    @ManyToOne(optional = false)
    private Empleados idEmpleado;

    @Transient
    private String formatoFechaEntrada; //variable que se utilizara para mostrar la fecha de entrada en String

    @Transient
    private String formatoFechaSalida;//variable que se utilizara para mostrar la fecha de salida en String

    @Transient
    private float totalReparacion;//variable que se utilizara para mostrar la fecha de salida en String

     @Transient
    private float totalReparacionIva;//variable que se utilizara para mostrar la fecha de salida en String

    public Reparaciones() {
    }

    public Reparaciones(Integer idReparacion) {
        this.idReparacion = idReparacion;
    }

    public Integer getIdReparacion() {
        return idReparacion;
    }

    public void setIdReparacion(Integer idReparacion) {
        Integer oldIdReparacion = this.idReparacion;
        this.idReparacion = idReparacion;
        changeSupport.firePropertyChange("idReparacion", oldIdReparacion, idReparacion);
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        String oldDiagnostico = this.diagnostico;
        this.diagnostico = diagnostico;
        changeSupport.firePropertyChange("diagnostico", oldDiagnostico, diagnostico);
    }

    public Date getFechaEnt() {
        return fechaEnt;
    }

    public void setFechaEnt(Date fechaEnt) {
        Date oldFechaEnt = this.fechaEnt;
        this.fechaEnt = fechaEnt;
        changeSupport.firePropertyChange("fechaEnt", oldFechaEnt, fechaEnt);
    }

    public Date getFechaSal() {
        return fechaSal;
    }

    public void setFechaSal(Date fechaSal) {
        Date oldFechaSal = this.fechaSal;
        this.fechaSal = fechaSal;
        changeSupport.firePropertyChange("fechaSal", oldFechaSal, fechaSal);
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        String oldObservaciones = this.observaciones;
        this.observaciones = observaciones;
        changeSupport.firePropertyChange("observaciones", oldObservaciones, observaciones);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        String oldEstado = this.estado;
        this.estado = estado;
        changeSupport.firePropertyChange("estado", oldEstado, estado);
    }

    public List<Acciones> getAccionesList() {
        return accionesList;
    }

    public void setAccionesList(List<Acciones> accionesList) {
        this.accionesList = accionesList;
    }

    public Clientes getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Clientes idCliente) {
        Clientes oldIdCliente = this.idCliente;
        this.idCliente = idCliente;
        changeSupport.firePropertyChange("idCliente", oldIdCliente, idCliente);
    }

    public Empleados getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Empleados idEmpleado) {
        Empleados oldIdEmpleado = this.idEmpleado;
        this.idEmpleado = idEmpleado;
        changeSupport.firePropertyChange("idEmpleado", oldIdEmpleado, idEmpleado);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idReparacion != null ? idReparacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reparaciones)) {
            return false;
        }
        Reparaciones other = (Reparaciones) object;
        if ((this.idReparacion == null && other.idReparacion != null) || (this.idReparacion != null && !this.idReparacion.equals(other.idReparacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return  idReparacion.toString();
    }

    /**
     * Este metodo devolvera la variable FormatoFechaEntrada, antes de devolverla se formateara la fecha de la variable fecha de tipo Date a String
     * @return the formatoFechaEntrada
     */
    public String getFormatoFechaEntrada() {
        if(getFechaEnt()!=null){
            SimpleDateFormat sdf=new java.text.SimpleDateFormat("dd/MM/yyyy");
            String fecha = sdf.format(getFechaEnt());
            formatoFechaEntrada=fecha;
        }
        
        return formatoFechaEntrada;
    }

    /**
     * @param formatoFechaEntrada the formatoFechaEntrada to set
     */
    public void setFormatoFechaEntrada(String formatoFechaEntrada) {
        this.formatoFechaEntrada = formatoFechaEntrada;
    }

    /**
     *  Este metodo devolvera la variable FormatoFechaSalida, antes de devolverla se formateara la fecha de la variable fecha de tipo Date a String
     * @return the formatoFechaSalida
     */
    public String getFormatoFechaSalida() {
        if(getFechaSal()!=null){
            SimpleDateFormat sdf=new java.text.SimpleDateFormat("dd/MM/yyyy");
            String fecha = sdf.format(getFechaSal());
            formatoFechaSalida=fecha;
        }
        
        return formatoFechaSalida;
    }

    /**
     * @param formatoFechaSalida the formatoFechaSalida to set
     */
    public void setFormatoFechaSalida(String formatoFechaSalida) {
        this.formatoFechaSalida = formatoFechaSalida;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * @return the totalReparacion
     */
    public float getTotalReparacion() {
        float total = 0;
        totalReparacionIva=0;
         if(!accionesList.isEmpty()){

            for(int i=0;i<accionesList.size();i++){

                total+=accionesList.get(i).getPrecio();
            }

            totalReparacion=total;
            totalReparacionIva=(float) (totalReparacion * 1.16);

        }
        return totalReparacion;
    }

    /**
     * @param totalReparacion the totalReparacion to set
     */
    public void setTotalReparacion(float totalReparacion) {
        this.totalReparacion = totalReparacion;
    }

    /**
     * @return the totalReparacionIva
     */
    public float getTotalReparacionIva() {

        float total = 0;
        totalReparacionIva=0;
         if(!accionesList.isEmpty()){

            for(int i=0;i<accionesList.size();i++){

                total+=accionesList.get(i).getPrecio();
            }

            //totalReparacion=total;
            totalReparacionIva=(float) (total * 1.16);

        }

        return totalReparacionIva;
    }

    /**
     * @param totalReparacionIva the totalReparacionIva to set
     */
    public void setTotalReparacionIva(float totalReparacionIva) {
        this.totalReparacionIva = totalReparacionIva;
    }

   

 

}
