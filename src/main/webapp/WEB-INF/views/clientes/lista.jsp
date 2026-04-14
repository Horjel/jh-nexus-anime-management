<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Clientes | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
    <style>
        .acciones a { margin-right: 8px; }
    </style>
    <script>
        function ordenarClientes(ascKey, descKey) {
            const params = new URLSearchParams(window.location.search);
            const actual = params.get('orden') || '';
            params.set('orden', actual === ascKey ? descKey : ascKey);
            params.set('pagina', '1');
            window.location = '<c:url value="/clientes" />' + '?' + params.toString();
        }
    </script>
</head>
<body class="app-body">
    <% boolean puedeGestionar = request.isUserInRole("ADMIN") || request.isUserInRole("DELEGADO"); %>
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="clientes" />
    <h1 class="page-title">Gestion de clientes</h1>
    <p class="page-subtitle">Base de clientes de JH Nexus Anime.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <c:if test="${not empty mensajeOk}">
        <p class="message-ok">${mensajeOk}</p>
    </c:if>

    <c:if test="${not empty mensajeError}">
        <p class="message-error">${mensajeError}</p>
    </c:if>

    <p class="secondary-link">
        <% if (puedeGestionar) { %>
        <a class="brand-link" href="<c:url value='/clientes/nuevo' />">Nuevo cliente</a>
        |
        <c:url var="exportarClientesUrl" value="/clientes/exportar">
            <c:param name="q" value="${q}" />
            <c:param name="orden" value="${orden}" />
        </c:url>
        <a class="brand-link" href="${exportarClientesUrl}">Exportar CSV</a>
        <% } %>
    </p>

    <form method="get" action="<c:url value='/clientes' />" style="margin-bottom: 16px;">
        <input type="text" name="q" value="${q}" placeholder="Buscar por nombre, apellidos, email o telefono" style="width: 360px; padding: 8px;" />
        <button type="submit">Buscar</button>
        <a href="<c:url value='/clientes' />">Limpiar</a>
    </form>

    <p class="results-meta">Mostrando ${inicioResultados}-${finResultados} de ${totalResultados} resultados. Pagina ${paginaActual} de ${totalPaginas}.</p>

    <div class="table-panel">
    <table>
        <thead>
        <tr>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'idAsc' || orden == 'idDesc'}">sort-active</c:if>" onclick="ordenarClientes('idAsc', 'idDesc')">
                    ID <span class="sort-indicator">${orden == 'idAsc' ? '↑' : orden == 'idDesc' ? '↓' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'nombreAsc' || orden == 'nombreDesc'}">sort-active</c:if>" onclick="ordenarClientes('nombreAsc', 'nombreDesc')">
                    Nombre <span class="sort-indicator">${orden == 'nombreAsc' ? 'A-Z' : orden == 'nombreDesc' ? 'Z-A' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'apellidosAsc' || orden == 'apellidosDesc'}">sort-active</c:if>" onclick="ordenarClientes('apellidosAsc', 'apellidosDesc')">
                    Apellidos <span class="sort-indicator">${orden == 'apellidosAsc' ? 'A-Z' : orden == 'apellidosDesc' ? 'Z-A' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'emailAsc' || orden == 'emailDesc'}">sort-active</c:if>" onclick="ordenarClientes('emailAsc', 'emailDesc')">
                    Email <span class="sort-indicator">${orden == 'emailAsc' ? 'A-Z' : orden == 'emailDesc' ? 'Z-A' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'telefonoAsc' || orden == 'telefonoDesc'}">sort-active</c:if>" onclick="ordenarClientes('telefonoAsc', 'telefonoDesc')">
                    Telefono <span class="sort-indicator">${orden == 'telefonoAsc' ? '↑' : orden == 'telefonoDesc' ? '↓' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'direccionAsc' || orden == 'direccionDesc'}">sort-active</c:if>" onclick="ordenarClientes('direccionAsc', 'direccionDesc')">
                    Direccion <span class="sort-indicator">${orden == 'direccionAsc' ? 'A-Z' : orden == 'direccionDesc' ? 'Z-A' : '↕'}</span>
                </button>
            </th>
            <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty clientes}">
                <tr>
                    <td colspan="7">Todavia no hay clientes registrados.</td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="cliente" items="${clientes}">
                    <tr>
                        <td>${cliente.id}</td>
                        <td>${cliente.nombre}</td>
                        <td>${cliente.apellidos}</td>
                        <td>${cliente.email}</td>
                        <td>${cliente.telefono}</td>
                        <td>${cliente.direccion}</td>
                        <td class="acciones">
                            <a href="<c:url value='/clientes/ver/${cliente.id}' />">Ver</a>
                            <% if (puedeGestionar) { %>
                            <a href="<c:url value='/clientes/editar/${cliente.id}' />">Editar</a>
                            <form method="post" action="<c:url value='/clientes/eliminar/${cliente.id}' />" style="display:inline;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <button type="submit" class="link-button"
                                        onclick="return confirm('Se eliminara el cliente. Continuar?');">Eliminar</button>
                            </form>
                            <% } %>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
    </div>

    <c:if test="${totalPaginas > 1}">
        <div class="pager">
            <c:if test="${hayAnterior}">
                <c:url var="paginaAnteriorUrl" value="/clientes">
                    <c:param name="q" value="${q}" />
                    <c:param name="orden" value="${orden}" />
                    <c:param name="pagina" value="${paginaAnterior}" />
                </c:url>
                <a class="pager-link" href="${paginaAnteriorUrl}">Anterior</a>
            </c:if>

            <span class="pager-current">Pagina ${paginaActual} / ${totalPaginas}</span>

            <c:if test="${haySiguiente}">
                <c:url var="paginaSiguienteUrl" value="/clientes">
                    <c:param name="q" value="${q}" />
                    <c:param name="orden" value="${orden}" />
                    <c:param name="pagina" value="${paginaSiguiente}" />
                </c:url>
                <a class="pager-link" href="${paginaSiguienteUrl}">Siguiente</a>
            </c:if>
        </div>
    </c:if>
</body>
</html>
