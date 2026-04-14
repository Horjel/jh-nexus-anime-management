<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Acceso | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
</head>
<body class="app-body">
    <div class="login-shell">
        <div class="login-card">
            <div class="login-showcase">
                <img class="login-logo" src="<c:url value='/assets/img/jh-nexus-anime-logo.png' />" alt="Logo oficial JH Nexus Anime" />
                <span class="login-eyebrow">Neo Tokyo Commerce</span>
                <h1 class="login-title">JH Nexus Anime</h1>
                <p class="login-copy">
                    Accede al panel de gestion de la tienda y controla catalogo, pedidos, clientes y usuarios desde una base visual inspirada en anime clasico.
                </p>
                <ul class="login-feature-list">
                    <li>Catalogo, stock y pedidos en una sola cabina de control.</li>
                    <li>Roles diferenciados para administracion y operativa diaria.</li>
                    <li>Panel visual listo para seguir evolucionando la marca.</li>
                </ul>
            </div>

            <div class="login-panel">
                <h1 class="page-title">Acceso</h1>
                <p class="page-subtitle">Entra al panel principal de JH Nexus Anime.</p>

                <c:if test="${param.blocked != null}">
                    <p class="message-error">La cuenta esta bloqueada. Debe activarla un administrador.</p>
                </c:if>

                <c:if test="${param.error != null}">
                    <p class="message-error">Usuario o contrasena incorrectos.</p>
                </c:if>

                <c:if test="${param.logout != null}">
                    <p class="message-ok">Sesion cerrada correctamente.</p>
                </c:if>

                <form class="login-form-shell" method="post" action="<c:url value='/login' />">
                    <div class="field-group">
                        <label for="username">Usuario</label>
                        <input class="field-input" id="username" name="username" type="text" required="required" autocomplete="username" />
                    </div>

                    <div class="field-group">
                        <label for="password">Contrasena</label>
                        <input class="field-input" id="password" name="password" type="password" required="required" autocomplete="current-password" />
                    </div>

                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                    <div class="actions-row">
                        <button class="primary-button" type="submit">Entrar al panel</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
