package br.com.fiap.orbifreight.repositories;

import br.com.fiap.orbifreight.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring Security vai usar isto para encontrar o utilizador pelo email
    UserDetails findByEmail(String email);
}