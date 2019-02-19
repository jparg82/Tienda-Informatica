/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Controlador.exceptions.IllegalOrphanException;
import Controlador.exceptions.NonexistentEntityException;
import Modelo.Empleados;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
import Modelo.Usuarios;
import Modelo.Reparaciones;
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
public class EmpleadosJpaController {

    private EntityManager em;

    public EmpleadosJpaController() {

        //emf = Persistence.createEntityManagerFactory("ProyectoDAIPU");
        emf= Utilidades.Utilidades.getEmf();
        em=getEntityManager();
        em.getTransaction().begin();
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Empleados empleados) {
        if (empleados.getReparacionesList() == null) {
            empleados.setReparacionesList(new ArrayList<Reparaciones>());
        }
       
        
            
            Usuarios usuario = empleados.getUsuario();
            if (usuario != null) {
                //usuario = getEm().getReference(usuario.getClass(), usuario.getUsuario());
                empleados.setUsuario(usuario);
            }
            
            List<Reparaciones> attachedReparacionesList = new ArrayList<Reparaciones>();
            for (Reparaciones reparacionesListReparacionesToAttach : empleados.getReparacionesList()) {
                reparacionesListReparacionesToAttach = getEm().getReference(reparacionesListReparacionesToAttach.getClass(), reparacionesListReparacionesToAttach.getIdReparacion());
                attachedReparacionesList.add(reparacionesListReparacionesToAttach);
            }
            empleados.setReparacionesList(attachedReparacionesList);
            getEm().persist(empleados);
            /*
            if (usuario != null) {
                usuario.getEmpleadosList().add(empleados);
                usuario = getEm().merge(usuario);
            }*/
            for (Reparaciones reparacionesListReparaciones : empleados.getReparacionesList()) {
                Empleados oldIdEmpleadoOfReparacionesListReparaciones = reparacionesListReparaciones.getIdEmpleado();
                reparacionesListReparaciones.setIdEmpleado(empleados);
                reparacionesListReparaciones = getEm().merge(reparacionesListReparaciones);
                if (oldIdEmpleadoOfReparacionesListReparaciones != null) {
                    oldIdEmpleadoOfReparacionesListReparaciones.getReparacionesList().remove(reparacionesListReparaciones);
                    oldIdEmpleadoOfReparacionesListReparaciones = getEm().merge(oldIdEmpleadoOfReparacionesListReparaciones);
                }
            }
      
    }

    public void edit(Empleados empleados) throws IllegalOrphanException, NonexistentEntityException, Exception {
        
        try {
            
            Empleados persistentEmpleados = getEm().find(Empleados.class, empleados.getIdEmpleado());
            Usuarios usuarioOld = persistentEmpleados.getUsuario();
            Usuarios usuarioNew = empleados.getUsuario();
            List<Reparaciones> reparacionesListOld = persistentEmpleados.getReparacionesList();
            List<Reparaciones> reparacionesListNew = empleados.getReparacionesList();
            List<String> illegalOrphanMessages = null;
            for (Reparaciones reparacionesListOldReparaciones : reparacionesListOld) {
                if (!reparacionesListNew.contains(reparacionesListOldReparaciones)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Reparaciones " + reparacionesListOldReparaciones + " since its idEmpleado field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (usuarioNew != null) {
                usuarioNew = getEm().getReference(usuarioNew.getClass(), usuarioNew.getUsuario());
                empleados.setUsuario(usuarioNew);
            }
            List<Reparaciones> attachedReparacionesListNew = new ArrayList<Reparaciones>();
            for (Reparaciones reparacionesListNewReparacionesToAttach : reparacionesListNew) {
                reparacionesListNewReparacionesToAttach = getEm().getReference(reparacionesListNewReparacionesToAttach.getClass(), reparacionesListNewReparacionesToAttach.getIdReparacion());
                attachedReparacionesListNew.add(reparacionesListNewReparacionesToAttach);
            }
            reparacionesListNew = attachedReparacionesListNew;
            empleados.setReparacionesList(reparacionesListNew);
            empleados = getEm().merge(empleados);
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.getEmpleadosList().remove(empleados);
                usuarioOld = getEm().merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.getEmpleadosList().add(empleados);
                usuarioNew = getEm().merge(usuarioNew);
            }
            for (Reparaciones reparacionesListNewReparaciones : reparacionesListNew) {
                if (!reparacionesListOld.contains(reparacionesListNewReparaciones)) {
                    Empleados oldIdEmpleadoOfReparacionesListNewReparaciones = reparacionesListNewReparaciones.getIdEmpleado();
                    reparacionesListNewReparaciones.setIdEmpleado(empleados);
                    reparacionesListNewReparaciones = getEm().merge(reparacionesListNewReparaciones);
                    if (oldIdEmpleadoOfReparacionesListNewReparaciones != null && !oldIdEmpleadoOfReparacionesListNewReparaciones.equals(empleados)) {
                        oldIdEmpleadoOfReparacionesListNewReparaciones.getReparacionesList().remove(reparacionesListNewReparaciones);
                        oldIdEmpleadoOfReparacionesListNewReparaciones = getEm().merge(oldIdEmpleadoOfReparacionesListNewReparaciones);
                    }
                }
            }
            
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = empleados.getIdEmpleado();
                if (findEmpleados(id) == null) {
                    throw new NonexistentEntityException("The empleados with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Empleados id) throws IllegalOrphanException, NonexistentEntityException {
 
            Empleados empleados=id;
            try {
                //empleados = getEm().getReference(Empleados.class, id);
                empleados.getIdEmpleado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empleados with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Reparaciones> reparacionesListOrphanCheck = empleados.getReparacionesList();
            for (Reparaciones reparacionesListOrphanCheckReparaciones : reparacionesListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("No se puede eliminar el empleado (" + empleados.getNombre() + " "+empleados.getApellidos()+ ") , No se pueden eliminar empleados que tengan asignadas reparaciones");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuarios usuario = empleados.getUsuario();
            if (usuario != null) {
                usuario.getEmpleadosList().remove(empleados);
                usuario = getEm().merge(usuario);
            }
            getEm().remove(empleados);
   
    }

    public void Guardar(){
        getEm().getTransaction().commit();
        getEm().getTransaction().begin();
    }

    public void Deshacer(){
        getEm().getTransaction().rollback();
        getEm().getTransaction().begin();
    }

    /*
    public List<Empleados> findEmpleadosEntities() {
        return findEmpleadosEntities(true, -1, -1);
    }

    public List<Empleados> findEmpleadosEntities(int maxResults, int firstResult) {
        return findEmpleadosEntities(false, maxResults, firstResult);
    }

    /*
    private List<Empleados> findEmpleadosEntities(boolean all, int maxResults, int firstResult) {
      
            CriteriaQuery cq = getEm().getCriteriaBuilder().createQuery();
            cq.select(cq.from(Empleados.class));
            Query q = getEm().createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        
    }*/

    public Empleados findEmpleados(Integer id) {
        
        
            return getEm().find(Empleados.class, id);
       
    }

    /*
    public int getEmpleadosCount() {
       
        
            CriteriaQuery cq = getEm().getCriteriaBuilder().createQuery();
            Root<Empleados> rt = cq.from(Empleados.class);
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

}
