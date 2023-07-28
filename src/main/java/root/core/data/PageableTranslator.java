package root.core.data;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableTranslator {

	
	//13.11
	//original: tem que colocar todas as propriedades ordenáveis
	//minha alteracao: somente o que precisa ser mapeado. 
	public static Pageable translate(
			Pageable pageable, 
			Map<String, String> fieldsMapping) 
	{
//		var orders = pageable.getSort().stream()
//			.filter(order -> fieldsMapping.containsKey(order.getProperty())) //filtrar para nao tentar pegar um mapeamento que nao exite.
//			.map(order -> new Sort.Order(order.getDirection(), 
//					fieldsMapping.get(order.getProperty())))
//			.collect(Collectors.toList());
		
		var orders = pageable.getSort().stream()
				.map(order -> { 
					if (fieldsMapping.containsKey(order.getProperty())) {
						return	new Sort.Order(order.getDirection(), 
								fieldsMapping.get(order.getProperty()));
					}
					else
					{
						return	new Sort.Order(order.getDirection(), 
								order.getProperty());
					}
				})
				.collect(Collectors.toList());		
		
		//System.out.println(pageable.getSort());
		//System.out.println(orders);
		
		return PageRequest.of(
				pageable.getPageNumber(), 
				pageable.getPageSize(),
				Sort.by(orders));
	}
	
}