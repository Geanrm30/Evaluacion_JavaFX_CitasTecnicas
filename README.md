# Evaluacion_JavaFX_CitasTecnicas

Sistema de gestión de citas de atención técnica para **TecnoAsistencia Nicaragua S.A.**
Aplicación de escritorio en JavaFX conectada a PostgreSQL mediante JDBC (sin ORM), con CRUD de clientes y citas técnicas, búsquedas, filtros y validación de datos.

Asignatura: Programación de Aplicaciones de Escritorio — Unidad II. Acceso a bases de datos
Universidad Americana (UAM) · Ingeniería de Sistemas · Docente: José A. Durán García · Octubre 2026

---

## Integrantes y distribución de tareas

| Integrante   | Usuario GitHub                           | Responsabilidades |
|--------------|------------------------------------------| --- |
| Geanfranco Alexander Rodriguez Mendieta | [Geanrm30](https://github.com/Geanrm30)  | Repositorio y estructura Maven, `.gitignore` y `db.properties.example`, script `gestion_citas.sql`, clase `ConexionDB` (prueba con `SELECT 1`), menú principal y unificación del modelo `Cliente`. |
| Anthony Sying Gonzalez Chow | [AnthonySGC](https://github.com/AnthonySGC) | Validaciones (`ClienteValidator`, `CitaValidator`), alertas (`AlertUtil`), traducción de errores SQL (`SqlErrorMessages`) e integración en el CRUD de clientes. |
| Jeremy Josue Gradiz Noguera | [JJGradiz](https://github.com/JJGradiz)  | Módulo de clientes: `ClienteDAO`, `ClientesController`, vista y búsqueda; rechazo de correo duplicado, bloqueo de eliminación de clientes con citas y confirmación antes de eliminar. |
| Claudio Antonio De La Rocha Acevedo | [Def-Claudingo](https://github.com/Def-Claudingo) | Módulo de citas: `CitaTecnicaDao`, `CitaController`, vista de citas, filtros por estado y fecha, integración de ramas y refactor del módulo (fechas, `INNER JOIN`). |

---

## Requisitos

- JDK 21
- IntelliJ IDEA (el proyecto usa Maven; el wrapper `mvnw` está incluido)
- PostgreSQL 14 o superior
- Dependencias (las descarga Maven): JavaFX 21 (controls, fxml), driver PostgreSQL 42.7.x, Lombok

## Instalación y ejecución

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Geanrm30/Evaluacion_JavaFX_CitasTecnicas.git
   ```
2. Crear la base de datos en PostgreSQL (el script crea las tablas, no la base):
   ```sql
   CREATE DATABASE gestion_citas;
   ```
3. Ejecutar el script dentro de esa base:
   ```bash
   psql -U postgres -d gestion_citas -f sql/gestion_citas.sql
   ```
   También puede abrirse `sql/gestion_citas.sql` en pgAdmin y ejecutarlo sobre `gestion_citas`.
   El script borra y recrea las tablas, e inserta dos clientes y dos citas de prueba.
4. Configurar las credenciales locales (ver la sección siguiente).
5. Abrir el proyecto en IntelliJ IDEA y ejecutar `MenuPrincipalApplication`, o desde la terminal:
   ```bash
   ./mvnw clean javafx:run
   ```

## Configuración local de PostgreSQL

Las credenciales **no** se guardan en el repositorio. La aplicación lee un archivo `db.properties` en la raíz del proyecto, que está en `.gitignore`.

1. Copiar la plantilla:
   ```bash
   cp db.properties.example db.properties
   ```
2. Editar `db.properties` con los datos propios:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/gestion_citas
   db.user=postgres
   db.password=TU_CONTRASEÑA
   ```

Al iniciar, la aplicación comprueba la conexión con `SELECT 1`. Si falla (servicio apagado, contraseña incorrecta o base inexistente), muestra un mensaje y se cierra.

---

## Modelo de datos

Base de datos `gestion_citas`, relación `cliente (1) — (N) cita_tecnica`.

| Tabla | Campos y reglas |
| --- | --- |
| `cliente` | `id SERIAL PK`, `nombres VARCHAR(80) NOT NULL`, `apellidos VARCHAR(80) NOT NULL`, `telefono VARCHAR(20) NOT NULL`, `correo VARCHAR(120) NOT NULL UNIQUE`, `activo BOOLEAN NOT NULL` |
| `cita_tecnica` | `id SERIAL PK`, `cliente_id INT NOT NULL` (FK a `cliente.id`, `ON DELETE RESTRICT`), `fecha DATE NOT NULL`, `hora TIME NOT NULL`, `tipo_servicio VARCHAR(80) NOT NULL`, `descripcion VARCHAR(255) NOT NULL`, `estado VARCHAR(20) NOT NULL` con `CHECK` (Pendiente, En proceso, Finalizada, Cancelada) |

La eliminación es física. Un cliente con citas asociadas no puede eliminarse: la base lo impide con la clave foránea y la aplicación explica el motivo.

## Estructura del proyecto

```
src/main/java/uam/edu/ni/evaluacion_javafx_citastecnicas/
├── MenuPrincipalApplication.java   Punto de entrada; prueba la conexión al iniciar
├── controller/                     Controladores FXML (menú, clientes, citas)
├── dao/                            Acceso a datos con JDBC (ClienteDAO, CitaTecnicaDao)
├── Interfaces/CRUD.java            Contrato común de los DAO
├── model/                          Cliente, CitaTecnica
├── util/                           ConexionDB, AlertUtil, SqlErrorMessages
└── validation/                     ClienteValidator, CitaValidator
src/main/resources/.../             Vistas FXML
sql/gestion_citas.sql               Script de creación de tablas y datos de prueba
```

## Uso de JDBC

- **Conexión centralizada:** `util/ConexionDB` lee `db.properties` y entrega `Connection` vía `DriverManager`.
- **Statement:** solo en `ConexionDB.probarConexion()`, con la sentencia fija `SELECT 1` (sin datos del usuario).
- **PreparedStatement con parámetros:** todas las inserciones, actualizaciones, eliminaciones, búsquedas y filtros. No se concatena texto del usuario en el SQL; en las búsquedas también se escapan `%` y `_`.
- **ResultSet:** lectura de listados, búsquedas, filtros y del `id` generado (`RETURNING id`).
- **try-with-resources** en todas las conexiones, sentencias y resultados.
- **SQLException** traducida a mensajes comprensibles por `SqlErrorMessages` (correo duplicado, restricción de clave foránea, campos obligatorios, credenciales, base inexistente).

## Requerimientos funcionales

| Código | Implementación |
| --- | --- |
| RF01 | `ConexionDB` centralizada; error de conexión con alerta al iniciar. |
| RF02 | Formulario y TableView de clientes: crear, listar, seleccionar (clic en la fila), editar y eliminar. |
| RF03 | Formulario y TableView de citas; cliente elegido en ComboBox. Para editar o eliminar, doble clic en la fila o clic derecho > "Cargar en formulario". |
| RF04 | Tabla de citas con ID, nombre completo del cliente, fecha, hora, servicio y estado. |
| RF05 | Búsqueda parcial por nombres, apellidos o correo (`ILIKE`, sin distinguir mayúsculas). |
| RF06 | Filtros por estado y por fecha, combinables; "Mostrar todas" los limpia. |
| RF07 | Campos obligatorios, formato de correo, correo duplicado, teléfono, longitudes máximas, fecha y hora requeridas (hora en formato HH:mm), fecha no anterior a hoy, cliente existente y estado permitido. |
| RF08 | Confirmación antes de eliminar; alertas de éxito, errores SQL y eliminación restringida. |
| RF09 | Las tablas se recargan después de cada inserción, modificación o eliminación. |
| RF10 | Todos los datos se guardan en PostgreSQL y persisten al cerrar y reabrir. |

---

## Capturas de funcionamiento

### Clientes

| | |
| --- | --- |
| Formulario vacío rechazado ![](docs/capturas/01-cliente-vacio.png) | Correo con formato inválido ![](docs/capturas/02-correo-invalido.png) |
| Teléfono con letras ![](docs/capturas/03-telefono-invalido.png) | Cliente guardado ![](docs/capturas/04-cliente-guardado.png) |
| Cliente visible en la tabla ![](docs/capturas/05-cliente-en-tabla.png) | Correo duplicado rechazado ![](docs/capturas/06-correo-duplicado.png) |
| Teléfono modificado ![](docs/capturas/07-cliente-actualizado.png) | Búsqueda parcial por apellido ![](docs/capturas/08-busqueda-apellido.png) |
| Cliente con citas: eliminación rechazada ![](docs/capturas/09-eliminar-cliente-con-citas.png) | Cliente sin citas eliminado ![](docs/capturas/21-cliente-eliminado.png) |

### Citas técnicas

| | |
| --- | --- |
| Formulario vacío rechazado ![](docs/capturas/10-cita-vacia.png) | Hora inválida ![](docs/capturas/11-hora-invalida.png) |
| Fecha anterior a hoy ![](docs/capturas/12-fecha-pasada.png) | Citas con nombre completo del cliente ![](docs/capturas/13-citas-registradas.png) |
| Filtro por estado ![](docs/capturas/14-filtro-estado.png) | Filtro por fecha ![](docs/capturas/15-filtro-fecha.png) |
| Filtros limpiados ![](docs/capturas/16-limpiar-filtros.png) | Estado cambiado a Finalizada ![](docs/capturas/17-cita-finalizada.png) |
| Tabla actualizada ![](docs/capturas/18-tabla-finalizada.png) | Confirmación de eliminación ![](docs/capturas/19-confirmar-eliminar-cita.png) |
| Cita eliminada ![](docs/capturas/20-cita-eliminada.png) | |

### Conexión

![Error de conexión](docs/capturas/22-error-conexion.png)

---

## Pruebas de aceptación

| Prueba | Resultado |
| --- | --- |
| Registrar un cliente y verlo en la TableView | Correcto (capturas 04, 05) |
| Registrar un correo repetido | Rechazado con mensaje (06) |
| Modificar el teléfono y comprobar que persiste | Correcto (07) |
| Registrar una cita y ver el nombre del cliente en la tabla | Correcto (13) |
| Buscar por una parte del apellido | Correcto (08) |
| Filtrar por estado y por fecha, y limpiar los filtros | Correcto (14, 15, 16) |
| Cambiar el estado de una cita a Finalizada | Correcto (17, 18) |
| Eliminar un cliente con citas | Rechazado con el motivo (09) |
| Eliminar la cita y luego el cliente, confirmando cada operación | Correcto (19, 20, 21) |
| Cerrar y reabrir la aplicación | Los registros persisten en PostgreSQL |
