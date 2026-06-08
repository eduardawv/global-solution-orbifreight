package br.com.fiap.orbifreight.dtos;

import java.time.LocalDateTime;

public record AlertaResponseDTO(
        Long id,
        Long cargaId,
        String titulo,
        String descricao,
        String nivel,
        String status,
        LocalDateTime dataCriacao
) {}