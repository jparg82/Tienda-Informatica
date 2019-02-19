/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Controlador.exceptions.NonexistentEntityException;
import Controlador.exceptions.PreexistingEntityException;
import Modelo.Usuarios;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
import Modelo.Empleados;
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
public class UsuariosJpaController {

    private EntityManager em;

    public UsuariosJpaController() {
        //emf = Persistence.createEntityManagerFactory("ProyectoDAIPU");
        emf= Utilidades.Utilidades.getEmf();
        em=getEntityManager();
        em.getTransaction().begin();
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuarios usuarios) throws PreexistingEntityException, Exception {
        if (usuarios.getEmpleadosList() == null) {
            usuarios.setEmpleadosList(new ArrayList<Empleados>());
        }

        try {
         
            List<Empleados> attachedEmpleadosList = new ArrayList<Empleados>();
            for (Empleados empleadosListEmpleadosToAttach : usuarios.getEmpleadosList()) {
                empleadosListEmpleadosToAttach = em.getReference(empleadosListEmpleadosToAttach.getClass(), empleadosListEmpleadosToAttach.getIdEmpleado());
                attachedEmpleadosList.add(empleadosListEmpleadosToAttach);
            }
            usuarios.setEmpleadosList(attachedEmpleadosList);
            em.persist(usuarios);
            for (Empleados empleadosListEmpleados : usuarios.getEmpleadosList()) {
                Usuarios oldUsuarioOfEmpleadosListEmpleados = empleadosListEmpleados.getUsuario();
                empleadosListEmpleados.setUsuario(usuarios);
                empleadosListEmpleados = em.merge(empleadosListEmpleados);
                if (oldUsuarioOfEmpleadosListEmpleados != null) {
                    oldUsuarioOfEmpleadosListEmpleados.getEmpleadosList().remove(empleadosListEmpleados);
                    oldUsuarioOfEmpleadosListEmpleados = em.merge(oldUsuarioOfEmpleadosListEmpleados);
                }
            }
           
        } catch (Exception ex) {
            if (findUsuarios(usuarios.getUsuario()) != null) {
                throw new PreexistingEntityException("Usuarios " + usuarios + " already exists.", ex);
            }
            throw ex;
        }
    }

    public void edit(Usuarios usuarios) throws NonexistentEntityException, Exception {
       
        try {
        
            Usuarios persistentUsuarios = em.find(Usuarios.class, usuarios.getUsuario());
            List<Empleados> empleadosListOld = persistentUsuarios.getEmpleadosList();
            List<Empleados> empleadosListNew = usuarios.getEmpleadosList();
            List<Empleados> attachedEmpleadosListNew = new ArrayList<Empleados>();
            for (Empleados empleadosListNewEmpleadosToAttach : empleadosListNew) {
                empleadosListNewEmpleadosToAttach = em.getReference(empleadosListNewEmpleadosToAttach.getClass(), empleadosListNewEmpleadosToAttach.getIdEmpleado());
                attachedEmpleadosListNew.add(empleadosListNewEmpleadosToAttach);
            }
            empleadosListNew = attachedEmpleadosListNew;
            usuarios.setEmpleadosList(empleadosListNew);
            usuarios = em.merge(usuarios);
            for (Empleados empleadosListOldEmpleados : empleadosListOld) {
                if (!empleadosListNew.contains(empleadosListOldEmpleados)) {
                    empleadosListOldEmpleados.setUsuario(null);
                    empleadosListOldEmpleados = em.merge(empleadosListOldEmpleados);
                }
            }
            for (Empleados empleadosListNewEmpleados : empleadosListNew) {
                if (!empleadosListOld.contains(empleadosListNewEmpleados)) {
                    Usuarios oldUsuarioOfEmpleadosListNewEmpleados = empleadosListNewEmpleados.getUsuario();
                    empleadosListNewEmpleados.setUsuario(usuarios);
                    empleadosListNewEmpleados = em.merge(empleadosListNewEmpleados);
                    if (oldUsuarioOfEmpleadosListNewEmpleados != null && !oldUsuarioOfEmpleadosListNewEmpleados.equals(usuarios)) {
                        oldUsuarioOfEmpleadosListNewEmpleados.getEmpleadosList().remove(empleadosListNewEmpleados);
                        oldUsuarioOfEmpleadosListNewEmpleados = em.merge(oldUsuarioOfEmpleadosListNewEmpleados);
                    }
                }
            }
            
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuarios.getUsuario();
                if (findUsuarios(id) == null) {
                    throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } 
    }

    public void destroy(String id) throws NonexistentEntityException {

            Usuarios usuarios;
            try {
                usuarios = em.getReference(Usuarios.class, id);
                usuarios.getUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.", enfe);
            }
            List<Empleados> empleadosList = usuarios.getEmpleadosList();
            for (Empleados empleadosListEmpleados : empleadosList) {
                empleadosListEmpleados.setUsuario(null);
                empleadosListEmpleados = em.merge(empleadosListEmpleados);
            }
            em.remove(usuarios);
       
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
    public List<Usuarios> findUsuariosEntities() {
        return findUsuariosEntities(true, -1, -1);
    }

    public List<Usuarios> findUsuariosEntities(int maxResults, int firstResult) {
        return findUsuariosEntities(false, maxResults, firstResult);
    }*/

    /*
    private List<Usuarios> findUsuariosEntities(boolean all, int maxResults, int firstResult) {
        
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuarios.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
      
    }*/

    public Usuarios findUsuarios(String id) {
       
            return em.find(Usuarios.class, id);
     
    }

    /*
    public int getUsuariosCount() {
       
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuarios> rt = cq.from(Usuarios.class);
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
