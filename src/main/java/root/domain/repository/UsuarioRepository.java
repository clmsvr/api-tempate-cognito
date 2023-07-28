package root.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import root.domain.model.UsuarioInterno;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioInterno, Long>{
	
	Optional<UsuarioInterno> findByOidcid(String openid);
}
