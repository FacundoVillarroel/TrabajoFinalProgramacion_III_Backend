package com.tp.jpa;

import com.tp.jpa.model.enums.EstadoPedido;
import com.tp.jpa.model.*;
import com.tp.jpa.model.enums.FormaPago;
import com.tp.jpa.model.enums.Rol;
import com.tp.jpa.repository.CategoriaRepository;
import com.tp.jpa.repository.PedidoRepository;
import com.tp.jpa.repository.ProductoRepository;
import com.tp.jpa.repository.UsuarioRepository;
import com.tp.jpa.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

/**
 * Clase principal: menú de consola del sistema Food Store.
 * Orden de uso natural: Categorías -> Productos -> Usuarios -> Pedidos.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static final CategoriaRepository categoriaRepo = new CategoriaRepository();
    private static final ProductoRepository productoRepo = new ProductoRepository();
    private static final UsuarioRepository usuarioRepo = new UsuarioRepository();
    private static final PedidoRepository pedidoRepo = new PedidoRepository();

    public static void main(String[] args) {
        boolean salir = false;

        while (!salir) {
            System.out.println();
            System.out.println("===== FOOD STORE - MENÚ PRINCIPAL =====");
            System.out.println("1. Gestionar Categorías");
            System.out.println("2. Gestionar Productos");
            System.out.println("3. Gestionar Usuarios");
            System.out.println("4. Gestionar Pedidos");
            System.out.println("5. Reportes");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            String op = sc.nextLine().trim();

            switch (op) {
                case "1": menuCategorias(); break;
                case "2": menuProductos(); break;
                case "3": menuUsuarios(); break;
                case "4": menuPedidos(); break;
                case "5": menuReportes(); break;
                case "0": salir = true; break;
                default: System.out.println("Opción inválida.");
            }
        }

        JPAUtil.close();
        System.out.println("Aplicación finalizada.");
    }

    // ── Submenús ─────────────────────────────────────────────────

    private static void menuCategorias() {
        // Opciones: 1-Alta  2-Modificar  3-Baja lógica  4-Listado  0-Volver
        boolean salir = false;

        while (!salir) {
            System.out.println();
            System.out.println("===== CATEGORÍAS =====");
            System.out.println("1. Alta");
            System.out.println("2. Modificar");
            System.out.println("3. Baja lógica");
            System.out.println("4. Listado");
            System.out.println("0. Volver");

            try {
                String op = leerTexto("Opción: ");

                switch (op) {
                    case "1": altaCategoria(); break;
                    case "2": modificarCategoria(); break;
                    case "3": bajaCategoria(); break;
                    case "4": listarCategorias(); break;
                    case "0": salir = true; break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
    }

    private static void menuProductos() {
        // TODO: Implementar submenú de Productos.
        // Opciones: 1-Alta  2-Modificar  3-Baja lógica  4-Listado  0-Volver
        System.out.println("[Productos] → TODO: implementar");
    }

    private static void menuUsuarios() {
        // TODO: Implementar submenú de Usuarios.
        // Opciones: 1-Alta  2-Modificar  3-Baja lógica  4-Listado  5-Buscar por mail  0-Volver
        System.out.println("[Usuarios] → TODO: implementar");
    }

    private static void menuPedidos() {
        // TODO: Implementar submenú de Pedidos.
        // Opciones: 1-Alta  2-Cambiar estado  3-Baja lógica  4-Listado
        //           5-Por usuario  6-Por estado  0-Volver
        System.out.println("[Pedidos] → TODO: implementar");
    }

    private static void menuReportes() {
        // TODO: Implementar submenú de Reportes.
        // Opciones: 1-Productos por categoría  2-Pedidos por usuario
        //           3-Pedidos por estado  4-Total facturado  0-Volver
        System.out.println("[Reportes] → TODO: implementar");
    }

    // ── Categorías ───────────────────────────────────────────────

    private static void altaCategoria() {
        String nombre = leerNombre("Nombre: ");
        String descripcion = leerTexto("Descripción (opcional): ");

        Categoria categoria = Categoria.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .build();

        Categoria guardada = categoriaRepo.guardar(categoria);
        System.out.println("Categoría creada. ID: " + guardada.getId());
    }

    private static void modificarCategoria() {
        if (listarCategorias().isEmpty()) {
            return;
        }

        Categoria categoria = buscarCategoriaActiva(leerId("ID de la categoría: "));

        System.out.println("Nombre actual: " + categoria.getNombre());
        System.out.println("Descripción actual: " + textoOpcional(categoria.getDescripcion()));

        String nombre = leerTexto("Nuevo nombre (Enter conserva el actual): ");
        String descripcion = leerTexto("Nueva descripción (Enter conserva la actual): ");

        if (!nombre.isEmpty()) {
            categoria.setNombre(nombre);
        }

        if (!descripcion.isEmpty()) {
            categoria.setDescripcion(descripcion);
        }

        Categoria guardada = categoriaRepo.guardar(categoria);
        System.out.println("Categoría modificada: " + guardada.getNombre());
    }

    private static void bajaCategoria() {
        if (listarCategorias().isEmpty()) {
            return;
        }

        Long id = leerId("ID de la categoría: ");
        Categoria categoria = buscarCategoriaActiva(id);

        // Consulta los productos activos antes de permitir la baja de su categoría.
        int cantidad = categoriaRepo.buscarProductosPorCategoria(id).size();

        if (cantidad > 0) {
            System.out.println("No se puede dar de baja: tiene " + cantidad + " producto(s) activo(s).");
            System.out.println("Primero deben darse de baja esos productos.");
            return;
        }

        if (categoriaRepo.eliminarLogico(id)) {
            System.out.println("Categoría dada de baja: " + categoria.getNombre());
        } else {
            System.out.println("La categoría no existe o ya está dada de baja.");
        }
    }

    private static List<Categoria> listarCategorias() {
        List<Categoria> categorias = categoriaRepo.listarActivos();

        if (categorias.isEmpty()) {
            System.out.println("No hay categorías activas.");
            return categorias;
        }

        System.out.println("\n--- Categorías activas ---");

        for (Categoria categoria : categorias) {
            System.out.printf(
                    "ID: %d | Nombre: %s | Descripción: %s%n",
                    categoria.getId(),
                    categoria.getNombre(),
                    textoOpcional(categoria.getDescripcion())
            );
        }

        return categorias;
    }

    private static Categoria buscarCategoriaActiva(Long id) {
        Categoria categoria = categoriaRepo.buscarPorId(id).orElse(null);

        if (categoria == null || categoria.isEliminado()) {
            throw new IllegalArgumentException("La categoría no existe o está dada de baja.");
        }

        return categoria;
    }

    // ── Métodos auxiliares ───────────────────────────────────────

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    private static String leerNombre(String mensaje) {
        String nombre = leerTexto(mensaje);

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        return nombre;
    }

    private static Long leerId(String mensaje) {
        try {
            long id = Long.parseLong(leerTexto(mensaje));

            if (id <= 0) {
                throw new IllegalArgumentException("El ID debe ser un entero mayor a cero.");
            }

            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID debe ser un número entero válido.");
        }
    }

    private static String textoOpcional(String texto) {
        return texto == null || texto.isBlank() ? "(sin descripción)" : texto;
    }

    private static void mostrarError(RuntimeException e) {
        if (e instanceof IllegalArgumentException) {
            System.out.println("Error: " + e.getMessage());
        } else {
            System.out.println("No se pudo completar la operación. Revise la conexión y los datos.");
            e.printStackTrace();
        }
    }
}