<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Detalle cliente | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
</head>
<body class="app-body">
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="clientes" />
    <h1 class="page-title">Detalle del cliente</h1>
    <p class="page-subtitle">Ficha completa del cliente seleccionado.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <p class="secondary-link">
        <a class="brand-link" href="<c:url value='/clientes' />">Volver al listado</a>
    </p>

    <div class="detail-grid">
        <div class="detail-card">
            <span class="detail-label">Nombre</span>
            <span class="detail-value">${cliente.nombre} ${cliente.apellidos}</span>
        </div>
        <div class="detail-card">
            <span class="detail-label">Email</span>
            <span class="detail-value">${cliente.email}</span>
        </div>
        <div class="detail-card">
            <span class="detail-label">Telefono</span>
            <span class="detail-value">${cliente.telefono}</span>
        </div>
        <div class="detail-card">
            <span class="detail-label">Direccion</span>
            <span class="detail-value">${cliente.direccion}</span>
        </div>
    </div>

    <div class="section-card">
        <h2 class="section-title">Auditoria</h2>
        <div class="detail-grid">
            <div class="detail-card">
                <span class="detail-label">Creado por</span>
                <span class="detail-value">${cliente.creadoPor}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Fecha de creacion</span>
                <span class="detail-value">${cliente.fechaCreacionTexto}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Ultima modificacion por</span>
                <span class="detail-value">${cliente.actualizadoPor}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Fecha ultima modificacion</span>
                <span class="detail-value">${cliente.fechaActualizacionTexto}</span>
            </div>
        </div>
    </div>
</body>
</html>
