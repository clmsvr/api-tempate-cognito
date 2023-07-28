#!/bin/bash -ex

chmod 744 ./s-config.sh
. ./s-config.sh

aws ecr batch-delete-image --repository-name ${Prefix}-repository --image-ids imageTag=latest
aws cloudformation delete-stack --stack-name ${PREFIX}-ecr-stack
aws cloudformation delete-stack --stack-name ${PREFIX}-root1-stack
aws cloudformation delete-stack --stack-name ${PREFIX}-root2-stack

