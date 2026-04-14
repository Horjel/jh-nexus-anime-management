<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
</head>
<body class="app-body">
    <%@ include file="../common/brand-header.jspf" %>
    <section class="hero-banner">
        <div class="hero-copy">
            <p class="hero-kicker">Incident report</p>
            <h1 class="page-title">${tituloError}</h1>
            <p class="page-subtitle">${mensajeError}</p>
            <div class="hero-actions">
                <a class="primary-button" href="<c:url value='/' />">Volver al inicio</a>
            </div>
        </div>
    </section>
</body>
</html>
