#!/bin/bash
## Execute this script to stop and remove cluster

echo "Stop and remove Node A"
docker stop A.${project.artifactId}
echo "Node A stopped"
docker rm A.${project.artifactId}
echo "Nodee A removed"

echo "Stop and remove Node B"
docker stop B.${project.artifactId}
echo "Node B stopped"
docker rm B.${project.artifactId}
echo "Node B removed"

echo "Remove example.com network"
docker network remove example.com