package root.api.v1.openapi;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import root.api.v1.model.GrupoModel;
import root.api.v1.model.input.GrupoInput;

@SecurityRequirement(name = "security_auth") //26.5
@Tag(name = "Grupo", description = "Gerencia Grupos")
public interface GrupoControllerOpenApi {

	List<GrupoModel> listar();

	GrupoModel buscar(String grupoId);

	void adicionar(GrupoInput grupoInput);

	void remover(String grupoId);

	ResponseEntity<Void> desassociar(String grupoId, String permissaoId);

	ResponseEntity<Void> associar(String grupoId, String permissaoId);
}