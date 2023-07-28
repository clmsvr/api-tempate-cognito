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


# 1. Build the image(s) and push the image(s) to ECR with a unique tag
unique_tag="$BUILD_TAG" # or whatever you want that is unique
new_image="<ecr repo>:$unique_tag"
service_arn="<service ARN>"

env_name="test" # or whatever
cache_image="<ecr repo>:$env_name"

# leverage docker cache from the currently active image in this environment, if it exists
docker pull $cache_image || echo "no cache"
docker build --cache-from "$cache_image" -t <ECR repo>:$unique_tag .
docer push <ECR repo>:$unique-tag .

# 2. Create a new task definition revision

# here we assume you don't store the task definition JSON in the repo, which means we need to get it from API call
# by default, this gets the latest ACTIVE revision, which is usually what you want.
existing_taskdef="$(aws ecs describe-task-definition --task-definition=<your-taskdef-family-name>)"

# create new taskdef using jq to replace the image key in the first container (we assume only one container `.containerDefinitions[0]` is defined in this example)
# describe-task-definition returns some keys that can't be used with register-task-defintiion, so we delete those, too
new_task_definition="$(jq --arg IMAGE "$new_image" '.taskDefinition | .containerDefinitions[0].image = $IMAGE | del(.taskDefinitionArn) | del(.revision) | del(.status) | del(.requiresAttributes) | del(.compatibilities)' <<< "$existing_taskdef")"

# register the new revision
new_revision_arn="$(aws ecs register-task-definition --cli-input-json "$new_taskdefinition" --output text --query 'taskDefinition.taskDefinitionArn')"


# 3. Update the service to use the new revision

aws ecs update-service --service="$service_arn" --task-definition="$new_revision_arn"

# 4. wait for deployment stabilization (omitted for brevity)

# 5 retag the environment

# in subsequent workflows, the newly built image will be used for the cache
docker tag "$new_image" "<ecr repo>:${env_name}"
docker push "<ecr repo>:${env_name}"
