package root.api.v1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import root.api.v1.assembler.UsuarioModelAssembler;
import root.api.v1.model.UsuarioModel;
import root.api.v1.model.UsuarioResumoModel;
import root.api.v1.model.input.SenhaInput;
import root.api.v1.model.input.UsuarioInputCreate;
import root.api.v1.model.input.UsuarioInputUpdate;
import root.api.v1.openapi.UsuarioControllerOpenApi;
import root.core.security.AlgaSecurity;
import root.core.security.CheckSecurity;
import root.domain.exception.BusinessException;
import root.domain.service.UsuarioService;

@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController implements UsuarioControllerOpenApi {

	@Autowired
	private UsuarioService usuarioService;
	
	//11.20
	@Autowired
	private UsuarioModelAssembler assembler;
	
//	@CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
//	@Override
//	@GetMapping
//	public Page<UsuarioModel> listar(@PageableDefault(size=10) Pageable pageable) 
//	{
//		Page<Usuario> usersPage = usuarioRepository.findAll(pageable);
//		
//		List<UsuarioModel> usersModel = 
//				assembler.toCollectionModel(usersPage.getContent());
//		
//		Page<UsuarioModel> usersModelPage = 
//				new PageImpl<>(usersModel, pageable,	
//						       usersPage.getTotalElements());
//		
//		return usersModelPage;		
//	}

	@CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
	@Override
	@GetMapping
	public List<UsuarioResumoModel> listar() {
		
		AlgaSecurity.getAuthentication().getAuthorities().stream()
		.forEach(System.out::println);
		
		return assembler.toCollectionModel(usuarioService.listar());
	}
	
	@CheckSecurity.UsuariosGruposPermissoes.PodeConsultar
	@Override
	@GetMapping("/{usuarioId}")
	public UsuarioModel buscar(@PathVariable String usuarioId) {
		
		AlgaSecurity.getAuthentication().getAuthorities().stream()
		.forEach(System.out::println);
		
		return  assembler.toModel(
				usuarioService.buscar(usuarioId));
	}
	
	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@Override
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResumoModel adicionar(@RequestBody @Valid UsuarioInputCreate usuarioInput) {
		
		return assembler.toResumoModel(usuarioService.criar(
				usuarioInput.getEmail(),
				usuarioInput.getNome(),
				//nullable
				null,//usuarioInput.getSenhaTemporaria(),
				//nullable
				null//usuarioInput.getEnviarEmail()
				));
	}
	
	@CheckSecurity.UsuariosGruposPermissoes.PodeAlterarUsuario
	@Override
	@PutMapping("/{usuarioId}")
	public UsuarioResumoModel atualizar(@PathVariable String usuarioId, 
			@RequestBody @Valid UsuarioInputUpdate usuarioInput) {
		
		return assembler.toResumoModel(
				usuarioService.atualizar(usuarioId, usuarioInput));
	}
	
	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@Override
	@DeleteMapping("/{usuarioId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void remover(@PathVariable String usuarioId) {
		usuarioService.excluir(usuarioId);	
	}	
	
	@CheckSecurity.UsuariosGruposPermissoes.PodeAlterarPropriaSenha
	@Override
	@PutMapping("/{usuarioId}/senha")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void atualizarSenha(@PathVariable String usuarioId, 
			@RequestBody @Valid SenhaInput senhaInput) 
	throws BusinessException
	{
		Jwt token = AlgaSecurity.getToken();
		if (token == null)
			throw new BusinessException("Token de Autorização nao encontrado.");
		
		usuarioService.atualizarSenha(
				        token,
						usuarioId, 
						senhaInput.getSenhaAtual(), 
						senhaInput.getNovaSenha());
	}	
	
	
	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@Override
	@DeleteMapping("/{userId}/grupos/{grupoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> desassociar(@PathVariable String userId, @PathVariable String grupoId) {
		usuarioService.desassociarGrupo(userId, grupoId);
		return ResponseEntity.noContent().build();
	}
	
	@CheckSecurity.UsuariosGruposPermissoes.PodeEditar
	@Override
	@PutMapping("/{userId}/grupos/{grupoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> associar(@PathVariable String userId, @PathVariable String grupoId) {
		
		usuarioService.associarGrupo(userId, grupoId);
		return ResponseEntity.noContent().build();
	}	
}