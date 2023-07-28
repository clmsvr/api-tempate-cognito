package root.core.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


//	@Autowired
//	private ApiDeprecationHandler apiDeprecationHandler;
	
	
// removido apos JUNCAO com authorization server
//
//	//apos inclusao de seguranca, precisa habilitar o cors() no 
//	//ResourceServerConfig tambem, porque a consulta de preflight ficará protegida.	
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**")
//			.allowedMethods("*")
//			.allowedOrigins("*")
//			.maxAge(30);
//	}
//	

//	//17.5  ETag
//	@Bean
//	public Filter shallowEtagHeaderFilter() {
//		return new ShallowEtagHeaderFilter();
//	}
//	
	
	//meu teste. nao mudou nada
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}
	
	//20.12. Definindo a versão padrão da API quando o Media Type não é informado
	//nao estou usando
//	@Override
//	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
//		configurer.defaultContentType(AlgaMediaTypes.V2_APPLICATION_JSON);
//	}

	
//20.18. Depreciando uma versão da API
//Deixado para documentacao. Nao usado.
//usado aqui para adicionar um interceptor para adicionar Header de deprecacao
//nas resposta de API deprectada.
//
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(apiDeprecationHandler);
//	}
}