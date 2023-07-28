package root.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import root.domain.model.Grupo;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, String>{
	
}
