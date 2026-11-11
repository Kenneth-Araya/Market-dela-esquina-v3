package cl.duoc.sistemaMarket.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.sistemaMarket.dto.EstadoRequest;
import cl.duoc.sistemaMarket.model.MensajeContacto;
import cl.duoc.sistemaMarket.service.ContactoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contactos")
@RequiredArgsConstructor
public class ContactoController {

    private final ContactoService contactoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MensajeContacto enviar(@RequestBody MensajeContacto mensaje) {
        return contactoService.enviar(mensaje);
    }

    @GetMapping
    public List<MensajeContacto> listar() {
        return contactoService.listar();
    }

    @GetMapping("/{id}")
    public MensajeContacto buscarPorId(@PathVariable Long id) {
        return contactoService.buscarPorId(id);
    }

    @PutMapping("/{id}/estado")
    public MensajeContacto cambiarEstado(@PathVariable Long id, @RequestBody EstadoRequest request) {
        return contactoService.cambiarEstado(id, request.getEstadoContacto());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        contactoService.eliminar(id);
    }
}
