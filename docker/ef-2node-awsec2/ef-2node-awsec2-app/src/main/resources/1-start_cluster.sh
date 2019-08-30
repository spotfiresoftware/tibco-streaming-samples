#!/bin/bash
## Execute this script to start nodes

echo "Create example.com network"
docker network create example.com

echo "Start Node A"
docker run --detach --hostname=A.example.com --network-alias=A.example.com --name=A.${project.artifactId} --network=example.com --env=NODENAME=A.${project.artifactId} docker/${project.artifactId}:${project.version}
until docker logs A.${project.artifactId} | grep -q "COMMAND FINISHED"; do sleep 1; done
echo "Node A started"

echo "Start Node B"
docker run --detach --hostname=B.example.com --network-alias=B.example.com --name=B.${project.artifactId} --network=example.com --env=NODENAME=B.${project.artifactId} docker/${project.artifactId}:${project.version}
until docker logs B.${project.artifactId} | grep -q "COMMAND FINISHED"; do sleep 1; done
echo "Node B started"
