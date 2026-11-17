package com.tp.jpa.repository;

import com.tp.jpa.model.Categoria;
import com.tp.jpa.model.Producto;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Repositorio de Categoria. Además del CRUD heredado implementa la consulta
 * de productos activos pertenecientes a una categoría.
 *
 * Nota de diseño: como la relación es unidireccional y Categoria es la dueña
 * de la colección Set<Producto>, la navegación se hace desde Categoria hacia
 * sus productos (p. ej. JPQL con JOIN sobre c.productos).
 */
public class CategoriaRepository extends BaseRepository<Categoria> {

    public CategoriaRepository() {
        super(Categoria.class);
    }

    public List<Producto> buscarProductosPorCategoria(Long categoriaId) {
        EntityManager em = emf.createEntityManager();

        try {
            // Categoria es la dueña de la relación unidireccional.
            // JOIN recorre sus productos y filtra los activos mediante :catId.
            String jpql = "SELECT p FROM Categoria c JOIN c.productos p "
                    + "WHERE c.id = :catId AND p.eliminado = false";

            return em.createQuery(jpql, Producto.class)
                    .setParameter("catId", categoriaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Producto agregarProducto(Long categoriaId, Producto producto) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Categoria categoria = em.find(Categoria.class, categoriaId);

            if (categoria == null || categoria.isEliminado()) {
                throw new IllegalArgumentException(
                        "La categoría no existe o está dada de baja."
                );
            }

            // La categoría está gestionada por este EntityManager.
            // El cascade persiste el producto y la colección administra categoria_id.
            categoria.addProducto(producto);

            em.getTransaction().commit();
            return producto;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;
        } finally {
            em.close();
        }
    }
}