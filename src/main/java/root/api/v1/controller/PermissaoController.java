package root.api.v1.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import root.api.v1.openapi.PermissaoControllerOpenApi;
import root.core.security.CheckSecurity;
import root.domain.service.PermissaoService;

@RestController
@RequestMapping("/v1/permissoes")
public class PermissaoController implements PermissaoControllerOpenApi {

	@Autowired
	private PermissaoService service;
	
	@CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
	@Override
	@GetMapping
	public List<String> listar() {
		return service.listar().stream()
				.map(p -> p.getNome())
			    .collect(Collectors.toList());
	}	
}