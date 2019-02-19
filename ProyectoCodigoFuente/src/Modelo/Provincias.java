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
@Table(name = "provincias")
@NamedQueries({
    @NamedQuery(name = "Provincias.findAll", query = "SELECT p FROM Provincias p"),
    @NamedQuery(name = "Provincias.findByCodProvincia", query = "SELECT p FROM Provincias p WHERE p.codProvincia = :codProvincia"),
    @NamedQuery(name = "Provincias.findByProvincia", query = "SELECT p FROM Provincias p WHERE p.provincia = :provincia")})
public class Provincias implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "cod_provincia")
    private Integer codProvincia;
    @Column(name = "provincia")
    private String provincia;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codProvincia")
    private List<Clientes> clientesList;

    public Provincias() {
    }

    public Provincias(Integer codProvincia) {
        this.codProvincia = codProvincia;
    }

    public Integer getCodProvincia() {
        return codProvincia;
    }

    public void setCodProvincia(Integer codProvincia) {
        Integer oldCodProvincia = this.codProvincia;
        this.codProvincia = codProvincia;
        changeSupport.firePropertyChange("codProvincia", oldCodProvincia, codProvincia);
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        String oldProvincia = this.provincia;
        this.provincia = provincia;
        changeSupport.firePropertyChange("provincia", oldProvincia, provincia);
    }

    public List<Clientes> getClientesList() {
        return clientesList;
    }

    public void setClientesList(List<Clientes> clientesList) {
        this.clientesList = clientesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codProvincia != null ? codProvincia.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Provincias)) {
            return false;
        }
        Provincias other = (Provincias) object;
        if ((this.codProvincia == null && other.codProvincia != null) || (this.codProvincia != null && !this.codProvincia.equals(other.codProvincia))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getProvincia();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

}
