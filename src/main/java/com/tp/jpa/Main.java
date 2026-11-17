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
import java.time.LocalDate;

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

    // Guarda la selección temporal sin conservar entidades de otro EntityManager.
    private static class ItemPedido {
        private final Long idProducto;
        private int cantidad;

        private ItemPedido(Long idProducto, int cantidad) {
            this.idProducto = idProducto;
            this.cantidad = cantidad;
        }
    }

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
        // Opciones: 1-Alta  2-Modificar  3-Baja lógica  4-Listado  5-Buscar por mail  0-Volver
        boolean salir = false;

        while (!salir) {
            System.out.println();
            System.out.println("===== USUARIOS =====");
            System.out.println("1. Alta");
            System.out.println("2. Modificar");
            System.out.println("3. Baja lógica");
            System.out.println("4. Listado");
            System.out.println("5. Buscar por mail");
            System.out.println("0. Volver");

            try {
                String op = leerTexto("Opción: ");

                switch (op) {
                    case "1": altaUsuario(); break;
                    case "2": modificarUsuario(); break;
                    case "3": bajaUsuario(); break;
                    case "4": listarUsuarios(); break;
                    case "5": buscarUsuarioPorMail(); break;
                    case "0": salir = true; break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
    }

    private static void menuPedidos() {
        // Opciones: 1-Alta  2-Cambiar estado  3-Baja lógica  4-Listado 5-Por usuario  6-Por estado  0-Volver
        boolean salir = false;

        while (!salir) {
            System.out.println();
            System.out.println("===== PEDIDOS =====");
            System.out.println("1. Alta de pedido");
            System.out.println("0. Volver");

            try {
                String op = leerTexto("Opción: ");

                switch (op) {
                    case "1": altaPedido(); break;
                    case "0": salir = true; break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
    }

    private static void menuReportes() {
        // Opciones: 1-Productos por categoría  2-Pedidos por usuario
        //           3-Pedidos por estado  4-Total facturado  0-Volver
        boolean salir = false;

        while (!salir) {
            System.out.println();
            System.out.println("===== REPORTES =====");
            System.out.println("1. Productos por categoría");
            System.out.println("0. Volver");

            try {
                String op = leerTexto("Opción: ");

                switch (op) {
                    case "1": reporteProductosPorCategoria(); break;
                    case "0": salir = true; break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
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

    // ── Usuarios ─────────────────────────────────────────────────

    private static void altaUsuario() {
        String nombre = leerNombre("Nombre: ");
        String apellido = leerTexto("Apellido: ");
        String mail = leerTexto("Mail: ");

        if (usuarioRepo.buscarPorMail(mail).isPresent()) {
            System.out.println("Ya existe un usuario activo con ese mail.");
            return;
        }

        String celular = leerTexto("Celular (opcional): ");
        String contrasena = leerTexto("Contraseña: ");
        Rol rol = leerRol();

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .apellido(apellido)
                .mail(mail)
                .celular(celular)
                .contrasena(contrasena)
                .rol(rol)
                .build();

        Usuario guardado = usuarioRepo.guardar(usuario);
        System.out.println("Usuario creado. ID: " + guardado.getId());
    }

    private static void modificarUsuario() {
        if (listarUsuarios().isEmpty()) {
            return;
        }

        Usuario usuario = buscarUsuarioActivo(leerId("ID del usuario: "));

        System.out.println("\nValores actuales:");
        mostrarDatosUsuario(usuario);
        System.out.println("Contraseña actual: [oculta]");

        String nombre = leerTexto("Nuevo nombre (Enter conserva el actual): ");
        String apellido = leerTexto("Nuevo apellido (Enter conserva el actual): ");
        String mail = leerTexto("Nuevo mail (Enter conserva el actual): ");
        String celular = leerTexto("Nuevo celular (Enter conserva el actual): ");
        String contrasena = leerTexto("Nueva contraseña (Enter conserva la actual): ");

        // Comprueba el mail antes de aplicar los cambios a la entidad.
        if (!mail.isEmpty()) {
            Optional<Usuario> existente = usuarioRepo.buscarPorMail(mail);

            if (existente.isPresent() && !existente.get().getId().equals(usuario.getId())) {
                System.out.println("Otro usuario activo ya tiene ese mail.");
                return;
            }
        }

        if (!nombre.isEmpty()) {
            usuario.setNombre(nombre);
        }

        if (!apellido.isEmpty()) {
            usuario.setApellido(apellido);
        }

        if (!mail.isEmpty()) {
            usuario.setMail(mail);
        }

        if (!celular.isEmpty()) {
            usuario.setCelular(celular);
        }

        if (!contrasena.isEmpty()) {
            usuario.setContrasena(contrasena);
        }

        Usuario guardado = usuarioRepo.guardar(usuario);

        System.out.println("Usuario modificado: "
                + guardado.getNombre() + " " + guardado.getApellido());
    }

    private static void bajaUsuario() {
        Long id = leerId("ID del usuario: ");
        Usuario usuario = buscarUsuarioActivo(id);

        // La baja modifica únicamente el campo eliminado del usuario.
        if (usuarioRepo.eliminarLogico(id)) {
            System.out.println("Usuario dado de baja: "
                    + usuario.getNombre() + " " + usuario.getApellido());
        } else {
            System.out.println("El usuario no existe o ya está dado de baja.");
        }
    }

    private static List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepo.listarActivos();

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios activos.");
            return usuarios;
        }

        System.out.println("\n--- Usuarios activos ---");

        for (Usuario usuario : usuarios) {
            System.out.printf(
                    "ID: %d | Nombre: %s %s | Mail: %s | Rol: %s%n",
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getMail(),
                    usuario.getRol()
            );
        }

        return usuarios;
    }

    private static void buscarUsuarioPorMail() {
        String mail = leerTexto("Mail: ");
        Optional<Usuario> usuario = usuarioRepo.buscarPorMail(mail);

        if (usuario.isEmpty()) {
            System.out.println("No existe un usuario activo con ese mail.");
            return;
        }

        mostrarDatosUsuario(usuario.get());
    }

    private static Usuario buscarUsuarioActivo(Long id) {
        Usuario usuario = usuarioRepo.buscarPorId(id).orElse(null);

        if (usuario == null || usuario.isEliminado()) {
            throw new IllegalArgumentException("El usuario no existe o está dado de baja.");
        }

        return usuario;
    }

    private static void mostrarDatosUsuario(Usuario usuario) {
        // Muestra campos explícitos para excluir la contraseña.
        System.out.println("ID: " + usuario.getId());
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Apellido: " + usuario.getApellido());
        System.out.println("Mail: " + usuario.getMail());

        if (usuario.getCelular() == null || usuario.getCelular().isBlank()) {
            System.out.println("Celular: (sin informar)");
        } else {
            System.out.println("Celular: " + usuario.getCelular());
        }

        System.out.println("Rol: " + usuario.getRol());
        System.out.println("Fecha de creación: " + usuario.getCreatedAt());
        System.out.println("Eliminado: " + usuario.isEliminado());
    }

    // ── Pedidos ──────────────────────────────────────────────────

    private static void altaPedido() {
        if (listarUsuarios().isEmpty()) {
            System.out.println("Para crear un pedido, primero debe crear un usuario.");
            return;
        }

        Long idUsuario = leerId("ID del usuario: ");
        buscarUsuarioActivo(idUsuario);

        FormaPago formaPago = leerFormaPago();
        List<ItemPedido> items = new ArrayList<>();
        boolean agregarOtro = true;

        while (agregarOtro) {
            List<Producto> productos = productoRepo.listarActivos();

            if (productos.isEmpty()) {
                System.out.println("No hay productos activos. Se cancela el alta.");
                return;
            }

            System.out.println("\n--- Productos activos ---");

            for (Producto producto : productos) {
                mostrarProducto(producto);
            }

            Producto producto = buscarProductoActivo(leerId("ID del producto: "));

            if (!Boolean.TRUE.equals(producto.getDisponible())) {
                throw new IllegalArgumentException("El producto no está disponible.");
            }

            int cantidad = leerCantidad();
            ItemPedido existente = null;

            for (ItemPedido item : items) {
                if (item.idProducto.equals(producto.getId())) {
                    existente = item;
                    break;
                }
            }

            int cantidadAnterior = 0;

            if (existente != null) {
                cantidadAnterior = existente.cantidad;
            }

            // También considera las unidades seleccionadas anteriormente.
            if (cantidad > producto.getStock() - cantidadAnterior) {
                throw new IllegalArgumentException(
                        "Stock disponible: " + producto.getStock()
                                + ". Ya seleccionadas: " + cantidadAnterior + "."
                );
            }

            // Agrupa un producto repetido para crear un único detalle por producto.
            if (existente == null) {
                items.add(new ItemPedido(producto.getId(), cantidad));
            } else {
                existente.cantidad += cantidad;
            }

            agregarOtro = leerAgregarOtroProducto();
        }

        if (items.isEmpty()) {
            System.out.println("El pedido debe tener al menos un detalle.");
            return;
        }

        guardarPedido(idUsuario, formaPago, items);
    }

    private static void guardarPedido(
            Long idUsuario,
            FormaPago formaPago,
            List<ItemPedido> items
    ) {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Recupera al usuario dentro del mismo contexto que guardará el pedido.
            Usuario usuario = em.find(Usuario.class, idUsuario);

            if (usuario == null || usuario.isEliminado()) {
                throw new IllegalArgumentException("El usuario no existe o está dado de baja.");
            }

            Pedido pedido = Pedido.builder()
                    .fecha(LocalDate.now())
                    .estado(EstadoPedido.PENDIENTE)
                    .formaPago(formaPago)
                    .build();

            for (ItemPedido item : items) {
                Producto producto = em.find(Producto.class, item.idProducto);

                // Revalida dentro de la transacción para evitar guardar datos desactualizados.
                if (producto == null || producto.isEliminado()) {
                    throw new IllegalArgumentException(
                            "El producto con ID " + item.idProducto + " ya no está activo."
                    );
                }

                if (!Boolean.TRUE.equals(producto.getDisponible())) {
                    throw new IllegalArgumentException(
                            "El producto " + producto.getNombre() + " ya no está disponible."
                    );
                }

                if (item.cantidad <= 0 || item.cantidad > producto.getStock()) {
                    throw new IllegalArgumentException(
                            "Cantidad inválida o stock insuficiente para "
                                    + producto.getNombre()
                                    + ". Stock disponible: " + producto.getStock()
                    );
                }

                pedido.addDetallePedido(item.cantidad, producto);

                // El producto está gestionado. Hibernate guarda el cambio al confirmar.
                producto.setStock(producto.getStock() - item.cantidad);
            }

            pedido.calcularTotal();

            // El cascade persiste los detalles junto con el pedido.
            em.persist(pedido);

            // Usuario administra la asociación y la columna usuario_id.
            usuario.getPedidos().add(pedido);

            tx.commit();

            System.out.println("\n--- Pedido creado ---");
            System.out.println("ID: " + pedido.getId());
            System.out.println("Fecha: " + pedido.getFecha());
            System.out.println("Usuario: " + usuario.getNombre() + " " + usuario.getApellido());
            System.out.println("Forma de pago: " + pedido.getFormaPago());

            for (DetallePedido detalle : pedido.getDetalles()) {
                System.out.printf(
                        "Producto: %s | Cantidad: %d | Subtotal: $%.2f%n",
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getSubtotal()
                );
            }

            System.out.printf(Locale.US, "Total: $%.2f%n", pedido.getTotal());
        } catch (RuntimeException e) {
            // Revierte pedido, detalles, asociación y stock si falla la operación.
            if (tx.isActive()) {
                tx.rollback();
            }

            throw e;
        } finally {
            em.close();
        }
    }

    // ── Reportes ─────────────────────────────────────────────────

    private static void reporteProductosPorCategoria() {
        if (listarCategorias().isEmpty()) {
            return;
        }

        Categoria categoria = buscarCategoriaActiva(leerId("ID de la categoría: "));
        List<Producto> productos = categoriaRepo.buscarProductosPorCategoria(categoria.getId());

        System.out.println("\n--- Productos de " + categoria.getNombre() + " ---");

        if (productos.isEmpty()) {
            System.out.println("Esta categoría no tiene productos activos.");
            return;
        }

        for (Producto producto : productos) {
            mostrarProducto(producto);
        }
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

    private static Rol leerRol() {
        System.out.println("Rol:");
        System.out.println("1. ADMIN");
        System.out.println("2. USUARIO");

        String opcion = leerTexto("Opción: ");

        switch (opcion) {
            case "1": return Rol.ADMIN;
            case "2": return Rol.USUARIO;
            default: throw new IllegalArgumentException("El rol seleccionado es inválido.");
        }
    }

    private static FormaPago leerFormaPago() {
        System.out.println("Forma de pago:");
        System.out.println("1. TARJETA");
        System.out.println("2. TRANSFERENCIA");
        System.out.println("3. EFECTIVO");

        String opcion = leerTexto("Opción: ");

        switch (opcion) {
            case "1": return FormaPago.TARJETA;
            case "2": return FormaPago.TRANSFERENCIA;
            case "3": return FormaPago.EFECTIVO;
            default: throw new IllegalArgumentException("La forma de pago es inválida.");
        }
    }

    private static int leerCantidad() {
        try {
            int cantidad = Integer.parseInt(leerTexto("Cantidad: "));

            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }

            return cantidad;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La cantidad debe ser un número entero válido.");
        }
    }

    private static boolean leerAgregarOtroProducto() {
        while (true) {
            String respuesta = leerTexto("¿Agregar otro producto? (S/N): ");

            if (respuesta.equalsIgnoreCase("S")) {
                return true;
            }

            if (respuesta.equalsIgnoreCase("N")) {
                return false;
            }

            System.out.println("Ingrese S o N.");
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