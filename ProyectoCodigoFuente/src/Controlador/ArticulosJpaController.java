/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Controlador.exceptions.NonexistentEntityException;
import Modelo.Articulos;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
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
public class ArticulosJpaController {

    private EntityManager em;

    public ArticulosJpaController() {
        //emf = Persistence.createEntityManagerFactory("ProyectoDAIPU");
        emf= Utilidades.Utilidades.getEmf();
        em=getEntityManager();
        em.getTransaction().begin();
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Articulos articulos) {
        if (articulos.getLineasDetallesList() == null) {
            articulos.setLineasDetallesList(new ArrayList<LineasDetalles>());
        }
             
            List<LineasDetalles> attachedLineasDetallesList = new ArrayList<LineasDetalles>();
            for (LineasDetalles lineasDetallesListLineasDetallesToAttach : articulos.getLineasDetallesList()) {
                lineasDetallesListLineasDetallesToAttach = getEm().getReference(lineasDetallesListLineasDetallesToAttach.getClass(), lineasDetallesListLineasDetallesToAttach.getLineasDetallesPK());
                attachedLineasDetallesList.add(lineasDetallesListLineasDetallesToAttach);
            }
            articulos.setLineasDetallesList(attachedLineasDetallesList);
            getEm().persist(articulos);
            for (LineasDetalles lineasDetallesListLineasDetalles : articulos.getLineasDetallesList()) {
                Articulos oldIdarticuloOfLineasDetallesListLineasDetalles = lineasDetallesListLineasDetalles.getIdarticulo();
                lineasDetallesListLineasDetalles.setIdarticulo(articulos);
                lineasDetallesListLineasDetalles = getEm().merge(lineasDetallesListLineasDetalles);
                if (oldIdarticuloOfLineasDetallesListLineasDetalles != null) {
                    oldIdarticuloOfLineasDetallesListLineasDetalles.getLineasDetallesList().remove(lineasDetallesListLineasDetalles);
                    oldIdarticuloOfLineasDetallesListLineasDetalles = getEm().merge(oldIdarticuloOfLineasDetallesListLineasDetalles);
                }
            }
                 
    }

    public void edit(Articulos articulos) throws NonexistentEntityException, Exception {
        
        try {
            
            Articulos persistentArticulos = getEm().find(Articulos.class, articulos.getIdarticulo());
            List<LineasDetalles> lineasDetallesListOld = persistentArticulos.getLineasDetallesList();
            List<LineasDetalles> lineasDetallesListNew = articulos.getLineasDetallesList();
            List<LineasDetalles> attachedLineasDetallesListNew = new ArrayList<LineasDetalles>();
            for (LineasDetalles lineasDetallesListNewLineasDetallesToAttach : lineasDetallesListNew) {
                lineasDetallesListNewLineasDetallesToAttach = getEm().getReference(lineasDetallesListNewLineasDetallesToAttach.getClass(), lineasDetallesListNewLineasDetallesToAttach.getLineasDetallesPK());
                attachedLineasDetallesListNew.add(lineasDetallesListNewLineasDetallesToAttach);
            }
            lineasDetallesListNew = attachedLineasDetallesListNew;
            articulos.setLineasDetallesList(lineasDetallesListNew);
            articulos = getEm().merge(articulos);
            for (LineasDetalles lineasDetallesListOldLineasDetalles : lineasDetallesListOld) {
                if (!lineasDetallesListNew.contains(lineasDetallesListOldLineasDetalles)) {
                    lineasDetallesListOldLineasDetalles.setIdarticulo(null);
                    lineasDetallesListOldLineasDetalles = getEm().merge(lineasDetallesListOldLineasDetalles);
                }
            }
            for (LineasDetalles lineasDetallesListNewLineasDetalles : lineasDetallesListNew) {
                if (!lineasDetallesListOld.contains(lineasDetallesListNewLineasDetalles)) {
                    Articulos oldIdarticuloOfLineasDetallesListNewLineasDetalles = lineasDetallesListNewLineasDetalles.getIdarticulo();
                    lineasDetallesListNewLineasDetalles.setIdarticulo(articulos);
                    lineasDetallesListNewLineasDetalles = getEm().merge(lineasDetallesListNewLineasDetalles);
                    if (oldIdarticuloOfLineasDetallesListNewLineasDetalles != null && !oldIdarticuloOfLineasDetallesListNewLineasDetalles.equals(articulos)) {
                        oldIdarticuloOfLineasDetallesListNewLineasDetalles.getLineasDetallesList().remove(lineasDetallesListNewLineasDetalles);
                        oldIdarticuloOfLineasDetallesListNewLineasDetalles = getEm().merge(oldIdarticuloOfLineasDetallesListNewLineasDetalles);
                    }
                }
            }
            
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = articulos.getIdarticulo();
                if (findArticulos(id) == null) {
                    throw new NonexistentEntityException("The articulos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } 
    }

    public void destroy(Integer id) throws NonexistentEntityException {
            
            Articulos articulos;
            try {
                articulos = getEm().getReference(Articulos.class, id);
                articulos.getIdarticulo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The articulos with id " + id + " no longer exists.", enfe);
            }
            List<LineasDetalles> lineasDetallesList = articulos.getLineasDetallesList();
            for (LineasDetalles lineasDetallesListLineasDetalles : lineasDetallesList) {
                lineasDetallesListLineasDetalles.setIdarticulo(null);
                lineasDetallesListLineasDetalles = getEm().merge(lineasDetallesListLineasDetalles);
            }
            getEm().remove(articulos);
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
    public List<Articulos> findArticulosEntities() {
        return findArticulosEntities(true, -1, -1);
    }

    public List<Articulos> findArticulosEntities(int maxResults, int firstResult) {
        return findArticulosEntities(false, maxResults, firstResult);
    }

    private List<Articulos> findArticulosEntities(boolean all, int maxResults, int firstResult) {
        
            CriteriaQuery cq = getEm().getCriteriaBuilder().createQuery();
            cq.select(cq.from(Articulos.class));
            Query q = getEm().createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();    
    }*/

    public Articulos findArticulos(Integer id) {
       
            return getEm().find(Articulos.class, id);
       
    }

    /*
    public int getArticulosCount() {
        
            CriteriaQuery cq = getEm().getCriteriaBuilder().createQuery();
            Root<Articulos> rt = cq.from(Articulos.class);
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
