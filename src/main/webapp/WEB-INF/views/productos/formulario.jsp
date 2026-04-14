<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Producto | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
    <style>
        .form-card { max-width: 720px; }
    </style>
</head>
<body class="app-body">
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="productos" />
    <h1 class="page-title">
        <c:choose>
            <c:when test="${modoEdicion}">Editar producto</c:when>
            <c:otherwise>Nuevo producto</c:otherwise>
        </c:choose>
    </h1>
    <p class="page-subtitle">Registro de productos en JH Nexus Anime.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <p class="secondary-link">
        <a class="brand-link" href="<c:url value='/productos' />">Volver al listado</a>
    </p>

    <c:if test="${not empty errorGeneral}">
        <p class="message-error">${errorGeneral}</p>
    </c:if>

    <form:form cssClass="form-card form-grid" method="post" action="${pageContext.request.contextPath}/productos/guardar" modelAttribute="producto">
        <form:hidden path="id" />

        <div class="field-group">
            <label for="nombre">Nombre <span class="required-mark">*</span></label>
            <form:input cssClass="field-input" path="nombre" id="nombre" placeholder="Ejemplo: Figura de Goku SSJ" />
            <div class="field-error"><form:errors path="nombre" /></div>
        </div>

        <div class="field-group">
            <label for="descripcion">Descripcion <span class="required-mark">*</span></label>
            <span class="field-help">Resume el producto de forma comercial y facil de identificar.</span>
            <form:input cssClass="field-input" path="descripcion" id="descripcion" placeholder="PVC 24 cm, edicion coleccionista" />
            <div class="field-error"><form:errors path="descripcion" /></div>
        </div>

        <div class="field-group">
            <label for="precio">Precio <span class="required-mark">*</span></label>
            <form:input cssClass="field-input" path="precio" id="precio" type="number" step="0.01" min="0" placeholder="0.00" />
            <div class="field-error"><form:errors path="precio" /></div>
        </div>

        <div class="field-group">
            <label for="stock">Stock <span class="required-mark">*</span></label>
            <form:input cssClass="field-input" path="stock" id="stock" type="number" min="0" placeholder="0" />
            <div class="field-error"><form:errors path="stock" /></div>
        </div>

        <div class="field-group">
            <label for="categoria">Categoria <span class="required-mark">*</span></label>
            <form:select cssClass="field-input" path="categoria.id" id="categoria">
                <form:option value="" label="Seleccione una categoria" />
                <form:options items="${categorias}" itemValue="id" itemLabel="nombre" />
            </form:select>
        </div>

        <div class="actions-row">
            <button class="primary-button" type="submit">Guardar producto</button>
            <a class="secondary-button" href="<c:url value='/productos' />">Cancelar</a>
        </div>
    </form:form>
</body>
</html>
