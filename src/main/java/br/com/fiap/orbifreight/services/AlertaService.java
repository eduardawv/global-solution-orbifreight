package br.com.fiap.orbifreight.services;

import br.com.fiap.orbifreight.dtos.AlertaRequestDTO;
import br.com.fiap.orbifreight.dtos.AlertaResponseDTO;
import br.com.fiap.orbifreight.models.Alerta;
import br.com.fiap.orbifreight.models.Carga;
import br.com.fiap.orbifreight.repositories.AlertaRepository;
import br.com.fiap.orbifreight.repositories.CargaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private CargaRepository cargaRepository;

    @Transactional
    public AlertaResponseDTO salvar(AlertaRequestDTO request) {
        Carga carga = cargaRepository.findById(request.cargaId())
                .orElseThrow(() -> new RuntimeException("Carga não encontrada com o ID: " + request.cargaId()));

        Alerta alerta = new Alerta();
        alerta.setCarga(carga);
        alerta.setTitulo(request.titulo());
        alerta.setDescricao(request.descricao());
        alerta.setNivel(request.nivel());
        alerta.setStatus(request.status());

        Alerta alertaSalvo = alertaRepository.save(alerta);
        return converterParaDTO(alertaSalvo);
    }

    public List<AlertaResponseDTO> listarTodos() {
        return alertaRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public AlertaResponseDTO buscarPorId(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado com o ID: " + id));
        return converterParaDTO(alerta);
    }
    @Transactional
    public AlertaResponseDTO atualizar(Long id, AlertaRequestDTO request) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado com o ID: " + id));

        Carga carga = cargaRepository.findById(request.cargaId())
                .orElseThrow(() -> new RuntimeException("Carga não encontrada com o ID: " + request.cargaId()));

        alerta.setCarga(carga);
        alerta.setTitulo(request.titulo());
        alerta.setDescricao(request.descricao());
        alerta.setNivel(request.nivel());
        alerta.setStatus(request.status());

        Alerta alertaAtualizado = alertaRepository.save(alerta);
        return converterParaDTO(alertaAtualizado);
    }

    public void excluir(Long id) {
        if (!alertaRepository.existsById(id)) {
            throw new RuntimeException("Alerta não encontrado com o ID: " + id);
        }
        alertaRepository.deleteById(id);
    }

    private AlertaResponseDTO converterParaDTO(Alerta alerta) {
        return new AlertaResponseDTO(
                alerta.getId(),
                alerta.getCarga().getId(),
                alerta.getTitulo(),
                alerta.getDescricao(),
                alerta.getNivel(),
                alerta.getStatus(),
                alerta.getDataCriacao()
        );
    }
}