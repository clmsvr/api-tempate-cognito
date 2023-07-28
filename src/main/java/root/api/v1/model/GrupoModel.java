package root.api.v1.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class GrupoModel{

    private String nome;
    
    private List<String> permissoes = new ArrayList<>();	
} 