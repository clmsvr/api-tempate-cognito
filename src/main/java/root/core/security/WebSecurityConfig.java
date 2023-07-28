package root.core.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de Seguranca SEM OAUTH2, ou seja , sem RESOURCE SERVER
 * Usado para acesso à API com HTTP BASIC
 */


//@Configuration
//@EnableWebSecurity //permite que nossa configuracao substitua as configurações default de seguranca dos Starters do Spring Security - https://stackoverflow.com/questions/44671457/what-is-the-use-of-enablewebsecurity-in-spring
public class WebSecurityConfig 
{
	
	//22.3
//	@Bean
	public SecurityFilterChain securityFilterChain1(HttpSecurity http) throws Exception 
	{
		http
		    //sem form login
		    .httpBasic((t) -> {	})  //.httpBasic(Customizer.withDefaults())
			.authorizeHttpRequests((requests) -> 
				requests
					//.requestMatchers("/v1/cozinhas/**")
					//.permitAll()
					.anyRequest().authenticated()
			)
			//sem cookies de sessao
 			.sessionManagement((sessionManagement) ->
				sessionManagement
				    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)							
			.csrf((csrf) -> 
				csrf.disable()
			);

		return http.build();
	}

//	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder enc) {

		//precisa adicionar o prefixo "ROLE_" nos nomes das roles.
		//varios metodos da api http e spring adicionam isso ao comparar nomes de roles.
		//vc so precisa usar o prefixo cadastrar as roles com o usuario.
		
		UserDetails user3 =
				 User.withUsername("user")
					.password(enc.encode("123"))
					.roles("worker","login") //roles() : adiciona o prefixo "ROLE_" automaticamente
					.build();
		
		UserDetails user4 =
				 User.withUsername("cl.silveira@gmail.com")
					.password(enc.encode("123"))
					.authorities("ROLE_worker","ROLE_login") //authorities() : NAO adiciona o prefixo "ROLE_"
					.build();
		

		return new InMemoryUserDetailsManager(user3,user4);
	}
	
//	@Bean
	PasswordEncoder passwordEncoder() {
		//return new Sha512HexPasswordEncoder();
		return new BCryptPasswordEncoder();
	}

}