package root.domain.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Usuario")
public class UsuarioInterno {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@EqualsAndHashCode.Include
	@Column(nullable = false)
	private String oidcid;

	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String nome;
	
	@JsonIgnore
	@CreationTimestamp  //especifico do HIBERNATE
	@Column(nullable = false, columnDefinition = "datetime")
	private OffsetDateTime dataCadastro;
	
	@JsonIgnore
	@UpdateTimestamp   //especifico do HIBERNATE
	@Column(nullable = false, columnDefinition = "datetime")
	private OffsetDateTime dataAtualizacao;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_grupo",
			joinColumns = @JoinColumn(name = "usuario_id"),
	        inverseJoinColumns = @JoinColumn(name = "grupo"))	
	private List<Grupo> grupos = new ArrayList<>();
	
	
	public boolean removerGrupo(Grupo grupo) {
	    return getGrupos().remove(grupo);
	}

	public boolean adicionarGrupo(Grupo grupo) {
		var list = getGrupos();
		if (list.contains(grupo)) return false;
	    list.add(grupo);
	    return true;
	}	
}