package root.api.v1.model.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioInputCreate {

	@NotBlank
	private String nome;
	@NotBlank
	@Email
	private String email;
	
	//@Nullable
	//private String senhaTemporaria;
	//@Nullable
	//private Boolean enviarEmail;
	
}