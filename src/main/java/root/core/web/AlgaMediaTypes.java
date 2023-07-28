package root.core.web;

import org.springframework.http.MediaType;

///nao esta implementado no projeto - documentacao
//20.11. Implementando o versionamento da API por Media Type
/*
Se usaria assim: 

@RestController
@RequestMapping(path = "/cidades", produces = AlgaMediaTypes.V1_APPLICATION_JSON_VALUE)
public class CidadeController implements CidadeControllerOpenApi {
....
}
 */
public class AlgaMediaTypes {

	public static final String V1_APPLICATION_JSON_VALUE = "application/vnd.algafood.v1+json";
	
	public static final MediaType V1_APPLICATION_JSON = MediaType.valueOf(V1_APPLICATION_JSON_VALUE);

	public static final String V2_APPLICATION_JSON_VALUE = "application/vnd.algafood.v2+json";
	
	public static final MediaType V2_APPLICATION_JSON = MediaType.valueOf(V2_APPLICATION_JSON_VALUE);
	
}