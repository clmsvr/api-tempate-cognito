package root.core.security;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

// https://www.baeldung.com/spring-security-expressions
// Now let's explore the security expressions:
//
// hasRole, hasAnyRole
// hasAuthority, hasAnyAuthority
// permitAll, denyAll
// isAnonymous, isRememberMe, isAuthenticated, isFullyAuthenticated
// principal, authentication
// hasPermission



//23.23. Simplificando o controle de acesso em métodos com meta-anotações
public @interface CheckSecurity {


    //para testes - lebeerado dcom token client_credential
    public @interface UsuariosGruposPermissoes_________ {

        //@PreAuthorize("hasAuthority('SCOPE_test-server/write')")
        @Retention(RUNTIME)
        @Target(METHOD)
        public @interface PodeAlterarPropriaSenha { }
        
        //@PreAuthorize("hasAuthority('SCOPE_test-server/write')")
        @Retention(RUNTIME)
        @Target(METHOD)
        public @interface PodeAlterarUsuario { }

        //@PreAuthorize("hasAuthority('SCOPE_test-server/write') ")
        @Retention(RUNTIME)
        @Target(METHOD)
        public @interface PodeEditar { }
        

        //@PreAuthorize("hasAuthority('SCOPE_test-server/read')")
        @Retention(RUNTIME)
        @Target(METHOD)
        public @interface PodeConsultar { }
        
    }  
    
    public @interface UsuariosGruposPermissoes {

        @PreAuthorize("hasAuthority('SCOPE_test-server/write') and "
                + "@algaSecurity.getUsuarioId() == #usuarioId")
        @Retention(RUNTIME)
        @Target(METHOD)
        public @interface PodeAlterarPropriaSenha { }
        
        @PreAuthorize("hasAuthority('SCOPE_test-server/write') and (hasAuthority('EDITAR_USUARIOS_GRUPOS_PERMISSOES') or "
                + "@algaSecurity.getUsuarioId() == #usuarioId)")
        @Retention(RUNTIME)
        @Target(METHOD)
        public @interface PodeAlterarUsuario { }

        @PreAuthorize("hasAuthority('SCOPE_test-server/write') and hasAuthority('EDITAR_USUARIOS_GRUPOS_PERMISSOES')")
        @Retention(RUNTIME)
        @Target(METHOD)
        public @interface PodeEditar { }
        

        @PreAuthorize("hasAuthority('SCOPE_test-server/read') and hasAuthority('CONSULTAR_USUARIOS_GRUPOS_PERMISSOES')")
        @Retention(RUNTIME)
        @Target(METHOD)
        public @interface PodeConsultar { }
        
    }    
 
}