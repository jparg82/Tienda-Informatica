/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Controlador.exceptions.IllegalOrphanException;
import Controlador.exceptions.NonexistentEntityException;
import Modelo.Compra;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
import Modelo.Clientes;
import Modelo.LineasDetalles;
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
public class CompraJpaController {

    private EntityManager em;

    public CompraJpaController() {
        //emf = Persistence.createEntityManagerFactory("ProyectoDAIPU");
        emf= Utilidades.Utilidades.getEmf();
        em=getEntityManager();
        em.getTransaction().begin();
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Compra compra) {
        if (compra.getLineasDetallesList() == null) {
            compra.setLineasDetallesList(new ArrayList<LineasDetalles>());
        }
        
            Clientes idCliente = compra.getIdCliente();
            if (idCliente != null) {
                idCliente = em.getReference(idCliente.getClass(), idCliente.getIdCliente());
                compra.setIdCliente(idCliente);
            }
            List<LineasDetalles> attachedLineasDetallesList = new ArrayList<LineasDetalles>();
            for (LineasDetalles lineasDetallesListLineasDetallesToAttach : compra.getLineasDetallesList()) {
                lineasDetallesListLineasDetallesToAttach = em.getReference(lineasDetallesListLineasDetallesToAttach.getClass(), lineasDetallesListLineasDetallesToAttach.getLineasDetallesPK());
                attachedLineasDetallesList.add(lineasDetallesListLineasDetallesToAttach);
            }
            compra.setLineasDetallesList(attachedLineasDetallesList);
            em.persist(compra);
            if (idCliente != null) {
                idCliente.getCompraList().add(compra);
                idCliente = em.merge(idCliente);
            }
            for (LineasDetalles lineasDetallesListLineasDetalles : compra.getLineasDetallesList()) {
                Compra oldCompraOfLineasDetallesListLineasDetalles = lineasDetallesListLineasDetalles.getCompra();
                lineasDetallesListLineasDetalles.setCompra(compra);
                lineasDetallesListLineasDetalles = em.merge(lineasDetallesListLineasDetalles);
                if (oldCompraOfLineasDetallesListLineasDetalles != null) {
                    oldCompraOfLineasDetallesListLineasDetalles.getLineasDetallesList().remove(lineasDetallesListLineasDetalles);
                    oldCompraOfLineasDetallesListLineasDetalles = em.merge(oldCompraOfLineasDetallesListLineasDetalles);
                }
            }
      
    }

    public void edit(Compra compra) throws IllegalOrphanException, NonexistentEntityException, Exception {
        
        try {
            
            Compra persistentCompra = em.find(Compra.class, compra.getNumeroCompra());
            Clientes idClienteOld = persistentCompra.getIdCliente();
            Clientes idClienteNew = compra.getIdCliente();
            List<LineasDetalles> lineasDetallesListOld = persistentCompra.getLineasDetallesList();
            List<LineasDetalles> lineasDetallesListNew = compra.getLineasDetallesList();
            List<String> illegalOrphanMessages = null;
            for (LineasDetalles lineasDetallesListOldLineasDetalles : lineasDetallesListOld) {
                if (!lineasDetallesListNew.contains(lineasDetallesListOldLineasDetalles)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain LineasDetalles " + lineasDetallesListOldLineasDetalles + " since its compra field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idClienteNew != null) {
                idClienteNew = em.getReference(idClienteNew.getClass(), idClienteNew.getIdCliente());
                compra.setIdCliente(idClienteNew);
            }
            List<LineasDetalles> attachedLineasDetallesListNew = new ArrayList<LineasDetalles>();
            for (LineasDetalles lineasDetallesListNewLineasDetallesToAttach : lineasDetallesListNew) {
                lineasDetallesListNewLineasDetallesToAttach = em.getReference(lineasDetallesListNewLineasDetallesToAttach.getClass(), lineasDetallesListNewLineasDetallesToAttach.getLineasDetallesPK());
                attachedLineasDetallesListNew.add(lineasDetallesListNewLineasDetallesToAttach);
            }
            lineasDetallesListNew = attachedLineasDetallesListNew;
            compra.setLineasDetallesList(lineasDetallesListNew);
            compra = em.merge(compra);
            if (idClienteOld != null && !idClienteOld.equals(idClienteNew)) {
                idClienteOld.getCompraList().remove(compra);
                idClienteOld = em.merge(idClienteOld);
            }
            if (idClienteNew != null && !idClienteNew.equals(idClienteOld)) {
                idClienteNew.getCompraList().add(compra);
                idClienteNew = em.merge(idClienteNew);
            }
            for (LineasDetalles lineasDetallesListNewLineasDetalles : lineasDetallesListNew) {
                if (!lineasDetallesListOld.contains(lineasDetallesListNewLineasDetalles)) {
                    Compra oldCompraOfLineasDetallesListNewLineasDetalles = lineasDetallesListNewLineasDetalles.getCompra();
                    lineasDetallesListNewLineasDetalles.setCompra(compra);
                    lineasDetallesListNewLineasDetalles = em.merge(lineasDetallesListNewLineasDetalles);
                    if (oldCompraOfLineasDetallesListNewLineasDetalles != null && !oldCompraOfLineasDetallesListNewLineasDetalles.equals(compra)) {
                        oldCompraOfLineasDetallesListNewLineasDetalles.getLineasDetallesList().remove(lineasDetallesListNewLineasDetalles);
                        oldCompraOfLineasDetallesListNewLineasDetalles = em.merge(oldCompraOfLineasDetallesListNewLineasDetalles);
                    }
                }
            }
           
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = compra.getNumeroCompra();
                if (findCompra(id) == null) {
                    throw new NonexistentEntityException("The compra with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Compra id) throws IllegalOrphanException, NonexistentEntityException {
                   
           Compra compra;
            //try {
                compra = id;
                //compra.getNumeroCompra();
            //} catch (EntityNotFoundException enfe) {
              //  throw new NonexistentEntityException("The compra with id " + id + " no longer exists.", enfe);
            //}
           // List<String> illegalOrphanMessages = null;
            //List<LineasDetalles> lineasDetallesListOrphanCheck = compra.getLineasDetallesList();
            //for (LineasDetalles lineasDetallesListOrphanCheckLineasDetalles : lineasDetallesListOrphanCheck) {
                //if (illegalOrphanMessages == null) {
                   // illegalOrphanMessages = new ArrayList<String>();
                //}
                //illegalOrphanMessages.add("This Compra (" + compra + ") cannot be destroyed since the LineasDetalles " + lineasDetallesListOrphanCheckLineasDetalles + " in its lineasDetallesList field has a non-nullable compra field.");
            //}
            //if (illegalOrphanMessages != null) {
            //    throw new IllegalOrphanException(illegalOrphanMessages);
            //}
            Clientes idCliente = compra.getIdCliente();
            if (idCliente != null) {
                idCliente.getCompraList().remove(compra);
                idCliente = em.merge(idCliente);
            }
            em.remove(compra);
            
        
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
    public List<Compra> findCompraEntities() {
        return findCompraEntities(true, -1, -1);
    }

    public List<Compra> findCompraEntities(int maxResults, int firstResult) {
        return findCompraEntities(false, maxResults, firstResult);
    }

    private List<Compra> findCompraEntities(boolean all, int maxResults, int firstResult) {
       
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Compra.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        
    }*/

    public Compra findCompra(Integer id) {

            return em.find(Compra.class, id);
       
    }
    /*
    public int getCompraCount() {
       
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Compra> rt = cq.from(Compra.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
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
