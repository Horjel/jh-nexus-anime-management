<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Productos | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
    <style>
        .acciones a { margin-right: 8px; }
    </style>
    <script>
        function ordenarProductos(ascKey, descKey) {
            const params = new URLSearchParams(window.location.search);
            const actual = params.get('orden') || '';
            params.set('orden', actual === ascKey ? descKey : ascKey);
            params.set('pagina', '1');
            window.location = '<c:url value="/productos" />' + '?' + params.toString();
        }
    </script>
</head>
<body class="app-body">
    <% boolean puedeGestionar = request.isUserInRole("ADMIN") || request.isUserInRole("DELEGADO"); %>
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="productos" />
    <h1 class="page-title">Gestion de productos</h1>
    <p class="page-subtitle">Inventario y catalogo de JH Nexus Anime.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <c:if test="${not empty mensajeOk}">
        <p class="message-ok">${mensajeOk}</p>
    </c:if>

    <c:if test="${not empty mensajeError}">
        <p class="message-error">${mensajeError}</p>
    </c:if>

    <p class="secondary-link">
        <% if (puedeGestionar) { %>
        <a class="brand-link" href="<c:url value='/productos/nuevo' />">Nuevo producto</a>
        |
        <c:url var="exportarProductosUrl" value="/productos/exportar">
            <c:param name="q" value="${q}" />
            <c:param name="orden" value="${orden}" />
            <c:param name="stockBajo" value="${stockBajo}" />
        </c:url>
        <a class="brand-link" href="${exportarProductosUrl}">Exportar CSV</a>
        <% } %>
    </p>

    <form method="get" action="<c:url value='/productos' />" style="margin-bottom: 16px;">
        <input type="text" name="q" value="${q}" placeholder="Buscar producto, descripcion o categoria" style="width: 320px; padding: 8px;" />
        <label class="filter-toggle">
            <input class="filter-toggle-input" type="checkbox" name="stockBajo" value="true" <c:if test="${stockBajo}">checked="checked"</c:if> />
            Solo stock bajo
        </label>
        <button type="submit">Buscar</button>
        <a href="<c:url value='/productos' />">Limpiar</a>
    </form>

    <p class="results-meta">Mostrando ${inicioResultados}-${finResultados} de ${totalResultados} resultados. Pagina ${paginaActual} de ${totalPaginas}.</p>

    <div class="table-panel">
    <table>
        <thead>
        <tr>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'idAsc' || orden == 'idDesc'}">sort-active</c:if>" onclick="ordenarProductos('idAsc', 'idDesc')">
                    ID <span class="sort-indicator">${orden == 'idAsc' ? '↑' : orden == 'idDesc' ? '↓' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'nombreAsc' || orden == 'nombreDesc'}">sort-active</c:if>" onclick="ordenarProductos('nombreAsc', 'nombreDesc')">
                    Nombre <span class="sort-indicator">${orden == 'nombreAsc' ? 'A-Z' : orden == 'nombreDesc' ? 'Z-A' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'descripcionAsc' || orden == 'descripcionDesc'}">sort-active</c:if>" onclick="ordenarProductos('descripcionAsc', 'descripcionDesc')">
                    Descripcion <span class="sort-indicator">${orden == 'descripcionAsc' ? 'A-Z' : orden == 'descripcionDesc' ? 'Z-A' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'precioAsc' || orden == 'precioDesc'}">sort-active</c:if>" onclick="ordenarProductos('precioAsc', 'precioDesc')">
                    Precio <span class="sort-indicator">${orden == 'precioAsc' ? '↑' : orden == 'precioDesc' ? '↓' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'stockAsc' || orden == 'stockDesc'}">sort-active</c:if>" onclick="ordenarProductos('stockAsc', 'stockDesc')">
                    Stock <span class="sort-indicator">${orden == 'stockAsc' ? '↑' : orden == 'stockDesc' ? '↓' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'categoriaAsc' || orden == 'categoriaDesc'}">sort-active</c:if>" onclick="ordenarProductos('categoriaAsc', 'categoriaDesc')">
                    Categoria <span class="sort-indicator">${orden == 'categoriaAsc' ? 'A-Z' : orden == 'categoriaDesc' ? 'Z-A' : '↕'}</span>
                </button>
            </th>
            <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty productos}">
                <tr>
                    <td colspan="7">Todavia no hay productos registrados.</td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="producto" items="${productos}">
                    <tr>
                        <td>${producto.id}</td>
                        <td>${producto.nombre}</td>
                        <td>${producto.descripcion}</td>
                        <td>${producto.precio}</td>
                        <td>${producto.stock}</td>
                        <td>${producto.categoria.nombre}</td>
                        <td class="acciones">
                            <a href="<c:url value='/productos/ver/${producto.id}' />">Ver</a>
                            <% if (puedeGestionar) { %>
                            <a href="<c:url value='/productos/editar/${producto.id}' />">Editar</a>
                            <form method="post" action="<c:url value='/productos/eliminar/${producto.id}' />" style="display:inline;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <button type="submit" class="link-button"
                                        onclick="return confirm('Se eliminara el producto. Continuar?');">Eliminar</button>
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
                <c:url var="paginaAnteriorUrl" value="/productos">
                    <c:param name="q" value="${q}" />
                    <c:param name="orden" value="${orden}" />
                    <c:param name="stockBajo" value="${stockBajo}" />
                    <c:param name="pagina" value="${paginaAnterior}" />
                </c:url>
                <a class="pager-link" href="${paginaAnteriorUrl}">Anterior</a>
            </c:if>

            <span class="pager-current">Pagina ${paginaActual} / ${totalPaginas}</span>

            <c:if test="${haySiguiente}">
                <c:url var="paginaSiguienteUrl" value="/productos">
                    <c:param name="q" value="${q}" />
                    <c:param name="orden" value="${orden}" />
                    <c:param name="stockBajo" value="${stockBajo}" />
                    <c:param name="pagina" value="${paginaSiguiente}" />
                </c:url>
                <a class="pager-link" href="${paginaSiguienteUrl}">Siguiente</a>
            </c:if>
        </div>
    </c:if>
</body>
</html>
