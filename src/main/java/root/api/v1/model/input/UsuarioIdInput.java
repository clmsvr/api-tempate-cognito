package root.api.v1.model.input;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsuarioIdInput {

	@NotNull
	private Long id;
	
}
