package detubarrio.rest.controller;

import detubarrio.rest.dto.*;
import detubarrio.rest.service.ComercioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comercios")
@RequiredArgsConstructor // Esto sustituye los @Autowired y limpia el código
public class ComercioController {

    private final ComercioService comercioService;

    @GetMapping
    public ResponseEntity<List<ComercioSummaryResponse>> listarComercios(@RequestParam Optional<Long> categoriaId) {
        return ResponseEntity.ok(comercioService.listarComercios(categoriaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComercioDetailResponse> obtenerComercio(@PathVariable Long id) {
        return ResponseEntity.ok(comercioService.obtenerComercio(id));
    }

    @PutMapping("/{id}/fotos")
    public ResponseEntity<ComercioDetailResponse> actualizarConFotos(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam String horario,
            @RequestParam String diasApertura,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) MultipartFile logo,
            @RequestParam(required = false) MultipartFile banner) {
        
        // 🛠️ CORREGIDO: Se añade 'ubicacion' en el orden correcto dentro de los argumentos
        ComercioDetailResponse actualizado = comercioService.actualizarConFotos(
                id, nombre, descripcion, horario, diasApertura, ubicacion, logo, banner);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ComercioDetailResponse> obtenerComercioPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(comercioService.obtenerComercioPorUsuario(usuarioId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComercioSummaryResponse crearComercio(@Valid @RequestBody ComercioRequest request) {
        return comercioService.crearComercio(request);
    }

    @GetMapping("/{comercioId}/productos")
    public ResponseEntity<List<ProductoComercioResponse>> listarProductosComercio(@PathVariable Long comercioId) {
        return ResponseEntity.ok(comercioService.obtenerProductosComercio(comercioId));
    }

    @PostMapping("/{comercioId}/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoComercioResponse agregarProductoAComercio(
            @PathVariable Long comercioId,
            @Valid @RequestBody ProductoComercioRequest request) {
        return comercioService.agregarProductoAComercio(comercioId, request);
    }

    @PostMapping("/{comercioId}/resenas")
    public ResponseEntity<?> agregarResena(
            @PathVariable Long comercioId,
            @Valid @RequestBody ResenaRequest request, // Asegura que esto sea lo ÚNICO que llega
            Authentication authentication) {
        
        try {
            String emailUsuarioLogueado = authentication.getName();
            ResenaResponse nuevaResena = comercioService.agregarResenaAComercio(comercioId, request, emailUsuarioLogueado);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaResena);
        } catch (Exception e) {
            // Esto te dirá exactamente el error en el log de tu servidor
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}