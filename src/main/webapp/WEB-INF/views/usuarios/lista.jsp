<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Usuarios | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
    <style>
        .acciones form { display: inline-block; margin: 0 8px 0 0; }
    </style>
</head>
<body class="app-body">
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="usuarios" />
    <h1 class="page-title">Gestion de usuarios</h1>
    <p class="page-subtitle">Panel de activacion y bloqueo de accesos.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <c:if test="${not empty mensajeOk}">
        <p class="message-ok">${mensajeOk}</p>
    </c:if>

    <c:if test="${not empty mensajeError}">
        <p class="message-error">${mensajeError}</p>
    </c:if>

    <p class="results-meta">Los cambios de estado y rol tambien se guardan en <strong>USUARIOS_PROYECTO.txt</strong>.</p>

    <div class="section-card" style="margin-bottom: 20px;">
        <h2 class="section-title">Nuevo usuario</h2>
        <form method="post" action="<c:url value='/usuarios/crear' />" class="actions-row" style="flex-wrap: wrap;">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="text" name="username" placeholder="Usuario" style="padding: 8px; width: 180px;" required="required" />
            <input type="text" name="password" placeholder="Contrasena inicial" style="padding: 8px; width: 180px;" required="required" />
            <select name="rol" style="padding: 8px;">
                <option value="USER">USER</option>
                <option value="DELEGADO">DELEGADO</option>
                <option value="ADMIN">ADMIN</option>
            </select>
            <label style="display: inline-flex; align-items: center; gap: 6px;">
                <input type="checkbox" name="activo" value="true" checked="checked" />
                Activo
            </label>
            <button class="primary-button" type="submit">Crear usuario</button>
        </form>
    </div>

    <div class="table-panel">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Usuario</th>
                <th>Rol</th>
                <th>Contrasena</th>
                <th>Estado</th>
                <th>Ultimo acceso</th>
                <th>Creado</th>
                <th>Actualizado</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="usuario" items="${usuarios}">
                <tr>
                    <td>${usuario.id}</td>
                    <td>${usuario.username}</td>
                    <td>
                        <div style="margin-bottom: 8px;">
                            <span class="role-chip role-${usuario.rol.toLowerCase()}">${usuario.rol}</span>
                        </div>
                        <form method="post" action="<c:url value='/usuarios/rol/${usuario.id}' />">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <select name="rol" style="padding: 8px;">
                                <option value="ADMIN" <c:if test="${usuario.rol == 'ADMIN'}">selected="selected"</c:if>>ADMIN</option>
                                <option value="DELEGADO" <c:if test="${usuario.rol == 'DELEGADO'}">selected="selected"</c:if>>DELEGADO</option>
                                <option value="USER" <c:if test="${usuario.rol == 'USER'}">selected="selected"</c:if>>USER</option>
                            </select>
                            <button class="secondary-button" type="submit">Guardar rol</button>
                        </form>
                    </td>
                    <td>
                        <form method="post" action="<c:url value='/usuarios/password/${usuario.id}' />">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <input
                                    type="text"
                                    name="nuevaPassword"
                                    placeholder="Nueva contrasena"
                                    style="padding: 8px; width: 180px;" />
                            <button class="secondary-button" type="submit">Guardar clave</button>
                        </form>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${usuario.activo}">
                                <span class="status-chip status-activo">Activo</span>
                            </c:when>
                            <c:otherwise>
                                <span class="status-chip status-bloqueado">Bloqueado</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty usuario.fechaUltimoAccesoTexto}">
                                ${usuario.fechaUltimoAccesoTexto}
                            </c:when>
                            <c:otherwise>
                                <span class="field-help">Sin accesos registrados</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        ${usuario.creadoPor}
                        <br />
                        <span class="field-help">${usuario.fechaCreacionTexto}</span>
                    </td>
                    <td>
                        ${usuario.actualizadoPor}
                        <br />
                        <span class="field-help">${usuario.fechaActualizacionTexto}</span>
                    </td>
                    <td class="acciones">
                        <c:choose>
                            <c:when test="${usuario.activo}">
                                <form method="post" action="<c:url value='/usuarios/desactivar/${usuario.id}' />">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <button class="secondary-button" type="submit">Bloquear</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form method="post" action="<c:url value='/usuarios/activar/${usuario.id}' />">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <button class="primary-button" type="submit">Desbloquear</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                        <form method="post" action="<c:url value='/usuarios/eliminar/${usuario.id}' />">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <button
                                    class="secondary-button"
                                    type="submit"
                                    onclick="return confirm('Se eliminara el usuario de la base de datos y del archivo del proyecto. Continuar?');">
                                Eliminar
                            </button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="section-card">
        <h2 class="section-title">Bitacora administrativa</h2>
        <p class="results-meta">Ultimas acciones sensibles ejecutadas desde este panel.</p>
        <form method="post" action="<c:url value='/usuarios/bitacora/limpiar' />" class="actions-row" style="flex-wrap: wrap; margin-bottom: 14px;">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="text" name="usernameConfirmacion" placeholder="Tu usuario admin" style="padding: 8px; width: 180px;" required="required" />
            <input type="password" name="passwordConfirmacion" placeholder="Tu contrasena actual" style="padding: 8px; width: 180px;" required="required" />
            <button
                    class="secondary-button"
                    type="submit"
                    onclick="return confirm('Se eliminara toda la bitacora administrativa actual. Continuar?');">
                Limpiar bitacora
            </button>
        </form>
        <div class="table-panel table-panel-scroll">
            <table>
                <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Actor</th>
                    <th>Accion</th>
                    <th>Objetivo</th>
                    <th>Detalle</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="entrada" items="${bitacoraAdmin}">
                    <tr>
                        <td>${entrada.fechaAccionTexto}</td>
                        <td>${entrada.actorUsername}</td>
                        <td>${entrada.accion}</td>
                        <td>${entrada.objetivo}</td>
                        <td>${entrada.detalle}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty bitacoraAdmin}">
                    <tr>
                        <td colspan="5">Todavia no hay acciones registradas.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
