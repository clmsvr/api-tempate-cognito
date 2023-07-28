package root.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

//@Slf4j
@Component
public class AlgaSecurity {

	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	//se retornar null pode gerar bug
	public static Jwt getToken() {
		//eh um JWT porque estamos configurados como resouce server.!!!
		//com a lib OAUTH cllient seria um OAuth2User
		Object obj = getAuthentication().getPrincipal();
		if (obj instanceof Jwt) 
			return (Jwt) obj;
		else
			return null;
	}
	
	//se retornar null pode gerar bug
	public static String getUsuarioId() {
		//eh um JWT porque estamos configurados como resouce server.!!!
		//com a lib OAUTH cllient seria um OAuth2User
		Jwt jwt = getToken();
		
		if (jwt == null) return null;
		
		return  jwt.getClaim("sub");
	}
	
	//solucao do thiago para evitar bug quando metodo acima retornava null.
	//precisa ainda alterar as anotações para usar este metodo
	public static boolean usuarioAutenticadoIgual(String usuarioId) {
		return getUsuarioId() != null && usuarioId != null
				&& getUsuarioId().equals(usuarioId);
	}
	
	
	//Daqui para baixo sao metodos criados para autorizar os link HAL nas respostas.
	//Podemos reescrever as anotações de CheckSecurity utilizando estes methodos.
	
	
	public boolean hasAuthority(String authorityName) {
		return getAuthentication().getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals(authorityName));
	}
	
	//23.40 daqui pra baixo
	
	public boolean isAutenticado() {
		return getAuthentication().isAuthenticated();
	}
	
	public boolean temEscopoEscrita() {
		return hasAuthority("SCOPE_write");
	}
	
	public boolean temEscopoLeitura() {
		return hasAuthority("SCOPE_read");
	}
	
	public boolean podeConsultarUsuariosGruposPermissoes() {
		return temEscopoLeitura() && hasAuthority("CONSULTAR_USUARIOS_GRUPOS_PERMISSOES");
	}
	
	public boolean podeEditarUsuariosGruposPermissoes() {
		return temEscopoEscrita() && hasAuthority("EDITAR_USUARIOS_GRUPOS_PERMISSOES");
	}
	
	public boolean podePesquisarPedidos() {
		return isAutenticado() && temEscopoLeitura();
	}
	
	public boolean podeConsultarFormasPagamento() {
		return isAutenticado() && temEscopoLeitura();
	}
	
	public boolean podeConsultarCidades() {
		return isAutenticado() && temEscopoLeitura();
	}
	
	public boolean podeConsultarEstados() {
		return isAutenticado() && temEscopoLeitura();
	}

	public boolean podeConsultarEstatisticas() {
		return temEscopoLeitura() && hasAuthority("GERAR_RELATORIOS");
	}
}