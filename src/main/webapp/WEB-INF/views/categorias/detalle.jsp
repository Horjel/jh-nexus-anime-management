<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Detalle categoria | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ccc; padding: 10px; text-align: left; }
        th { background: #f3f3f3; }
    </style>
</head>
<body class="app-body">
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="categorias" />
    <h1 class="page-title">Detalle de la categoria</h1>
    <p class="page-subtitle">Ficha de la categoria y productos asociados.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <p class="secondary-link">
        <a class="brand-link" href="<c:url value='/categorias' />">Volver al listado</a>
    </p>

    <div class="detail-grid">
        <div class="detail-card">
            <span class="detail-label">Nombre</span>
            <span class="detail-value">${categoria.nombre}</span>
        </div>
        <div class="detail-card">
            <span class="detail-label">Productos asociados</span>
            <span class="detail-value">${totalProductosCategoria}</span>
        </div>
    </div>

    <div class="section-card">
        <h2 class="section-title">Auditoria</h2>
        <div class="detail-grid">
            <div class="detail-card">
                <span class="detail-label">Creado por</span>
                <span class="detail-value">${categoria.creadoPor}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Fecha de creacion</span>
                <span class="detail-value">${categoria.fechaCreacionTexto}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Ultima modificacion por</span>
                <span class="detail-value">${categoria.actualizadoPor}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Fecha ultima modificacion</span>
                <span class="detail-value">${categoria.fechaActualizacionTexto}</span>
            </div>
        </div>
    </div>

    <div class="section-card">
        <h2 class="section-title">Productos de la categoria</h2>
        <table>
            <thead>
            <tr>
                <th>Producto</th>
                <th>Precio</th>
                <th>Stock</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty productosCategoria}">
                    <tr>
                        <td colspan="3">No hay productos asociados a esta categoria.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="producto" items="${productosCategoria}">
                        <tr>
                            <td><a class="brand-link" href="<c:url value='/productos/ver/${producto.id}' />">${producto.nombre}</a></td>
                            <td>${producto.precio}</td>
                            <td>${producto.stock}</td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</body>
</html>
