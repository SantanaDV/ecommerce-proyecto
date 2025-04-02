# Proyecto Ecommerce Spring Boot
## Descripción
Este proyecto implementa una aplicación web de ecommerce usando Spring Boot, con
autenticación JWT, gestión de usuarios, pedidos y productos.
## Tecnologías utilizadas
## 🛠️ Tecnologías utilizadas
## 🛠️ Tecnologías utilizadas

![Java](https://img.shields.io/badge/Java_17-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Lombok](https://img.shields.io/badge/Lombok-E74430?style=for-the-badge&logo=lombok&logoColor=white)

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
- **SantanaDV** - [GitHub](https://github.com/SantanaDV)
## Licencia
Este proyecto está licenciado bajo la Licencia MIT.
