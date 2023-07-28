package root.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;


@JsonInclude(Include.NON_NULL)
@Getter
@Builder
@Schema(name = Problem.SCHEMA_NAME)
public class Problem {

	public static final String SCHEMA_NAME = "Problema";
	
	@Schema(example = "400")
	private int status;
	
	@Schema(example = "https://algafood.com.br/dados-invalidos")
	private String type;
	
	@Schema(example = "Dados inválidos")
	private String title;
	
	@Schema(example = "Um ou mais campos estão inválidos.")
	private String detail;
	
	@Schema(example = "2007-12-03T10:15:30+01:00")
	private OffsetDateTime timestamp;
	
	//nulo nao eh serializado
	@Schema(description = "Lista de campos que geraram o erro")
	private List<Field> fields;
	
	@Getter
	@Builder
	@Schema(name = "FieldError")
	public static class Field {
		
		@Schema(example = "preco")
		private String name;
		
		@Schema(example = "O preço é inválido")
		private String userMessage;
	}	
}
