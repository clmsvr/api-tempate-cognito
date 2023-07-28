#!/usr/bin/env bash

get_task_definition_arns() {
    aws ecs list-task-definitions | jq -M -r '.taskDefinitionArns | .[]'
}

get_task_inative_definition_arns() {
    aws ecs list-task-definitions --status INACTIVE | jq -M -r '.taskDefinitionArns | .[]'
}

delete_task_definition() {
    local arn=$1
    #aws ecs deregister-task-definition --task-definition "${arn}" > /dev/null
    ##nao existe: aws ecs delete-task-definitions    --task-definitions "${arn}" > /dev/null
}

for arn in $(get_task_definition_arns)
do
    echo "Deregistering ${arn}..."
    delete_task_definition "${arn}"
done


for arn in $(get_task_inative_definition_arns)
do
    echo "Deregistering ${arn}..."
    delete_task_definition "${arn}"
done