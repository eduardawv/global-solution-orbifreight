package br.com.fiap.orbifreight.controllers;

import br.com.fiap.orbifreight.dtos.TipoCargaRequestDTO;
import br.com.fiap.orbifreight.dtos.TipoCargaResponseDTO;
import br.com.fiap.orbifreight.services.TipoCargaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-carga")
public class TipoCargaController {

    @Autowired
    private TipoCargaService tipoCargaService;

    @PostMapping
    public ResponseEntity<TipoCargaResponseDTO> criar(@Valid @RequestBody TipoCargaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoCargaService.salvar(request));
    }

    @GetMapping
    public ResponseEntity<List<TipoCargaResponseDTO>> listar() {
        return ResponseEntity.ok(tipoCargaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoCargaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoCargaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoCargaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TipoCargaRequestDTO request) {
        return ResponseEntity.ok(tipoCargaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        tipoCargaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}