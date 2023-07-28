package root.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import root.domain.model.Permissao;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, String>{

}
