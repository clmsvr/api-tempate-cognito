#!/bin/bash -x

chmod 744 ./config.sh
. ./config.sh


#aws ecr list-images --repository-name ${EcrRepositoryName}
#aws ecr batch-delete-image --repository-name ${EcrRepositoryName} --image-ids imageTag=latest

# aws ecr  describe-repositories | jq --raw-output .repositories[].repositoryName | \
# while read repo; do  
#     echo $repo
# done

# remover untag images
if ! imageids=$(aws ecr list-images --repository-name $EcrRepositoryName --filter tagStatus=UNTAGGED --query 'imageIds[*]'  --output json  | jq -r '[.[].imageDigest] | map("imageDigest="+.) | join (" ")' ) ;
then
    echo falha buscando imagens untagged
else
    if [ -n $imageids ]
    then
       aws ecr batch-delete-image --repository-name $EcrRepositoryName --image-ids $imageids
       echo removed
    fi    
fi
