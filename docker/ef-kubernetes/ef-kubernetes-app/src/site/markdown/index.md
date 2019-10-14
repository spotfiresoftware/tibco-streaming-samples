# Docker : Kubernetes EventFlow

This sample describes how to deploy an application archive containing an EventFlow fragment to Docker using Kubernetes.

* [Prerequisites](#prerequisites)
* [Creating an application archive project for Kubernetes from TIBCO StreamBase Studio&trade;](#creating-an-application-archive-project-for-kubernetes-from-tibco-streambase-studio-trade)
* [Containers and nodes](#containers-and-nodes)
* [Building and running from TIBCO StreamBase Studio&trade;](#building-and-running-from-tibco-streambase-studio-trade)
* [Building this sample from the command line and running the integration test cases](#building-this-sample-from-the-command-line-and-running-the-integration-test-cases)
* [Example kubernetes commands](#example-kubernetes-commands)


FIX THIS - TODO -

* heath check
* replace web port number
* add cluster monitor ?
* show web admin ?

<a name="prerequisites"></a>

## Prerequisites

In addition to docker (see main docker sample FIX THIS - ADD LINK), Kubernetes is also required to be
installed and configured.

When using docker desktop, this can most easily be archived by enabling Kubernetes :

![resources](images/enable.png)

<a name="creating-an-application-archive-project-for-kubernetes-from-tibco-streambase-studio-trade"></a>

## Creating an application archive project for Kubernetes from TIBCO StreamBase Studio&trade;

Create a new StreamBase Project and enable both Docker and Kubernetes :

FIX THIS - show animated gif of creating new project

The resulting project contains maven rules for building a docker image containing the application and 
the necessary Kubernetes configurations for deployment.

The Kubernetes configurations include -

* [stateful.yaml](../../../src/main/kubernetes/stateful.yaml) - Kubernetes Service and StatefulSet definition for a scaling cluster
* [security.conf](../../../src/main/configurations/security.conf) - Trusted hosts names need to match Kubernetes DNS names
* [start-node](../../../src/main/docker/base/start-node) - Script updated to default NODENAME if not set

FIX THIS - current version of Kitematic doesn't display container logs.  I've been using the older 0.17.6
from https://github.com/docker/kitematic/releases/download/v0.17.6/Kitematic-0.17.6-Mac.zip

<a name="containers-and-nodes"></a>

## Containers and nodes

FIX THIS - describe statefulstate ( this gives us sensible DNS/hostname, templates and scaling )
FIX THIS - add some diagrams showing the Kubernetes deployment

<a name="building-and-running-from-tibco-streambase-studio-trade"></a>

## Building and running from TIBCO StreamBase Studio&trade;

FIX THIS - look for useful Studio plugins

<a name="building-this-sample-from-the-command-line-and-running-the-integration-test-cases"></a>

## Building this sample from the command line and running the integration test cases

Running **mvn install** will -

* Build the eventflow fragment
* Run eventflow fragment unit test cases
* Build the application archive that contains the eventflow fragment
* If docker is installed -
    * Build docker image containing the application archive
    * Run basic system test case in docker
* If docker is not installed -
    * Run basic system test cases natively

FIX THIS - add animated gif

To start the cluster use the **kubectl apply** command :

```
$ kubectl apply -f ef-kubernetes-app/src/main/kubernetes/stateful.yaml
service/ef-kubernetes-app created
statefulset.apps/ef-kubernetes-app created
```

The **kubectl describe** command sgive further details :

```
$ kubectl describe service ef-kubernetes-app
Name:              ef-kubernetes-app
Namespace:         default
Labels:            app=ef-kubernetes-app
Annotations:       kubectl.kubernetes.io/last-applied-configuration:
                     {"apiVersion":"v1","kind":"Service","metadata":{"annotations":{},"labels":{"app":"ef-kubernetes-app"},"name":"ef-kubernetes-app","namespac...
Selector:          app=ef-kubernetes-app
Type:              ClusterIP
IP:                None
Port:              web  80/TCP
TargetPort:        80/TCP
Endpoints:         10.1.0.67:80,10.1.0.68:80,10.1.0.69:80
Session Affinity:  None
Events:            <none>

$ kubectl describe statefulset ef-kubernetes-app
Name:               ef-kubernetes-app
Namespace:          default
CreationTimestamp:  Mon, 14 Oct 2019 13:59:19 +0100
Selector:           app=ef-kubernetes-app
Labels:             <none>
Annotations:        kubectl.kubernetes.io/last-applied-configuration:
                      {"apiVersion":"apps/v1","kind":"StatefulSet","metadata":{"annotations":{},"name":"ef-kubernetes-app","namespace":"default"},"spec":{"repli...
Replicas:           3 desired | 3 total
Update Strategy:    RollingUpdate
  Partition:        824642045692
Pods Status:        3 Running / 0 Waiting / 0 Succeeded / 0 Failed
Pod Template:
  Labels:  app=ef-kubernetes-app
  Containers:
   ef-kubernetes-app:
    Image:        docker/ef-kubernetes-app:1.0.0
    Port:         80/TCP
    Host Port:    0/TCP
    Environment:  <none>
    Mounts:       <none>
  Volumes:        <none>
Volume Claims:    <none>
Events:
  Type    Reason            Age    From                    Message
  ----    ------            ----   ----                    -------
  Normal  SuccessfulCreate  8m47s  statefulset-controller  create Pod ef-kubernetes-app-0 in StatefulSet ef-kubernetes-app successful
  Normal  SuccessfulCreate  8m45s  statefulset-controller  create Pod ef-kubernetes-app-1 in StatefulSet ef-kubernetes-app successful
  Normal  SuccessfulCreate  8m43s  statefulset-controller  create Pod ef-kubernetes-app-2 in StatefulSet ef-kubernetes-app successful
```

The configuration file defines 3 replicas and so 3 POD's were created ( ef-kubernetes-app-0, ef-kubernetes-app-1 and ef-kubernetes-app-2 ).

To view the logs use **kubectl logs** :

```
$ kubectl logs ef-kubernetes-app-0
[ef-kubernetes-app-0.ef-kubernetes-app]     Installing node
[ef-kubernetes-app-0.ef-kubernetes-app]         PRODUCTION executables
[ef-kubernetes-app-0.ef-kubernetes-app]         Memory shared memory
[ef-kubernetes-app-0.ef-kubernetes-app]         6 concurrent allocation segments
[ef-kubernetes-app-0.ef-kubernetes-app]         Host name ef-kubernetes-app-0
[ef-kubernetes-app-0.ef-kubernetes-app]         Container tibco/sb
[ef-kubernetes-app-0.ef-kubernetes-app]         Starting container services
[ef-kubernetes-app-0.ef-kubernetes-app]         Loading node configuration
[ef-kubernetes-app-0.ef-kubernetes-app]         Auditing node security
[ef-kubernetes-app-0.ef-kubernetes-app]         Deploying application
[ef-kubernetes-app-0.ef-kubernetes-app]             Engine default-engine-for-com.tibco.ep.samples.docker.ef-kubernetes-ef
[ef-kubernetes-app-0.ef-kubernetes-app]         Application deployed
[ef-kubernetes-app-0.ef-kubernetes-app]         Administration port is 2000
[ef-kubernetes-app-0.ef-kubernetes-app]         Discovery Service running on port 54321
[ef-kubernetes-app-0.ef-kubernetes-app]         Service name is ef-kubernetes-app-0.ef-kubernetes-app
[ef-kubernetes-app-0.ef-kubernetes-app]     Node installed
[ef-kubernetes-app-0.ef-kubernetes-app]     Starting node
[ef-kubernetes-app-0.ef-kubernetes-app]         Engine application::default-engine-for-com.tibco.ep.samples.docker.ef-kubernetes-ef started
[ef-kubernetes-app-0.ef-kubernetes-app]         Loading node configuration
[ef-kubernetes-app-0.ef-kubernetes-app]         Auditing node security
[ef-kubernetes-app-0.ef-kubernetes-app]         Host name ef-kubernetes-app-0
[ef-kubernetes-app-0.ef-kubernetes-app]         Administration port is 2000
[ef-kubernetes-app-0.ef-kubernetes-app]         Discovery Service running on port 54321
[ef-kubernetes-app-0.ef-kubernetes-app]         Service name is ef-kubernetes-app-0.ef-kubernetes-app
[ef-kubernetes-app-0.ef-kubernetes-app]     Node started
COMMAND FINISHED
```

epadmin commands can be run with **kubectl exec** :

```
$ kubectl exec ef-kubernetes-app-0 epadmin servicename=ef-kubernetes-app-0.ef-kubernetes-app display cluster
[ef-kubernetes-app-0.ef-kubernetes-app] Node Name = ef-kubernetes-app-2.ef-kubernetes-app
[ef-kubernetes-app-0.ef-kubernetes-app] Network Address = IPv4:ef-kubernetes-app-2:17318,IPv4:ef-kubernetes-app-2:17317
[ef-kubernetes-app-0.ef-kubernetes-app] Current State = Up
[ef-kubernetes-app-0.ef-kubernetes-app] Last State Change = 2019-10-14 12:59:46
[ef-kubernetes-app-0.ef-kubernetes-app] Number of Connections = 2
[ef-kubernetes-app-0.ef-kubernetes-app] Number of Queued PDUs = 0
[ef-kubernetes-app-0.ef-kubernetes-app] Discovered = Dynamic
[ef-kubernetes-app-0.ef-kubernetes-app] Location Code = 9439063122635943377

[ef-kubernetes-app-0.ef-kubernetes-app] Node Name = ef-kubernetes-app-1.ef-kubernetes-app
[ef-kubernetes-app-0.ef-kubernetes-app] Network Address = IPv4:ef-kubernetes-app-1:15968,IPv4:ef-kubernetes-app-1:15967
[ef-kubernetes-app-0.ef-kubernetes-app] Current State = Up
[ef-kubernetes-app-0.ef-kubernetes-app] Last State Change = 2019-10-14 12:59:43
[ef-kubernetes-app-0.ef-kubernetes-app] Number of Connections = 3
[ef-kubernetes-app-0.ef-kubernetes-app] Number of Queued PDUs = 0
[ef-kubernetes-app-0.ef-kubernetes-app] Discovered = Dynamic
[ef-kubernetes-app-0.ef-kubernetes-app] Location Code = 11990177587896688850
```

<a name="example-kubernetes-commands"></a>

## Example kubernetes commands

To scale up the cluster we can increase the number of replicas with the **kubectl scale** command :

```
$ kubectl scale statefulsets ef-kubernetes-app --replicas=4
statefulset.apps/ef-kubernetes-app scaled
```

The new node is discovered and added to the cluster :

```
$ kubectl exec ef-kubernetes-app-0 epadmin servicename=ef-kubernetes-app-0.ef-kubernetes-app display cluster
[ef-kubernetes-app-0.ef-kubernetes-app] Node Name = ef-kubernetes-app-3.ef-kubernetes-app
[ef-kubernetes-app-0.ef-kubernetes-app] Network Address = IPv4:ef-kubernetes-app-3:53214,IPv4:ef-kubernetes-app-3:53213
[ef-kubernetes-app-0.ef-kubernetes-app] Current State = Up
[ef-kubernetes-app-0.ef-kubernetes-app] Last State Change = 2019-10-14 13:16:59
[ef-kubernetes-app-0.ef-kubernetes-app] Number of Connections = 2
[ef-kubernetes-app-0.ef-kubernetes-app] Number of Queued PDUs = 0
[ef-kubernetes-app-0.ef-kubernetes-app] Discovered = Dynamic
[ef-kubernetes-app-0.ef-kubernetes-app] Location Code = 840900389728819536

[ef-kubernetes-app-0.ef-kubernetes-app] Node Name = ef-kubernetes-app-2.ef-kubernetes-app
[ef-kubernetes-app-0.ef-kubernetes-app] Network Address = IPv4:ef-kubernetes-app-2:17318,IPv4:ef-kubernetes-app-2:17317
[ef-kubernetes-app-0.ef-kubernetes-app] Current State = Up
[ef-kubernetes-app-0.ef-kubernetes-app] Last State Change = 2019-10-14 12:59:46
[ef-kubernetes-app-0.ef-kubernetes-app] Number of Connections = 2
[ef-kubernetes-app-0.ef-kubernetes-app] Number of Queued PDUs = 0
[ef-kubernetes-app-0.ef-kubernetes-app] Discovered = Dynamic
[ef-kubernetes-app-0.ef-kubernetes-app] Location Code = 9439063122635943377

[ef-kubernetes-app-0.ef-kubernetes-app] Node Name = ef-kubernetes-app-1.ef-kubernetes-app
[ef-kubernetes-app-0.ef-kubernetes-app] Network Address = IPv4:ef-kubernetes-app-1:15968,IPv4:ef-kubernetes-app-1:15967
[ef-kubernetes-app-0.ef-kubernetes-app] Current State = Up
[ef-kubernetes-app-0.ef-kubernetes-app] Last State Change = 2019-10-14 12:59:43
[ef-kubernetes-app-0.ef-kubernetes-app] Number of Connections = 3
[ef-kubernetes-app-0.ef-kubernetes-app] Number of Queued PDUs = 0
[ef-kubernetes-app-0.ef-kubernetes-app] Discovered = Dynamic
[ef-kubernetes-app-0.ef-kubernetes-app] Location Code = 11990177587896688850

```

Similarly, to scale down we can reduce the number of replicas with the **kubectl scale** command :

```
$ kubectl scale statefulsets ef-kubernetes-app --replicas=3
statefulset.apps/ef-kubernetes-app scaled
```

To delete the service and statefulset, use the kubectl delete command :

```
$ kubectl delete service ef-kubernetes-app
service "ef-kubernetes-app" deleted

$ kubectl delete statefulset ef-kubernetes-app
statefulset.apps "ef-kubernetes-app" deleted
```

To start the Kubernetes web-ui-dashboard see https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard :

```
$ kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.0.0-beta4/aio/deploy/recommended.yaml
namespace/kubernetes-dashboard created
serviceaccount/kubernetes-dashboard created
service/kubernetes-dashboard created
secret/kubernetes-dashboard-certs created
secret/kubernetes-dashboard-csrf created
secret/kubernetes-dashboard-key-holder created
configmap/kubernetes-dashboard-settings created
role.rbac.authorization.k8s.io/kubernetes-dashboard created
clusterrole.rbac.authorization.k8s.io/kubernetes-dashboard created
rolebinding.rbac.authorization.k8s.io/kubernetes-dashboard created
clusterrolebinding.rbac.authorization.k8s.io/kubernetes-dashboard created
deployment.apps/kubernetes-dashboard created
service/dashboard-metrics-scraper created
deployment.apps/dashboard-metrics-scraper created

$ kubectl -n kubernetes-dashboard get secret
NAME                               TYPE                                  DATA   AGE
default-token-4nc9f                kubernetes.io/service-account-token   3      18s
kubernetes-dashboard-certs         Opaque                                0      18s
kubernetes-dashboard-csrf          Opaque                                1      18s
kubernetes-dashboard-key-holder    Opaque                                2      18s
kubernetes-dashboard-token-jq4z8   kubernetes.io/service-account-token   3      18s

$ kubectl -n kubernetes-dashboard describe secrets kubernetes-dashboard-token-jq4z8
Name:         kubernetes-dashboard-token-jq4z8
Namespace:    kubernetes-dashboard
Labels:       <none>
Annotations:  kubernetes.io/service-account.name: kubernetes-dashboard
              kubernetes.io/service-account.uid: 65fc5a3d-ee85-11e9-b8d1-025000000001

Type:  kubernetes.io/service-account-token

Data
====
token:      eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlcm5ldGVzLWRhc2hib2FyZCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJrdWJlcm5ldGVzLWRhc2hib2FyZC10b2tlbi1qcTR6OCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJrdWJlcm5ldGVzLWRhc2hib2FyZCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjY1ZmM1YTNkLWVlODUtMTFlOS1iOGQxLTAyNTAwMDAwMDAwMSIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDprdWJlcm5ldGVzLWRhc2hib2FyZDprdWJlcm5ldGVzLWRhc2hib2FyZCJ9.IPOva4ok6-w2VrLNidNdREWUWlYvkN4hJ0Tw3Kfik8tD50aofAO2DRFuH6GkSO94FOSbSCRay8bRIECIIjxt8FUjXXInhe7sh32NRTYs_-yRARId5V0kIHP7lUKuXTQcg5cIg7GoCO0ZkWwfdxMhQnMBUVY_HMF1LQVOx_Etth72ujJbIVBoSbQNoenTdOl9xUCFsQa9O_fa33PK5Uv0PaCqAo9vBO4j8CMVpLcXgjN_nypNx6PgUGzcLAIRqvCf-s7RYVhR3njHN6H_VrH92G_NkszWkX2T5cPsXlX6cIEK_CHskaQ5MnZhRPobAmjz3nXRy-KNkratn3HfZwcIPQ
ca.crt:     1025 bytes
namespace:  20 bytes

$ kubectl proxy
```

The web-ui-dashboard can be found at http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/
with token credentials as exported above :

![resources](images/web-ui.png)


