#!/usr/bin/bash -ex

export BUCKET=cloudf-bucket
export PREFIX=tpl-01

#vpc
export VPCCidrBlock=10.0.0.0/16
export CidrIp1=10.0.1.0/24   
export CidrIp2=10.0.2.0/24   

#Cognito
export EmailSenderArn=arn:aws:ses:us-east-1:232762117680:identity/claudioms0909@gmail.com
export PoolDomain=test-pool-232

# rds
export DBAdminName=root
export DBAdminPassword=1d2f3qweadfajQdWdE
export DBAppDatabase=appdata
export DBAppUser=appuser
export DBAppPassword=1qaz!QAZ

# fargate
export TaskCpu=512
export TaskMemory=1024

# LEITURA DAS VARIAVEIS EXPORTADAS

# ${Prefix}-vpc                 : !Ref VPC
# ${Prefix}-public-subnet1      : !Ref PublicSubnet1
# ${Prefix}-public-subnet2      : !Ref PublicSubnet2
# ${Prefix}-erc-repository-name       : !Ref ERCRepository
# ${Prefix}-erc-repository-fullname   : !Sub ${AWS::AccountId}.dkr.ecr.${AWS::Region}.amazonaws.com/${Prefix}-repository
# ${Prefix}-erc-repository-login      : !Sub ${AWS::AccountId}.dkr.ecr.${AWS::Region}.amazonaws.com
# ${Prefix}-user-pool           : !Ref UserPool
# ${Prefix}-user-pool-arn       : !GetAtt UserPool.Arn
# ${Prefix}-rds-endpoint        : !GetAtt Database.Endpoint.Address

# ## aws cloudformation list-exports --query 'Exports[?Name==`tpl-01-vpc`].Value' --output text 
# CMD="aws cloudformation list-exports --query 'Exports[?Name==\`${PREFIX}-vpc\`].Value' --output text"
# VPC=$(echo "$CMD" | bash)
# export VPC

# v1
# CMD="jq -r '.Exports | .[] | select(.Name == \"${PREFIX}-vpc\")  |  .Value '  ./exports.json "
# VPC=$(echo "$CMD" | bash)
# export VPC

# v2
# RETVAL=
# function parsej {
#     CMD="jq -r '.Exports | .[] | select(.Name == \"${PREFIX}-$1\")  |  .Value '  ./exports.json "
#     RETVAL=$(echo "$CMD" | bash)    
# }
# parsej vpc
# export VPC=$RETVAL

rm ./temp.json

if ! aws cloudformation list-exports > ./temp.json  ;
then
  echo "Falha buscando exports."
  exit  1
fi

# v3
trap "exit 1" TERM
export TOP_PID=$$
function parsej()
{
    RETVAL=$( jq -r ".Exports | .[] | select(.Name == \"${PREFIX}-$1\")  |  .Value" ./temp.json )
    if [ $? -ne 0 ] 
    then
        echo "Falha no parse json."
        kill -s TERM $TOP_PID
    fi    
    echo $RETVAL
}

export VPC=$(parsej vpc)
export PublicSubnet1=$(parsej public-subnet1)
export PublicSubnet2=$(parsej public-subnet2)
export EcrRepositoryName=$(parsej erc-repository-name)
export EcrRepositoryFullname=$(parsej erc-repository-fullname)
export EcrRepositoryLogin=$(parsej erc-repository-login)
export UserPool=$(parsej user-pool)
export UserPoolArn=$(parsej user-pool-arn)
export RdsEndpoint=$(parsej rds-endpoint)

