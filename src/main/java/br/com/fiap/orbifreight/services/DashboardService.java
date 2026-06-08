package br.com.fiap.orbifreight.services;

import br.com.fiap.orbifreight.dtos.DashboardResponseDTO;
import br.com.fiap.orbifreight.repositories.AlertaRepository;
import br.com.fiap.orbifreight.repositories.CargaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private CargaRepository cargaRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    public DashboardResponseDTO obterEstatisticas() {
        // Usa o count() padrão do JPA para contar tudo
        long totalCargas = cargaRepository.count();

        // Assume que as cargas em andamento têm o status "ATIVA" ou "EM_TRANSITO"
        // (Pode alterar esta palavra se estiver a usar outra no seu React Native)
        long cargasAtivas = cargaRepository.countByStatus("ATIVA");

        long alertasAbertos = alertaRepository.countByStatus("ABERTO");
        long alertasCriticos = alertaRepository.countByNivelAndStatus("CRITICO", "ABERTO");

        return new DashboardResponseDTO(
                (int) totalCargas,
                (int) cargasAtivas,
                (int) alertasAbertos,
                (int) alertasCriticos
        );
    }
}