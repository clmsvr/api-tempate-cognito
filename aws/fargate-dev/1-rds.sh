#!/usr/bin/bash -ex

chmod 744 ./config.sh
. ./config.sh


if ! aws s3 cp ./t-rds.yaml s3://${BUCKET}/${PREFIX}/t-rds.yaml  ;
then
  echo "UPLOAD do template falhou"
  exit  1
fi

STACK=${PREFIX}-rds-stack

if ! aws cloudformation create-stack --stack-name ${STACK} \
--template-url https://${BUCKET}.s3.amazonaws.com/${PREFIX}/t-rds.yaml \
--capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND \
--parameters \
ParameterKey=Prefix,ParameterValue=${PREFIX} \
ParameterKey=VPC,ParameterValue=${VPC} \
ParameterKey=Subnet1,ParameterValue=${PublicSubnet1}  \
ParameterKey=Subnet2,ParameterValue=${PublicSubnet2}  \
ParameterKey=Username,ParameterValue=${DBAdminName}  \
ParameterKey=Password,ParameterValue=${DBAdminPassword}   ;
then
    echo "create-stack falhou"
    exit 1
fi

aws cloudformation wait stack-create-complete --stack-name  ${STACK}  
aws cloudformation describe-stacks --stack-name  ${STACK} --query "Stacks[0].Outputs" 

mysql -u${DBAdminName} -p${DBAdminPassword} -h${RdsEndpoint} <<EOD 

CREATE DATABASE ${DBAppDatabase};
CREATE USER '${DBAppUser}' IDENTIFIED BY '${DBAppPassword}';
GRANT ALL PRIVILEGES ON ${DBAppDatabase}.* TO '${DBAppUser}'@'%';

EOD

if [ $? -ne 0 ]
then
   echo "*** Problemas Verifique Mensagem Abaixo !!! ***"
   exit 1
fi

echo FIM