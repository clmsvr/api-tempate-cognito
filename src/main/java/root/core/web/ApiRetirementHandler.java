package root.core.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//20.19. Desligando uma versão da API
//Intercepta a invocação do metodos dos controllers da API desligada
//usado para adicionar um header informando que api nao existe mais.

//remover tambem a documentacao openapi

//!! Precisa tambel ser adicionado como Interceptor na classe WebConfig

//@Component
public class ApiRetirementHandler implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, 
	                         HttpServletResponse response, Object handler)
			throws Exception {
		if (request.getRequestURI().startsWith("/v1/")) {
			response.setStatus(HttpStatus.GONE.value());
			return false;
		}

		return true;
	}

}