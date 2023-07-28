#!/usr/bin/bash -ex

chmod 744 ./config.sh
. ./config.sh

#########################################
# Criar usuarios no Cognito
#########################################

# aws cognito-idp admin-delete-user --user-pool-id $UserPool --username userdevtemp+gerente@gmail.com
# aws cognito-idp admin-delete-user --user-pool-id $UserPool --username userdevtemp+secretaria@gmail.com
# aws cognito-idp admin-delete-user --user-pool-id $UserPool --username userdevtemp+comum@gmail.com

GERENTE_EMAIL=userdevtemp+gerente@gmail.com
GERENTE_NOME="Diego Gerente"
PWD=123qwe

aws cognito-idp admin-create-user --user-pool-id $UserPool \
--username ${GERENTE_EMAIL} \
--user-attributes Name=name,Value="${GERENTE_NOME}" \
--message-action SUPPRESS \
--temporary-password $PWD

aws cognito-idp admin-create-user --user-pool-id $UserPool \
--username userdevtemp+secretaria@gmail.com \
--user-attributes Name=name,Value="Andreia Secretaria" \
--message-action SUPPRESS \
--temporary-password $PWD

aws cognito-idp admin-create-user --user-pool-id $UserPool \
--username userdevtemp+comum@gmail.com \
--user-attributes Name=name,Value="Usuario Comum" \
--message-action SUPPRESS \
--temporary-password $PWD

# aws cognito-idp list-users --user-pool-id $UserPool
