package root.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import root.api.v1.model.input.UsuarioInputUpdate;
import root.core.aws.Cognito;
import root.domain.exception.BusinessException;
import root.domain.exception.NotFoundException;
import root.domain.model.Grupo;
import root.domain.model.UsuarioInterno;
import root.domain.model.UsuarioOidc;
import root.domain.repository.UsuarioRepository;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

@Service
@Slf4j
public class UsuarioService {

	//private static final String MSG_IN_USE = "Usuario de código %d não pode ser removida, pois está em uso";
	private static final String MSG_NOT_FOUND = "Não existe um cadastro de Usuario com código %s";
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	GrupoService grupoService;

	@Autowired
	Cognito cognito;

	
	@Transactional
	public List<UsuarioOidc> listar()
	throws CognitoIdentityProviderException
	{
        try 
        {
        	return cognito.listUsers();
        }
        catch (CognitoIdentityProviderException e){
            throw e;
        }		
	}
	
	
	@Transactional
	public UsuarioOidc atualizar(String usuarioId, UsuarioInputUpdate usuarioInput)
	throws NotFoundException, BusinessException
	{
        try 
        {
        	return cognito.updateUser(usuarioId, usuarioInput.getNome());
        }
        catch(UserNotFoundException e) {
        	throw new NotFoundException(String.format(MSG_NOT_FOUND, usuarioId));
        }
        catch (CognitoIdentityProviderException e){
            throw e;
        }		
	}
	
	@Transactional
	public UsuarioOidc criar(String email, String nome, String senha, Boolean sendEmail) 
	throws BusinessException, CognitoIdentityProviderException
	{
		try 
		{
			return cognito.createUser(email, nome, senha, sendEmail);
		} 
		catch (UsernameExistsException e) {
			throw new BusinessException("Já existe um usuário cadastrado com email: "+ email);
		} 
		catch (CognitoIdentityProviderException e) {
			throw e;
		}
	}
	
	@Transactional
	public void excluir(String usuarioId) 
	throws NotFoundException, CognitoIdentityProviderException
	{
		//TODO
		//NO FUTURO, SE O USUARIO ESTIER RALACIONADO COM OUTRA ENDIDADE, Verificar
		//e lançar exception IN_USE
		
		try 
		{
			cognito.removeUser(usuarioId);
		} 
		catch (UserNotFoundException e) {
			throw new NotFoundException(String.format(MSG_NOT_FOUND, usuarioId));
		} 
		catch (CognitoIdentityProviderException e) {
			throw e;
		}
		
		//Limpeza do banco de dados
		var optional = usuarioRepository.findByOidcid(usuarioId);
		if(optional.isEmpty()) return;
		
		try {
			//If the entity is not found in the persistence store it is silently ignored.
			usuarioRepository.deleteById(optional.get().getId());
			usuarioRepository.flush();
		} catch (Exception e) {
			log.warn("falha removendo usuario do banco de dados.",e);
		}
	}
	
	public UsuarioOidc buscar(String oidcid) 
	throws NotFoundException, CognitoIdentityProviderException
	{
		try 
		{
			UsuarioOidc usuario = cognito.getUser(oidcid);
			
			Optional<UsuarioInterno> userOp = usuarioRepository.findByOidcid(usuario.getOidcid());
			if (userOp.isPresent()) usuario.setUsuarioInterno(userOp.get());
			return usuario;
		} 
		catch (UserNotFoundException e) {
			throw new NotFoundException(String.format(MSG_NOT_FOUND, oidcid) );
		} 
		catch (CognitoIdentityProviderException e) {
			throw e;
		}
	}

	@Transactional  
	public void atualizarSenha(Jwt accessToken, String oidcid, String senhaAtual, String novaSenha)
	throws NotFoundException, BusinessException
	{
		String tokenSub = accessToken.getClaim("sub");
		if(tokenSub == null) {
			throw new BusinessException("Token Invalido: claim 'sub' não encontrada.");
		}
		
		if (tokenSub.equals(oidcid) == false) {
			log.debug("Token não pertence ao usuario com OIDC ID: "+oidcid + " ["+tokenSub+"]");
			throw new BusinessException("Token não pertence ao usuario com OIDC ID: "+oidcid );
		}
			
		try {
			cognito.chantePassword(accessToken, senhaAtual, novaSenha);
		} 
		catch (NotAuthorizedException e) {
			throw new BusinessException(e.awsErrorDetails().errorMessage());
		} 
		catch (CognitoIdentityProviderException e) {
			throw e;
		}
	}

	@Transactional 
	public void desassociarGrupo(String oidcid, String grupoId)
	throws NotFoundException
	{
		UsuarioOidc usuario = buscar(oidcid);
		Grupo grupo = grupoService.buscar(grupoId);
		usuario.getUsuarioInterno().removerGrupo(grupo);
	}

	@Transactional 
	public void associarGrupo(String oidcid, String grupoId) 
	throws NotFoundException
	{
		UsuarioOidc usuario = buscar(oidcid);
		
		if(usuario.getUsuarioInterno() == null) {
			UsuarioInterno userIn = new UsuarioInterno();
			userIn.setOidcid (usuario.getOidcid());
			userIn.setNome(usuario.getNome());
			userIn.setEmail(usuario.getEmail());
			//usuarioRepository.save(userIn);
			usuario.setUsuarioInterno(userIn);
		}
		
		Grupo grupo = grupoService.buscar(grupoId);
		usuario.getUsuarioInterno().adicionarGrupo(grupo);
		usuarioRepository.save(usuario.getUsuarioInterno());
	}
}