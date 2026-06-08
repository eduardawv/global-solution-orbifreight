package br.com.fiap.orbifreight.controllers;

import br.com.fiap.orbifreight.dtos.CargaRequestDTO;
import br.com.fiap.orbifreight.dtos.CargaResponseDTO;
import br.com.fiap.orbifreight.services.CargaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cargas") // URL base para todos os endpoints desta classe
public class CargaController {

    @Autowired
    private CargaService cargaService;

    // POST: Criar uma nova carga
    @PostMapping
    public ResponseEntity<CargaResponseDTO> criar(@Valid @RequestBody CargaRequestDTO request) {
        CargaResponseDTO response = cargaService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET: Listar todas as cargas
    @GetMapping
    public ResponseEntity<List<CargaResponseDTO>> listar() {
        List<CargaResponseDTO> response = cargaService.listarTodas();
        return ResponseEntity.ok(response);
    }

    // GET: Buscar carga específica por ID
    @GetMapping("/{id}")
    public ResponseEntity<CargaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cargaService.buscarPorId(id));
    }

    // PUT: Atualizar carga por ID
    @PutMapping("/{id}")
    public ResponseEntity<CargaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody CargaRequestDTO request) {
        return ResponseEntity.ok(cargaService.atualizar(id, request));
    }

    // DELETE: Eliminar carga por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        cargaService.excluir(id);
        return ResponseEntity.noContent().build(); // Retorna o HTTP Status 204 No Content
    }
}