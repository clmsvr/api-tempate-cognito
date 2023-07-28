package root.api.exceptionhandler;

import lombok.Getter;

@Getter
public enum ProblemUriTitle {

	BUSINESS_ERROR("/erro-negocio", "Violação de regra de negócio"),
	NOT_FOUND("/recurso-nao-encontrado", "Recurso não encontrado"),
	NOT_FOUND_CHILD("/entidade-associada-nao-encontrada", "entidade associada não encontrada"),
	IN_USE("/entidade-em-uso", "Entidade em uso"),
	ACCESS_DENIED("/acesso-negado","Acesso Negado"),
	INTERNAL("/erro-de-sistema", "Erro de sistema"),
	
	MSG_UNKNOW("/mensagem-incompreensivel", "Mensagem incompreensível"),
    INVALID_DATA("/dados-invalidos", "Dados inválidos"),
	INVALID_URL_PARM("/parametro-invalido", "Parâmetro Invalido");
	
	private String title;
	private String uri;
	
	ProblemUriTitle(String uri, String title) {
		this.uri = "https://cms.dev" + uri;
		this.title = title;
	}
	
//	NOT_FOUND(HttpStatus.NOT_FOUND, "/entidade-nao-encontrada", "Entidade não encontrada"),
//	NOT_FOUND_CHILD(HttpStatus.BAD_REQUEST,"/entidade-associada-nao-encontrada", "entidade associada não encontrada"),
//	IN_USE(HttpStatus.CONFLICT,"/entidade-em-uso", "Entidade em uso"),
//	INTERNAL(HttpStatus.INTERNAL_SERVER_ERROR,"/internal-server-error", "Erro interno");
//	
//	private HttpStatus status;
//	private String title;
//	private String uri;
//	
//	ProblemType(HttpStatus status, String path, String title) {
//		this.status = status;
//		this.uri = "https://cms.dev" + path;
//		this.title = title;
//	}	
	
}