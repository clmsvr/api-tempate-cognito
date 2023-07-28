package com.algaworks.algafood.security;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.algaworks.algafood.util.ResourceUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

//Atualizado apos conclusao do curso para incluir seguranca e novo path com a versao da aplicacao.
//https://app.algaworks.com/forum/topicos/85632/como-permitir-testes-de-api-depois-de-implementar-autenticacao-oauth2-com-jwt
//https://www.loom.com/share/d2f10091007c4d36a70f88e0f4fc4e8d	

//Exemplos:
//https://www.youtube.com/watch?v=Fs8fsrngI3Q


//precisa ficar a porta por causa das urls de resource server configuradas no application.properties para acesso ao autentication server
//In the SpringBootTest, we'll use the DEFINED_PORT for the embedded web server
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) 
@TestPropertySource("/application-test.properties")   //10.13 ambiente de teste - outra base de dados
public class CozinhaRestApi_IT {

	@Value("${test.auth.token}")
	String authToken;
	
	@LocalServerPort
	private int port;

	@BeforeEach
	public void setUp() {
		//logar o que foi enviado e recebido
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/cozinhas";
		RestAssured.baseURI = "http://localhost/v1";
	}
	
	@Test
	@Sql(scripts = "classpath:db/testdata/afterMigrate.sql")
	public void deveRetornarStatus200_QuandoConsultarCozinhas() {
		
//		//logar o que foi enviado e recebido
//		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		
		RestAssured.given()
		    .header("Authorization", "Bearer " + authToken)
//			.basePath("/cozinhas")
//			.port(port)
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value());
	}

	@Test
	@Sql(scripts = "classpath:db/testdata/afterMigrate.sql")
	public void deveRetornarStatus201_QuandoCadastrarCozinha() {
		
		String jsonCorretoCozinhaChinesa = ResourceUtils.getContentFromResource(
				"/json/correto/cozinha-chinesa.json");	
		
		RestAssured.given()
		    .header("Authorization", "Bearer " + authToken)
			.body(jsonCorretoCozinhaChinesa)
			//.body("{\"nome\": \"Chinesa\" }")
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value());
	}
	
	@Test
	@Sql(scripts = "classpath:db/testdata/afterMigrate.sql")
	public void deveConterNCozinhas_QuandoConsultarCozinhas() {
		
		RestAssured.given()
			.header("Authorization", "Bearer " + authToken)
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.body("_embedded.cozinhas.id", Matchers.hasSize(4))
			.body("_embedded.cozinhas.nome", Matchers.hasItems("Indiana", "Tailandesa"));
	}
	
	@Test
	@Sql(scripts = "classpath:db/testdata/afterMigrate.sql")
	public void deveRetornarRespostaEStatusCorretos_QuandoConsultarCozinhaExistente() {
		RestAssured.given()
			.header("Authorization", "Bearer " + authToken)
			.pathParam("cozinhaId", 2)
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("nome", Matchers.equalTo("Indiana"));
	}
	
	@Test
	@Sql(scripts = "classpath:db/testdata/afterMigrate.sql")
	public void deveRetornarStatus404_QuandoConsultarCozinhaInexistente() {
		RestAssured.given()
		    .header("Authorization", "Bearer " + authToken)
			.pathParam("cozinhaId", 100)
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}	
}