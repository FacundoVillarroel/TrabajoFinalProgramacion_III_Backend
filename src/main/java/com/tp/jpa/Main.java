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
        // Opciones: 1-Alta  2-Modificar  3-Baja lógica  4-Listado  0-Volver
        boolean salir = false;

        while (!salir) {
            System.out.println();
            System.out.println("===== PRODUCTOS =====");
            System.out.println("1. Alta");
            System.out.println("2. Modificar");
            System.out.println("3. Baja lógica");
            System.out.println("4. Listado");
            System.out.println("0. Volver");

            try {
                String op = leerTexto("Opción: ");

                switch (op) {
                    case "1": altaProducto(); break;
                    case "2": modificarProducto(); break;
                    case "3": bajaProducto(); break;
                    case "4": listarProductosPorCategoria(); break;
                    case "0": salir = true; break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
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

    // ── Productos ────────────────────────────────────────────────

    private static void altaProducto() {
        if (listarCategorias().isEmpty()) {
            System.out.println("Para crear un producto, primero debe crear una categoría.");
            return;
        }

        Categoria categoria = buscarCategoriaActiva(leerId("ID de la categoría: "));

        String nombre = leerNombre("Nombre: ");
        String descripcion = leerTexto("Descripción: ");
        double precio = validarPrecio(leerTexto("Precio: "));
        int stock = validarStock(leerTexto("Stock: "));
        String imagen = leerTexto("Imagen (opcional): ");
        boolean disponible = leerDisponible();

        Producto producto = Producto.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(precio)
                .stock(stock)
                .imagen(imagen)
                .disponible(disponible)
                .build();

        // El repositorio persiste el producto y su asociación en una transacción.
        Producto guardado = categoriaRepo.agregarProducto(categoria.getId(), producto);

        System.out.println("Producto creado. ID: " + guardado.getId()
                + " | Categoría: " + categoria.getNombre());
    }

    private static void modificarProducto() {
        List<Producto> productos = productoRepo.listarActivos();

        if (productos.isEmpty()) {
            System.out.println("No hay productos activos.");
            return;
        }

        System.out.println("\n--- Productos activos ---");

        for (Producto producto : productos) {
            mostrarProducto(producto);
        }

        Producto producto = buscarProductoActivo(leerId("ID del producto: "));

        System.out.println("\nValores actuales:");
        mostrarProducto(producto);

        String nombre = leerTexto("Nuevo nombre (Enter conserva el actual): ");
        String precio = leerTexto("Nuevo precio (Enter conserva el actual): ");
        String stock = leerTexto("Nuevo stock (Enter conserva el actual): ");

        // Valida todos los valores antes de modificar la entidad.
        double nuevoPrecio = precio.isEmpty() ? producto.getPrecio() : validarPrecio(precio);
        int nuevoStock = stock.isEmpty() ? producto.getStock() : validarStock(stock);

        if (!nombre.isEmpty()) {
            producto.setNombre(nombre);
        }

        producto.setPrecio(nuevoPrecio);
        producto.setStock(nuevoStock);

        Producto guardado = productoRepo.guardar(producto);
        System.out.println("Producto modificado: " + guardado.getNombre());
    }

    private static void bajaProducto() {
        List<Producto> productos = productoRepo.listarActivos();

        if (productos.isEmpty()) {
            System.out.println("No hay productos activos.");
            return;
        }

        System.out.println("\n--- Productos activos ---");

        for (Producto producto : productos) {
            mostrarProducto(producto);
        }

        Long id = leerId("ID del producto: ");
        Producto producto = buscarProductoActivo(id);

        if (productoRepo.eliminarLogico(id)) {
            System.out.println("Producto dado de baja: " + producto.getNombre());
        } else {
            System.out.println("El producto no existe o ya está dado de baja.");
        }
    }

    private static void listarProductosPorCategoria() {
        List<Categoria> categorias = categoriaRepo.listarActivos();
        boolean hayProductos = false;

        // Consulta desde Categoria porque Producto no conoce su categoría.
        for (Categoria categoria : categorias) {
            List<Producto> productos = categoriaRepo.buscarProductosPorCategoria(categoria.getId());

            for (Producto producto : productos) {
                System.out.printf(
                        "ID: %d | Nombre: %s | Precio: %.2f | Stock: %d | Disponible: %s | Categoría: %s%n",
                        producto.getId(),
                        producto.getNombre(),
                        producto.getPrecio(),
                        producto.getStock(),
                        Boolean.TRUE.equals(producto.getDisponible()) ? "Sí" : "No",
                        categoria.getNombre()
                );

                hayProductos = true;
            }
        }

        if (!hayProductos) {
            System.out.println("No hay productos activos en las categorías activas.");
        }
    }

    private static Producto buscarProductoActivo(Long id) {
        Producto producto = productoRepo.buscarPorId(id).orElse(null);

        if (producto == null || producto.isEliminado()) {
            throw new IllegalArgumentException("El producto no existe o está dado de baja.");
        }

        return producto;
    }

    private static void mostrarProducto(Producto producto) {
        System.out.printf(
                "ID: %d | Nombre: %s | Precio: %.2f | Stock: %d%n",
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock()
        );
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

    private static double validarPrecio(String texto) {
        try {
            // Acepta punto o coma como separador decimal.
            double precio = Double.parseDouble(texto.replace(',', '.'));

            if (!Double.isFinite(precio) || precio <= 0) {
                throw new IllegalArgumentException("El precio debe ser un número mayor a cero.");
            }

            return precio;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser numérico.");
        }
    }

    private static int validarStock(String texto) {
        try {
            int stock = Integer.parseInt(texto);

            if (stock < 0) {
                throw new IllegalArgumentException("El stock no puede ser negativo.");
            }

            return stock;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El stock debe ser un número entero válido.");
        }
    }

    private static boolean leerDisponible() {
        String respuesta = leerTexto("Disponible (S/N, Enter = S): ");

        if (respuesta.isEmpty() || respuesta.equalsIgnoreCase("S")) {
            return true;
        }

        if (respuesta.equalsIgnoreCase("N")) {
            return false;
        }

        throw new IllegalArgumentException("La disponibilidad debe ser S o N.");
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