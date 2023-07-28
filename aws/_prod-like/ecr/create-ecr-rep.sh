#!/usr/bin/bash
#set -x

#### CONFIG #####
BUCKET=cloudf-bucket
PREFIX=tpl-01
#################

if ! aws s3 cp ./ecr.yaml s3://${BUCKET}/${PREFIX}/ecr.yaml  ;
then
  echo "'s3 cp' falhou"
  exit  1
fi

if ! aws cloudformation create-stack --stack-name ${PREFIX}-ecr-stack \
--template-url https://${BUCKET}.s3.amazonaws.com/${PREFIX}/ecr.yaml \
--parameters ParameterKey=Prefix,ParameterValue=${PREFIX}  ;
then
    echo "create-stack falhou"
    exit 1
fi

## FEITO PREVIAMENTE
#### aws s3 mb s3://$BUCKET
## aws s3api create-bucket --bucket $BUCKET --object-ownership BucketOwnerEnforced 
## aws s3api put-public-access-block --bucket $BUCKET --public-access-block-configuration "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
## aws cloudformation delete-stack --stack-name tpl-01-ecr
