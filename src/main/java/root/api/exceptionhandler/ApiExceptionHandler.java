package root.api.exceptionhandler;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;

import lombok.extern.slf4j.Slf4j;
import root.core.validation.ValidationException;
import root.domain.exception.BusinessException;
import root.domain.exception.InUseException;
import root.domain.exception.NotFoundChildException;
import root.domain.exception.NotFoundException;


// Extender a classe ResponseEntityEExceptionHandler objetiva 
// customizar o corpo das respostas para as exceções internas do Spring.

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler{

	//9.11
	@Autowired
	private MessageSource messageSource;

//	@ExceptionHandler(DomainException.class)
//	public ResponseEntity<?> tratarEntidadeNaoEncontradaException( DomainException e) {
//		
//		HttpStatus status = HttpStatus.BAD_REQUEST;
//		if (e instanceof InUseException)         status = HttpStatus.CONFLICT;
//		if (e instanceof NotFoundException)      status = HttpStatus.NOT_FOUND;
//		if (e instanceof NotFoundChildException) status = HttpStatus.BAD_REQUEST;
//		
//		Problema problema = Problema.builder()
//				.dataHora(OffsetDateTime.now())
//				.mensagem(e.getMessage())
//				.httpStatus(status.toString())
//				.build();
//		
//		return ResponseEntity.status(status).body(problema);
//	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<?> handleDomainException( 
			BusinessException e, WebRequest request) {
		
		ProblemUriTitle type = ProblemUriTitle.BUSINESS_ERROR;
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		//Implementando o padrão da RFC7807 na resposta
		
		if (e instanceof InUseException) {
			type = ProblemUriTitle.IN_USE;
			status = HttpStatus.CONFLICT;
		}
		if (e instanceof NotFoundException){
			type = ProblemUriTitle.NOT_FOUND;
			status = HttpStatus.NOT_FOUND;
		}
		if (e instanceof NotFoundChildException) {
			type = ProblemUriTitle.NOT_FOUND_CHILD;
			status = HttpStatus.BAD_REQUEST;
		}
		
		Problem problem = problemBuilder(status.value(), type, e.getMessage()).build();
		
	    return handleExceptionInternal(e, problem, new HttpHeaders(), status, request);
	}	
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode status, WebRequest request) {

		String title = status instanceof HttpStatus ? ((HttpStatus) status).getReasonPhrase() : status.toString();
		//String title = HttpStatus.valueOf(status.value()).getReasonPhrase();
		
		//Implementando o padrão da RFC7807 na resposta
		
		if (body == null) {
			body = Problem.builder()
					.timestamp(OffsetDateTime.now())
					.status(status.value())
					.title(title)
					//.detail(ex.getMessage()) //comentado para nao expor detalhes internos da app nas exceçoes internas do Spring
					.build();
		} else if (body instanceof String) {
			body = Problem.builder()
					.timestamp(OffsetDateTime.now())
					.status(status.value())
					.title(title)			
					//.detail(ex.getMessage()) //comentado para nao expor detalhes internos da app nas exceçoes internas do Spring
					.build();
		}
		
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}	
	
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) 
	{
		Throwable rootCause = ExceptionUtils.getRootCause(ex);
		
		//melhorando o tratamento de erro para quando a exceção raiz for InvalidFormatException
		if (rootCause instanceof InvalidFormatException) {
			return handleInvalidFormatException((InvalidFormatException) rootCause, headers, status, request);
		}
		else if (rootCause instanceof PropertyBindingException) {
			return handlePropertyBindingException((PropertyBindingException) rootCause, headers, status, request);
		}
			
		ProblemUriTitle uriTitle = ProblemUriTitle.MSG_UNKNOW;
		String detail = "O corpo da requisição está inválido. Verifique erro de sintaxe.";
		
		Problem problem = problemBuilder(status.value(), uriTitle, detail).build();
		
		return handleExceptionInternal(ex, problem, headers, status, request);
	}
	
	private ResponseEntity<Object> handlePropertyBindingException(PropertyBindingException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		String path = joinPath(ex.getPath());
		
		ProblemUriTitle uriTitle = ProblemUriTitle.MSG_UNKNOW;
		String detail = String.format("O uso da proriedade '%s' não é permitido.",path);//ex.getPropertyName());
		
		Problem problem = problemBuilder(status.value(), uriTitle, detail).build();
		return handleExceptionInternal(ex, problem, headers, status, request);
	}

	private ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		ProblemUriTitle uriTitle = ProblemUriTitle.MSG_UNKNOW;
		
		//descobriu isso depurando a exceção
	    // Criei o método joinPath para reaproveitar em todos os métodos que precisam
	    // concatenar os nomes das propriedades (separando por ".")
	    String path = joinPath(ex.getPath());
		
		String detail = String.format("A propriedade '%s' recebeu o valor '%s', "
				+ "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
				path, ex.getValue(), ex.getTargetType().getSimpleName());
		
		Problem problem = problemBuilder(status.value(), uriTitle, detail).build();
		return handleExceptionInternal(ex, problem, headers, status, request);
	}
	
	private static Problem.ProblemBuilder problemBuilder(
			int  httpStatus,
			ProblemUriTitle uriTitle, 
			String      detail) 
	{
		return Problem.builder()
			.status(httpStatus)
			.type(uriTitle.getUri())
			.title(uriTitle.getTitle())
			.detail(detail)
			.timestamp(OffsetDateTime.now());
	}	
	
	private String joinPath(List<Reference> references) {
	    return references.stream()
	        .map(ref -> ref.getFieldName())
	        .collect(Collectors.joining("."));
	}
	
	
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(
				TypeMismatchException ex, HttpHeaders headers,
				HttpStatusCode status, WebRequest request) 
	{
		
		String detail = "O parâmetro de URL '%s' recebeu o valor '%s', que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.";
		detail = String.format(detail, ex.getPropertyName(), ex.getValue(), ex.getRequiredType().getSimpleName());

		ProblemUriTitle uriTitle = ProblemUriTitle.INVALID_URL_PARM;
		
		Problem problem = problemBuilder(status.value(), uriTitle, detail).build();
		
		return handleExceptionInternal(ex, problem, headers, status, request);
	}	
	
	//para tratar esta exception foi preciso adicionar 2 chaves no application.properties.
	//8.26
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(
				NoHandlerFoundException ex, HttpHeaders headers,
				HttpStatusCode status, WebRequest request) 
	{
		ProblemUriTitle uriTitle = ProblemUriTitle.NOT_FOUND;
	    String detail = String.format("O recurso %s, que você tentou acessar, é inexistente.", 
	            ex.getRequestURL());
	    
	    Problem problem = problemBuilder(status.value(), uriTitle, detail).build();
	    
	    return handleExceptionInternal(ex, problem, headers, status, request);
	}
	
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<Object> handleValidacaoException(
			ValidationException ex, WebRequest request) 
	{
		return handleValidationInternal(ex, ex.getBindingResult(), 
				new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) 
	{
		BindingResult bindingResult = ex.getBindingResult();
		return handleValidationInternal(ex, bindingResult, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	private ResponseEntity<Object> handleValidationInternal(
			Exception ex, BindingResult bindingResult, HttpHeaders headers,
			HttpStatus status, WebRequest request) 
	{
		ProblemUriTitle uriTitle = ProblemUriTitle.INVALID_DATA;
		String detail = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";
        		
	    List<Problem.Field> problemFields = bindingResult.getFieldErrors().stream()
	    		.map(fieldError -> {
	    			
	    			//9.11
	    			String message = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
	    			
    				return Problem.Field.builder()
    						.name(fieldError.getField())
    						.userMessage(message)
    						//9.11
    						//.userMessage(fieldError.getDefaultMessage()
    				.build();
	    		})
	    		.collect(Collectors.toList());
	    
	    Problem problem = problemBuilder(status.value(), uriTitle, detail)
	        .fields(problemFields)
	        .build();	  
	    
	    return handleExceptionInternal(ex, problem, headers, status, request);		
	}	
	
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<?> handleMaxUploadSizeExceededException( 
			MaxUploadSizeExceededException e, WebRequest request) 
	{
		Throwable rootCause = ExceptionUtils.getRootCause(e);
		
		ProblemUriTitle type = ProblemUriTitle.INVALID_DATA;
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String detail = "Tamanho máximo do upload excedido.";
		
		if (rootCause instanceof FileSizeLimitExceededException) {
			FileSizeLimitExceededException fe = (FileSizeLimitExceededException)rootCause;
			detail = String.format(
					"Excedido o tamanho máximo de %d Kb para upload do arquivo [%s].",fe.getPermittedSize()/1024,fe.getFileName());
		}
		
		Problem problem = problemBuilder(status.value(), type, detail).build();
		
	    return handleExceptionInternal(e, problem, new HttpHeaders(), status, request);
	}	
	
	//14.16
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
			HttpMediaTypeNotAcceptableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) 
	{
		return ResponseEntity.status(status).headers(headers).build();
	}
	
	//23.22
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> handleAccessDeniedException( 
			AccessDeniedException e, WebRequest request)//, HttpHeaders paradarerro) 
	{
		//nao consegui capturar o header "WWW-Authenticate" que eh gerado na resosta default com uma otima mensagem de erro.
		ProblemUriTitle type = ProblemUriTitle.ACCESS_DENIED;
		HttpStatus status = HttpStatus.FORBIDDEN;		
		Problem problem = problemBuilder(status.value(), type, e.getMessage()).build();
		
	    return handleExceptionInternal(e, problem, new HttpHeaders(), status, request);
	}	
	
	//Para todas as Exceptions nao Tratadas 
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) 
	{
	    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;		
	    ProblemUriTitle uriTitle = ProblemUriTitle.INTERNAL;
	    String detail = "Ocorreu um erro interno inesperado no sistema. "
	            + "Tente novamente e se o problema persistir, entre em contato "
	            + "com o administrador do sistema.";

	    // Importante colocar o printStackTrace (pelo menos por enquanto, que não estamos
	    // fazendo logging) para mostrar a stacktrace no console
	    // Se não fizer isso, você não vai ver a stacktrace de exceptions que seriam importantes
	    // para você durante, especialmente na fase de desenvolvimento
	    //ex.printStackTrace();
	    log.error(ex.getMessage(), ex);
	    
	    Problem problem = problemBuilder(status.value(), uriTitle, detail).build();

	    return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}  	

	
}