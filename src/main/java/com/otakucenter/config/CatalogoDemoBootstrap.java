package com.otakucenter.config;

import com.otakucenter.dao.CategoriaDao;
import com.otakucenter.dao.ClienteDao;
import com.otakucenter.dao.ProductoDao;
import com.otakucenter.model.Categoria;
import com.otakucenter.model.Cliente;
import com.otakucenter.model.Producto;
import com.otakucenter.util.TextoNormalizador;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@ConditionalOnProperty(value = "app.demo.catalog-bootstrap.enabled", havingValue = "true")
public class CatalogoDemoBootstrap implements CommandLineRunner {

    private static final String ACTOR = "CATALOG_BOOTSTRAP";

    private final CategoriaDao categoriaDao;
    private final ProductoDao productoDao;
    private final ClienteDao clienteDao;

    public CatalogoDemoBootstrap(CategoriaDao categoriaDao, ProductoDao productoDao, ClienteDao clienteDao) {
        this.categoriaDao = categoriaDao;
        this.productoDao = productoDao;
        this.clienteDao = clienteDao;
    }

    @Override
    public void run(String... args) {
        Categoria figurasEscala = asegurarCategoria("Categoria20260403", "Figuras de escala");
        Categoria nendoroids = asegurarCategoria(null, "Nendoroids y mini figuras");
        Categoria posters = asegurarCategoria(null, "Posters y wall scrolls");
        Categoria ropa = asegurarCategoria(null, "Ropa y accesorios");
        Categoria tazas = asegurarCategoria(null, "Tazas y papeleria");
        Categoria coleccionista = asegurarCategoria(null, "Ediciones coleccionista");

        asegurarProducto(
                "Producto20260403",
                "Figura Edward Elric 1/8",
                "Figura de coleccion de Fullmetal Alchemist Brotherhood con pose de transmutacion.",
                "29.99",
                4,
                figurasEscala
        );
        asegurarProducto(
                "ProductoExtra20260403",
                "Poster de Frieren bajo la luna",
                "Poster panoramico premium con acabado satinado inspirado en Frieren.",
                "15.50",
                8,
                posters
        );
        asegurarProducto(
                null,
                "Figura Naruto modo sabio chibi",
                "Mini figura estilizada para vitrina con base decorativa de Konoha.",
                "24.90",
                7,
                nendoroids
        );
        asegurarProducto(
                null,
                "Figura Alphonse Elric armor edition",
                "Figura articulada metal finish con base circular alquimica.",
                "64.95",
                3,
                coleccionista
        );
        asegurarProducto(
                null,
                "Sudadera emblema de Amestris",
                "Sudadera gris grafito con bordado frontal y detalles alquimicos discretos.",
                "39.95",
                12,
                ropa
        );
        asegurarProducto(
                null,
                "Taza grimorio de Frieren",
                "Taza ceramica de 350 ml con ilustracion magica y caja para regalo.",
                "12.95",
                15,
                tazas
        );
        asegurarProducto(
                null,
                "Wall scroll de Naruto y Sasuke",
                "Lienzo textil vertical para pared con barras negras y cuerda incluida.",
                "22.50",
                9,
                posters
        );
        asegurarProducto(
                null,
                "Pack de pins Fullmetal legacy",
                "Set de 4 pins metalicos inspirados en alquimia, armaduras y simbolos clasicos.",
                "14.90",
                11,
                ropa
        );
        asegurarProducto(
                null,
                "Figura Fern spellcaster edition",
                "Figura de sobremesa con capa violeta, peana translucida y detalle de hechizo.",
                "34.95",
                6,
                figurasEscala
        );
        asegurarProducto(
                null,
                "Artbook heroes shonen selection",
                "Compendio visual con ilustraciones de series de aventuras, combate y fantasia.",
                "27.80",
                5,
                coleccionista
        );

        asegurarCliente(
                "cliente.demo.20260403@otaku.local",
                "Jon",
                "Salchichon Ranbo",
                "600123123",
                "Avenida Kame House 7"
        );
        asegurarCliente(null, "Tomas", "Turbado", "611221144", "Calle Villa Vicio 14");
        asegurarCliente(null, "Johnny", "Tecuento", "622334455", "Plaza Namek 3");
        asegurarCliente(null, "Lola", "Mento Fresco", "633445566", "Calle Hoja Oculta 12");
        asegurarCliente(null, "Elsa", "Pato del Bosque", "644556677", "Camino de los Elfos 19");
        asegurarCliente(null, "Harry", "Plotter", "655667788", "Pasaje Resen 5");
        asegurarCliente(null, "Rosa", "Melano", "666778899", "Avenida Central Dogma 8");
        asegurarCliente(null, "Paco", "Tepilla", "677889900", "Calle Trigun 21");
        asegurarCliente(null, "Sara", "Magozza", "688990011", "Bulevar Alquimia 4");
        asegurarCliente(null, "Nora", "Maki Roll", "699001122", "Calle Udon 10");
    }

