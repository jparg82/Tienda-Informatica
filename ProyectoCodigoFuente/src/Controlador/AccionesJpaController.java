/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Controlador.exceptions.NonexistentEntityException;
import Modelo.Acciones;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
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
public class AccionesJpaController {

    private EntityManager em;

    public AccionesJpaController() {
        //emf = Persistence.createEntityManagerFactory("ProyectoDAIPU");
        emf= Utilidades.Utilidades.getEmf();
        em=getEntityManager();
        em.getTransaction().begin();
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Acciones acciones) {
        if (acciones.getReparacionesList() == null) {
            acciones.setReparacionesList(new ArrayList<Reparaciones>());
        }
               
            List<Reparaciones> attachedReparacionesList = new ArrayList<Reparaciones>();
            for (Reparaciones reparacionesListReparacionesToAttach : acciones.getReparacionesList()) {
                reparacionesListReparacionesToAttach = em.getReference(reparacionesListReparacionesToAttach.getClass(), reparacionesListReparacionesToAttach.getIdReparacion());
                attachedReparacionesList.add(reparacionesListReparacionesToAttach);
            }
            acciones.setReparacionesList(attachedReparacionesList);
            em.persist(acciones);
            for (Reparaciones reparacionesListReparaciones : acciones.getReparacionesList()) {
                reparacionesListReparaciones.getAccionesList().add(acciones);
                reparacionesListReparaciones = em.merge(reparacionesListReparaciones);
            }              
    }

    public void edit(Acciones acciones) throws NonexistentEntityException, Exception {
        
        try {
            
            Acciones persistentAcciones = em.find(Acciones.class, acciones.getIdAccion());
            List<Reparaciones> reparacionesListOld = persistentAcciones.getReparacionesList();
            List<Reparaciones> reparacionesListNew = acciones.getReparacionesList();
            List<Reparaciones> attachedReparacionesListNew = new ArrayList<Reparaciones>();
            for (Reparaciones reparacionesListNewReparacionesToAttach : reparacionesListNew) {
                reparacionesListNewReparacionesToAttach = em.getReference(reparacionesListNewReparacionesToAttach.getClass(), reparacionesListNewReparacionesToAttach.getIdReparacion());
                attachedReparacionesListNew.add(reparacionesListNewReparacionesToAttach);
            }
            reparacionesListNew = attachedReparacionesListNew;
            acciones.setReparacionesList(reparacionesListNew);
            acciones = em.merge(acciones);
            for (Reparaciones reparacionesListOldReparaciones : reparacionesListOld) {
                if (!reparacionesListNew.contains(reparacionesListOldReparaciones)) {
                    reparacionesListOldReparaciones.getAccionesList().remove(acciones);
                    reparacionesListOldReparaciones = em.merge(reparacionesListOldReparaciones);
                }
            }
            for (Reparaciones reparacionesListNewReparaciones : reparacionesListNew) {
                if (!reparacionesListOld.contains(reparacionesListNewReparaciones)) {
                    reparacionesListNewReparaciones.getAccionesList().add(acciones);
                    reparacionesListNewReparaciones = em.merge(reparacionesListNewReparaciones);
                }
            }
           
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = acciones.getIdAccion();
                if (findAcciones(id) == null) {
                    throw new NonexistentEntityException("The acciones with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } 
    }

    public void destroy(Integer id) throws NonexistentEntityException {
            
            Acciones acciones;
            try {
                acciones = em.getReference(Acciones.class, id);
                acciones.getIdAccion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The acciones with id " + id + " no longer exists.", enfe);
            }
            List<Reparaciones> reparacionesList = acciones.getReparacionesList();
            for (Reparaciones reparacionesListReparaciones : reparacionesList) {
                reparacionesListReparaciones.getAccionesList().remove(acciones);
                reparacionesListReparaciones = em.merge(reparacionesListReparaciones);
            }
            em.remove(acciones);

    }

    public void Guardar(){
        em.getTransaction().commit();
        em.getTransaction().begin();
    }

    public void Deshacer(){
        em.getTransaction().rollback();
        em.getTransaction().begin();
    }

    /*
    public List<Acciones> findAccionesEntities() {
        return findAccionesEntities(true, -1, -1);
    }

    public List<Acciones> findAccionesEntities(int maxResults, int firstResult) {
        return findAccionesEntities(false, maxResults, firstResult);
    }

    private List<Acciones> findAccionesEntities(boolean all, int maxResults, int firstResult) {
       
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Acciones.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        
    }*/

    public Acciones findAcciones(Integer id) {
       
            return em.find(Acciones.class, id);
        
    }

    /*
    public int getAccionesCount() {
        
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Acciones> rt = cq.from(Acciones.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
       
    }

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
