<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Detalle pedido | JH Nexus Anime</title>
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
    <c:set var="navSection" value="pedidos" />
    <h1 class="page-title">Detalle del pedido #${pedido.id}</h1>
    <p class="page-subtitle">Vista completa del pedido y sus lineas.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <p class="secondary-link">
        <a class="brand-link" href="<c:url value='/pedidos' />">Volver al listado</a>
    </p>

    <div class="detail-grid">
        <div class="detail-card">
            <span class="detail-label">Fecha</span>
            <span class="detail-value">${pedido.fechaPedidoTexto}</span>
        </div>
        <div class="detail-card">
            <span class="detail-label">Estado</span>
            <span class="detail-value">
                <span class="status-chip status-${pedido.estado.toLowerCase()}">${pedido.estado}</span>
            </span>
        </div>
        <div class="detail-card">
            <span class="detail-label">Unidades</span>
            <span class="detail-value">${pedido.cantidadTotal}</span>
        </div>
        <div class="detail-card">
            <span class="detail-label">Total</span>
            <span class="detail-value">${pedido.total}</span>
        </div>
    </div>

    <div class="section-card">
        <h2 class="section-title">Auditoria</h2>
        <div class="detail-grid">
            <div class="detail-card">
                <span class="detail-label">Creado por</span>
                <span class="detail-value">${pedido.creadoPor}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Fecha de creacion</span>
                <span class="detail-value">${pedido.fechaCreacionTexto}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Ultima modificacion por</span>
                <span class="detail-value">${pedido.actualizadoPor}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Fecha ultima modificacion</span>
                <span class="detail-value">${pedido.fechaActualizacionTexto}</span>
            </div>
        </div>
    </div>

    <div class="section-card">
        <h2 class="section-title">Cliente</h2>
        <div class="detail-grid">
            <div class="detail-card">
                <span class="detail-label">Nombre</span>
                <span class="detail-value">
                    <a class="brand-link" href="<c:url value='/clientes/ver/${pedido.cliente.id}' />">${pedido.cliente.nombre} ${pedido.cliente.apellidos}</a>
                </span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Email</span>
                <span class="detail-value">${pedido.cliente.email}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Telefono</span>
                <span class="detail-value">${pedido.cliente.telefono}</span>
            </div>
            <div class="detail-card">
                <span class="detail-label">Direccion</span>
                <span class="detail-value">${pedido.cliente.direccion}</span>
            </div>
        </div>
    </div>

    <div class="section-card">
        <h2 class="section-title">Lineas del pedido</h2>
        <div class="table-panel">
        <table>
            <thead>
            <tr>
                <th>Producto</th>
                <th>Cantidad</th>
                <th>Precio unitario</th>
                <th>Subtotal</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${not empty pedido.detalles}">
                    <c:forEach var="detalle" items="${pedido.detalles}">
                        <tr>
                            <td>
                                <a class="brand-link" href="<c:url value='/productos/ver/${detalle.producto.id}' />">${detalle.producto.nombre}</a>
                            </td>
                            <td>${detalle.cantidad}</td>
                            <td>${detalle.precioUnitario}</td>
                            <td>${detalle.subtotal}</td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td>
                            <a class="brand-link" href="<c:url value='/productos/ver/${pedido.productoLegacy.id}' />">${pedido.productoLegacy.nombre}</a>
                        </td>
                        <td>${pedido.cantidadLegacy}</td>
                        <td>-</td>
                        <td>${pedido.total}</td>
                    </tr>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
        </div>
    </div>
</body>
</html>
