/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Controlador.exceptions.IllegalOrphanException;
import Controlador.exceptions.NonexistentEntityException;
import Modelo.Clientes;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
import Modelo.Provincias;
import Modelo.Compra;
import java.util.ArrayList;
import java.util.List;
import Modelo.Reparaciones;

/**
 * Esta clase es la clase controlador que se encargar de hace las operaciones CRUD(crear,borrar,actualizar y modificar) sobre el entityManagager.
 * La clase Entity Manager es la principal en la API de JPA. Esta es utilizada para crear nuevas entidades (o registros),
 * crear queries con el fin de retornar conjuntos existentes de ellas, borrarlas de la base de datos, y más.
 * Esta clase se ha creado automaticamente con el entorno de desarrollo. Solo se han añadido los metodos guardar y deshacer para poder guardar o deshacer los
 * Cambios en la BD.
 * @author JuanPaulo
 */
public class ClientesJpaController {

    private EntityManager em=null;


    public ClientesJpaController() {
        //emf = Persistence.createEntityManagerFactory("ProyectoDAIPU");
        emf= Utilidades.Utilidades.getEmf();
        em=getEntityManager();
        em.getTransaction().begin();
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Clientes clientes) {
        if (clientes.getCompraList() == null) {
            clientes.setCompraList(new ArrayList<Compra>());
        }
        if (clientes.getReparacionesList() == null) {
            clientes.setReparacionesList(new ArrayList<Reparaciones>());
        }
                
            Provincias codProvincia = clientes.getCodProvincia();
            if (codProvincia != null) {
                codProvincia = em.getReference(codProvincia.getClass(), codProvincia.getCodProvincia());
                clientes.setCodProvincia(codProvincia);
            }
            List<Compra> attachedCompraList = new ArrayList<Compra>();
            for (Compra compraListCompraToAttach : clientes.getCompraList()) {
                compraListCompraToAttach = em.getReference(compraListCompraToAttach.getClass(), compraListCompraToAttach.getNumeroCompra());
                attachedCompraList.add(compraListCompraToAttach);
            }
            clientes.setCompraList(attachedCompraList);
            List<Reparaciones> attachedReparacionesList = new ArrayList<Reparaciones>();
            for (Reparaciones reparacionesListReparacionesToAttach : clientes.getReparacionesList()) {
                reparacionesListReparacionesToAttach = em.getReference(reparacionesListReparacionesToAttach.getClass(), reparacionesListReparacionesToAttach.getIdReparacion());
                attachedReparacionesList.add(reparacionesListReparacionesToAttach);
            }
            clientes.setReparacionesList(attachedReparacionesList);
            em.persist(clientes);
            if (codProvincia != null) {
                codProvincia.getClientesList().add(clientes);
                codProvincia = em.merge(codProvincia);
            }
            for (Compra compraListCompra : clientes.getCompraList()) {
                Clientes oldIdClienteOfCompraListCompra = compraListCompra.getIdCliente();
                compraListCompra.setIdCliente(clientes);
                compraListCompra = em.merge(compraListCompra);
                if (oldIdClienteOfCompraListCompra != null) {
                    oldIdClienteOfCompraListCompra.getCompraList().remove(compraListCompra);
                    oldIdClienteOfCompraListCompra = em.merge(oldIdClienteOfCompraListCompra);
                }
            }
            for (Reparaciones reparacionesListReparaciones : clientes.getReparacionesList()) {
                Clientes oldIdClienteOfReparacionesListReparaciones = reparacionesListReparaciones.getIdCliente();
                reparacionesListReparaciones.setIdCliente(clientes);
                reparacionesListReparaciones = em.merge(reparacionesListReparaciones);
                if (oldIdClienteOfReparacionesListReparaciones != null) {
                    oldIdClienteOfReparacionesListReparaciones.getReparacionesList().remove(reparacionesListReparaciones);
                    oldIdClienteOfReparacionesListReparaciones = em.merge(oldIdClienteOfReparacionesListReparaciones);
                }
            }
       
    }