    private Categoria asegurarCategoria(String nombreAntiguo, String nombreNuevo) {
        Optional<Categoria> categoriaExistente = categoriaDao.findByNombre(nombreNuevo);
        if (categoriaExistente.isPresent()) {
            return categoriaExistente.get();
        }

        Categoria categoria = buscarCategoriaPlaceholder(nombreAntiguo).orElseGet(Categoria::new);
        categoria.setNombre(nombreNuevo);
        aplicarAuditoria(categoria.getFechaCreacion() == null, categoria);
        return categoriaDao.save(categoria);
    }

    private Optional<Categoria> buscarCategoriaPlaceholder(String nombreAntiguo) {
        if (nombreAntiguo == null || nombreAntiguo.trim().isEmpty()) {
            return Optional.empty();
        }
        return categoriaDao.findByNombre(nombreAntiguo);
    }

    private void asegurarProducto(
            String nombreAntiguo,
            String nombreNuevo,
            String descripcion,
            String precio,
            int stock,
            Categoria categoria
    ) {
        if (productoDao.findByNombre(nombreNuevo).isPresent()) {
            return;
        }

        Producto producto = buscarProductoPlaceholder(nombreAntiguo).orElseGet(Producto::new);
        producto.setNombre(nombreNuevo);
        producto.setDescripcion(descripcion);
        producto.setPrecio(new BigDecimal(precio));
        producto.setStock(stock);
        producto.setCategoria(categoria);
        aplicarAuditoria(producto.getFechaCreacion() == null, producto);
        productoDao.save(producto);
    }

    private Optional<Producto> buscarProductoPlaceholder(String nombreAntiguo) {
        if (nombreAntiguo == null || nombreAntiguo.trim().isEmpty()) {
            return Optional.empty();
        }
        return productoDao.findByNombre(nombreAntiguo);
    }

    private void asegurarCliente(
            String emailAntiguo,
            String nombre,
            String apellidos,
            String telefono,
            String direccion
    ) {
        String email = construirEmail(nombre, apellidos);
        if (clienteDao.findByEmail(email).isPresent()) {
            return;
        }

        Cliente cliente = buscarClientePlaceholder(emailAntiguo).orElseGet(Cliente::new);
        cliente.setNombre(nombre);
        cliente.setApellidos(apellidos);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        aplicarAuditoria(cliente.getFechaCreacion() == null, cliente);
        clienteDao.save(cliente);
    }

    private Optional<Cliente> buscarClientePlaceholder(String emailAntiguo) {
        if (emailAntiguo == null || emailAntiguo.trim().isEmpty()) {
            return Optional.empty();
        }
        return clienteDao.findByEmail(emailAntiguo);
    }

    private String construirEmail(String nombre, String apellidos) {
        String base = TextoNormalizador.normalizarParaEmail(nombre + "." + apellidos);
        return base + "@jhnexusanime.local";
    }

    private void aplicarAuditoria(boolean nuevo, Object entidad) {
        LocalDateTime ahora = LocalDateTime.now();
        if (entidad instanceof Categoria) {
            Categoria categoria = (Categoria) entidad;
            if (nuevo) {
                categoria.setFechaCreacion(ahora);
                categoria.setCreadoPor(ACTOR);
            }
            categoria.setFechaActualizacion(ahora);
            categoria.setActualizadoPor(ACTOR);
            return;
        }

        if (entidad instanceof Producto) {
            Producto producto = (Producto) entidad;
            if (nuevo) {
                producto.setFechaCreacion(ahora);
                producto.setCreadoPor(ACTOR);
            }
            producto.setFechaActualizacion(ahora);
            producto.setActualizadoPor(ACTOR);
            return;
        }

        if (entidad instanceof Cliente) {
            Cliente cliente = (Cliente) entidad;
            if (nuevo) {
                cliente.setFechaCreacion(ahora);
                cliente.setCreadoPor(ACTOR);
            }
            cliente.setFechaActualizacion(ahora);
            cliente.setActualizadoPor(ACTOR);
        }
    }
}
