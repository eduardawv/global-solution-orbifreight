package br.com.fiap.orbifreight.controllers;

import br.com.fiap.orbifreight.dtos.AlertaRequestDTO;
import br.com.fiap.orbifreight.dtos.AlertaResponseDTO;
import br.com.fiap.orbifreight.services.AlertaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    @PostMapping
    public ResponseEntity<AlertaResponseDTO> criar(@Valid @RequestBody AlertaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertaService.salvar(request));
    }

    @GetMapping
    public ResponseEntity<List<AlertaResponseDTO>> listar() {
        return ResponseEntity.ok(alertaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AlertaRequestDTO request) {
        return ResponseEntity.ok(alertaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        alertaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}