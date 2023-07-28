package root.api.v1.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import root.api.v1.model.GrupoModel;
import root.api.v1.model.UsuarioModel;
import root.api.v1.model.UsuarioResumoModel;
import root.domain.model.UsuarioOidc;

@Component
public class UsuarioModelAssembler {
	
	@Autowired
	private ModelMapper modelMapper;
		
	public ModelMapper getModelMapper() {
		return modelMapper;
	}
	
	public UsuarioModel toModel(UsuarioOidc usuario) {
		
		UsuarioModel um = new UsuarioModel();
		
		if (usuario.getUsuarioInterno() != null) {
			um.setGrupos( usuario.getUsuarioInterno().getGrupos().stream()
                          .map(g -> modelMapper.map(g, GrupoModel.class))
                          .collect(Collectors.toList()) );
		}
			
		modelMapper.map(usuario, um);
		return um;
	}

	public UsuarioResumoModel toResumoModel(UsuarioOidc usuario) {
		
		return modelMapper.map(usuario, UsuarioResumoModel.class);
	} 
	
    public List<UsuarioResumoModel> toCollectionModel(List<UsuarioOidc> usuarios) {
        return usuarios.stream()
                .map(g -> toResumoModel(g))
                .collect(Collectors.toList());
    }

   
    
}