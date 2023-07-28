#!/usr/bin/bash -x

chmod 744 ./config.sh
. ./config.sh


if ! aws s3 cp ./t-cognito.yaml s3://${BUCKET}/${PREFIX}/t-cognito.yaml  ;
then
  echo "UPLOAD do template falhou"
  exit  1
fi

STACK=${PREFIX}-cognito-stack

if ! aws cloudformation create-stack --stack-name ${STACK} \
--template-url https://${BUCKET}.s3.amazonaws.com/${PREFIX}/t-cognito.yaml \
--capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND \
--parameters \
ParameterKey=Prefix,ParameterValue=${PREFIX} \
ParameterKey=EmailSenderArn,ParameterValue=${EmailSenderArn} \
ParameterKey=PoolDomain,ParameterValue=${PoolDomain} ;
then
    echo "create-stack falhou"
    exit 1
fi

aws cloudformation wait stack-create-complete --stack-name  ${STACK}  
aws cloudformation describe-stacks --stack-name  ${STACK} --query "Stacks[0].Outputs" 
