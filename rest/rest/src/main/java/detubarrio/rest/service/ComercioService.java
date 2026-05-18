package detubarrio.rest.service;

import detubarrio.rest.dto.*;
import detubarrio.rest.model.*;
import detubarrio.rest.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComercioService {

    private final String UPLOAD_DIR = "uploads/";

    @Autowired private ComercioRepository comercioRepository;
    @Autowired private ResenaRepository resenaRepository;
    @Autowired private ComercioProductoRepository comercioProductoRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    

    @Transactional(readOnly = true)
    public List<ComercioSummaryResponse> listarComercios(Optional<Long> categoriaId) {
        List<Comercio> comercios = categoriaId
                .map(id -> comercioRepository.findByCategoriaIdAndEstado(id, EstadoComercio.APROBADO))
                .orElseGet(() -> comercioRepository.findByEstado(EstadoComercio.APROBADO));

        return comercios.stream()
                .filter(c -> c.isGestionAutorizada())
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComercioDetailResponse obtenerComercio(Long id) {
        Comercio comercio = comercioRepository.findByIdWithDisponibilidades(id)
                .orElseThrow(() -> new EntityNotFoundException("Comercio no encontrado"));
        return toDetailResponse(comercio);
    }

    @Transactional(readOnly = true)
    public ComercioDetailResponse obtenerComercioPorUsuario(Long usuarioId) {
        Comercio comercio = comercioRepository.findByUsuarioCreadorId(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Comercio no encontrado para este usuario"));
        return toDetailResponse(comercio);
    }

    @Transactional
    public ComercioSummaryResponse crearComercio(ComercioRequest request) {
        Categoria cat = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Comercio comercio = Comercio.builder()
                .nombreComercio(request.nombreComercio())
                .descripcion(request.descripcion())
                .categoria(cat)
                .estado(EstadoComercio.PENDIENTE)
                .gestionAutorizada(true)
                .build();
        return toSummaryResponse(comercioRepository.save(comercio));
    }

    @Transactional
    public ComercioDetailResponse actualizarConFotos(Long id, String nombre, String descripcion, String horario, String diasApertura, MultipartFile logo, MultipartFile banner) {
        Comercio comercio = comercioRepository.findById(id).orElseThrow(() -> new RuntimeException("No encontrado"));
        comercio.setNombreComercio(nombre);
        comercio.setDescripcion(descripcion);
        comercio.setHorario(horario);
        comercio.setDiasApertura(diasApertura);
        try {
            if (logo != null && !logo.isEmpty()) comercio.setLogo("/uploads/" + guardarArchivo(logo));
            if (banner != null && !banner.isEmpty()) comercio.setBanner("/uploads/" + guardarArchivo(banner));
        } catch (IOException e) { throw new RuntimeException(e); }
        return toDetailResponse(comercioRepository.save(comercio));
    }

    @Transactional(readOnly = true)
    public List<ProductoComercioResponse> obtenerProductosComercio(Long comercioId) {
        Comercio comercio = comercioRepository.findById(comercioId).orElseThrow(() -> new RuntimeException("No encontrado"));
        return comercio.getComercioProductos().stream()
                .map(cp -> new ProductoComercioResponse(
                    cp.getProducto().getId(), 
                    cp.getProducto().getNombreProducto(), 
                    cp.getProducto().getDescripcion(), 
                    cp.getPrecio(), 
                    cp.getProducto().getImagen()))
                .toList();
    }

    @Transactional
    public ProductoComercioResponse agregarProductoAComercio(Long comercioId, ProductoComercioRequest request) {
        Comercio comercio = comercioRepository.findById(comercioId).orElseThrow(() -> new RuntimeException("No encontrado"));
        Producto p = productoRepository.save(Producto.builder()
                .nombreProducto(request.nombreProducto())
                .descripcion(request.descripcion())
                .imagen(request.imagen())
                .build());
        ComercioProducto cp = comercioProductoRepository.save(ComercioProducto.builder()
                .comercio(comercio)
                .producto(p)
                .precio(request.precio())
                .stock(request.stock())
                .build());
        return new ProductoComercioResponse(p.getId(), p.getNombreProducto(), p.getDescripcion(), cp.getPrecio(), p.getImagen());
    }

    private String guardarArchivo(MultipartFile archivo) throws IOException {
        Path root = Paths.get(UPLOAD_DIR);
        if (!Files.exists(root)) Files.createDirectories(root);
        String nombreUnico = UUID.randomUUID().toString() + (archivo.getOriginalFilename().contains(".") ? archivo.getOriginalFilename().substring(archivo.getOriginalFilename().lastIndexOf(".")) : "");
        Files.copy(archivo.getInputStream(), root.resolve(nombreUnico), StandardCopyOption.REPLACE_EXISTING);
        return nombreUnico;
    }

    private ComercioSummaryResponse toSummaryResponse(Comercio c) {
        return new ComercioSummaryResponse(c.getId(), c.getNombreComercio(), c.getDescripcion(), c.getHorario(), c.getDiasApertura(), c.getLogo(), c.getCategoria() != null ? c.getCategoria().getNombreCategoria() : "Sin categoría", 0.0, 0L);
    }

    private ComercioDetailResponse toDetailResponse(Comercio c) {
        // 1. Mapear Disponibilidades
        List<DisponibilidadResponse> disps = (c.getDisponibilidades() != null) ?
            c.getDisponibilidades().stream()
                .map(d -> new DisponibilidadResponse(d.getId(), d.getFecha(), d.getHoraInicio(), d.getHoraFin(), d.isReservado()))
                .toList() : List.of();
        
        // 2. Mapear Productos
        List<ProductoComercioResponse> productos = (c.getComercioProductos() != null) ?
            c.getComercioProductos().stream()
                .map(cp -> new ProductoComercioResponse(
                    cp.getProducto().getId(), 
                    cp.getProducto().getNombreProducto(), 
                    cp.getProducto().getDescripcion(), 
                    cp.getPrecio(), 
                    cp.getProducto().getImagen()))
                .toList() : List.of();

        // 3. Mapear Reseñas
        List<ResenaResponse> resenas = (c.getResenas() != null) ?
            c.getResenas().stream()
                .map(r -> new ResenaResponse(
                    r.getId(), r.getTitulo(), r.getComentario(), r.getValoracion(), 
                    r.getAutorNombre(), r.getAutorEmail(), r.getFecha()))
                .toList() : List.of();

        return new ComercioDetailResponse(
            c.getId(),
            c.getNombreComercio(),
            c.getDescripcion(),
            c.getHorario(),
            c.getDiasApertura(),
            c.getLogo(),
            c.getBanner(),
            c.getCategoria() != null ? c.getCategoria().getNombreCategoria() : "Sin categoría",
            0.0, 
            0L,  
            productos,
            resenas,
            disps // <--- Ahora se llama 'disps' y coincide con el record
        );
    }
}