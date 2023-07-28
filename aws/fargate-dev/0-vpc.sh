#!/usr/bin/bash -ex

chmod 744 ./config.sh
. ./config.sh


if ! aws s3 cp ./t-vpc.yaml s3://${BUCKET}/${PREFIX}/t-vpc.yaml  ;
then
  echo "UPLOAD do template falhou"
  exit  1
fi

STACK=${PREFIX}-vpc-stack

if ! aws cloudformation create-stack --stack-name ${STACK} \
--template-url https://${BUCKET}.s3.amazonaws.com/${PREFIX}/t-vpc.yaml \
--capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND \
--parameters \
ParameterKey=Prefix,ParameterValue=${PREFIX} \
ParameterKey=VPCCidrBlock,ParameterValue=${VPCCidrBlock} \
ParameterKey=CidrIp1,ParameterValue=${CidrIp1} \
ParameterKey=CidrIp2,ParameterValue=${CidrIp2}  ;
then
    echo "create-stack falhou"
    exit 1
fi

aws cloudformation wait stack-create-complete --stack-name  ${STACK}  
aws cloudformation describe-stacks --stack-name  ${STACK} --query "Stacks[0].Outputs" 
