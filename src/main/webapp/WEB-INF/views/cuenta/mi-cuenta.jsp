<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mi cuenta | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
</head>
<body class="app-body">
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="mi-cuenta" />
    <h1 class="page-title">Mi cuenta</h1>
    <p class="page-subtitle">Resumen de tu acceso actual en JH Nexus Anime.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <div class="section-card">
        <h2 class="section-title">Datos de acceso</h2>
        <div class="detail-grid">
            <div class="detail-card">
                <span class="detail-label">Usuario</span>
                <div class="detail-value">${usuario.username}</div>
            </div>
            <div class="detail-card">
                <span class="detail-label">Rol</span>
                <div class="detail-value">
                    <span class="role-chip role-${usuario.rol.toLowerCase()}">${usuario.rol}</span>
                </div>
            </div>
            <div class="detail-card">
                <span class="detail-label">Estado</span>
                <div class="detail-value">
                    <c:choose>
                        <c:when test="${usuario.activo}">
                            <span class="status-chip status-activo">Activo</span>
                        </c:when>
                        <c:otherwise>
                            <span class="status-chip status-bloqueado">Bloqueado</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="detail-card">
                <span class="detail-label">Creado por</span>
                <div class="detail-value">${usuario.creadoPor}</div>
            </div>
            <div class="detail-card">
                <span class="detail-label">Fecha de alta</span>
                <div class="detail-value">${usuario.fechaCreacionTexto}</div>
            </div>
            <div class="detail-card">
                <span class="detail-label">Ultimo acceso</span>
                <div class="detail-value">
                    <c:choose>
                        <c:when test="${not empty usuario.fechaUltimoAccesoTexto}">
                            ${usuario.fechaUltimoAccesoTexto}
                        </c:when>
                        <c:otherwise>
                            Sin accesos registrados
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="detail-card">
                <span class="detail-label">Ultimo cambio por</span>
                <div class="detail-value">${usuario.actualizadoPor}</div>
            </div>
            <div class="detail-card">
                <span class="detail-label">Ultima actualizacion</span>
                <div class="detail-value">${usuario.fechaActualizacionTexto}</div>
            </div>
        </div>

        <p class="results-meta">
            Los cambios de rol, bloqueo o contrasena los gestiona actualmente el administrador desde el panel de usuarios.
        </p>
    </div>
</body>
</html>
