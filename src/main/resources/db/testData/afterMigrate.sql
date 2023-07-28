set foreign_key_checks = 0;

# 24.15 Locks para permitir mais de uma instancia da aplicação 
# serem levantedas simultaneamente.

lock tables grupo write, grupo_permissao write, permissao write,
    usuario write, usuario_grupo write; 
    
truncate usuario;
truncate permissao;
truncate grupo;
truncate grupo_permissao;
truncate usuario_grupo;

set foreign_key_checks = 1;

insert into permissao (nome) values ('EDITAR_COZINHAS');
insert into permissao (nome) values ('EDITAR_FORMAS_PAGAMENTO');
insert into permissao (nome) values ('EDITAR_CIDADES');
insert into permissao (nome) values ('EDITAR_ESTADOS');
insert into permissao (nome) values ('CONSULTAR_USUARIOS_GRUPOS_PERMISSOES');
insert into permissao (nome) values ('EDITAR_USUARIOS_GRUPOS_PERMISSOES');
insert into permissao (nome) values ('EDITAR_RESTAURANTES');
insert into permissao (nome) values ('CONSULTAR_PEDIDOS');
insert into permissao (nome) values ('GERENCIAR_PEDIDOS');
insert into permissao (nome) values ('GERAR_RELATORIOS');

insert into grupo (nome) 
values ('gerente'), ('vendedor'), ('secretaria'), ('cadastrador');

# Adiciona todas as permissoes no grupo do gerente
insert into grupo_permissao (grupo, permissao)
select 'gerente', nome from permissao;

# Adiciona permissoes no grupo do vendedor
insert into grupo_permissao (grupo, permissao)
select 'vendedor', nome from permissao where nome like 'CONSULTAR_%';

insert into grupo_permissao (grupo, permissao)
select 'vendedor', nome from permissao where nome = 'EDITAR_RESTAURANTES';

# Adiciona permissoes no grupo do auxiliar
insert into grupo_permissao (grupo, permissao)
select 'secretaria', nome from permissao where nome like 'CONSULTAR_%';

# Adiciona permissoes no grupo cadastrador
insert into grupo_permissao (grupo, permissao)
select 'cadastrador', nome from permissao where nome like '%_RESTAURANTES';


unlock tables;

