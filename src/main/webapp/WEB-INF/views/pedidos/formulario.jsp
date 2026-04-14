<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Pedido | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
    <style>
        .form-card { max-width: 920px; }
        table { border-collapse: collapse; width: 760px; margin-bottom: 16px; }
        th, td { border: 1px solid #ccc; padding: 10px; text-align: left; }
        th { background: #f3f3f3; }
    </style>
</head>
<body class="app-body">
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="pedidos" />
    <h1 class="page-title">
        <c:choose>
            <c:when test="${modoEdicion}">Editar pedido</c:when>
            <c:otherwise>Nuevo pedido</c:otherwise>
        </c:choose>
    </h1>
    <p class="page-subtitle">Creacion y edicion de pedidos en JH Nexus Anime.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <p class="secondary-link">
        <a class="brand-link" href="<c:url value='/pedidos' />">Volver al listado</a>
    </p>

    <c:if test="${not empty errorGeneral}">
        <p class="message-error">${errorGeneral}</p>
    </c:if>

    <form:form cssClass="form-card form-grid" method="post" action="${pageContext.request.contextPath}/pedidos/guardar" modelAttribute="pedidoForm">
        <form:hidden path="id" />
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

        <div class="field-group">
            <label for="cliente">Cliente <span class="required-mark">*</span></label>
            <span class="field-help">Selecciona el cliente asociado al pedido.</span>
            <form:select cssClass="field-input" path="clienteId" id="cliente">
                <form:option value="" label="Seleccione un cliente" />
                <form:options items="${clientes}" itemValue="id" itemLabel="email" />
            </form:select>
            <div class="field-error"><form:errors path="clienteId" /></div>
        </div>

        <div class="field-group">
            <label>Lineas del pedido <span class="required-mark">*</span></label>
            <span class="field-help">Rellena al menos una linea con producto y cantidad.</span>
            <div class="table-panel">
            <table>
                <thead>
                <tr>
                    <th>Linea</th>
                    <th>Producto</th>
                    <th>Cantidad</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="linea" items="${pedidoForm.lineas}" varStatus="estadoLinea">
                    <tr>
                        <td>${estadoLinea.index + 1}</td>
                        <td>
                            <form:select cssClass="field-input" path="lineas[${estadoLinea.index}].productoId">
                                <form:option value="" label="Seleccione un producto" />
                                <form:options items="${productos}" itemValue="id" itemLabel="nombre" />
                            </form:select>
                        </td>
                        <td>
                            <form:input cssClass="field-input" path="lineas[${estadoLinea.index}].cantidad" type="number" min="1" placeholder="1" />
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
            <div class="field-error"><form:errors path="lineasValidas" /></div>
        </div>

        <div class="field-group">
            <label for="estado">Estado <span class="required-mark">*</span></label>
            <form:select cssClass="field-input" path="estado" id="estado">
                <form:options items="${estados}" />
            </form:select>
            <div class="field-error"><form:errors path="estado" /></div>
        </div>

        <div class="actions-row">
            <button class="primary-button" type="submit">Guardar pedido</button>
            <a class="secondary-button" href="<c:url value='/pedidos' />">Cancelar</a>
        </div>
    </form:form>
</body>
</html>
