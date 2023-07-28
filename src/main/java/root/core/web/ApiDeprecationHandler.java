package root.core.web;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//20.18. Depreciando uma versão da API
//Intercepta a invocação do metodos dos controllers
//usado para adicionar um header informando api deprectata.

// !! Precisa tambel ser adicionado como Interceptor na classe WebConfig

//@Component
public class ApiDeprecationHandler implements  HandlerInterceptor  {

	@Override
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler)
	throws Exception 
	{
		if (request.getRequestURI().startsWith("/v1/")) {
			response.addHeader("X-AlgaFood-Deprecated", 
					"Essa versão da API está depreciada e deixará de existir a partir de 01/01/2021."
					+ "Use a versão mais atual da API.");
		}
		
		//se retornar false, interrompe a execução 
		return true;
	}
	
}