#!/usr/bin/bash
#set -x

chmod 744 ./s-config.sh
. ./s-config.sh

if ! aws s3 cp . s3://${BUCKET}/${PREFIX} --recursive --exclude "*" --include "*.yaml"  ;
then
  echo "UPLOAD dos templates falhou"
  exit  1
fi

STACK=${PREFIX}-root1-stack

if ! aws cloudformation create-stack --stack-name ${STACK} \
--template-url https://${BUCKET}.s3.amazonaws.com/${PREFIX}/root1.yaml \
--capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND \
--parameters \
ParameterKey=Prefix,ParameterValue=${PREFIX} \
ParameterKey=Bucket,ParameterValue=${BUCKET} \
ParameterKey=EmailSenderArn,ParameterValue=${EmailSenderArn} \
ParameterKey=PoolDomain,ParameterValue=${PoolDomain} \
ParameterKey=DBUsername,ParameterValue=${DBUsername} \
ParameterKey=DBPassword,ParameterValue=${DBPassword} ;
then
    echo "create-stack falhou"
    exit 1
fi



aws cloudformation wait stack-create-complete --stack-name  ${STACK}  
aws cloudformation describe-stacks --stack-name  ${STACK} --query "Stacks[0].Outputs" --output text


//criar schema banco
//criar usuario e senha


## https://docs.aws.amazon.com/cli/latest/reference/rds/create-db-parameter-group.html
# aws rds create-db-parameter-group \
#     --db-parameter-group-name mydbparametergroup \
#     --db-parameter-group-family MySQL5.6 \
#     --description "My new parameter group"

# aws rds describe-db-instances --query "DBInstances[0].Endpoint"
# mysqldump -u $UserName -p --all-databases > dump.sql
# mysqldump -u $UserName -p $DatabaseName > dump.sql
# mysqldump -u $UserName -p $DatabaseName --host $Host > dump.sql

aws rds describe-db-instances --filter "Name=db-instance-id, Values=database-1"  \
--query "DBInstances[0].Endpoint.Address" --output text

## FEITO PREVIAMENTE
#### aws s3 mb s3://$BUCKET
## aws s3api create-bucket --bucket $BUCKET --object-ownership BucketOwnerEnforced 
## aws s3api put-public-access-block --bucket $BUCKET --public-access-block-configuration "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
## aws cloudformation delete-stack --stack-name tpl-01-ecr
