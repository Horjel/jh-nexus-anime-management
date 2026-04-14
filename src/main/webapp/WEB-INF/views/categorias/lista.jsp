<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Categorias | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
    <style>
        .acciones a { margin-right: 8px; }
    </style>
    <script>
        function ordenarCategorias(ascKey, descKey) {
            const params = new URLSearchParams(window.location.search);
            const actual = params.get('orden') || '';
            params.set('orden', actual === ascKey ? descKey : ascKey);
            params.set('pagina', '1');
            window.location = '<c:url value="/categorias" />' + '?' + params.toString();
        }
    </script>
</head>
<body class="app-body">
    <% boolean esAdmin = request.isUserInRole("ADMIN"); %>
    <% boolean puedeExportar = esAdmin || request.isUserInRole("DELEGADO"); %>
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="categorias" />
    <h1 class="page-title">Gestion de categorias</h1>
    <p class="page-subtitle">Catalogo base de JH Nexus Anime.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <c:if test="${not empty mensajeOk}">
        <p class="message-ok">${mensajeOk}</p>
    </c:if>

    <c:if test="${not empty mensajeError}">
        <p class="message-error">${mensajeError}</p>
    </c:if>

    <p class="secondary-link">
        <% if (esAdmin) { %>
        <a class="brand-link" href="<c:url value='/categorias/nueva' />">Nueva categoria</a>
        <% } %>
        <% if (puedeExportar) { %>
        <% if (esAdmin) { %>
        |
        <% } %>
        <c:url var="exportarCategoriasUrl" value="/categorias/exportar">
            <c:param name="q" value="${q}" />
            <c:param name="orden" value="${orden}" />
        </c:url>
        <a class="brand-link" href="${exportarCategoriasUrl}">Exportar CSV</a>
        <% } %>
    </p>

    <form method="get" action="<c:url value='/categorias' />" style="margin-bottom: 16px;">
        <input type="text" name="q" value="${q}" placeholder="Buscar por nombre" style="width: 280px; padding: 8px;" />
        <button type="submit">Buscar</button>
        <a href="<c:url value='/categorias' />">Limpiar</a>
    </form>

    <p class="results-meta">Mostrando ${inicioResultados}-${finResultados} de ${totalResultados} resultados. Pagina ${paginaActual} de ${totalPaginas}.</p>

    <div class="table-panel">
    <table>
        <thead>
        <tr>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'idAsc' || orden == 'idDesc'}">sort-active</c:if>" onclick="ordenarCategorias('idAsc', 'idDesc')">
                    ID <span class="sort-indicator">${orden == 'idAsc' ? '↑' : orden == 'idDesc' ? '↓' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'nombreAsc' || orden == 'nombreDesc'}">sort-active</c:if>" onclick="ordenarCategorias('nombreAsc', 'nombreDesc')">
                    Nombre <span class="sort-indicator">${orden == 'nombreAsc' ? 'A-Z' : orden == 'nombreDesc' ? 'Z-A' : '↕'}</span>
                </button>
            </th>
            <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty categorias}">
                <tr>
                    <td colspan="3">Todavia no hay categorias registradas.</td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="categoria" items="${categorias}">
                    <tr>
                        <td>${categoria.id}</td>
                        <td>${categoria.nombre}</td>
                        <td class="acciones">
                            <a href="<c:url value='/categorias/ver/${categoria.id}' />">Ver</a>
                            <% if (esAdmin) { %>
                            <a href="<c:url value='/categorias/editar/${categoria.id}' />">Editar</a>
                            <form method="post" action="<c:url value='/categorias/eliminar/${categoria.id}' />" style="display:inline;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <button type="submit" class="link-button"
                                        onclick="return confirm('Se eliminara la categoria. Continuar?');">Eliminar</button>
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
                <c:url var="paginaAnteriorUrl" value="/categorias">
                    <c:param name="q" value="${q}" />
                    <c:param name="orden" value="${orden}" />
                    <c:param name="pagina" value="${paginaAnterior}" />
                </c:url>
                <a class="pager-link" href="${paginaAnteriorUrl}">Anterior</a>
            </c:if>

            <span class="pager-current">Pagina ${paginaActual} / ${totalPaginas}</span>

            <c:if test="${haySiguiente}">
                <c:url var="paginaSiguienteUrl" value="/categorias">
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
