package detubarrio.rest.service;

import detubarrio.rest.dto.ComercioDetailResponse;
import detubarrio.rest.dto.ComercioRequest;
import detubarrio.rest.dto.ComercioSummaryResponse;
import detubarrio.rest.dto.ProductoComercioRequest;
import detubarrio.rest.dto.ProductoComercioResponse;
import detubarrio.rest.dto.ResenaResponse;
import detubarrio.rest.model.Categoria;
import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.ComercioProducto;
import detubarrio.rest.model.EstadoComercio;
import detubarrio.rest.model.Producto;
import detubarrio.rest.repository.CategoriaRepository;
import detubarrio.rest.repository.ComercioProductoRepository;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.ProductoRepository;   
import detubarrio.rest.repository.ResenaRepository;

// CORRECCIÓN DE IMPORTS:
import java.io.IOException; // Usar el de Java estándar, no el de JWT
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jakarta.persistence.EntityNotFoundException;
// Eliminados: Path de criterios y Paths de Swagger que daban conflicto

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComercioService {

    private final String UPLOAD_DIR = "uploads/";   

    @Autowired
    private ComercioRepository comercioRepository;

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private ComercioProductoRepository comercioProductoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ComercioSummaryResponse> listarComercios(Optional<Long> categoriaId) {
        List<Comercio> comercios = categoriaId
                .map(id -> comercioRepository.findByCategoriaIdAndEstado(id, EstadoComercio.APROBADO))
                .orElseGet(() -> comercioRepository.findByEstado(EstadoComercio.APROBADO));

        comercios = comercios.stream()
                .filter(comercio -> comercio.getEstado() == EstadoComercio.APROBADO && comercio.isGestionAutorizada())
                .toList();

        return comercios.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Transactional // <- No olvides añadir esto arriba del método
    public ComercioDetailResponse actualizarConFotos(Long id, String nombre, String descripcion, 
                                                        String horario, String diasApertura, 
                                                        MultipartFile logo, MultipartFile banner) {
        
        Comercio comercio = comercioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));

        comercio.setNombreComercio(nombre);
        comercio.setDescripcion(descripcion);
        comercio.setHorario(horario);
        comercio.setDiasApertura(diasApertura);

        try {
                if (logo != null && !logo.isEmpty()) {
                String nombreLogo = guardarArchivo(logo);
                comercio.setLogo("/uploads/" + nombreLogo);
                }
                if (banner != null && !banner.isEmpty()) {
                String nombreBanner = guardarArchivo(banner);
                comercio.setBanner("/uploads/" + nombreBanner);
                }
        } catch (IOException e) {
                throw new RuntimeException("Error al guardar las imágenes", e);
        }

        comercio = comercioRepository.save(comercio);
        
        return toDetailResponse(comercio); 
    }

    private String guardarArchivo(MultipartFile archivo) throws IOException {
        Path root = Paths.get(UPLOAD_DIR);

        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        String nombreOriginal = archivo.getOriginalFilename();
        String extension = "";
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }
        
        String nombreUnico = UUID.randomUUID().toString() + extension;
        Path destino = root.resolve(nombreUnico);

        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return nombreUnico;
    }

    @Transactional(readOnly = true)
    public ComercioDetailResponse obtenerComercio(Long comercioId) {
        Comercio comercio = comercioRepository.findById(comercioId)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));

        if (comercio.getEstado() != EstadoComercio.APROBADO || !comercio.isGestionAutorizada()) {
            throw new RuntimeException("Comercio no disponible");
        }

        return toDetailResponse(comercio);
    }

    public ComercioSummaryResponse crearComercio(ComercioRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
                
        Comercio comercio = Comercio.builder()
                .nombreComercio(request.nombreComercio())
                .descripcion(request.descripcion())
                .horario(request.horario())
                .diasApertura(request.diasApertura())
                .logo(request.logo())
                .banner(request.banner())
                .categoria(categoria)
                .estado(EstadoComercio.PENDIENTE) // Importante: por defecto al crear
                .gestionAutorizada(true)
                .build();
                
        comercio = comercioRepository.save(comercio);
        return toSummaryResponse(comercio);
    }

    @Transactional
    public ComercioDetailResponse actualizarComercio(Long comercioId, ComercioRequest request) {
        Comercio comercio = comercioRepository.findById(comercioId)
            .orElseThrow(() -> new EntityNotFoundException("No existe el comercio con id " + comercioId));

        // CORRECCIÓN: Usar métodos del Record (sin "get")
        comercio.setNombreComercio(request.nombreComercio());
        comercio.setDescripcion(request.descripcion());
        comercio.setHorario(request.horario());
        comercio.setDiasApertura(request.diasApertura());
        comercio.setLogo(request.logo());
        comercio.setBanner(request.banner());

        if (request.categoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
            comercio.setCategoria(categoria);
        }

        comercio = comercioRepository.save(comercio);
        return toDetailResponse(comercio); // Usamos tu conversor completo
    }

    @Transactional(readOnly = true)
    public ComercioDetailResponse obtenerComercioPorUsuario(Long usuarioId) {
        Comercio comercio = comercioRepository.findByUsuarioCreadorId(usuarioId)
            .orElseThrow(() -> new EntityNotFoundException("Este usuario no tiene un comercio asignado"));
        
        return toDetailResponse(comercio); // Usamos tu conversor completo
    }

    @Transactional(readOnly = true)
    public List<ProductoComercioResponse> obtenerProductosComercio(Long comercioId) {
        Comercio comercio = comercioRepository.findById(comercioId)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));
                
        return comercio.getComercioProductos().stream()
                .map(cp -> new ProductoComercioResponse(
                        cp.getProducto().getId(),
                        cp.getProducto().getNombreProducto(),
                        cp.getProducto().getDescripcion(),
                        cp.getPrecio(),
                        cp.getStock(),
                        cp.getProducto().getImagen()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoComercioResponse agregarProductoAComercio(Long comercioId, ProductoComercioRequest request) {
        Comercio comercio = comercioRepository.findById(comercioId)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));
                
        Producto producto = Producto.builder()
                .nombreProducto(request.nombreProducto())
                .descripcion(request.descripcion())
                .imagen(request.imagen())
                .build();
        producto = productoRepository.save(producto);
        
        ComercioProducto cp = ComercioProducto.builder()
                .comercio(comercio)
                .producto(producto)
                .precio(request.precio())
                .stock(request.stock())
                .build();
        cp = comercioProductoRepository.save(cp);
        
        return new ProductoComercioResponse(
                cp.getProducto().getId(),
                cp.getProducto().getNombreProducto(),
                cp.getProducto().getDescripcion(),
                cp.getPrecio(),
                cp.getStock(),
                cp.getProducto().getImagen()
        );
    }

    private ComercioSummaryResponse toSummaryResponse(Comercio comercio) {
        Double puntuacionMedia = resenaRepository.findAverageValoracionByComercioId(comercio.getId());
        Long totalResenas = resenaRepository.countByComercioId(comercio.getId());

        return new ComercioSummaryResponse(
                comercio.getId(),
                comercio.getNombreComercio(),
                comercio.getDescripcion(),
                comercio.getHorario(),
                comercio.getDiasApertura(),
                comercio.getLogo(),
                comercio.getCategoria() != null ? comercio.getCategoria().getNombreCategoria() : "Sin categoría",
                puntuacionMedia != null ? puntuacionMedia : 0.0,
                totalResenas != null ? totalResenas : 0L
        );
    }

    private ComercioDetailResponse toDetailResponse(Comercio comercio) {
        Double puntuacionMedia = resenaRepository.findAverageValoracionByComercioId(comercio.getId());
        Long totalResenas = resenaRepository.countByComercioId(comercio.getId());

        List<ProductoComercioResponse> productos = (comercio.getComercioProductos() != null) ? 
            comercio.getComercioProductos().stream()
                .map(cp -> new ProductoComercioResponse(
                        cp.getProducto().getId(),
                        cp.getProducto().getNombreProducto(),
                        cp.getProducto().getDescripcion(),
                        cp.getPrecio(),
                        cp.getStock(),
                        cp.getProducto().getImagen()
                ))
                .collect(Collectors.toList()) : List.of();

        List<ResenaResponse> resenas = (comercio.getResenas() != null) ?
            comercio.getResenas().stream()
                .map(r -> new ResenaResponse(
                        r.getId(),
                        r.getTitulo(),
                        r.getComentario(),
                        r.getValoracion(),
                        r.getAutorNombre(),
                        r.getAutorEmail(),
                        r.getFecha()
                ))
                .collect(Collectors.toList()) : List.of();

        return new ComercioDetailResponse(
                comercio.getId(),
                comercio.getNombreComercio(),
                comercio.getDescripcion(),
                comercio.getHorario(),
                comercio.getDiasApertura(),
                comercio.getLogo(),
                comercio.getBanner(),
                comercio.getCategoria() != null ? comercio.getCategoria().getNombreCategoria() : "Sin categoría",
                puntuacionMedia != null ? puntuacionMedia : 0.0,
                totalResenas != null ? totalResenas : 0L,
                productos,
                resenas
        );
    }
    
}