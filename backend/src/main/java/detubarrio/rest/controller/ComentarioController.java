package detubarrio.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import detubarrio.rest.dto.ComentarioRequest;
import detubarrio.rest.dto.ResenaRequest;
import detubarrio.rest.dto.ResenaResponse;
import detubarrio.rest.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ResenaService resenaService;

    // Se añade el {comercioId} a la ruta para que @PathVariable funcione
    @PostMapping("/{comercioId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResenaResponse crearComentario(
        @PathVariable Long comercioId,
        @Valid @RequestBody ResenaRequest request // Cambiado de ComentarioRequest a ResenaRequest
    ) {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Ya no necesitas hacer el 'new ResenaRequest(...)', usa 'request' directamente
        return resenaService.crearResena(comercioId, request, emailUsuario);
    }
}