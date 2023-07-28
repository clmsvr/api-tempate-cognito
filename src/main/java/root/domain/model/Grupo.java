package root.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Grupo {

	@EqualsAndHashCode.Include
	@Id
	private String nome;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "grupo_permissao",
			joinColumns = @JoinColumn(name = "grupo"),
	        inverseJoinColumns = @JoinColumn(name = "permissao"))	
	private List<Permissao> permissoes = new ArrayList<>();	

	
	public boolean removerPermissao(Permissao permissao) {
	    return getPermissoes().remove(permissao);
	}

	public boolean adicionarPermissao(Permissao permissao) {
		
		var list = getPermissoes();
		if (list.contains(permissao))
			return false;
	    list.add(permissao);
	    return true;
	} 	
}