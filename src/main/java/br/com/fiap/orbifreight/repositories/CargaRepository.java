package br.com.fiap.orbifreight.repositories;

import br.com.fiap.orbifreight.models.Carga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargaRepository extends JpaRepository<Carga, Long> {

    long countByStatus(String status);
}
