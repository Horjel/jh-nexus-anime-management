<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Pedidos | JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
    <style>
        .acciones a { margin-right: 8px; }
    </style>
    <script>
        function ordenarPedidos(ascKey, descKey) {
            const params = new URLSearchParams(window.location.search);
            const actual = params.get('orden') || '';
            params.set('orden', actual === ascKey ? descKey : ascKey);
            params.set('pagina', '1');
            window.location = '<c:url value="/pedidos" />' + '?' + params.toString();
        }
    </script>
</head>
<body class="app-body">
    <% boolean puedeGestionar = request.isUserInRole("ADMIN") || request.isUserInRole("DELEGADO"); %>
    <%@ include file="../common/brand-header.jspf" %>
    <c:set var="navSection" value="pedidos" />
    <h1 class="page-title">Gestion de pedidos</h1>
    <p class="page-subtitle">Seguimiento comercial de JH Nexus Anime.</p>
    <%@ include file="../common/main-nav.jspf" %>

    <c:if test="${not empty mensajeOk}">
        <p class="message-ok">${mensajeOk}</p>
    </c:if>

    <c:if test="${not empty mensajeError}">
        <p class="message-error">${mensajeError}</p>
    </c:if>

    <p class="secondary-link">
        <% if (puedeGestionar) { %>
        <a class="brand-link" href="<c:url value='/pedidos/nuevo' />">Nuevo pedido</a>
        |
        <c:url var="exportarPedidosUrl" value="/pedidos/exportar">
            <c:param name="q" value="${q}" />
            <c:param name="estado" value="${estadoSeleccionado}" />
            <c:param name="orden" value="${orden}" />
        </c:url>
        <a class="brand-link" href="${exportarPedidosUrl}">Exportar CSV</a>
        <% } %>
    </p>

    <form method="get" action="<c:url value='/pedidos' />" style="margin-bottom: 16px;">
        <input type="text" name="q" value="${q}" placeholder="Buscar cliente o producto" style="width: 280px; padding: 8px;" />
        <select name="estado" style="padding: 8px;">
            <option value="">Todos los estados</option>
            <option value="PENDIENTE" <c:if test="${estadoSeleccionado == 'PENDIENTE'}">selected="selected"</c:if>>PENDIENTE</option>
            <option value="ENVIADO" <c:if test="${estadoSeleccionado == 'ENVIADO'}">selected="selected"</c:if>>ENVIADO</option>
            <option value="ENTREGADO" <c:if test="${estadoSeleccionado == 'ENTREGADO'}">selected="selected"</c:if>>ENTREGADO</option>
        </select>
        <button type="submit">Buscar</button>
        <a href="<c:url value='/pedidos' />">Limpiar</a>
    </form>

    <p class="results-meta">Mostrando ${inicioResultados}-${finResultados} de ${totalResultados} resultados. Pagina ${paginaActual} de ${totalPaginas}.</p>

    <div class="table-panel">
    <table>
        <thead>
        <tr>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'idAsc' || orden == 'idDesc'}">sort-active</c:if>" onclick="ordenarPedidos('idAsc', 'idDesc')">
                    ID <span class="sort-indicator">${orden == 'idAsc' ? '↑' : orden == 'idDesc' ? '↓' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'fechaAsc' || orden == 'fechaDesc'}">sort-active</c:if>" onclick="ordenarPedidos('fechaAsc', 'fechaDesc')">
                    Fecha <span class="sort-indicator">${orden == 'fechaAsc' ? '↑' : orden == 'fechaDesc' ? '↓' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'clienteAsc' || orden == 'clienteDesc'}">sort-active</c:if>" onclick="ordenarPedidos('clienteAsc', 'clienteDesc')">
                    Cliente <span class="sort-indicator">${orden == 'clienteAsc' ? 'A-Z' : orden == 'clienteDesc' ? 'Z-A' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'detalleAsc' || orden == 'detalleDesc'}">sort-active</c:if>" onclick="ordenarPedidos('detalleAsc', 'detalleDesc')">
                    Detalle <span class="sort-indicator">${orden == 'detalleAsc' ? 'A-Z' : orden == 'detalleDesc' ? 'Z-A' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'unidadesAsc' || orden == 'unidadesDesc'}">sort-active</c:if>" onclick="ordenarPedidos('unidadesAsc', 'unidadesDesc')">
                    Unidades <span class="sort-indicator">${orden == 'unidadesAsc' ? '↑' : orden == 'unidadesDesc' ? '↓' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'estadoAsc' || orden == 'estadoDesc'}">sort-active</c:if>" onclick="ordenarPedidos('estadoAsc', 'estadoDesc')">
                    Estado <span class="sort-indicator">${orden == 'estadoAsc' ? 'A-Z' : orden == 'estadoDesc' ? 'Z-A' : '↕'}</span>
                </button>
            </th>
            <th>
                <button type="button" class="sort-button <c:if test="${orden == 'totalAsc' || orden == 'totalDesc'}">sort-active</c:if>" onclick="ordenarPedidos('totalAsc', 'totalDesc')">
                    Total <span class="sort-indicator">${orden == 'totalAsc' ? '↑' : orden == 'totalDesc' ? '↓' : '↕'}</span>
                </button>
            </th>
            <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty pedidos}">
                <tr>
                    <td colspan="8">Todavia no hay pedidos registrados.</td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="pedido" items="${pedidos}">
                    <tr>
                        <td>${pedido.id}</td>
                        <td>${pedido.fechaPedidoTexto}</td>
                        <td>${pedido.cliente.nombre} ${pedido.cliente.apellidos}</td>
                        <td>${pedido.resumenProductos}</td>
                        <td>${pedido.cantidadTotal}</td>
                        <td>
                            <span class="status-chip status-${pedido.estado.toLowerCase()}">${pedido.estado}</span>
                        </td>
                        <td>${pedido.total}</td>
                        <td class="acciones">
                            <a href="<c:url value='/pedidos/ver/${pedido.id}' />">Ver</a>
                            <% if (puedeGestionar) { %>
                            <a href="<c:url value='/pedidos/editar/${pedido.id}' />">Editar</a>
                            <form method="post" action="<c:url value='/pedidos/eliminar/${pedido.id}' />" style="display:inline;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <button type="submit" class="link-button"
                                        onclick="return confirm('Se eliminara el pedido. Continuar?');">Eliminar</button>
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
                <c:url var="paginaAnteriorUrl" value="/pedidos">
                    <c:param name="q" value="${q}" />
                    <c:param name="estado" value="${estadoSeleccionado}" />
                    <c:param name="orden" value="${orden}" />
                    <c:param name="pagina" value="${paginaAnterior}" />
                </c:url>
                <a class="pager-link" href="${paginaAnteriorUrl}">Anterior</a>
            </c:if>

            <span class="pager-current">Pagina ${paginaActual} / ${totalPaginas}</span>

            <c:if test="${haySiguiente}">
                <c:url var="paginaSiguienteUrl" value="/pedidos">
                    <c:param name="q" value="${q}" />
                    <c:param name="estado" value="${estadoSeleccionado}" />
                    <c:param name="orden" value="${orden}" />
                    <c:param name="pagina" value="${paginaSiguiente}" />
                </c:url>
                <a class="pager-link" href="${paginaSiguienteUrl}">Siguiente</a>
            </c:if>
        </div>
    </c:if>
</body>
</html>
