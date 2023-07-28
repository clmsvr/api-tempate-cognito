#!/usr/bin/bash -ex

chmod 744 ./config.sh
. ./config.sh


if ! aws s3 cp ./t-fargate.yaml s3://${BUCKET}/${PREFIX}/t-fargate.yaml  ;
then
  echo "UPLOAD do template falhou"
  exit  1
fi

STACK=${PREFIX}-fargate-stack

if ! aws cloudformation create-stack --stack-name ${STACK} \
--template-url https://${BUCKET}.s3.amazonaws.com/${PREFIX}/t-fargate.yaml \
--capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND \
--parameters \
ParameterKey=Prefix,ParameterValue=${PREFIX} \
ParameterKey=VPC,ParameterValue=${VPC} \
ParameterKey=Subnet1,ParameterValue=${PublicSubnet1} \
ParameterKey=RepositoryName,ParameterValue=${EcrRepositoryName} \
ParameterKey=TaskCpu,ParameterValue=${TaskCpu} \
ParameterKey=TaskMemory,ParameterValue=${TaskMemory} \
ParameterKey=DBAppUser,ParameterValue=${DBAppUser} \
ParameterKey=DBAppPassword,ParameterValue=${DBAppPassword} \
ParameterKey=DBAppDatabase,ParameterValue=${DBAppDatabase} \
ParameterKey=RdsEndpoint,ParameterValue=${RdsEndpoint} \
ParameterKey=UserPool,ParameterValue=${UserPool}  ;
then
    echo "create-stack FALHOU !!"
    exit 1
fi

if ! aws cloudformation wait stack-create-complete --stack-name  ${STACK}  ;
then
    echo "WAIT stack FALHOU !!"
    exit 1
fi
aws cloudformation describe-stacks --stack-name  ${STACK} --query "Stacks[0].Outputs" 
