# Java Project: Shop Management System 🏪

Sistema de gestión de tienda desarrollado en Java con persistencia en MySQL.

**Autor:** Marc Muntané Clarà  
**Versión:** 2.0

## Descripción del Proyecto

El sistema permite gestionar el inventario de productos, ventas y autenticación de empleados de una tienda. Utiliza una arquitectura DAO (Data Access Object) para la persistencia de datos en MySQL.

Actualmente incluye dos implementaciones principales:
- JDBC (DaoImplJDBC)
- Hibernate ORM (DaoImplHibernate)

## Características Principales

### Gestión de Inventario SQL
- **Carga inicial desde tabla `inventory`**: Al iniciar la aplicación, se recuperan todos los productos desde la base de datos MySQL.
- **Exportación a tabla histórica `historical_inventory`**: Permite guardar snapshots del inventario con timestamp.
- **Sincronización en tiempo real**: Todas las operaciones (añadir, actualizar, eliminar productos) se reflejan inmediatamente en la base de datos.

### Operaciones CRUD de Productos
- `addProduct()` - Añade nuevos productos al inventario y BD
- `updateProduct()` - Actualiza stock y disponibilidad
- `deleteProduct()` - Elimina productos del inventario y BD
- `findProduct()` - Busca productos por nombre

### Autenticación de Empleados
- Login seguro contra tabla `employee` de MySQL
- Validación de credenciales mediante JDBC

### Interfaz Gráfica (Swing)
- **LoginView**: Autenticación de empleados
- **ShopView**: Dashboard principal con menú de opciones
- **ProductView**: Gestión de productos (añadir, stock, eliminar)
- **CashView**: Visualización del dinero en caja

## Estructura de Base de Datos

```sql
-- Tabla de inventario actual
CREATE TABLE inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    stock INT DEFAULT 0
);

-- Tabla de inventario histórico
CREATE TABLE historical_inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_product INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    stock INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de empleados
CREATE TABLE employee (
    employeeId INT PRIMARY KEY,
    name VARCHAR(100),
    password VARCHAR(100)
);
```

## Requisitos

- **JDK 17** o superior
- **MySQL Server** corriendo en localhost:3306
- (Recomendado) **Maven** instalado para resolver dependencias de Hibernate
- Base de datos llamada `shop`
- Usuario MySQL: `root` (sin contraseña por defecto)

La configuración de Hibernate se encuentra en `src/main/resources/hibernate.cfg.xml`.
El esquema SQL de referencia está en `sql/create_tables.sql`.

## Estructura del Proyecto

```
src/
├── dao/
│   ├── Dao.java              # Interfaz DAO
│   ├── DaoImplJDBC.java      # Implementación JDBC
│   └── DaoImplFile.java      # Implementación ficheros
├── main/
│   ├── Shop.java             # Clase principal
│   ├── Logable.java          # Interfaz de login
│   └── Payable.java          # Interfaz de pago
├── model/
│   ├── Product.java          # Modelo de producto
│   ├── Amount.java           # Modelo de cantidad monetaria
│   ├── Employee.java         # Modelo de empleado
│   ├── Client.java           # Modelo de cliente
│   └── Sale.java             # Modelo de venta
├── view/
│   ├── ShopView.java         # Vista principal
│   ├── ProductView.java      # Vista de productos
│   ├── LoginView.java        # Vista de login
│   └── CashView.java         # Vista de caja
└── utils/
    └── Constants.java        # Constantes de la aplicación
```

## Compilación y Ejecución

```bash
# Compilar
javac -d bin -sourcepath src src/main/Shop.java

# Ejecutar
java -cp bin main.Shop
```

### Selección de persistencia (DAO)

Por defecto, la aplicación usa Hibernate. Puedes cambiarlo con una propiedad del sistema:

- Hibernate: `-Dshop.dao=hibernate`
- JDBC: `-Dshop.dao=jdbc`
- Ficheros: `-Dshop.dao=file`

## Métodos Principales Refactorizados

| Método Anterior | Método Nuevo | Descripción |
|-----------------|--------------|-------------|
| `loadInventory()` | `initializeInventory()` | Inicializa inventario desde BD |
| `readInventory()` | `fetchInventoryFromDatabase()` | Lee productos de tabla SQL |
| `writeInventory()` | `exportInventoryToDatabase()` | Exporta a tabla histórica |
| `initSession()` | `authenticateEmployee()` | Autentica empleado |
| `inventoryDao` | `productRepository` | Repositorio de productos |
| `numberProducts` | `productCount` | Contador de productos |

## Arquitectura

- **Patrón DAO**: Abstrae el acceso a datos con interfaz `Dao` y múltiples implementaciones
- **Arquitectura MVC**: Separación clara entre modelo, vista y controlador
- **JDBC**: Conexión directa a MySQL sin frameworks ORM
