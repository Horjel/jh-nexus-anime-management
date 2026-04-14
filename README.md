# JH Nexus Anime

Aplicacion web de gestion para una tienda de productos de anime y manga, desarrollada con un enfoque clasico Java empresarial.

## Stack

- Java 8
- Spring Boot 2.7.18
- JSP + JSTL
- JPA / Hibernate
- MySQL 8
- Maven

## Objetivo

El proyecto parte del documento `EVO.txt` y cubre la base de una aplicacion MVC por capas:

- gestion de categorias
- gestion de productos
- gestion de clientes
- gestion de pedidos
- control de stock
- persistencia relacional

Sobre esa base se han anadido mejoras de evolucion:

- branding `JH Nexus Anime`
- dashboard de inicio
- filtros, ordenacion y paginacion
- fichas de detalle
- exportacion CSV
- login y roles
- panel de usuarios
- auditoria simple y bitacora administrativa

## Estructura principal

```text
src/main/java/com/otakucenter
|- config
|- controller
|- dao
|  \- impl
|- model
|- service
|  \- impl

src/main/webapp/WEB-INF/views
|- categorias
|- clientes
|- common
|- cuenta
|- pedidos
|- productos
\- usuarios
```

## Funcionalidades principales

- CRUD de categorias, productos, clientes y pedidos
- control de stock
- pedidos con varias lineas
- busqueda, ordenacion y paginacion
- exportacion CSV
- dashboard con resumen de actividad
- autenticacion con roles `ADMIN`, `DELEGADO` y `USER`
- gestion de usuarios
- auditoria simple y bitacora administrativa

## Como ejecutar

1. Abre una terminal en la carpeta del proyecto.
2. Ejecuta:

```powershell
mvn spring-boot:run
```

3. Abre en el navegador:

- `http://localhost:8080/login`

## Configuracion basica

Archivo principal:

- `src/main/resources/application.properties`

Parametros importantes:

- base de datos MySQL `otaku_center`
- vistas JSP en `src/main/webapp/WEB-INF/views`
- usuarios del proyecto en `USUARIOS_PROYECTO.txt`

## Usuarios base de la demo

El proyecto arranca leyendo el archivo `USUARIOS_PROYECTO.txt`.

Estado base recomendado:

- `admin` / `*****` / `ADMIN` / activo
- `Sergio Delegado` / `Sergio2026A` / `DELEGADO` / bloqueado
- `Joyux` / `Joyux2026A` / `USER` / activo

## Documentacion util

- `EVO.txt`
- `GUIA_RAPIDA.txt`
- `RESUMEN_VERSIONES.txt`
- `GUIA_PRESENTACION.txt`

## Versionado funcional

- `v1.0`: base MVC y CRUDs
- `v1.1`: mejoras de interfaz y explotacion
- `v2.0`: seguridad, usuarios y auditoria
