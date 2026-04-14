<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Categoria | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
    <style>
        .form-card { max-width: 620px; }
    </style>
</head>
<body class="app-body">
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="categorias" />
    <h1 class="page-title">
        <c:choose>
            <c:when test="${modoEdicion}">Editar categoria</c:when>
            <c:otherwise>Nueva categoria</c:otherwise>
        </c:choose>
    </h1>
    <p class="page-subtitle">Alta y mantenimiento de categorias para JH Nexus Anime.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <p class="secondary-link">
        <a class="brand-link" href="<c:url value='/categorias' />">Volver al listado</a>
    </p>

    <c:if test="${not empty errorGeneral}">
        <p class="message-error">${errorGeneral}</p>
    </c:if>

    <form:form cssClass="form-card form-grid" method="post" action="${pageContext.request.contextPath}/categorias/guardar" modelAttribute="categoria">
        <form:hidden path="id" />

        <div class="field-group">
            <label for="nombre">Nombre <span class="required-mark">*</span></label>
            <span class="field-help">Usa un nombre corto y claro para organizar los productos.</span>
            <form:input cssClass="field-input" path="nombre" id="nombre" placeholder="Ejemplo: Figuras shonen" />
            <div class="field-error"><form:errors path="nombre" /></div>
        </div>

        <div class="actions-row">
            <button class="primary-button" type="submit">Guardar categoria</button>
            <a class="secondary-button" href="<c:url value='/categorias' />">Cancelar</a>
        </div>
    </form:form>
</body>
</html>
