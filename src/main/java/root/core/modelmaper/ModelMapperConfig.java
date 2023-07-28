
package root.core.modelmaper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		var mapper = new ModelMapper();
		
		
//		mapper.createTypeMap(List<t String.class>.class, List<String>.class).addMapping(
//				src -> grupooSrc.getNome(),
//				(dest, value) -> dest.getCidade().setEstado(value));

		
		return mapper;
	}
	
}

