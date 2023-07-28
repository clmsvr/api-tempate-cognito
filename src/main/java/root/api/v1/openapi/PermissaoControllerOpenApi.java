package root.api.v1.openapi;

import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@SecurityRequirement(name = "security_auth") //26.5
@Tag(name = "Permissão", description = "Gerencia Permissões")
public interface PermissaoControllerOpenApi {

	List<String> listar();
}