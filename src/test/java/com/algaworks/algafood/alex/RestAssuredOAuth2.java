package com.algaworks.algafood.alex;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Base64;

import static io.restassured.RestAssured.given;


public class RestAssuredOAuth2 {

	public static String clientId = "postman1";
	public static String clientPassword = "123";
	public static String scope = "read write";

	public static String encodeToBase64(String str1, String str2) {
		return new String(Base64.getEncoder().encode((str1 + ":" + str2).getBytes()));
	}

	public Response doAuth() {
		String authorization = encodeToBase64(clientId, clientPassword);

		return
				given()
					.basePath("/")
					.header("Authorization", "Basic " + authorization)
					.contentType(ContentType.URLENC)
					.formParam("grant_type", "client_credentials")
					.formParam("scope", scope)
				.post("/oauth2/token")
				.then()
					.statusCode(200)
					.extract()
					.response();
	}

	public String parseToken(Response response) {
		return response.jsonPath().getString("access_token");
	}
	
	public String getToken() {
		return parseToken(doAuth());
	}

}