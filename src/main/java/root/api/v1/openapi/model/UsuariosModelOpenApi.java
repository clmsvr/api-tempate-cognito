package root.api.v1.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import root.api.v1.model.UsuarioModel;

@Schema(name = "UsuariosModelPage")
public class UsuariosModelOpenApi extends PagedModelOpenApi<UsuarioModel> {
	
}