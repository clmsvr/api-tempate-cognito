package root.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import root.domain.model.UsuarioInterno;
import root.domain.repository.UsuarioRepository;

@Component
public class ProxyGetAuthorities {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
    @Cacheable(value="authorities", key="#oidcid", unless = "#result.size() <= 0")
	public Collection<GrantedAuthority> getAuthorities(String oidcid) 
	{
    	System.out.println("SEM CACHE  #############");
		//verificar se ele tem autorities associadas no banco de dados
		Optional<UsuarioInterno> usuarioInterno = usuarioRepository.findByOidcid(oidcid);
		
		//List<String> authorities = jwt.getClaimAsStringList("authorities");
		final Collection<GrantedAuthority> authorities =  new ArrayList<>();
		if (usuarioInterno.isPresent()) {
			
//				authorities = usuarioInterno.get().getGrupos().stream()
//					.flatMap(g -> g.getPermissoes().stream())
//					.map(p -> p.getNome())
//					.collect(Collectors.toList());
			
//				usuarioInterno.get().getGrupos().stream()
//				.flatMap(g -> g.getPermissoes().stream())
//				.map(p -> authorities.add(p.getNome()));
			
			usuarioInterno.get().getGrupos().stream()
			.flatMap(g -> g.getPermissoes().stream())
			.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getNome())));
		}
		return authorities;
	}	
}
