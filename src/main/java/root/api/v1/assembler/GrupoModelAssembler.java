package root.api.v1.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import root.api.v1.model.GrupoModel;
import root.domain.model.Grupo;

@Component
public class GrupoModelAssembler {
	
	@Autowired
	private ModelMapper modelMapper;
	
	public ModelMapper getModelMapper() {
		return modelMapper;
	}

	public GrupoModel toModel(Grupo grupo) {
		
		return modelMapper.map(grupo, GrupoModel.class);
	}
	
	public List<GrupoModel> toCollectionModel(List<Grupo> list) 
	{
        return list.stream()
                .map(g -> toModel(g))
                .collect(Collectors.toList());
	}   
	
}