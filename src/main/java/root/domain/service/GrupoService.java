package root.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import root.domain.exception.InUseException;
import root.domain.exception.NotFoundException;
import root.domain.model.Grupo;
import root.domain.model.Permissao;
import root.domain.repository.GrupoRepository;

@Service
public class GrupoService {

	private static final String MSG_IN_USE = "Grupo de código %d não pode ser removida, pois está em uso";
	private static final String MSG_NOT_FOUND = "Não existe um cadastro de Grupo com código %d";
	@Autowired
	private GrupoRepository grupoRepository;
	@Autowired
	private PermissaoService permissaoService;

	@Transactional
	public Grupo criar(String nomeGrupo) 
	{
		Grupo grupo = new Grupo();
		grupo.setNome(nomeGrupo.toLowerCase());
		return grupoRepository.save(grupo);
	}
	
	@Transactional
	public void excluir(String grupoId) 
			throws NotFoundException, InUseException{
		
		if(grupoRepository.findById(grupoId).isEmpty())
			throw new NotFoundException(String.format(MSG_NOT_FOUND, grupoId));
		
		try {
			//If the entity is not found in the persistence store it is silently ignored.
			grupoRepository.deleteById(grupoId);
			//por causa do agora estendido contexto transacional, nao ha garantias de que a operação vai ser executada agora para capturarmos as exceptions. 
			//Nao estamos capturando as exceptions. operaçoes estao enfileiradas no EntityManager
			//Precisamos usar o comit() para executar as operacoes e capturarmos as exceptions.
			grupoRepository.flush();
		} catch (EmptyResultDataAccessException e) {
			throw e;  //nao é mais lancada
		
		} catch (DataIntegrityViolationException e) {
			throw new InUseException(
				String.format(MSG_IN_USE, grupoId));
		}
	}
	
	public Grupo buscar(String grupoId) throws NotFoundException
	{
		return grupoRepository.findById(grupoId)
				.orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, grupoId) ) );	
	}

	@Transactional
	public void desassociarPermissao(String grupoId, String permissaoId) 
	throws NotFoundException
	{
		Grupo grupo = buscar(grupoId);
		Permissao permissao = permissaoService.buscar(permissaoId);
		grupo.removerPermissao(permissao);
	}

	@Transactional
	public void associarPermissao(String grupoId, String permissaoId) 
	throws NotFoundException
	{
		Grupo grupo = buscar(grupoId);
		Permissao permissao = permissaoService.buscar(permissaoId);
		grupo.adicionarPermissao(permissao);		
	}
}