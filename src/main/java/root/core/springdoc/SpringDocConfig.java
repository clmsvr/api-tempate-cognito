package root.core.springdoc;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import root.api.exceptionhandler.Problem;



//26.5 configuracao acesso do Swagger UI à aplicacao
// url de redirect do Swagger UI: http://localhost:8080/swagger-ui/oauth2-redirect.html
// precisa tambem anotar as interface openapiControler com : @SecurityRequirement(name = "security_auth")
@SecurityScheme(name = "security_auth",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(authorizationCode = @OAuthFlow(
                authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}",
                tokenUrl = "${springdoc.oAuthFlow.tokenUrl}",
                scopes = {
                        @OAuthScope(name = "read", description = "read scope"),
                        @OAuthScope(name = "write", description = "write scope")
                }
        )))
//26.3
@Configuration
public class SpringDocConfig {

    private static final String badRequestResponse = "BadRequestResponse";
    private static final String notFoundResponse = "NotFoundResponse";
    private static final String notAcceptableResponse = "NotAcceptableResponse";
    private static final String internalServerErrorResponse = "InternalServerErrorResponse";

    
    @Bean
    public OpenAPI openAPI() {
    	
    	return new OpenAPI()
                .info(new Info()
                        .title("AlgaFood API (Depreciada)")
                        .version("v1")
                        .description("API aberta.<br>"
        						+ "<strong>Essa versão da API está depreciada e deixará de existir a partir de 01/01/2021. "
        						+ "Use a versão mais atual da API.")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.com")
                        )
                ).externalDocs(new ExternalDocumentation()
                        .description("AlgaWorks")
                        .url("https://algaworks.com")
                        
//                ).tags(Arrays.asList( 
//                		//deixei aqui doc. Pode fazer direto no Controle na anotacao @Tag
//                        new Tag().name("Cidades").description("Gerencia as cidades")
//                        
//			    )
                ).components(new Components()
			            .schemas(gerarSchemas())
			            .responses(gerarResponses())
			    );
    }
    
//entra para customizar apos o carregamentos de todos os beans e paths    
//adiciona resposta para todos os endpoints    
//    public OpenApiCustomiser openApiCustomiser() {
//        return openApi -> {
//            openApi.getPaths()
//                    .values()
//                    .stream()
//                    .flatMap(pathItem -> pathItem.readOperations().stream())
//                    .forEach(operation -> {
//                        ApiResponses responses = operation.getResponses();
//
//                        ApiResponse apiResponseNaoEncontrado = new ApiResponse().description("Recurso não encontrado");
//                        ApiResponse apiResponseErroInterno = new ApiResponse().description("Erro interno no servidor");
//                        ApiResponse apiResponseSemRepresentacao = new ApiResponse()
//                                .description("Recurso não possui uma representação que poderia ser aceita pelo consumidor");
//
//                        responses.addApiResponse("404", apiResponseNaoEncontrado);
//                        responses.addApiResponse("406", apiResponseSemRepresentacao);
//                        responses.addApiResponse("500", apiResponseErroInterno);
//                    });
//        };
//    }
    
    
    //entra para customizar apos o carregamentos de todos os beans e paths
    //adiciona respoatas de erro para todas a operações.
    //por Verbo http   
    @Bean
    public OpenApiCustomizer openApiCustomiser() {
        return openApi -> {
        	
        	//ordenar os Schemas na apresentacao 
        	//fonte: https://stackoverflow.com/questions/62473023/how-to-sort-the-schemas-on-swagger-ui-springdoc-open-ui
        	@SuppressWarnings("rawtypes")
			Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            openApi.getComponents().setSchemas(new TreeMap<>(schemas));
        	
            openApi.getPaths()
                    .values()
                    .forEach(pathItem -> pathItem.readOperationsMap()
                            .forEach((httpMethod, operation) -> {
                            	ApiResponses responses = operation.getResponses();
                                switch (httpMethod) {
                                    case GET:
                                    	//responses.addApiResponse("404", new ApiResponse().description("not foud descriprion"));
                                        responses.addApiResponse("406", new ApiResponse().$ref(notAcceptableResponse));
                                        responses.addApiResponse("500", new ApiResponse().$ref(internalServerErrorResponse));
                                        break;
                                    case POST:
                                        responses.addApiResponse("400", new ApiResponse().$ref(badRequestResponse));
                                        responses.addApiResponse("500", new ApiResponse().$ref(internalServerErrorResponse));
                                        break;
                                    case PUT:
                                        responses.addApiResponse("400", new ApiResponse().$ref(badRequestResponse));
                                        responses.addApiResponse("500", new ApiResponse().$ref(internalServerErrorResponse));
                                        break;
                                    case DELETE:
                                        responses.addApiResponse("500", new ApiResponse().$ref(internalServerErrorResponse));
                                        break;
                                    default:
                                        responses.addApiResponse("500", new ApiResponse().$ref(internalServerErrorResponse));
                                        break;
                                }
                            })
                    );
        };
    }

    @SuppressWarnings("rawtypes")
	private Map<String, Schema> gerarSchemas() {
        final Map<String, Schema> schemaMap = new HashMap<>();

//Pelo fato de se referenciar a classe e não o nome do schema no codigo
//nao precisa mais declarar o schema aqui.        
//        Map<String, Schema> problemSchema = ModelConverters.getInstance().read(Problem.class);
//        Map<String, Schema> problemFieldsSchema = ModelConverters.getInstance().read(Problem.Field.class);
//
//        schemaMap.putAll(problemSchema);
//        schemaMap.putAll(problemFieldsSchema);

        return schemaMap;
    }

    private Map<String, ApiResponse> gerarResponses() 
    {
        final Map<String, ApiResponse> apiResponseMap = new HashMap<>();

        Content content = new Content()
                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, // "application/json"
                        new MediaType().schema(new Schema<Problem>().$ref(Problem.SCHEMA_NAME)));

        apiResponseMap.put(badRequestResponse, new ApiResponse()
                .description("Requisição inválida")
                .content(content));

        apiResponseMap.put(notFoundResponse, new ApiResponse()
                .description("Recurso não encontrado")
                .content(content));

        apiResponseMap.put(notAcceptableResponse, new ApiResponse()
                .description("Recurso não possui representação que poderia ser aceita pelo consumidor")
                .content(content));

        apiResponseMap.put(internalServerErrorResponse, new ApiResponse()
                .description("Erro interno no servidor")
                .content(content));

        return apiResponseMap;
    }
    
}