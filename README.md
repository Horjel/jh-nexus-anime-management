# JH Nexus Anime

Aplicacion web de gestion interna para una tienda de productos de anime y manga. El proyecto esta desarrollado con Java 8, Spring Boot, JSP, JPA/Hibernate y MySQL, con una arquitectura MVC clasica orientada a entorno academico y portfolio.

![Dashboard principal](docs/img/03-dashboard.png)

## Que resuelve

La aplicacion permite gestionar la operativa basica de una tienda anime:

- categorias de catalogo
- productos y stock
- clientes
- pedidos con varias lineas
- panel de usuarios internos
- auditoria simple y bitacora administrativa

No es una API REST. Es una aplicacion **Spring MVC + JSP** con renderizado server-side.

## Stack tecnico

- Java 8
- Spring Boot 2.7.18
- JSP + JSTL
- JPA / Hibernate
- MySQL 8
- Maven
- Spring Security

## Funcionalidades principales

- CRUD de categorias, productos, clientes y pedidos
- control de stock con validacion de disponibilidad
- pedidos con multiples lineas
- busqueda, ordenacion y paginacion
- exportacion CSV
- dashboard con metricas y resumen comercial
- autenticacion y autorizacion por roles `ADMIN`, `DELEGADO` y `USER`
- gestion de usuarios internos
- auditoria simple y bitacora administrativa

## Arquitectura

```text
src/main/java/com/otakucenter
|- config
|- controller
|- dao
|  \- impl
|- exception
|- model
|- service
|  \- impl
\- util

src/main/webapp/WEB-INF/views
|- categorias
|- clientes
|- common
|- cuenta
|- error
|- pedidos
|- productos
\- usuarios
```

## Ejecucion local

### Requisitos

- Java 8
- Maven
- MySQL 8

### Pasos

1. Arrancar MySQL.
2. Situarse en la carpeta del proyecto.
3. Ejecutar:

```powershell
mvn spring-boot:run
```

4. Abrir:

- `http://localhost:8080/login`

## Configuracion

El proyecto usa perfiles:

- `application.properties`: configuracion comun
- `application-dev.properties`: entorno local de desarrollo
- `application-prod.properties`: configuracion orientada a despliegue

Base de datos por defecto en local:

- esquema: `otaku_center`

## Usuarios demo

El fichero [USUARIOS_PROYECTO.txt](USUARIOS_PROYECTO.txt) contiene cuentas **solo para desarrollo y demostracion local**. No representan usuarios reales.

Estado actual del ejemplo:

- `admin` / `*****` / `ADMIN` / activo
- `Sergio Delegado` / `Alpargata96` / `DELEGADO` / bloqueado
- `Joyux` / `Joyux2026A` / `USER` / activo

## Documentacion del proyecto

- [MEMORIA_PROYECTO.md](docs/MEMORIA_PROYECTO.md)
- [DOCUMENTACION.md](docs/DOCUMENTACION.md)
- [GUIA_RAPIDA.txt](GUIA_RAPIDA.txt)
- [GUIA_PRESENTACION.txt](GUIA_PRESENTACION.txt)
- [RESUMEN_VERSIONES.txt](RESUMEN_VERSIONES.txt)
- [EVO.txt](EVO.txt)

## Capturas

- Login: ![Login](docs/img/01-login.png)
- Panel de usuarios: ![Usuarios](docs/img/13-usuarios-panel.png)
- Pedidos: ![Pedidos](docs/img/11-pedidos-lista.png)

## Evolucion funcional

- `v1.0`: base MVC, CRUDs y persistencia
- `v1.1`: dashboard, filtros, ordenacion, paginacion y exportacion
- `v2.0`: seguridad, roles, gestion de usuarios, auditoria y bitacora

## Estado actual

El proyecto esta en un punto funcional y presentable de portfolio. Mantiene algunas deudas tecnicas deliberadas para no sobredimensionar el alcance:

- compatibilidad legacy en parte del modelo de pedidos
- ausencia de migraciones con Flyway/Liquibase
- cobertura de tests todavia inicial
