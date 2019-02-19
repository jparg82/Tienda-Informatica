/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Controlador.exceptions.NonexistentEntityException;
import Modelo.Reparaciones;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
import Modelo.Clientes;
import Modelo.Empleados;
import Modelo.Acciones;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase es la clase controlador que se encargar de hace las operaciones CRUD(crear,borrar,actualizar y modificar) sobre el entityManagager.
 * La clase Entity Manager es la principal en la API de JPA. Esta es utilizada para crear nuevas entidades (o registros),
 * crear queries con el fin de retornar conjuntos existentes de ellas, borrarlas de la base de datos, y más.
 * Esta clase se ha creado automaticamente con el entorno de desarrollo. Solo se han añadido los metodos guardar y deshacer para poder guardar o deshacer los
 * Cambios en la BD.
 * @author JuanPaulo
 */
public class ReparacionesJpaController {

    private EntityManager em=null;


    public ReparacionesJpaController() {
        //emf = Persistence.createEntityManagerFactory("ProyectoDAIPU");
        emf= Utilidades.Utilidades.getEmf();
        em=getEntityManager();
        em.getTransaction().begin();
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Reparaciones reparaciones) {
        if (reparaciones.getAccionesList() == null) {
            reparaciones.setAccionesList(new ArrayList<Acciones>());
        }
    
            Clientes idCliente = reparaciones.getIdCliente();
            if (idCliente != null) {
                idCliente = getEm().getReference(idCliente.getClass(), idCliente.getIdCliente());
                reparaciones.setIdCliente(idCliente);
            }
            Empleados idEmpleado = reparaciones.getIdEmpleado();
            if (idEmpleado != null) {
                idEmpleado = getEm().getReference(idEmpleado.getClass(), idEmpleado.getIdEmpleado());
                reparaciones.setIdEmpleado(idEmpleado);
            }
            List<Acciones> attachedAccionesList = new ArrayList<Acciones>();
            for (Acciones accionesListAccionesToAttach : reparaciones.getAccionesList()) {
                accionesListAccionesToAttach = getEm().getReference(accionesListAccionesToAttach.getClass(), accionesListAccionesToAttach.getIdAccion());
                attachedAccionesList.add(accionesListAccionesToAttach);
            }
            reparaciones.setAccionesList(attachedAccionesList);
            getEm().persist(reparaciones);
            if (idCliente != null) {
                idCliente.getReparacionesList().add(reparaciones);
                idCliente = getEm().merge(idCliente);
            }
            if (idEmpleado != null) {
                idEmpleado.getReparacionesList().add(reparaciones);
                idEmpleado = getEm().merge(idEmpleado);
            }
            for (Acciones accionesListAcciones : reparaciones.getAccionesList()) {
                accionesListAcciones.getReparacionesList().add(reparaciones);
                accionesListAcciones = getEm().merge(accionesListAcciones);
            }
            
    }

