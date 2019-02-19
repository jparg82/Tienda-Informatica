/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Controlador.exceptions.NonexistentEntityException;
import Controlador.exceptions.PreexistingEntityException;
import Modelo.LineasDetalles;
import Modelo.LineasDetallesPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
import Modelo.Compra;
import Modelo.Articulos;

/**
 * Esta clase es la clase controlador que se encargar de hace las operaciones CRUD(crear,borrar,actualizar y modificar) sobre el entityManagager.
 * La clase Entity Manager es la principal en la API de JPA. Esta es utilizada para crear nuevas entidades (o registros),
 * crear queries con el fin de retornar conjuntos existentes de ellas, borrarlas de la base de datos, y más.
 * Esta clase se ha creado automaticamente con el entorno de desarrollo. Solo se han añadido los metodos guardar y deshacer para poder guardar o deshacer los
 * Cambios en la BD.
 * @author JuanPaulo
 */
public class LineasDetallesJpaController {

    private EntityManager em;
    
    public LineasDetallesJpaController() {
        //emf = Persistence.createEntityManagerFactory("ProyectoDAIPU");
        emf= Utilidades.Utilidades.getEmf();
        em=getEntityManager();
        em.getTransaction().begin();
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(LineasDetalles lineasDetalles) throws PreexistingEntityException, Exception {
        if (lineasDetalles.getLineasDetallesPK() == null) {
            lineasDetalles.setLineasDetallesPK(new LineasDetallesPK());
        }
        lineasDetalles.getLineasDetallesPK().setNumeroCompra(lineasDetalles.getCompra().getNumeroCompra());
       
        try {
           
            Compra compra = lineasDetalles.getCompra();
            if (compra != null) {
                compra = em.getReference(compra.getClass(), compra.getNumeroCompra());
                lineasDetalles.setCompra(compra);
            }
            Articulos idarticulo = lineasDetalles.getIdarticulo();
            if (idarticulo != null) {
                idarticulo = em.getReference(idarticulo.getClass(), idarticulo.getIdarticulo());
                lineasDetalles.setIdarticulo(idarticulo);
            }
            em.persist(lineasDetalles);
            if (compra != null) {
                compra.getLineasDetallesList().add(lineasDetalles);
                //compra = em.merge(compra);
            }
            if (idarticulo != null) {
                idarticulo.getLineasDetallesList().add(lineasDetalles);
                idarticulo = em.merge(idarticulo);
            }
            
        } catch (Exception ex) {
            if (findLineasDetalles(lineasDetalles.getLineasDetallesPK()) != null) {
                throw new PreexistingEntityException("LineasDetalles " + lineasDetalles + " already exists.", ex);
            }
            throw ex;
        } 
    }

    public void edit(LineasDetalles lineasDetalles) throws NonexistentEntityException, Exception {
        lineasDetalles.getLineasDetallesPK().setNumeroCompra(lineasDetalles.getCompra().getNumeroCompra());
       
        try {
            
            LineasDetalles persistentLineasDetalles = em.find(LineasDetalles.class, lineasDetalles.getLineasDetallesPK());
            Compra compraOld = persistentLineasDetalles.getCompra();
            Compra compraNew = lineasDetalles.getCompra();
            Articulos idarticuloOld = persistentLineasDetalles.getIdarticulo();
            Articulos idarticuloNew = lineasDetalles.getIdarticulo();
            if (compraNew != null) {
                compraNew = em.getReference(compraNew.getClass(), compraNew.getNumeroCompra());
                lineasDetalles.setCompra(compraNew);
            }
            if (idarticuloNew != null) {
                idarticuloNew = em.getReference(idarticuloNew.getClass(), idarticuloNew.getIdarticulo());
                lineasDetalles.setIdarticulo(idarticuloNew);
            }
            lineasDetalles = em.merge(lineasDetalles);
            if (compraOld != null && !compraOld.equals(compraNew)) {
                compraOld.getLineasDetallesList().remove(lineasDetalles);
                compraOld = em.merge(compraOld);
            }
            if (compraNew != null && !compraNew.equals(compraOld)) {
                compraNew.getLineasDetallesList().add(lineasDetalles);
                compraNew = em.merge(compraNew);
            }
            if (idarticuloOld != null && !idarticuloOld.equals(idarticuloNew)) {
                idarticuloOld.getLineasDetallesList().remove(lineasDetalles);
                idarticuloOld = em.merge(idarticuloOld);
            }
            if (idarticuloNew != null && !idarticuloNew.equals(idarticuloOld)) {
                idarticuloNew.getLineasDetallesList().add(lineasDetalles);
                idarticuloNew = em.merge(idarticuloNew);
            }
            
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                LineasDetallesPK id = lineasDetalles.getLineasDetallesPK();
                if (findLineasDetalles(id) == null) {
                    throw new NonexistentEntityException("The lineasDetalles with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } 
    }

    public void destroy(LineasDetallesPK id) throws NonexistentEntityException {

            LineasDetalles lineasDetalles;
            try {
                lineasDetalles = em.getReference(LineasDetalles.class, id);
                lineasDetalles.getLineasDetallesPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The lineasDetalles with id " + id + " no longer exists.", enfe);
            }
            Compra compra = lineasDetalles.getCompra();
            if (compra != null) {
                compra.getLineasDetallesList().remove(lineasDetalles);
                compra = em.merge(compra);
            }
            Articulos idarticulo = lineasDetalles.getIdarticulo();
            if (idarticulo != null) {
                idarticulo.getLineasDetallesList().remove(lineasDetalles);
                idarticulo = em.merge(idarticulo);
            }
            em.remove(lineasDetalles);
       
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
    public List<LineasDetalles> findLineasDetallesEntities() {
        return findLineasDetallesEntities(true, -1, -1);
    }

    public List<LineasDetalles> findLineasDetallesEntities(int maxResults, int firstResult) {
        return findLineasDetallesEntities(false, maxResults, firstResult);
    }

    private List<LineasDetalles> findLineasDetallesEntities(boolean all, int maxResults, int firstResult) {
 
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LineasDetalles.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
    
    }*/

    public LineasDetalles findLineasDetalles(LineasDetallesPK id) {
   
            return em.find(LineasDetalles.class, id);
    }
    /*
    public int getLineasDetallesCount() {
       
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LineasDetalles> rt = cq.from(LineasDetalles.class);
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
