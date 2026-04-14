<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Detalle producto | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
</head>
<body class="app-body">
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="productos" />
    <h1 class="page-title">Detalle del producto</h1>
    <p class="page-subtitle">Ficha completa del producto seleccionado.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <p class="secondary-link">
        <a class="brand-link" href="<c:url value='/productos' />">Volver al listado</a>
    </p>

    <div class="detail-grid">
        <div class="detail-card">
            <span class="detail-label">Nombre</span>
            <span class="detail-value">${producto.nombre}</span>
        </div>
        <div class="detail-card">
            <span class="detail-label">Categoria</span>
            <span class="detail-value">${producto.categoria.nombre}</span>
        </div>
        <div class="detail-card">
            <span class="detail-label">Precio</span>
            <span class="detail-value">${producto.precio}</span>
        </div>
        <div class="detail-card">
            <span class="detail-label">Stock</span>
            <span class="detail-value">${producto.stock}</span>
        </div>
    </div>

    <div class="section-card">
        <h2 class="section-title">Descripcion</h2>
        <p>
            <c:choose>
                <c:when test="${not empty producto.descripcion}">
                    ${producto.descripcion}
                </c:when>
                <c:otherwise>
                    No hay descripcion registrada para este producto.
                </c:otherwise>
            </c:choose>
        </p>
    </div>

    <div class="section-card">
        <h2 class="section-title">Auditoria</h2>
        <div class="detail-grid">
            <div class="detail-card">
                <span class="detail-label">Creado por</span>
                <span class="detail-value">${producto.creadoPor}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Fecha de creacion</span>
                <span class="detail-value">${producto.fechaCreacionTexto}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Ultima modificacion por</span>
                <span class="detail-value">${producto.actualizadoPor}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Fecha ultima modificacion</span>
                <span class="detail-value">${producto.fechaActualizacionTexto}</span>
            </div>
        </div>
    </div>
</body>
</html>
