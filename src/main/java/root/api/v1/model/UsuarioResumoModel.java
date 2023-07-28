package root.api.v1.model;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResumoModel{

	private String oidcid;
	private String email;
	private String nome;
	private String status;
	private OffsetDateTime dataCadastro;
}