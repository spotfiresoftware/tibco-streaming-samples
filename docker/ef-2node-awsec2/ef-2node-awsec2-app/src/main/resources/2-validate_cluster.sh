#!/bin/bash
## Execute this script to validate cluster

echo "Run epadmin on Node A"
docker exec A.${project.artifactId} epadmin --servicename=${project.artifactId} display cluster

echo "Run epadmin on Node B"
docker exec B.${project.artifactId} epadmin --servicename=${project.artifactId} display cluster

