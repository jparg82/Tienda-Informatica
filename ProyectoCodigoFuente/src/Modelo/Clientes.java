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
@Table(name = "clientes")
@NamedQueries({
    @NamedQuery(name = "Clientes.findAll", query = "SELECT c FROM Clientes c"),
    @NamedQuery(name = "Clientes.findByIdCliente", query = "SELECT c FROM Clientes c WHERE c.idCliente = :idCliente"),
    @NamedQuery(name = "Clientes.findByDniCliente", query = "SELECT c FROM Clientes c WHERE c.dniCliente = :dniCliente"),
    @NamedQuery(name = "Clientes.findByNombre", query = "SELECT c FROM Clientes c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Clientes.findByApellidos", query = "SELECT c FROM Clientes c WHERE c.apellidos = :apellidos"),
    @NamedQuery(name = "Clientes.findByDireccion", query = "SELECT c FROM Clientes c WHERE c.direccion = :direccion"),
    @NamedQuery(name = "Clientes.findByCiudad", query = "SELECT c FROM Clientes c WHERE c.ciudad = :ciudad"),
    @NamedQuery(name = "Clientes.findByCp", query = "SELECT c FROM Clientes c WHERE c.cp = :cp"),
    @NamedQuery(name = "Clientes.findByTelefono", query = "SELECT c FROM Clientes c WHERE c.telefono = :telefono"),
    @NamedQuery(name = "Clientes.findByMail", query = "SELECT c FROM Clientes c WHERE c.mail = :mail")})
public class Clientes implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_cliente")
    private Integer idCliente;
    @Column(name = "dni_cliente")
    private String dniCliente;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "apellidos")
    private String apellidos;
    @Column(name = "direccion")
    private String direccion;
    @Column(name = "Ciudad")
    private String ciudad;
    @Column(name = "CP")
    private Integer cp;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "mail")
    private String mail;
    @JoinColumn(name = "cod_provincia", referencedColumnName = "cod_provincia")
    @ManyToOne(optional = false)
    private Provincias codProvincia;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCliente")
    private List<Compra> compraList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCliente")
    private List<Reparaciones> reparacionesList;

    public Clientes() {
    }

    public Clientes(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdCliente() {

        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        Integer oldIdCliente = this.idCliente;
        this.idCliente = idCliente;
        changeSupport.firePropertyChange("idCliente", oldIdCliente, idCliente);
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        String oldDniCliente = this.dniCliente;
        this.dniCliente = dniCliente;
        changeSupport.firePropertyChange("dniCliente", oldDniCliente, dniCliente);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        String oldNombre = this.nombre;
        this.nombre = nombre;
        changeSupport.firePropertyChange("nombre", oldNombre, nombre);
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        String oldApellidos = this.apellidos;
        this.apellidos = apellidos;
        changeSupport.firePropertyChange("apellidos", oldApellidos, apellidos);
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        String oldDireccion = this.direccion;
        this.direccion = direccion;
        changeSupport.firePropertyChange("direccion", oldDireccion, direccion);
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        String oldCiudad = this.ciudad;
        this.ciudad = ciudad;
        changeSupport.firePropertyChange("ciudad", oldCiudad, ciudad);
    }

    public Integer getCp() {
        return cp;
    }

    public void setCp(Integer cp) {
        Integer oldCp = this.cp;
        this.cp = cp;
        changeSupport.firePropertyChange("cp", oldCp, cp);
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        String oldTelefono = this.telefono;
        this.telefono = telefono;
        changeSupport.firePropertyChange("telefono", oldTelefono, telefono);
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        String oldMail = this.mail;
        this.mail = mail;
        changeSupport.firePropertyChange("mail", oldMail, mail);
    }

    public Provincias getCodProvincia() {
        return codProvincia;
    }

    public void setCodProvincia(Provincias codProvincia) {
        Provincias oldCodProvincia = this.codProvincia;
        this.codProvincia = codProvincia;
        changeSupport.firePropertyChange("codProvincia", oldCodProvincia, codProvincia);
    }

    public List<Compra> getCompraList() {
        return compraList;
    }

    public void setCompraList(List<Compra> compraList) {
        this.compraList = compraList;
    }

    public List<Reparaciones> getReparacionesList() {
        return reparacionesList;
    }

    public void setReparacionesList(List<Reparaciones> reparacionesList) {
        this.reparacionesList = reparacionesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCliente != null ? idCliente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Clientes)) {
            return false;
        }
        Clientes other = (Clientes) object;
        if ((this.idCliente == null && other.idCliente != null) || (this.idCliente != null && !this.idCliente.equals(other.idCliente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getDniCliente();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

}
