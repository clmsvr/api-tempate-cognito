#!/usr/bin/bash -ex

chmod 744 ./config.sh
. ./config.sh


#########################################
# Registrar as permissões do gerente no Banco de Dados.
#########################################

GERENTE_EMAIL=userdevtemp+gerente@gmail.com
GERENTE_NOME="Diego Gerente"

if ! aws cognito-idp admin-get-user --user-pool-id $UserPool --username ${GERENTE_EMAIL}  > temp.json ;
then
  echo "Falha buscando usuario no cognito."
  exit  1
fi

GERENTE_SUB=$(jq -r '.UserAttributes | .[] | select(.Name == "sub")  |  .Value '  ./temp.json)
if [ $? -ne 0 ] 
then
   echo "Falha no parse json."
   exit  1
fi  

mysql -u${DBAdminName} -p${DBAdminPassword} -h${RdsEndpoint} <<EOD 

USE ${DBAppDatabase}
INSERT INTO usuario (oidcid, email, nome, data_cadastro, data_atualizacao) 
VALUES ("${GERENTE_SUB}","${GERENTE_EMAIL}","${GERENTE_NOME}",utc_timestamp,utc_timestamp)
;
INSERT INTO usuario_grupo (usuario_id, grupo) 
select id, 'gerente'  from usuario
where email = '${GERENTE_EMAIL}'
;

EOD

if [ $? -ne 0 ]
then
   echo "*** Problemas Verifique Mensagem Abaixo !!! ***"
   exit 1
fi
echo FIM
