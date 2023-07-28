#!/usr/bin/bash
#set -x

TAG=$(aws cloudformation describe-stacks --stack-name tpl-01-ecr-stack  --query "Stacks[0].Outputs[*]" --output text | grep RepositoryFullName | awk '{ print $2}' )
LOGIN=$(aws cloudformation describe-stacks --stack-name tpl-01-ecr-stack  --query "Stacks[0].Outputs" --output text | grep RepositoryLogin | awk '{ print $2}' )
NAME=$(aws cloudformation describe-stacks --stack-name tpl-01-ecr-stack  --query "Stacks[0].Outputs" --output text | grep RepositoryName | awk '{ print $2}' )

rm ./*.jar 
if ! cp ../../target/*.jar .  ;
then
    echo "nao encontrou o JAR da aplicação"
    exit 1
fi

if ! docker image build -t "$NAME"  .  --no-cache ;
then
    echo "erro criando image"
    exit 1
fi

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin "$LOGIN"
docker tag "${NAME}":latest "${TAG}":latest
docker push "${TAG}":latest
