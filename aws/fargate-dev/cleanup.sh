#!/bin/bash -x

chmod 744 ./config.sh
. ./config.sh

exit 1

aws cloudformation delete-stack --stack-name ${PREFIX}-ecr-stack
aws cloudformation delete-stack --stack-name ${PREFIX}-cognito-stack
