<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>JH Nexus Anime</title>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/assets/img/jh-nexus-anime-favicon-32.png' />" />
    <link rel="stylesheet" href="<c:url value='/assets/css/brand.css' />" />
</head>
<body class="app-body">
    <%@ include file="common/brand-header.jspf" %>
    <c:set var="navSection" value="inicio" />
    <%@ include file="common/main-nav.jspf" %>

    <div class="hero-banner">
        <div class="hero-grid">
            <div>
                <span class="hero-kicker">Anime Commerce Hub</span>
                <h1 class="hero-title">Panel central de JH Nexus Anime</h1>
                <p class="hero-copy">
                    Supervisa el movimiento de la tienda, detecta alertas de stock y revisa la actividad comercial desde una portada mas marcada y tematica.
                </p>
                <div class="hero-actions">
                    <a class="primary-button" href="<c:url value='/productos' />">Explorar catalogo</a>
                    <a class="secondary-button" href="<c:url value='/pedidos' />">Revisar pedidos</a>
                    <a class="secondary-button" href="<c:url value='/mi-cuenta' />">Mi cuenta</a>
                </div>
                <div class="home-callout">
                    <strong>Mission Brief</strong>
                    <p>Esta portada combina control comercial, identidad anime y acceso rapido a los modulos principales para que el panel tenga mas caracter de marca.</p>
                </div>
            </div>
            <div class="hero-sidecard">
                <h2>Modo operacion</h2>
                <p>El tablero combina control de inventario, ventas y actividad reciente para una navegacion mas directa.</p>
                <ul>
                    <li>Alertas visuales para stock bajo.</li>
                    <li>Seguimiento rapido del rendimiento del catalogo.</li>
                    <li>Acceso directo a clientes, pedidos y gestion interna.</li>
                </ul>
            </div>
        </div>
    </div>

    <div class="resumen">
        <div class="tarjeta">
            <img class="metric-badge" src="<c:url value='/assets/img/theme/badge-categories-v1.png' />" alt="Insignia de categorias" />
            <span class="module-tag module-core">Catalog Core</span>
            <h2>Categorias</h2>
            <div class="valor"><a class="brand-link" href="<c:url value='/categorias' />">${totalCategorias}</a></div>
            <div class="meta">Base del catalogo</div>
        </div>
        <div class="tarjeta">
            <img class="metric-badge" src="<c:url value='/assets/img/theme/badge-inventory-v3.png' />" alt="Insignia de inventario" />
            <span class="module-tag module-inventory">Inventory</span>
            <h2>Productos</h2>
            <div class="valor"><a class="brand-link" href="<c:url value='/productos' />">${totalProductos}</a></div>
            <div class="meta">Inventario total</div>
        </div>
        <div class="tarjeta">
            <img class="metric-badge" src="<c:url value='/assets/img/theme/badge-customers-v3.png' />" alt="Insignia de clientes" />
            <span class="module-tag module-customers">Customer Base</span>
            <h2>Clientes</h2>
            <div class="valor"><a class="brand-link" href="<c:url value='/clientes' />">${totalClientes}</a></div>
            <div class="meta">Clientes registrados</div>
        </div>
        <div class="tarjeta">
            <img class="metric-badge" src="<c:url value='/assets/img/theme/badge-orders-v3.png' />" alt="Insignia de pedidos" />
            <span class="module-tag module-orders">Orders Flow</span>
            <h2>Pedidos</h2>
            <div class="valor"><a class="brand-link" href="<c:url value='/pedidos' />">${totalPedidos}</a></div>
            <div class="meta">Pedidos acumulados</div>
        </div>
    </div>

    <div class="mini-resumen">
        <div class="mini-tarjeta is-hot">
            <img class="mini-badge" src="<c:url value='/assets/img/theme/badge-revenue-v3.png' />" alt="Insignia de ingresos" />
            <span class="module-tag">Revenue</span>
            <div class="label">Ventas acumuladas</div>
            <div class="dato">${ventasTotales}</div>
        </div>
        <div class="mini-tarjeta is-gold">
            <span class="module-tag">Average</span>
            <div class="label">Ticket medio</div>
            <div class="dato">${ticketMedio}</div>
        </div>
        <div class="mini-tarjeta is-hot">
            <span class="module-tag">Queue</span>
            <img class="mini-badge" src="<c:url value='/assets/img/theme/badge-orders-v3.png' />" alt="Insignia de pedidos" />
            <div class="label">Pendientes</div>
            <div class="dato">${pedidosPendientes}</div>
        </div>
        <div class="mini-tarjeta is-cool">
            <span class="module-tag">Transit</span>
            <div class="label">Enviados</div>
            <div class="dato">${pedidosEnviados}</div>
        </div>
        <div class="mini-tarjeta">
            <span class="module-tag">Delivered</span>
            <div class="label">Entregados</div>
            <div class="dato">${pedidosEntregados}</div>
        </div>
    </div>

    <div class="bloques">
        <div class="bloque">
            <span class="module-tag">Critical Stock</span>
            <h2>Productos con stock bajo</h2>
            <div class="table-panel">
            <table>
                <thead>
                <tr>
                    <th>Producto</th>
                    <th>Stock</th>
                    <th>Categoria</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty productosBajoStock}">
                        <tr>
                            <td colspan="3">No hay alertas de stock.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="producto" items="${productosBajoStock}">
                            <tr>
                                <td><a class="brand-link" href="<c:url value='/productos/ver/${producto.id}' />">${producto.nombre}</a></td>
                                <td class="alerta">${producto.stock}</td>
                                <td><a class="brand-link" href="<c:url value='/categorias/ver/${producto.categoria.id}' />">${producto.categoria.nombre}</a></td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
            </div>
        </div>

        <div class="bloque">
            <span class="module-tag">Top Sellers</span>
            <h2>Top productos vendidos</h2>
            <div class="table-panel">
            <table>
                <thead>
                <tr>
                    <th>Producto</th>
                    <th>Unidades</th>
                    <th>Stock actual</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty topProductosVendidos}">
                        <tr>
                            <td colspan="3">Todavia no hay ventas registradas.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="item" items="${topProductosVendidos}">
                            <tr>
                                <td><a class="brand-link" href="<c:url value='/productos/ver/${item.producto.id}' />">${item.producto.nombre}</a></td>
                                <td>${item.unidadesVendidas}</td>
                                <td>${item.producto.stock}</td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
            </div>
        </div>

        <div class="bloque">
            <span class="module-tag">Recent Orders</span>
            <h2>Pedidos recientes</h2>
            <div class="table-panel">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Cliente</th>
                    <th>Detalle</th>
                    <th>Total</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty pedidosRecientes}">
                        <tr>
                            <td colspan="4">Todavia no hay pedidos.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="pedido" items="${pedidosRecientes}">
                            <tr>
                                <td><a class="brand-link" href="<c:url value='/pedidos/ver/${pedido.id}' />">${pedido.id}</a></td>
                                <td><a class="brand-link" href="<c:url value='/clientes/ver/${pedido.cliente.id}' />">${pedido.cliente.nombre} ${pedido.cliente.apellidos}</a></td>
                                <td>${pedido.resumenProductos}</td>
                                <td>${pedido.total}</td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
            </div>
        </div>
    </div>
</body>
</html>
