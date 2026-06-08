package br.com.fiap.orbifreight.repositories;

import br.com.fiap.orbifreight.models.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    long countByStatus(String status);

    long countByNivelAndStatus(String nivel, String status);
}