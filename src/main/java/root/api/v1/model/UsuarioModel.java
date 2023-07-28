package root.api.v1.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioModel{

	private String oidcid;
	private String email;
	private String nome;
	//private String status;
	private OffsetDateTime dataCadastro;
	//private OffsetDateTime dataAtualizacao;
	
	private List<GrupoModel> grupos = new ArrayList<>();
}