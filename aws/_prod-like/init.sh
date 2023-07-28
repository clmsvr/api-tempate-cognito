#!/usr/bin/bash
#set -x

#### CONFIG #####
BUCKET=cloudf-bucket
PREFIX=tpl-01
#################


if ! aws s3 cp . s3://${BUCKET}/${PREFIX} --recursive --exclude "*" --include "*.yaml"  ;
then
  echo "UPLOAD dos templates falhou"
  exit  1
fi

if ! aws cloudformation create-stack --stack-name ${PREFIX}-stack \
--template-url https://${BUCKET}.s3.amazonaws.com/${PREFIX}/root.yaml \
--capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND ;
then
    echo "create-stack falhou"
    exit 1
fi

aws cloudformation wait stack-create-complete --stack-name ${PREFIX}-stack  
aws cloudformation describe-stacks --stack-name ${PREFIX}-stack --query "Stacks[0].Outputs" --output text

## FEITO PREVIAMENTE
#### aws s3 mb s3://$BUCKET
## aws s3api create-bucket --bucket $BUCKET --object-ownership BucketOwnerEnforced 
## aws s3api put-public-access-block --bucket $BUCKET --public-access-block-configuration "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
## aws cloudformation delete-stack --stack-name tpl-01-ecr
