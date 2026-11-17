# Food Store — Backend

Trabajo Práctico Integrador de Programación III de la Tecnicatura Universitaria en Programación a Distancia.

**Autor** Facundo Villarroel.

## Descripción

Aplicación de consola desarrollada en Java con JPA, Hibernate y H2. Permite gestionar categorías, productos, usuarios y pedidos de comida. Los datos se almacenan en una base H2 en archivo y se conservan al cerrar la aplicación.

El backend funciona de forma independiente del frontend web.

## Funcionalidades

- Alta, modificación, listado y baja lógica de categorías.
- Alta de productos asociados a una categoría, modificación, listado y baja lógica.
- Edición de nombre, precio, stock y disponibilidad de productos.
- Alta, modificación, listado, baja lógica y búsqueda de usuarios por mail.
- Creación de pedidos con productos, cantidades y forma de pago.
- Validación de disponibilidad y stock, cálculo de subtotales y total, y descuento de inventario.
- Cambio de estado y baja lógica de pedidos.
- Reportes de productos por categoría, pedidos por usuario, pedidos por estado y total facturado.

## Requisitos

- JDK 21.
- Conexión a Internet para descargar Gradle y las dependencias en la primera ejecución.
- IntelliJ IDEA, si se desea ejecutar desde el IDE.

El proyecto incluye Gradle Wrapper, que descarga y utiliza Gradle 8.5. Las dependencias de Hibernate, JPA, H2 y Lombok se obtienen automáticamente.

## Ejecución desde la terminal

Abrir una terminal en la raíz del backend, donde se encuentra `build.gradle`.

### Windows — PowerShell

Compilar el proyecto.

```powershell
.\gradlew.bat clean build
```

Ejecutar el menú de consola.

```powershell
.\gradlew.bat run
```

## Ejecución desde IntelliJ IDEA

1. Abrir la carpeta del backend e importarla como proyecto Gradle.
2. Seleccionar JDK 21 como SDK del proyecto y como Gradle JVM.
3. Esperar la sincronización de dependencias.
4. Ejecutar el método `main` de `src/main/java/com/tp/jpa/Main.java`.

El directorio de trabajo de la ejecución debe ser la raíz del backend.

## Uso

El menú principal ofrece estas opciones.

| Opción | Módulo |
| --- | --- |
| 1 | Categorías |
| 2 | Productos |
| 3 | Usuarios |
| 4 | Pedidos |
| 5 | Reportes |
| 0 | Salir |

Para comenzar con una base nueva, crear categorías, luego productos y usuarios, y finalmente pedidos. Los productos necesitan una categoría activa y los pedidos necesitan un usuario activo y productos disponibles con stock suficiente.

En las modificaciones, presionar Enter conserva el valor actual del campo. La baja es lógica y conserva los registros en la base. El total facturado suma únicamente pedidos activos en estado `TERMINADO`.

Seleccionar `0` en el menú principal para finalizar y cerrar la fábrica de persistencia.

## Base de datos

La configuración está en `src/main/resources/META-INF/persistence.xml`.

| Parámetro | Valor |
| --- | --- |
| Motor | H2 en archivo |
| URL JDBC | `jdbc:h2:file:./data/jpa_db` |
| Usuario | `sa` |
| Contraseña | Vacía |
| Archivo generado | `data/jpa_db.mv.db` |

Hibernate crea la base y las tablas necesarias cuando se inicia la aplicación por primera vez. En las siguientes ejecuciones se conservan los datos existentes.

## Tecnologías

- Java 21
- JPA / Hibernate 6
- H2 (base de datos en archivo — `./data/jpa_db`)
- Lombok
- Gradle 8

---

## Estructura del proyecto

```
src/main/java/com/tp/jpa/
│
├── model/                        # Modelo de dominio y entidades JPA 
│   ├── Base.java                 # Clase abstracta base (id, eliminado, createdAt)
│   ├── Calculable.java           # Interfaz con calcularTotal()
│   ├── Categoria.java            # Categoría y sus productos
│   ├── Producto.java             # Datos del producto, precio y stock
│   ├── Usuario.java              # Datos del usuario y sus pedidos
│   ├── Pedido.java               # Pedido, detalles y cálculo del total
│   ├── DetallePedido.java        # Producto, cantidad y subtotal
│   └── enums/
│       ├── Rol.java              # ADMIN y USUARIO
│       ├── Estado.java           # Estados del pedido
│       └── FormaPago.java        # Formas de pago admitidas
│
├── util/
│   └── JPAUtil.java              # Creación y cierre del EntityManagerFactory
│
├── repository/                   # Persistencia y consultas JPQL
│   ├── BaseRepository.java       # CRUD genérico y baja lógica
│   ├── ProductoRepository.java   # CRUD heredado de BaseRepository
│   ├── CategoriaRepository.java  # Alta asociada y consulta de productos
│   ├── UsuarioRepository.java    # Búsqueda por mail y pedidos por usuario
│   └── PedidoRepository.java     # Consulta de pedidos por estado
│
├─ Main.java                      # Menús, validaciones, reportes y alta transaccional
└── resources/
    └── META-INF/
        └── persistence.xml       # Configuración de JPA, Hibernate y H2
```

## Entrega

- **Video demostrativo:** [link aquí]
- **Informe PDF:** [link aquí]
