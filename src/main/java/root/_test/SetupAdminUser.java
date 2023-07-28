package root._test;

import java.util.Optional;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import root.core.aws.Cognito;
import root.domain.model.Grupo;
import root.domain.model.UsuarioInterno;
import root.domain.model.UsuarioOidc;
import root.domain.repository.GrupoRepository;
import root.domain.repository.UsuarioRepository;

@Component
@Profile("default")
public class SetupAdminUser {

	
	public SetupAdminUser(UsuarioRepository urep, GrupoRepository grep, Cognito cognito, Flyway flyway) 
	{
		setup(urep,grep,cognito);
	}
	
	@Transactional
	private void setup(UsuarioRepository urep, GrupoRepository grep, Cognito cognito) {
	
		UsuarioOidc user = cognito.getUser("userdevtemp+gerente@gmail.com");
		
		UsuarioInterno ui = new UsuarioInterno();
		ui.setEmail(user.getEmail());
		ui.setNome(user.getNome());
		ui.setOidcid(user.getOidcid());
		
		Optional<Grupo> g = grep.findById("gerente");
		if (g.isPresent())
			ui.adicionarGrupo(g.get());
		
		urep.save(ui);
	}
	

}
