# MP0486 RA1 - Refactor Login usando ObjectDB

Este README se ha rehcho desde cero con capturas nuevas y comprobacion funcional real de frontend y backend, incluyendo lo que el sistema puede y no puede hacer.

## Documento funcional

### 1. Refactor login usando ObjectDB

Las credenciales se validan en ObjectDB en `objects/users.odb` (tabla `users`) mediante `DaoImplObjectDB`.

Archivos implicados:

- `src/dao/DaoImplObjectDB.java`
- `src/utils/ObjectDbSupport.java`
- `src/model/Employee.java`
- `src/dao/DaoFactory.java`

### Comprobacion funcional de la app (capturas nuevas)

#### Login

- Login incorrecto con datos reales (`666` / `nose`):
  ![Login incorrecto datos](evidence/screenshots/01-login-incorrecto-datos.png)
- Mensaje de error en login incorrecto:
  ![Login incorrecto error](evidence/screenshots/02-login-incorrecto-error.png)
- Login correcto con datos reales (`123` / `test`):
  ![Login correcto datos](evidence/screenshots/03-login-correcto-datos.png)
- Acceso al menu principal:
  ![Menu principal](evidence/screenshots/04-menu-principal.png)

#### Mantenimiento (que puede hacer)

- Exportar inventario:
  ![Exportar inventario OK](evidence/screenshots/05-exportar-inventario-ok.png)
- Anadir producto con stock visible (`7`):
  ![Anadir producto datos stock](evidence/screenshots/06-anadir-producto-datos-stock7.png)
- Confirmacion producto anadido:
  ![Anadir producto OK](evidence/screenshots/07-anadir-producto-ok.png)
- Anadir stock con cantidad visible (`5`):
  ![Anadir stock datos cantidad](evidence/screenshots/09-anadir-stock-datos-cantidad5.png)
- Confirmacion stock actualizado:
  ![Anadir stock OK](evidence/screenshots/10-anadir-stock-ok.png)
- Eliminar producto:
  ![Eliminar producto datos](evidence/screenshots/12-eliminar-producto-datos.png)
- Confirmacion producto eliminado:
  ![Eliminar producto OK](evidence/screenshots/13-eliminar-producto-ok.png)

#### Mantenimiento (que NO puede hacer)

- No permite anadir producto duplicado:
  ![Anadir duplicado error](evidence/screenshots/08-anadir-producto-duplicado-error.png)
- No permite anadir stock a un producto inexistente:
  ![Anadir stock inexistente error](evidence/screenshots/11-anadir-stock-inexistente-error.png)
- No permite eliminar un producto inexistente:
  ![Eliminar inexistente error](evidence/screenshots/14-eliminar-producto-inexistente-error.png)

### Comprobacion backend real (can / cannot)

Se ejecuta una verificacion real contra ObjectDB y MongoDB con resultado consolidado:

- `LOGIN_OK_CAN=true`
- `LOGIN_BAD_CANNOT=true`
- `ADD_PRODUCT_CAN=true`
- `ADD_STOCK_CAN=true`
- `DELETE_MISSING_CANNOT=true`
- `DELETE_PRODUCT_CAN=true`

Archivo de salida real:

- `evidence/dumps/backend_verification.txt`

Captura de backend:

![Backend verificacion can-cannot](evidence/screenshots/backend-check.png)

## Evidencias de base de datos

### ObjectDB

- Dump real de usuarios en ObjectDB: `evidence/dumps/users_objectdb.json`
- Captura del contenido de usuarios ObjectDB:
  ![ObjectDB users content](evidence/screenshots/objectdb-users.png)

### MongoDB

- Dump real de usuarios en MongoDB: `evidence/dumps/users.json`
- Captura del contenido de usuarios MongoDB:
  ![MongoDB users content](evidence/screenshots/mongodb-users.png)
- Dump real de inventario tras comprobaciones backend: `evidence/dumps/inventory_backend.json`
- Captura del inventario backend:
  ![MongoDB inventory backend](evidence/screenshots/mongodb-inventory.png)

## Test unitario (Regresion)

Se mantiene la clase de regresion pedida para login:

- `src/test/java/view/LoginObjectDbRegressionTest.java`

Casos:

1. Verificar login correcto accede al menu principal
2. Verificar login incorrecto muestra mensaje de error

Comando:

```powershell
.\mvnw.cmd -Dtest=view.LoginObjectDbRegressionTest test
```

## Documento tecnico

### 1. Instalar dependencias ObjectDB

En `pom.xml`:

```xml
<repositories>
  <repository>
    <id>objectdb</id>
    <name>ObjectDB Repository</name>
    <url>https://m2.objectdb.com</url>
  </repository>
</repositories>
```

```xml
<dependency>
  <groupId>com.objectdb</groupId>
  <artifactId>objectdb</artifactId>
  <version>2.9.2</version>
</dependency>
```

### 2. Crear carpeta `objects` a nivel de proyecto

Carpeta creada en raiz con `objects/users.odb`.

### 3. Nueva clase `DaoImplObjectDB`

Cumplido en `src/dao/DaoImplObjectDB.java`:

- creada en package `dao`
- implementa interfaz `Dao`
- implementa logica de `getEmployee`

### 4. Modificar clase `Employee`

Cumplido en `src/model/Employee.java`:

- `@Entity`
- `@Transient` en `dao`
- tipo de `employee.dao` -> `DaoImplObjectDB`
- tabla `users` con `@Table(name = "users")`
