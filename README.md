# Proyecto Ecommerce Spring Boot
## Descripción
Este proyecto implementa una aplicación web de comercio electrónico usando Spring Boot, con
autenticación JWT, gestión de usuarios, pedidos y productos.
## Tecnologías utilizadas
- Java 17
- Spring Boot 3.x
- Spring Security (JWT)
- Thymeleaf (para vistas HTML)
- MySQL
- Swagger (para documentación API)
- Lombok
## Características principales
- Autenticación y autorización con JWT.
- Gestión de usuarios con roles ADMIN y USER.
- CRUD completo para productos y pedidos.
- Gestión avanzada de pedidos, incluyendo asociaciones con productos y usuarios.
- Endpoints protegidos con diferentes niveles de acceso según roles.
- Integración de vistas HTML con Thymeleaf.
## Estructura del proyecto
- `controller`: Controladores REST y MVC.
- `entity`: Entidades JPA (Usuario, Producto, Pedido, etc.).
- `repository`: Repositorios Spring Data JPA.
- `service`: Servicios que implementan la lógica de negocio.
- `security`: Configuración y filtros de seguridad.
## Cómo ejecutar el proyecto
1. Clona el repositorio:
 ```bash
 git clone https://github.com/tu-usuario/ecommerce.git
 ```
2. Configura la base de datos MySQL:
 ```sql
 CREATE DATABASE ecommerce;
 ```
3. Modifica el archivo `application.properties` con tus credenciales MySQL.
4. Ejecuta el proyecto desde tu IDE o terminal:
 ```bash
 ./mvnw spring-boot:run
 ```
5. Accede a la aplicación en:
 - Frontend: `http://localhost:8080`
 - Swagger UI: `http://localhost:8080/swagger-ui/index.html`
## Endpoints API principales
- Autenticación:
 - `POST /login`
- Usuarios:
 - `POST /api/usuarios/register`
 - `GET /api/usuarios/{idUsuario}`
- Productos:
 - `GET /api/productos`
 - `POST /api/productos`
- Pedidos:
 - `POST /api/pedidos`
 - `GET /api/pedidos/{idPedido}`
 - `GET /api/pedidos/usuario/{username}`
## Seguridad
Los endpoints protegidos requieren enviar un token JWT en el header `Authorization`:
```
Authorization: Bearer <tu_token_jwt>
```
## Autor
- **Tu nombre** - [GitHub](https://github.com/tu-usuario)
## Licencia
Este proyecto está licenciado bajo la Licencia MIT.
