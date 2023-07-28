package root.domain.model;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UsuarioOidc {

	@EqualsAndHashCode.Include
	private String oidcid;
	private String email;
	private String nome;
	private String status;
	private OffsetDateTime dataCadastro;
	private OffsetDateTime dataAtualizacao;
	
	private UsuarioInterno usuarioInterno;
}