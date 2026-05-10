package detubarrio.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Usamos el asterisco para limpiar las imports de anotaciones
import org.springframework.web.multipart.MultipartFile;

import detubarrio.rest.dto.ComercioDetailResponse;
import detubarrio.rest.dto.ComercioRequest;
import detubarrio.rest.dto.ComercioSummaryResponse;
import detubarrio.rest.dto.ProductoComercioRequest;
import detubarrio.rest.dto.ProductoComercioResponse;
import detubarrio.rest.model.Comercio;
import detubarrio.rest.service.ComercioService;
import org.springframework.http.MediaType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comercios")
@RequiredArgsConstructor
public class ComercioController {

    private final ComercioService comercioService;

    @GetMapping
    public List<ComercioSummaryResponse> listarComercios(@RequestParam Optional<Long> categoriaId) {
        return comercioService.listarComercios(categoriaId);
    }

    @GetMapping("/{comercioId}")
    public ComercioDetailResponse obtenerComercio(@PathVariable Long comercioId) {
        return comercioService.obtenerComercio(comercioId);
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComercioDetailResponse> actualizarComercio(
        @PathVariable Long id,
        @RequestParam("nombreComercio") String nombre,
        @RequestParam("descripcion") String descripcion,
        @RequestParam("horario") String horario,
        @RequestParam("diasApertura") String diasApertura,
        @RequestParam(value = "logoFile", required = false) MultipartFile logo,
        @RequestParam(value = "bannerFile", required = false) MultipartFile banner
    ) {

        ComercioDetailResponse actualizado = comercioService.actualizarConFotos(id, nombre, descripcion, horario, diasApertura, logo, banner);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ComercioDetailResponse obtenerComercioPorUsuario(@PathVariable Long usuarioId) {
        return comercioService.obtenerComercioPorUsuario(usuarioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComercioSummaryResponse crearComercio(@Valid @RequestBody ComercioRequest request) {
        return comercioService.crearComercio(request);
    }

    @GetMapping("/{comercioId}/productos")
    public List<ProductoComercioResponse> listarProductosComercio(@PathVariable Long comercioId) {
        return comercioService.obtenerProductosComercio(comercioId);
    }

    @PostMapping("/{comercioId}/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoComercioResponse agregarProductoAComercio(
        @PathVariable Long comercioId,
        @Valid @RequestBody ProductoComercioRequest request
    ) {
        return comercioService.agregarProductoAComercio(comercioId, request);
    }
}