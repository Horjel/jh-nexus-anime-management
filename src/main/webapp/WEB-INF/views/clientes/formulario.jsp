<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cliente | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
    <style>
        .form-card { max-width: 760px; }
    </style>
</head>
<body class="app-body">
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="clientes" />
    <h1 class="page-title">
        <c:choose>
            <c:when test="${modoEdicion}">Editar cliente</c:when>
            <c:otherwise>Nuevo cliente</c:otherwise>
        </c:choose>
    </h1>
    <p class="page-subtitle">Alta y mantenimiento de clientes de JH Nexus Anime.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <p class="secondary-link">
        <a class="brand-link" href="<c:url value='/clientes' />">Volver al listado</a>
    </p>

    <c:if test="${not empty errorGeneral}">
        <p class="message-error">${errorGeneral}</p>
    </c:if>

    <form:form cssClass="form-card form-grid" method="post" action="${pageContext.request.contextPath}/clientes/guardar" modelAttribute="cliente">
        <form:hidden path="id" />

        <div class="field-group">
            <label for="nombre">Nombre <span class="required-mark">*</span></label>
            <form:input cssClass="field-input" path="nombre" id="nombre" placeholder="Ana" />
            <div class="field-error"><form:errors path="nombre" /></div>
        </div>

        <div class="field-group">
            <label for="apellidos">Apellidos <span class="required-mark">*</span></label>
            <form:input cssClass="field-input" path="apellidos" id="apellidos" placeholder="Lopez Garcia" />
            <div class="field-error"><form:errors path="apellidos" /></div>
        </div>

        <div class="field-group">
            <label for="email">Email <span class="required-mark">*</span></label>
            <span class="field-help">Este correo se usa como referencia principal del cliente.</span>
            <form:input cssClass="field-input" path="email" id="email" type="email" placeholder="cliente@correo.com" />
            <div class="field-error"><form:errors path="email" /></div>
        </div>

        <div class="field-group">
            <label for="telefono">Telefono <span class="required-mark">*</span></label>
            <form:input cssClass="field-input" path="telefono" id="telefono" placeholder="600123123" />
            <div class="field-error"><form:errors path="telefono" /></div>
        </div>

        <div class="field-group">
            <label for="direccion">Direccion <span class="required-mark">*</span></label>
            <form:input cssClass="field-input" path="direccion" id="direccion" placeholder="Calle Mayor 12, Madrid" />
            <div class="field-error"><form:errors path="direccion" /></div>
        </div>

        <div class="actions-row">
            <button class="primary-button" type="submit">Guardar cliente</button>
            <a class="secondary-button" href="<c:url value='/clientes' />">Cancelar</a>
        </div>
    </form:form>
</body>
</html>
