/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Controlador.exceptions.IllegalOrphanException;
import Controlador.exceptions.NonexistentEntityException;
import Modelo.Provincias;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
import Modelo.Clientes;
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
public class ProvinciasJpaController {

    private EntityManager em;

    public ProvinciasJpaController() {
        //emf = Persistence.createEntityManagerFactory("ProyectoDAIPU");
        emf= Utilidades.Utilidades.getEmf();
        em=getEntityManager();
        em.getTransaction().begin();
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Provincias provincias) {
        if (provincias.getClientesList() == null) {
            provincias.setClientesList(new ArrayList<Clientes>());
        }

            List<Clientes> attachedClientesList = new ArrayList<Clientes>();
            for (Clientes clientesListClientesToAttach : provincias.getClientesList()) {
                clientesListClientesToAttach = em.getReference(clientesListClientesToAttach.getClass(), clientesListClientesToAttach.getIdCliente());
                attachedClientesList.add(clientesListClientesToAttach);
            }
            provincias.setClientesList(attachedClientesList);
            em.persist(provincias);
            for (Clientes clientesListClientes : provincias.getClientesList()) {
                Provincias oldCodProvinciaOfClientesListClientes = clientesListClientes.getCodProvincia();
                clientesListClientes.setCodProvincia(provincias);
                clientesListClientes = em.merge(clientesListClientes);
                if (oldCodProvinciaOfClientesListClientes != null) {
                    oldCodProvinciaOfClientesListClientes.getClientesList().remove(clientesListClientes);
                    oldCodProvinciaOfClientesListClientes = em.merge(oldCodProvinciaOfClientesListClientes);
                }
            }
       
    }

    public void edit(Provincias provincias) throws IllegalOrphanException, NonexistentEntityException, Exception {
       
        try {
          
            Provincias persistentProvincias = em.find(Provincias.class, provincias.getCodProvincia());
            List<Clientes> clientesListOld = persistentProvincias.getClientesList();
            List<Clientes> clientesListNew = provincias.getClientesList();
            List<String> illegalOrphanMessages = null;
            for (Clientes clientesListOldClientes : clientesListOld) {
                if (!clientesListNew.contains(clientesListOldClientes)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Clientes " + clientesListOldClientes + " since its codProvincia field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Clientes> attachedClientesListNew = new ArrayList<Clientes>();
            for (Clientes clientesListNewClientesToAttach : clientesListNew) {
                clientesListNewClientesToAttach = em.getReference(clientesListNewClientesToAttach.getClass(), clientesListNewClientesToAttach.getIdCliente());
                attachedClientesListNew.add(clientesListNewClientesToAttach);
            }
            clientesListNew = attachedClientesListNew;
            provincias.setClientesList(clientesListNew);
            provincias = em.merge(provincias);
            for (Clientes clientesListNewClientes : clientesListNew) {
                if (!clientesListOld.contains(clientesListNewClientes)) {
                    Provincias oldCodProvinciaOfClientesListNewClientes = clientesListNewClientes.getCodProvincia();
                    clientesListNewClientes.setCodProvincia(provincias);
                    clientesListNewClientes = em.merge(clientesListNewClientes);
                    if (oldCodProvinciaOfClientesListNewClientes != null && !oldCodProvinciaOfClientesListNewClientes.equals(provincias)) {
                        oldCodProvinciaOfClientesListNewClientes.getClientesList().remove(clientesListNewClientes);
                        oldCodProvinciaOfClientesListNewClientes = em.merge(oldCodProvinciaOfClientesListNewClientes);
                    }
                }
            }
            
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = provincias.getCodProvincia();
                if (findProvincias(id) == null) {
                    throw new NonexistentEntityException("The provincias with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } 
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
   
            Provincias provincias;
            try {
                provincias = em.getReference(Provincias.class, id);
                provincias.getCodProvincia();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The provincias with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Clientes> clientesListOrphanCheck = provincias.getClientesList();
            for (Clientes clientesListOrphanCheckClientes : clientesListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Provincias (" + provincias + ") cannot be destroyed since the Clientes " + clientesListOrphanCheckClientes + " in its clientesList field has a non-nullable codProvincia field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(provincias);   
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
    public List<Provincias> findProvinciasEntities() {
        return findProvinciasEntities(true, -1, -1);
    }

    public List<Provincias> findProvinciasEntities(int maxResults, int firstResult) {
        return findProvinciasEntities(false, maxResults, firstResult);
    }

    private List<Provincias> findProvinciasEntities(boolean all, int maxResults, int firstResult) {
 
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Provincias.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
    
    }*/

    public Provincias findProvincias(Integer id) {
            return em.find(Provincias.class, id);
    }
    /*
    public int getProvinciasCount() {
        
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Provincias> rt = cq.from(Provincias.class);
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