    public void edit(Clientes clientes) throws IllegalOrphanException, NonexistentEntityException, Exception {
        
        try {
            
            Clientes persistentClientes = em.find(Clientes.class, clientes.getIdCliente());
            Provincias codProvinciaOld = persistentClientes.getCodProvincia();
            Provincias codProvinciaNew = clientes.getCodProvincia();
            List<Compra> compraListOld = persistentClientes.getCompraList();
            List<Compra> compraListNew = clientes.getCompraList();
            List<Reparaciones> reparacionesListOld = persistentClientes.getReparacionesList();
            List<Reparaciones> reparacionesListNew = clientes.getReparacionesList();
            List<String> illegalOrphanMessages = null;
            for (Compra compraListOldCompra : compraListOld) {
                if (!compraListNew.contains(compraListOldCompra)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Compra " + compraListOldCompra + " since its idCliente field is not nullable.");
                }
            }
            for (Reparaciones reparacionesListOldReparaciones : reparacionesListOld) {
                if (!reparacionesListNew.contains(reparacionesListOldReparaciones)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Reparaciones " + reparacionesListOldReparaciones + " since its idCliente field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (codProvinciaNew != null) {
                codProvinciaNew = em.getReference(codProvinciaNew.getClass(), codProvinciaNew.getCodProvincia());
                clientes.setCodProvincia(codProvinciaNew);
            }
            List<Compra> attachedCompraListNew = new ArrayList<Compra>();
            for (Compra compraListNewCompraToAttach : compraListNew) {
                compraListNewCompraToAttach = em.getReference(compraListNewCompraToAttach.getClass(), compraListNewCompraToAttach.getNumeroCompra());
                attachedCompraListNew.add(compraListNewCompraToAttach);
            }
            compraListNew = attachedCompraListNew;
            clientes.setCompraList(compraListNew);
            List<Reparaciones> attachedReparacionesListNew = new ArrayList<Reparaciones>();
            for (Reparaciones reparacionesListNewReparacionesToAttach : reparacionesListNew) {
                reparacionesListNewReparacionesToAttach = em.getReference(reparacionesListNewReparacionesToAttach.getClass(), reparacionesListNewReparacionesToAttach.getIdReparacion());
                attachedReparacionesListNew.add(reparacionesListNewReparacionesToAttach);
            }
            reparacionesListNew = attachedReparacionesListNew;
            clientes.setReparacionesList(reparacionesListNew);
            clientes = em.merge(clientes);
            if (codProvinciaOld != null && !codProvinciaOld.equals(codProvinciaNew)) {
                codProvinciaOld.getClientesList().remove(clientes);
                codProvinciaOld = em.merge(codProvinciaOld);
            }
            if (codProvinciaNew != null && !codProvinciaNew.equals(codProvinciaOld)) {
                codProvinciaNew.getClientesList().add(clientes);
                codProvinciaNew = em.merge(codProvinciaNew);
            }
            for (Compra compraListNewCompra : compraListNew) {
                if (!compraListOld.contains(compraListNewCompra)) {
                    Clientes oldIdClienteOfCompraListNewCompra = compraListNewCompra.getIdCliente();
                    compraListNewCompra.setIdCliente(clientes);
                    compraListNewCompra = em.merge(compraListNewCompra);
                    if (oldIdClienteOfCompraListNewCompra != null && !oldIdClienteOfCompraListNewCompra.equals(clientes)) {
                        oldIdClienteOfCompraListNewCompra.getCompraList().remove(compraListNewCompra);
                        oldIdClienteOfCompraListNewCompra = em.merge(oldIdClienteOfCompraListNewCompra);
                    }
                }
            }
            for (Reparaciones reparacionesListNewReparaciones : reparacionesListNew) {
                if (!reparacionesListOld.contains(reparacionesListNewReparaciones)) {
                    Clientes oldIdClienteOfReparacionesListNewReparaciones = reparacionesListNewReparaciones.getIdCliente();
                    reparacionesListNewReparaciones.setIdCliente(clientes);
                    reparacionesListNewReparaciones = em.merge(reparacionesListNewReparaciones);
                    if (oldIdClienteOfReparacionesListNewReparaciones != null && !oldIdClienteOfReparacionesListNewReparaciones.equals(clientes)) {
                        oldIdClienteOfReparacionesListNewReparaciones.getReparacionesList().remove(reparacionesListNewReparaciones);
                        oldIdClienteOfReparacionesListNewReparaciones = em.merge(oldIdClienteOfReparacionesListNewReparaciones);
                    }
                }
            }
            
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = clientes.getIdCliente();
                if (findClientes(id) == null) {
                    throw new NonexistentEntityException("The clientes with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } 
    }

    public void destroy(Clientes cli) throws IllegalOrphanException, NonexistentEntityException {
            
            Clientes clientes = cli;
            try {
                //clientes = em.getReference(Clientes.class, id);
                clientes.getIdCliente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The clientes with id " + clientes.getNombre() + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Compra> compraListOrphanCheck = clientes.getCompraList();
            for (Compra compraListOrphanCheckCompra : compraListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("No se puede eliminar el cliente (" + clientes.getDniCliente() + ") , no se pueden eliminar clientes con compras " );
            }
            List<Reparaciones> reparacionesListOrphanCheck = clientes.getReparacionesList();
            for (Reparaciones reparacionesListOrphanCheckReparaciones : reparacionesListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("No se puede eliminar el cliente (" + clientes.getDniCliente() + ") , no se pueden eliminar clientes con reparaciones " );
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Provincias codProvincia = clientes.getCodProvincia();
            if (codProvincia != null) {
                codProvincia.getClientesList().remove(clientes);
                codProvincia = em.merge(codProvincia);
            }
            em.remove(clientes);
            
       
    }

    public void Guardar(){
        //getEm().flush();
        getEm().getTransaction().commit();
        getEm().getTransaction().begin();
    }

    public void Deshacer(){
        getEm().getTransaction().rollback();
        getEm().getTransaction().begin();
    }

    /*
    public List<Clientes> findClientesEntities() {
        return findClientesEntities(true, -1, -1);
    }

    public List<Clientes> findClientesEntities(int maxResults, int firstResult) {
        return findClientesEntities(false, maxResults, firstResult);
    }

    private List<Clientes> findClientesEntities(boolean all, int maxResults, int firstResult) {
        
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Clientes.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            List <Clientes> list = q.getResultList();
            for(int i=0;i<list.size();i++){
                em.refresh(list.get(i));
            }
            return list;
        
    }*/

    public Clientes findClientes(Integer id) {
       
            return em.find(Clientes.class, id);
    }

    /*
    public int getClientesCount() {
        
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Clientes> rt = cq.from(Clientes.class);
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
