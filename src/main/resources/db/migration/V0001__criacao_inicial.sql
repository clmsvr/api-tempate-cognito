
create table usuario (
  id               bigint not null auto_increment,
  oidcid           varchar(255) not null,
  email            varchar(255) not null,
  nome             varchar(255) not null,  
  data_cadastro    datetime not null,
  data_atualizacao datetime not null,
primary key (id),
constraint uk_usuario_oidcid unique (oidcid)
)engine = innodb default character set = utf8mb4 collate = utf8mb4_0900_ai_ci;


create table grupo (
  nome varchar(50) not null,
primary key (nome))
engine = innodb default character set = utf8mb4 collate = utf8mb4_0900_ai_ci;


create table permissao (
  nome varchar(50) not null,
primary key (nome))
engine = innodb default character set = utf8mb4 collate = utf8mb4_0900_ai_ci;

create table usuario_grupo (
  usuario_id bigint not null,
  grupo      varchar(50) not null,
primary key (usuario_id, grupo),
constraint fk_usuario_grupo__usuario foreign key (usuario_id)  references usuario (id),
constraint fk_usuario_grupo__grupo   foreign key (grupo)    references grupo   (nome))
engine = innodb default character set = utf8mb4 collate = utf8mb4_0900_ai_ci;

create table grupo_permissao (
  grupo     varchar(50) not null,
  permissao varchar(50) not null,
primary key (grupo, permissao),
constraint fk_grupo_permissao__permissao foreign key (permissao) references permissao (nome),
constraint fk_grupo_permissao__grupo     foreign key (grupo)     references grupo (nome))
engine = innodb default character set = utf8mb4 collate = utf8mb4_0900_ai_ci;

