package br.com.fiap.orbifreight.repositories;

import br.com.fiap.orbifreight.models.TipoCarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoCargaRepository extends JpaRepository<TipoCarga, Long> {
}