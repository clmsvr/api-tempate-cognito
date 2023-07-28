package root.core.security;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de Seguranca COM OAUTH2
 * O Servidor passa a ser um RESOURCE SERVER
 */

@Configuration
@EnableCaching
@EnableWebSecurity //permite que nossa configuracao substitua as configurações default de seguranca dos Starters do Spring Security - https://stackoverflow.com/questions/44671457/what-is-the-use-of-enablewebsecurity-in-spring
@EnableMethodSecurity(prePostEnabled = true) //23.21 MAis: https://www.baeldung.com/spring-enablemethodsecurity
public class ResourceServerConfig 
{
	
	@Autowired
	ProxyGetAuthorities proxyGetAuthorities;
	
	//22.11
	@Bean
	public SecurityFilterChain securityFilterChain1(HttpSecurity http) throws Exception 
	{
		http
		    //se comentar esta seção, libera todo os acessos
		    //cotrole por petodo do Controler
//			.authorizeHttpRequests((requests) -> 
//				requests
//////23.20 Exemplo: permitor alguns e proibir os demais			
//////					.requestMatchers(HttpMethod.POST, "/v1/cozinhas/**").hasAnyAuthority("EDITAR_COZINHAS")
//////					.requestMatchers(HttpMethod.PUT, "/v1/cozinhas/**").hasAnyAuthority("EDITAR_COZINHAS")
//////					.requestMatchers(HttpMethod.GET, "/v1/cozinhas/**").authenticated()
//////					.anyRequest().denyAll()
////				//.anyRequest().permitAll()
////				//.anyRequest().authenticated()
////				.anyRequest().denyAll()				
//			)
			//apos inclusao de seguranca, precisa habilitar o cors aqui tambem, 
			//porque a consulta de preflight está protegida.
			.cors(cust -> {})
			.csrf(cust -> 
				cust.disable()
			)
			.formLogin(cust -> cust.loginPage("/login"))
			.oauth2ResourceServer(cust -> {
				//cust.opaqueToken(t -> {}); //Considera todos os Tokens como OPACO, e faz a introspecção (mesmo se for um token JWT).
				//JWT - SEM introspecção
				cust.jwt(jwtCust ->     
				    //ler as authorities
					jwtCust.jwtAuthenticationConverter(jwtAuthenticationConverter())
				);
			});
		
		return http.build();
	}
	
//	//23.6 - Geracao de JWT com chave simetrica - nao se deve usar
//	@Bean
//	public JwtDecoder jwtDecoder() {
//		var secretKey = new SecretKeySpec("89a7sd89f7as98f7dsa98fds7fd89sasd9898asdf98s".getBytes(), "HmacSHA256");		
//		return NimbusJwtDecoder.withSecretKey(secretKey).build();
//	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		//return new Sha512HexPasswordEncoder();
		return new BCryptPasswordEncoder();
	}
	

	//Esta classe permite o spring identificar as autorities que estao no Token jwt
    private JwtAuthenticationConverter jwtAuthenticationConverter() 
    {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

                                                    //Converter<Jwt, Collection<GrantedAuthority>>
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
        	
        	String oidcid = jwt.getClaim("sub");
        	
        	//como getAuthotities() é cacheado, precisa ficar em uma classe à parte.
        	Collection<GrantedAuthority> authorities = proxyGetAuthorities.getAuthorities(oidcid);

            //SCOPES - esta é a implementacao default que substituimos - carrega os scopos automaticamente.
            var scopesAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            //Converte os SCOPES do token em Autorities com prefixo "SCOPE_"
            Collection<GrantedAuthority> grantedAuthorities = scopesAuthoritiesConverter.convert(jwt);

//            grantedAuthorities.addAll(authorities
//                    .stream()
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toList()));

            grantedAuthorities.addAll(authorities);
            
            
            System.out.println(grantedAuthorities);
            
            return grantedAuthorities;
        });

        return converter;
    }
}