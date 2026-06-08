package br.com.fiap.orbifreight.dtos;

public record DashboardResponseDTO(
        Integer totalCargas,
        Integer cargasAtivas,
        Integer alertasAbertos,
        Integer alertasCriticos
) {}