    public void edit(Reparaciones reparaciones) throws NonexistentEntityException, Exception {
        
        try {
            
            Reparaciones persistentReparaciones = getEm().find(Reparaciones.class, reparaciones.getIdReparacion());
            Clientes idClienteOld = persistentReparaciones.getIdCliente();
            Clientes idClienteNew = reparaciones.getIdCliente();
            Empleados idEmpleadoOld = persistentReparaciones.getIdEmpleado();
            Empleados idEmpleadoNew = reparaciones.getIdEmpleado();
            List<Acciones> accionesListOld = persistentReparaciones.getAccionesList();
            List<Acciones> accionesListNew = reparaciones.getAccionesList();
            if (idClienteNew != null) {
                idClienteNew = getEm().getReference(idClienteNew.getClass(), idClienteNew.getIdCliente());
                reparaciones.setIdCliente(idClienteNew);
            }
            if (idEmpleadoNew != null) {
                idEmpleadoNew = getEm().getReference(idEmpleadoNew.getClass(), idEmpleadoNew.getIdEmpleado());
                reparaciones.setIdEmpleado(idEmpleadoNew);
            }
            List<Acciones> attachedAccionesListNew = new ArrayList<Acciones>();
            for (Acciones accionesListNewAccionesToAttach : accionesListNew) {
                accionesListNewAccionesToAttach = getEm().getReference(accionesListNewAccionesToAttach.getClass(), accionesListNewAccionesToAttach.getIdAccion());
                attachedAccionesListNew.add(accionesListNewAccionesToAttach);
            }
            accionesListNew = attachedAccionesListNew;
            reparaciones.setAccionesList(accionesListNew);
            reparaciones = getEm().merge(reparaciones);
            if (idClienteOld != null && !idClienteOld.equals(idClienteNew)) {
                idClienteOld.getReparacionesList().remove(reparaciones);
                idClienteOld = getEm().merge(idClienteOld);
            }
            if (idClienteNew != null && !idClienteNew.equals(idClienteOld)) {
                idClienteNew.getReparacionesList().add(reparaciones);
                idClienteNew = getEm().merge(idClienteNew);
            }
            if (idEmpleadoOld != null && !idEmpleadoOld.equals(idEmpleadoNew)) {
                idEmpleadoOld.getReparacionesList().remove(reparaciones);
                idEmpleadoOld = getEm().merge(idEmpleadoOld);
            }
            if (idEmpleadoNew != null && !idEmpleadoNew.equals(idEmpleadoOld)) {
                idEmpleadoNew.getReparacionesList().add(reparaciones);
                idEmpleadoNew = getEm().merge(idEmpleadoNew);
            }
            for (Acciones accionesListOldAcciones : accionesListOld) {
                if (!accionesListNew.contains(accionesListOldAcciones)) {
                    accionesListOldAcciones.getReparacionesList().remove(reparaciones);
                    accionesListOldAcciones = getEm().merge(accionesListOldAcciones);
                }
            }
            for (Acciones accionesListNewAcciones : accionesListNew) {
                if (!accionesListOld.contains(accionesListNewAcciones)) {
                    accionesListNewAcciones.getReparacionesList().add(reparaciones);
                    accionesListNewAcciones = getEm().merge(accionesListNewAcciones);
                }
            }
            
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = reparaciones.getIdReparacion();
                if (findReparaciones(id) == null) {
                    throw new NonexistentEntityException("The reparaciones with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Reparaciones id) throws NonexistentEntityException {
     
            Reparaciones reparaciones;
         
            reparaciones = id;
                   
            Clientes idCliente = reparaciones.getIdCliente();
            if (idCliente != null) {
                idCliente.getReparacionesList().remove(reparaciones);
                idCliente = getEm().merge(idCliente);
            }
            Empleados idEmpleado = reparaciones.getIdEmpleado();
            if (idEmpleado != null) {
                idEmpleado.getReparacionesList().remove(reparaciones);
                idEmpleado = getEm().merge(idEmpleado);
            }
            List<Acciones> accionesList = reparaciones.getAccionesList();
            for (Acciones accionesListAcciones : accionesList) {
                accionesListAcciones.getReparacionesList().remove(reparaciones);
                accionesListAcciones = getEm().merge(accionesListAcciones);
            }
            getEm().remove(reparaciones);
           
    }

       public void Guardar(){
        getEm().flush();
        getEm().getTransaction().commit();
        getEm().getTransaction().begin();
    }

    public void Deshacer(){
        getEm().getTransaction().rollback();
        getEm().getTransaction().begin();
    }

    /*
    public List<Reparaciones> findReparacionesEntities() {
        return findReparacionesEntities(true, -1, -1);
    }

    public List<Reparaciones> findReparacionesEntities(int maxResults, int firstResult) {
        return findReparacionesEntities(false, maxResults, firstResult);
    }*/

    /*
    private List<Reparaciones> findReparacionesEntities(boolean all, int maxResults, int firstResult) {
        
            CriteriaQuery cq = getEm().getCriteriaBuilder().createQuery();
            cq.select(cq.from(Reparaciones.class));
            Query q = getEm().createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
       
    }*/

    public Reparaciones findReparaciones(Integer id) {
        
            return getEm().find(Reparaciones.class, id);
     
    }

    /*
    public int getReparacionesCount() {
       
            CriteriaQuery cq = getEm().getCriteriaBuilder().createQuery();
            Root<Reparaciones> rt = cq.from(Reparaciones.class);
            cq.select(getEm().getCriteriaBuilder().count(rt));
            Query q = getEm().createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
     
    }*/

    /**
     * @return the em
     */
    public EntityManager getEm() {
        return em;
    }

    /**
     * @param em the em to set
     */
    public void setEm(EntityManager em) {
        this.em = em;
    }

    public List<Reparaciones> ReparacionesSinFacturar(){
        List<Reparaciones> lista;
        lista=getEm().createQuery("SELECT r FROM Reparaciones r WHERE r.estado in('reparado','reparacion')").getResultList();
        for(int i=0;i<lista.size();i++){
            em.refresh(lista.get(i));
        }
        return lista;
    }

}
