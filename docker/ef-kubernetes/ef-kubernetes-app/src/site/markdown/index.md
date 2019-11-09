# Docker : Kubernetes EventFlow

This sample describes how to deploy an application archive containing a StreamBase fragment to Docker using 
Kubernetes.  The primary focus is desktop development, ie testing of application images in a desktop Kubernetes 
node.

* [Terminology](#terminology)
* [Prerequisites](#prerequisites)
* [Development lifecycle](#development-lifecycle)
* [Creating an application archive project for Kubernetes from TIBCO StreamBase Studio&trade;](#creating-an-application-archive-project-for-kubernetes-from-tibco-streambase-studio-trade)
* [Cluster monitor](#cluster-monitor)
* [Containers and nodes](#containers-and-nodes)
* [Building and running from TIBCO StreamBase Studio&trade;](#building-and-running-from-tibco-streambase-studio-trade)
* [Building this sample from the command line and running the integration test cases](#building-this-sample-from-the-command-line-and-running-the-integration-test-cases)
* [Deployment](#deployment)
* [Runtime settings](#runtime-settings)
* [Further Kubernetes commands](#further-kubernetes-commands)

<a name="terminology"></a>

## Terminology

In this sample we are using various technologies with terminology that overlap at times. Here are some key terms for clarification:

* **Kubernetes**
    * **[Kubernetes Node](https://kubernetes.io/docs/concepts/architecture/nodes/)** -  A worker machine
    * **[Kubernetes Cluster](https://kubernetes.io/docs/concepts/)** - A set of machines, called nodes, that run containerized applications managed by Kubernetes. A cluster has at least one worker node and at least one master node.
    * **[K8s](https://kubernetes.io/)** - Abbreviation of Kubernetes
    * **[CNI](https://kubernetes.io/docs/concepts/extend-kubernetes/compute-storage-net/network-plugins/)** - Kubernetes Container Network Interface
    * **[POD](https://kubernetes.io/docs/concepts/workloads/pods/pod-overview/)** - Smallest deployable unit of computing that can be created and managed in Kubernetes.
    * **[StatefulSets](https://kubernetes.io/docs/concepts/workloads/controllers/statefulset/)** - Manages the deployment and scaling of a set of Pods, and provides guarantees about the ordering and uniqueness of these Pods.
* **Tibco**
    * **[StreamBase Machine](http://devzone.tibco.com/sites/streambase/latest/sb/sb-product/documentation/architectsguide/ch03s01.html)** - An execution context for a node
    * **[StreamBase Application](http://devzone.tibco.com/sites/streambase/latest/sb/sb-product/documentation/architectsguide/ch03s01.html)** - Business specific functionality
    * **[StreamBase Fragment](http://devzone.tibco.com/sites/streambase/latest/sb/sb-product/documentation/architectsguide/ch03s01.html)** - An executable part of an application
    * **[StreamBase Cluster](http://devzone.tibco.com/sites/streambase/latest/sb/sb-product/documentation/architectsguide/ch03s01.html)** - A logical grouping of StreamBase nodes that communicate to support an application
    * **[StreamBase Node](http://devzone.tibco.com/sites/streambase/latest/sb/sb-product/documentation/architectsguide/ch03s01.html)** - A StreamBase container for engines
    * **[StreamBase Engine](http://devzone.tibco.com/sites/streambase/latest/sb/sb-product/documentation/architectsguide/ch03s01.html)** - Executable context for a fragment

<a name="prerequisites"></a>

## Prerequisites

In addition to Docker (see [main Docker sample](../../../../../ef-2node/ef-2node-app/src/site/markdown/index.md) ), 
Kubernetes is also required to be installed and configured.  There are several options.

### Docker for desktop

When using Docker desktop, this can most easily be achieved by enabling Kubernetes :

![resources](images/enable.png)

Validate that **docker-for-desktop** is the current context :

```shell
$ kubectl config current-context
docker-for-desktop
```

Docker for desktop only supports a single Kubernetes Node.

### Minikube

An alternative is **Minikube** - see https://kubernetes.io/docs/setup/learning-environment/minikube/ 
for installation instructions.  Minikube runs Kubernetes and Docker in VirtualBox.  See also 
https://kubernetes.io/docs/setup/learning-environment/minikube/#use-local-images-by-re-using-the-docker-daemon
to allow Minikube to access locally built docker images.  

```shell
$ minikube start
😄  minikube v1.5.2 on Darwin 10.15
💡  Tip: Use 'minikube start -p <name>' to create a new cluster, or 'minikube delete' to delete this one.
🔄  Starting existing hyperkit VM for "minikube" ...
⌛  Waiting for the host to be provisioned ...
🐳  Preparing Kubernetes v1.16.2 on Docker '18.09.9' ...
🔄  Relaunching Kubernetes using kubeadm ... 
⌛  Waiting for: apiserver
🏄  Done! kubectl is now configured to use "minikube"

$ eval $(minikube docker-env)
...
```

You may want to grant more resources to Minikube, for example :

```shell
$ minikube start --cpus=4 --memory=8g
```

Validate that **minikube** is the current context :

```shell
$ kubectl config current-context
minikube
```

Minikube only supports a single Kubernetes Node.

### Kind

An alternative is **Kind** - see https://kind.sigs.k8s.io/docs/user/quick-start/ for installation instructions. 
This is a Docker-in-Docker approach, so its usage is slightly different.

```shell
$ kind create cluster
Creating cluster "kind" ...
 ✓ Ensuring node image (kindest/node:v1.15.3) 🖼 
 ✓ Preparing nodes 📦 
 ✓ Creating kubeadm config 📜 
 ✓ Starting control-plane 🕹️ 
 ✓ Installing CNI 🔌 
 ✓ Installing StorageClass 💾 
Cluster creation complete. You can now use the cluster with:

export KUBECONFIG="$(kind get kubeconfig-path --name="kind")"
kubectl cluster-info

$ export KUBECONFIG="$(kind get kubeconfig-path --name="kind")"
```

Kind supports multiple Kubernetes Nodes.

**FIX THIS:** **Kind** doesn't support UDP broadcasts so StreamBase Nodes won't discover each other.  See https://github.com/kubernetes-sigs/kind/issues/1063.
Pending specific support for Kubernetes Discovery.  Work-around is to add **weave-net** :

The default CNI, kind-net, can be disabled by using the following yaml :

```yaml
kind: Cluster
apiVersion: kind.sigs.k8s.io/v1alpha3
networking:
  disableDefaultCNI: true
```

and used when the cluster is created :

```shell
$ kind create cluster --config cluster.yaml
```

Once the default CNI is disabled, an alternative CNI can be loaded such as **weave-net** that does support UDP broadcasts :

```shell
$ kubectl apply -f "https://cloud.weave.works/k8s/net?k8s-version=$(kubectl version | base64 | tr -d '\n')&disable-npc=true"
serviceaccount/weave-net created
clusterrole.rbac.authorization.k8s.io/weave-net created
clusterrolebinding.rbac.authorization.k8s.io/weave-net created
role.rbac.authorization.k8s.io/weave-net created
rolebinding.rbac.authorization.k8s.io/weave-net created
daemonset.apps/weave-net created
```

### Minishift

An alternative is **Minishift** - see https://docs.okd.io/latest/minishift/getting-started/installing.html
for installation instructions.

**Minishift** contains OpenShift 3.

```shell
$ minishift start
-- Starting profile 'minishift'
-- Check if deprecated options are used ... OK
-- Checking if https://github.com is reachable ... OK
...
OpenShift server started.

The server is accessible via web console at:
    https://192.168.99.112:8443/console

You are logged in as:
    User:     developer
    Password: <any value>

To login as administrator:
    oc login -u system:admin

$ eval $(minishift docker-env)

$ eval $(minishift oc-env)
...
```

You may want to grant more resources to **Minishift**, for example :

```shell
$ minishift start --cpus=4 --memory=8GB
```

Default namespace is **myproject**.

**FIX THIS:** DNS is failing for me - see https://github.com/minishift/minishift/issues/3368

### CodeReady Containers

An alternative is **CodeReady Containers** - see https://cloud.redhat.com/openshift/install/crc/installer-provisioned
for installation instructions ( requires RedHat account and secret ).

**CodeReady Containers** contains OpenShift 4.

```shell
$ crc start
INFO Checking if running as non-root              
INFO Checking if oc binary is cached              
INFO Checking if HyperKit is installed            
INFO Checking if crc-driver-hyperkit is installed 
INFO Checking file permissions for /etc/resolver/testing 
INFO Checking file permissions for /etc/hosts     
INFO Starting CodeReady Containers VM for OpenShift 4.2.2... 
INFO Verifying validity of the cluster certificates ... 
INFO Network restart not needed                   
INFO Check internal and public DNS query ...      
INFO Starting OpenShift cluster ... [waiting 3m]  
INFO                                              
INFO To access the cluster, first set up your environment by following 'crc oc-env' instructions 
INFO Then you can access it by running 'oc login -u developer -p developer https://api.crc.testing:6443' 
INFO To login as an admin, username is 'kubeadmin' and password is e4FEb-9dxdF-9N2wH-Dj7B8 
INFO                                              
INFO You can now run 'crc console' and use these credentials to access the OpenShift web console 
Started the OpenShift cluster
WARN The cluster might report a degraded or error state. This is expected since several operators have been disabled to lower the resource usage. For more information, please consult the documentation 

$ eval $(crc oc-env)

$ oc login -u kubeadmin -p e4FEb-9dxdF-9N2wH-Dj7B8
Login successful.

You have access to 51 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "default".
```

To enable pushing images into **CodeReady Containers** ensure a default route is enabled :

```
$ oc patch config.imageregistry.operator.openshift.io/cluster --patch '{"spec":{"defaultRoute":true}}' --type=merge
config.imageregistry.operator.openshift.io/cluster patched (no change)
```

**FIX THIS:** **CodeReady Containers** doesn't support UDP broadcasts so StreamBase Nodes won't discover each other.
Pending specific support for Kubernetes Discovery.

<a name="development_lifecycle"></a>

## Development lifecycle

The assumed development lifecycle is :

* All source in git
* Images and packages are built from source by maven
    * Studio calls maven for builds
    * Continuous integration system calls maven for builds 
* Testing and development can be performed on a desktop running Docker and Kubernetes
* Deployment of images and packages to a repository is by maven
    * Continuous integration system calls maven for deployment
* Production deployment uses containers stored in a Docker registry

![resources](images/devops.svg)

Hence its useful to make use of maven settings.xml ( desktop or continuous automation ) to avoid
hard-coding addresses in git.

Maven lifecycle mapping is :

* **mvn compile** - compile any java source in a StreamBase Fragment to classes
* **mvn test** - run any junit test cases on the StreamBase Fragment
* **mvn package** - build StreamBase Fragment archive or StreamBase Application archive
* **mvn pre-integration-phase** - build Docker image
* **mvn pre-integration-phase** - start Docker container(s)
* **mvn integration-phase** - run any system test cases
* **mvn post-integration-phase** - stop Docker container(s)
* **mvn deploy** - push Docker images to registry

The [TIBCO Streaming maven plugin](https://github.com/TIBCOSoftware/tibco-streaming-maven-plugin) provides the lifecycle.

<a name="creating-an-application-archive-project-for-kubernetes-from-tibco-streambase-studio-trade"></a>

## Creating an application archive project for Kubernetes from TIBCO StreamBase Studio&trade;

Create a new StreamBase Project and enable both Docker and Kubernetes :

**FIX THIS:** We need to update studio menu to something like :

* Existing tickbox for **Docker**
  * If **Docker** tickbox selected, show new radio button for container orchestration framework.  Options are **None** or  **Kubernetes**
    * **None** - Docker only files generated
    * **Kubernetes** - Kubernetes yaml file generated in src/main/kubernetes, security.conf updated for the default Kubernetes network name
  * If **Kubernetes** is selected, new radio button for packaging type.  Options are **None** and **Helm**
    * **None** - no change
    * **Helm** - Helm Chart yaml file generated in src/main/helm and Kubernetes files are generated in the Helm structure instead.  Rule added to pom.xml to generate and deploy helm package.

**FIX THIS:** - show animated gif of creating new project in studio

The resulting project contains maven rules for building a Docker image containing the application and 
the necessary Kubernetes configurations for deployment.

The Kubernetes configurations include -

* [ef-kubernetes-app.yaml](../../../src/main/kubernetes/ef-kubernetes-app.yaml) - Kubernetes Service and StatefulSet definition for a scaling cluster
* [security.conf](../../../src/main/configurations/security.conf) - Trusted hosts names need to match Kubernetes DNS names
* [start-node](../../../src/main/docker/base/start-node) - Script to start the StreamBase node

**Note:** - current version of Kitematic doesn't display container logs.  I've been using the older 0.17.6 from https://github.com/docker/kitematic/releases/download/v0.17.6/Kitematic-0.17.6-Mac.zip

## Cluster monitor

In this sample the generated project is enhanced by adding a [Cluster Monitor](http://devzone.tibco.com/sites/streambase/latest/sb/sb-product/clustermonitor.html)
docker image.  To support this an additional maven execution step is needed in pom.xml :

```xml
                    <!-- cluster monitor image -->
                    <execution>
                        <id>build clustermonitor image</id>
                        <phase>pre-integration-test</phase>
                        <goals>
                            <goal>build</goal>
                        </goals>
                        <configuration>
                            <images>
                                <image>
                                    <name>clustermonitor:%l</name>
                                    <build>
                                        <dockerFileDir>${project.basedir}/src/main/docker/clustermonitor</dockerFileDir>
                                        <optimise>true</optimise>
                                        <assembly>
                                            <inline>
                                                <files>
                                                    <file>
                                                        <source>${project.basedir}/src/main/docker/clustermonitor/start-cluster-monitor</source>
                                                        <outputDirectory></outputDirectory>
                                                        <lineEnding>unix</lineEnding>
                                                    </file>
                                                </files>
                                            </inline>
                                        </assembly>
                                    </build>
                                </image>
                            </images>
                        </configuration>
                    </execution>
                    <execution>
                        <id>push clustermonitor image</id>
                        <phase>deploy</phase>
                        <goals>
                            <goal>push</goal>
                        </goals>
                        <configuration>
                            <images>
                                <image>
                                    <name>clustermonitor:%l</name>
                                    <build/>
                                </image>
                            </images>
                        </configuration>
                    </execution>
```

The trusted hosts configuration needs to include the cluster monitor host(s) :

```scala
configuration = {
    TrustedHosts = {
        hosts = [ "*.ef-kubernetes-app.${POD_NAMESPACE:-default}.svc.cluster.local", "*.clustermonitor.${POD_NAMESPACE:-default}.svc.cluster.local" ]
    }
}
```

Along with additional files :

* [start-cluster-monitor](../../../src/main/docker/clustermonitor/start-cluster-monitor)
* [Dockerfile](../../../src/main/docker/clustermonitor/Dockerfile)
* [clustermonitor.yaml](../../../src/main/kubernetes/clustermonitor.yaml)

<a name="containers-and-nodes"></a>

## Containers and nodes

StreamBase nodes require the hostname, nodename and DNS working together.  Tests have shown that the Kubernetes 
*statefulset* option supports StreamBase nodes easiest.

The goal of this sample is to construct the deployment shown below :

![resources](images/kubernetes-docker.svg)

<a name="building-and-running-from-tibco-streambase-studio-trade"></a>

## Building and running from TIBCO StreamBase Studio&trade;

Useful plugins include :

* [Kubernetes Manifest Editor](https://marketplace.eclipse.org/content/kubernetes-manifest-editor) - YAML editor ( for both Kubernetes and Helm configurations )
* [EKube](https://marketplace.eclipse.org/content/ekube) - Kubernetes explorer 
* [Docker tooling](https://marketplace.eclipse.org/content/eclipse-docker-tooling), requires [TM Terminal](https://marketplace.eclipse.org/content/tm-terminal) - Docker explorer

![resources](images/studio.png)

<a name="building-this-sample-from-the-command-line-and-running-the-integration-test-cases"></a>

## Building this sample from the command line and running the integration test cases

Running *mvn install* will :

* Build the StreamBase Fragment
* Run StreamBase Fragment unit test cases
* Build the application archive that contains the StreamBase Fragment
* If Docker is installed :
    * Build a base image containing just the product
    * Build a application Docker image containing the application archive
    * Build a cluster monitor Docker image
    * Run basic system test to validate configuration
* If Docker is not installed -
    * Run basic system test cases natively

**FIX THIS:** - add animated gif

Some Kubernetes environments ( notably **Kind** and **CodeReady Containers** ) require the image to be pushed to the internal registry 
before running *kubectl apply*. See [Deployment](#deployment) below.

**Note:** **CodeReady Containers* seem to require a internal repository reference, so the yaml file should be updated with :

```
          #
          # docker image to use
          #
          image: image-registry.openshift-image-registry.svc:5000/default/ef-kubernetes-app:1.0.0
```

To start the cluster use the *kubectl apply* command :

```shell
$ kubectl apply -f ef-kubernetes-app/src/main/kubernetes/ef-kubernetes-app.yaml 
service/ef-kubernetes-app created
statefulset.apps/ef-kubernetes-app created
```

The *kubectl describe* command gives further details :

```shell
$ kubectl describe statefulset ef-kubernetes-app
Name:               ef-kubernetes-app
Namespace:          default
CreationTimestamp:  Wed, 30 Oct 2019 11:06:09 +0000
Selector:           app=ef-kubernetes-app
Labels:             <none>
Annotations:        kubectl.kubernetes.io/last-applied-configuration:
                      {"apiVersion":"apps/v1","kind":"StatefulSet","metadata":{"annotations":{},"name":"ef-kubernetes-app","namespace":"default"},"spec":{"podMa...
Replicas:           2 desired | 2 total
Update Strategy:    RollingUpdate
  Partition:        824633996040
Pods Status:        2 Running / 0 Waiting / 0 Succeeded / 0 Failed
Pod Template:
  Labels:  app=ef-kubernetes-app
  Containers:
   ef-kubernetes-app:
    Image:      ef-kubernetes-app:1.0.0
    Port:       <none>
    Host Port:  <none>
    Liveness:   http-get http://:8008/healthcheck/v1/status delay=120s timeout=1s period=10s #success=1 #failure=3
    Readiness:  http-get http://:8008/healthcheck/v1/status delay=120s timeout=1s period=10s #success=1 #failure=3
    Environment:
      POD_NAME:        (v1:metadata.name)
      POD_NAMESPACE:   (v1:metadata.namespace)
      STREAMING_NODENAME:       $(POD_NAME).$(POD_NAMESPACE).ef-kubernetes-app
    Mounts:           <none>
  Volumes:            <none>
Volume Claims:        <none>
Events:
  Type    Reason            Age   From                    Message
  ----    ------            ----  ----                    -------
  Normal  SuccessfulCreate  3m4s  statefulset-controller  create Pod ef-kubernetes-app-0 in StatefulSet ef-kubernetes-app successful
  Normal  SuccessfulCreate  3m4s  statefulset-controller  create Pod ef-kubernetes-app-1 in StatefulSet ef-kubernetes-app successful
```

The configuration file defines 2 replicas and so 2 POD's were created ( ef-kubernetes-app-0 and ef-kubernetes-app-1 ).

To view the logs use *kubectl logs* :

```shell
$ kubectl logs ef-kubernetes-app-0
...
11:06:33.000        203 INFO  t.e.d.h.distribution : Node ef-kubernetes-app-0.default.ef-kubernetes-app has new interface: 'IPv4:ef-kubernetes-app-0:22141' Local resolve, old interface: 'IPv4:ef-kubernetes-app-0:22140' Local resolve.
[ef-kubernetes-app-0.default.ef-kubernetes-app]         Auditing node security
[ef-kubernetes-app-0.default.ef-kubernetes-app]         Host name ef-kubernetes-app-0
[ef-kubernetes-app-0.default.ef-kubernetes-app]         Administration port is 52269
[ef-kubernetes-app-0.default.ef-kubernetes-app]         Discovery Service running on port 54321
[ef-kubernetes-app-0.default.ef-kubernetes-app]         Service name is ef-kubernetes-app-0.default.ef-kubernetes-app
[ef-kubernetes-app-0.default.ef-kubernetes-app]     Node started
COMMAND FINISHED
11:06:37.296 adPool - 1 INFO  StreamBaseHTTPServer : sbd at ef-kubernetes-app-0.ef-kubernetes-app.default.svc.cluster.local:10000; pid=190; version=10.6.0-SNAPSHOT_a2fc1c56fa113822f013c4031f0895e3d53fcc89; Listening
11:08:34.610  [tid=202] INFO  n.ActiveNodeNotifier : Node ef-kubernetes-app-1.default.ef-kubernetes-app is active
```

epadmin commands can be run with *kubectl exec* :

```shell
$ kubectl exec ef-kubernetes-app-0 epadmin servicename=ef-kubernetes-app-0.default.ef-kubernetes-app display cluster
[ef-kubernetes-app-0.default.ef-kubernetes-app] Node Name = ef-kubernetes-app-1.default.ef-kubernetes-app
[ef-kubernetes-app-0.default.ef-kubernetes-app] Network Address = IPv4:ef-kubernetes-app-1:18364
[ef-kubernetes-app-0.default.ef-kubernetes-app] Current State = Discovered
[ef-kubernetes-app-0.default.ef-kubernetes-app] Last State Change = 2019-10-30 11:06:33
[ef-kubernetes-app-0.default.ef-kubernetes-app] Number of Connections = 0
[ef-kubernetes-app-0.default.ef-kubernetes-app] Number of Queued PDUs = 0
[ef-kubernetes-app-0.default.ef-kubernetes-app] Discovered = Dynamic
[ef-kubernetes-app-0.default.ef-kubernetes-app] Location Code = 7674331909344911063
```

## Deployment

The Docker image(s) can be pushed to a Docker registry using the *mvn deploy* command.  Parameters
may be supplied to set registry address and any credentials.  Standard parameter *-Dmaven.deploy.skip=true* 
is useful to skip deploying the maven artifacts.

```shell
$ mvn -Dmaven.deploy.skip=true -Ddocker.push.registry=server.example.com:2001 -Ddocker.push.username=username -Ddocker.push.password=password deploy
...
[INFO] --- docker-maven-plugin:0.31.0:push (push application image) @ ef-kubernetes-app ---
[INFO] DOCKER> The push refers to repository [na-bos-artifacts.na.tibco.com:2001/ef-kubernetes-app]
4960dc540fa2: Pushed      
6aba3a55a28a: Layer already exists 
e3deac335052: Layer already exists 
2438d0874da2: Layer already exists 
a51f3f96403a: Layer already exists 
8aa787eac029: Layer already exists 
5087ff81b27f: Layer already exists 
877b494a9f30: Layer already exists 
[INFO] DOCKER> 1.0.0: digest: sha256:8376f155273b662149e9e4a4bdc2524a6e0bc1a9682e8326803e4207b5783093 size: 1992
[INFO] DOCKER> Pushed ef-kubernetes-app:1.0.0 in 18 seconds 
...
```

In the case of **MiniShift**, the password token can be obtained from *oc whoami -t* :

```shell
$ mvn -Dmaven.deploy.skip=true -Ddocker.push.registry=$(minishift openshift registry)/myproject -Ddocker.push.username=$(oc whoami) -Ddocker.push.password=$(oc whoami -t) deploy
```

In the case of **CodeReady Containers**, the registry is **default-route-openshift-image-registry.apps-crc.testing/PROJECT** :

```shell
$ mvn -Dmaven.deploy.skip=true -Ddocker.push.registry=default-route-openshift-image-registry.apps-crc.testing/default -Ddocker.push.username=kubeadmin -Ddocker.push.password=$(oc whoami -t) deploy
```

In the case of **Kind**, a specific tool *kind load docker-image* is used instead :


```shell
$ kind load docker-image ef-kubernetes-app:1.0.0

```

Yaml file can then be applied.

These parameters are typically set in continuous integration builds in maven's settings.xml :

```xml
<settings>
    <servers>
        <server>
            <id>server.example.com:2001</id>
            <username>username</username>
            <password>password</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>cloud</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <docker.push.registry>server.example.com:2001</docker.push.registry>
            </properties>
        </profile>
    </profiles>
</settings>
```

The Docker registry used should be secured.  However if it can't be ( for example a self-signed ssl certificate
is being used or plain http ) then it may be possible to still use the registry by declaring it insecure :

![resources](images/insecure-registry.png)

<a name="runtime-settings"></a>

## Runtime settings

The application docker image will usually contain all configurations and files to support the application.  However
it is possible to inject configurations and files at runtime.

### StreamBase node name

The environment variable **STREAMING_NODENAME** can be set in the yaml StatefulSet - this is passed to the
**nodename** parameter of *epadmin install node*.  The generated default is **pod name.namespace.application name** :

```yaml
          env:
          - name: POD_NAME
            valueFrom:
              fieldRef:
                fieldPath: metadata.name
          - name: POD_NAMESPACE
            valueFrom:
              fieldRef:
                fieldPath: metadata.namespace
          - name: STREAMING_NODENAME
            value: "$(POD_NAME).$(POD_NAMESPACE).ef-kubernetes-app"
```

### Node deployment file

The environment variable **STREAMING_NODEDEPLOY** can be set to the path of a node deployment file - this is passed to the
**nodedeploy** parameter of *epadmin install node*.

```yaml
...
spec:
  ...
  template:
    ...
    spec:
      ...
      containers:
        - name: ef-kubernetes-app
          env:
          - name: STREAMING_NODEDEPLOY
            value: "/var/opt/tibco/streambase/configuration/node.conf"
...
```

The file referenced can be included in the application docker image (from src/test/configurations/node.conf), 
or supplied via a Kubernetes ConfigMap :

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: configuration
  namespace: default
apiVersion: v1
data:
  node.conf: |-
    name = "ef-kubernetes-app"
    version = "2.0.0"
    type = "com.tibco.ep.dtm.configuration.node"

    configuration = {
        NodeDeploy = {
            nodes = {
                "${EP_NODE_NAME}" = {
                    nodeType = "docker"
                }
            }
        }
    }
```

The ConfgMap has to be mounted via a volume :

```yaml
...
spec:
...
  template:
  ...
    spec:
      volumes:
        - name: configuration
          configMap:
            name: configuration
      ...
      containers:
        - name: ef-kubernetes-app
          volumeMounts:
          - name: configuration
            mountPath: /var/opt/tibco/streambase/configuration
          env:
          - name: STREAMING_NODEDEPLOY
            value: "/var/opt/tibco/streambase/configuration/node.conf"
...
```

### Substitution variables

The environment variable **STREAMING_SUBSTITUTIONS** can be set in the yaml StatefulSet - this is passed to the
**substitutions** parameter of *epadmin install node* :

```yaml
...
spec:
...
  template:
  ...
    spec:
    ...
      containers:
        - name: ef-kubernetes-app
          env:
          - name: STREAMING_SUBSTITUTIONS
            value: "param1=value1,param2=value2"
...
```

### Substitution file

The environment variable **STREAMING_SUBSTITUTIONFILE** can be set to the path of a substitution file - this is passed to the
**substitutionfile** parameter of *epadmin install node*.

```yaml
...
spec:
  ...
  template:
    ...
    spec:
      ...
      containers:
        - name: ef-kubernetes-app
          env:
          - name: STREAMING_SUBSTITUTIONFILE
            value: "/var/opt/tibco/streambase/configuration/substitutions.txt"
...
```

The file referenced can be included in the application docker image (from src/test/configurations/substitutions.txt),
or supplied via a Kubernetes ConfigMap :

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: configuration
  namespace: default
apiVersion: v1
data:
  substitutions.txt: |-
    name1=value1
    name2=value2
```

The ConfgMap has to be mounted via a volume :

```yaml
...
spec:
...
  template:
  ...
    spec:
      volumes:
        - name: configuration
          configMap:
            name: configuration
      ...
      containers:
        - name: ef-kubernetes-app
          volumeMounts:
          - name: configuration
            mountPath: /var/opt/tibco/streambase/configuration
          env:
          - name: STREAMING_SUBSTITUTIONFILE
            value: "/var/opt/tibco/streambase/configuration/substitutions.txt"
...
```

### Administration port

The environment variable **STREAMING_ADMINPORT** can be set in the yaml StatefulSet - this is passed to the
**adminport** parameter of *epadmin install node* :

```yaml
...
spec:
...
  template:
  ...
    spec:
    ...
      containers:
        - name: ef-kubernetes-app
          env:
          - name: STREAMING_ADMINPORT
            value: "0"
...
```

This may be required in some cases where there is a port controlled firewall between StreamBase nodes.

### Logback and other deploy directory files

The environment variable **STREAMING_DEPLOYDIRECTORIES** can be set to the path of a deployment directory - this is passed to the
**deploydirectories** parameter of *epadmin install node*.

```yaml
...
spec:
  ...
  template:
    ...
    spec:
      ...
      containers:
        - name: ef-kubernetes-app
          env:
          - name: STREAMING_DEPLOYDIRECTORIES
            value: "/var/opt/tibco/streambase/resources"
...
```

Any files to be included in a deploy directory can be included in the application docker image (from src/test/resources), 
or supplied via a Kubernetes ConfigMap :

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: resources
  namespace: default
data:
  logback-test.xml: |-
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} %10.10thread %-5level %20.20logger{5} : %msg%n</pattern>
            </encoder>
        </appender>
        <root level="DEBUG">
            <appender-ref ref="Console" />
        </root>
    </configuration>
```

The ConfgMap has to be mounted via a volume :

```yaml
...
spec:
...
  template:
  ...
    spec:
      volumes:
        - name: resources
          configMap:
            name: resources
      ...
      containers:
        - name: ef-kubernetes-app
          volumeMounts:
          - name: resources
            mountPath: /var/opt/tibco/streambase/resources
          env:
          - name: STREAMING_DEPLOYDIRECTORIES
            value: "/var/opt/tibco/streambase/resources"
...
```

### Key store and passwords 

The environment variable **STREAMING_KEYSTORE** can be set to the path of a key store file - this is passed to the
**keystore** parameter of *epadmin install node*.  Also, the environment variable **STREAMING_KEYSTOREPASSWORD**
can be set to the key store password - this is passed to the **keystorepassword** parameter of *epadmin install node*.

Plain text password can be used, but secret ConfigMap is preferred.

```yaml
...
spec:
  ...
  template:
    ...
    spec:
      ...
      containers:
        - name: ef-kubernetes-app
          env:
          - name: STREAMING_KEYSTORE
            value: "/var/opt/tibco/streambase/configuration/mastersecret.ks"
          - name: STREAMING_KEYSTOREPASSWORD
            valueFrom:
              secretKeyRef:
                name: secret
                key: keystorepassword
...
```

The file referenced can be included in the application docker image (via src/test/configurations/mastersecret.ks), 
or supplied via a Kubernetes ConfigMap :

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: configuration
  namespace: default
apiVersion: v1
data:
  mastersecret.ks: 1$+b3hxBKCxDOIFCyxBaeztZaKiYANEKBPAjLlPZ9XwCw=$OUd5KZraLPRSAWvxquMtmrSdAmBC99G9oNLoBUk+aDc4x13DqFoQuN2b500=
```

The encoded password can be specified via a Secret :

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: secret
type: Opaque
data:
  keystorepassword: c2VjcmV0c2VjcmV0
```

The ConfgMap has to be mounted via a volume :

```yaml
...
spec:
...
  template:
  ...
    spec:
      volumes:
        - name: configuration
          configMap:
            name: configuration
      ...
      containers:
        - name: ef-kubernetes-app
          volumeMounts:
          - name: configuration
            mountPath: /var/opt/tibco/streambase/configuration
          env:
          - name: STREAMING_KEYSTORE
            value: "/var/opt/tibco/streambase/configuration/mastersecret.ks"
          - name: STREAMING_KEYSTOREPASSWORD
            valueFrom:
              secretKeyRef:
                name: secret
                key: keystorepassword
...
```

### Files referenced via HOCON configurations

In the case of a HOCON configuration file referencing a file, this can be included in the docker image or via
a ConfigMap in the same was as above.
 
### Upgrades

Kubernetes ConfigMaps are global, so when upgrading with different configurations in ConfgMaps, it is recommended to
include a version with the map name :

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: configuration-1.0.1
  namespace: default
apiVersion: v1
data:
...
```

<a name="further-kubernetes-commands"></a>

## Further Kubernetes commands

### Scaling the application

To scale up the cluster we can increase the number of replicas with the *kubectl scale* command :

```shell
$ kubectl scale statefulsets ef-kubernetes-app --replicas=4
statefulset.apps/ef-kubernetes-app scaled
```

The new node is discovered and added to the cluster :

```shell
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

Similarly, to scale down we can reduce the number of replicas with the *kubectl scale* command :

```shell
$ kubectl scale statefulsets ef-kubernetes-app --replicas=3
statefulset.apps/ef-kubernetes-app scaled
```

### Status

To check POD status use the *kubectl get* command :

```shell
$ kubectl get pod  
NAME                  READY   STATUS    RESTARTS   AGE
ef-kubernetes-app-0   1/1     Running   0          46m
ef-kubernetes-app-1   1/1     Running   0          45m
ef-kubernetes-app-2   1/1     Running   0          45m
```

### Healthcheck

Should a pod fail, the healthcheck will cause a re-start :

```shell
$ kubectl get pods 
NAME                  READY   STATUS    RESTARTS   AGE
ef-kubernetes-app-0   1/1     Running   0          55m
ef-kubernetes-app-1   1/1     Running   0          54m
ef-kubernetes-app-2   1/1     Running   1          53m
```

### Delete

To delete everything defined in the yaml file, use the *kubectl delete* command :

```shell
$ kubectl delete -f ef-kubernetes-app/src/main/kubernetes/ef-kubernetes-app.yaml 
service "ef-kubernetes-app" deleted
statefulset.apps "ef-kubernetes-app" deleted
```

Individual services and statefulsets can also be deleted :

```shell
$ kubectl delete service ef-kubernetes-app
service "ef-kubernetes-app" deleted

$ kubectl delete statefulset ef-kubernetes-app
statefulset.apps "ef-kubernetes-app" deleted
```

### Cluster monitor

To start the cluster monitor user the *kubectl apply* command to apply clustermonitor.yaml :

```shell
$ kubectl apply -f ef-kubernetes-app/src/main/kubernetes/clustermonitor.yaml 
service/clustermonitor created
statefulset.apps/clustermonitor created
```

This configuration uses **NodePort** to expose the web server externally.  Use the *kubectl describe service*
command to determine the mapped port :

```shell
$ kubectl describe service clustermonitor
Name:                     clustermonitor
Namespace:                default
Labels:                   app=clustermonitor
Annotations:              <none>
Selector:                 app=clustermonitor
Type:                     NodePort
IP:                       10.100.141.122
LoadBalancer Ingress:     localhost
Port:                     lvweb  11080/TCP
TargetPort:               11080/TCP
NodePort:                 lvweb  31044/TCP
Endpoints:                10.1.1.38:11080
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>
```

In this case the URL http://localhost:31044 can be used to access the cluster monitor :

![resources](images/clustermonitor.png)

### Web UI Dashboard

### Docker for desktop

To start the Kubernetes dashboard in a **docker-for-desktop** context see https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard :

```shell
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

The dashboard can be found at http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/
with token credentials as exported above :

![resources](images/web-ui.png)

### Minikube

Starting the dashboard in a **minikube** context is via the *minikube dashboard* command :

```shell
$ minikube dashboard
🔌  Enabling dashboard ...
🤔  Verifying dashboard health ...
🚀  Launching proxy ...
🤔  Verifying proxy health ...
🎉  Opening http://127.0.0.1:57841/api/v1/namespaces/kubernetes-dashboard/services/http:kubernetes-dashboard:/proxy/ in your default browser...
```

### Minishift

Starting the dashboard in a **minishift** context is via the *minishift console* command :

```shell
$ minishift console
Opening the OpenShift Web console in the default browser...
```

### CodeReady Containers

Starting the dashboard in a **CodeReady Containers** context is via the *crc console* command :

```shell
$ crc console --credentials
To login as a regular user, username is 'developer' and password is 'developer'.
To login as an admin, username is 'kubeadmin' and password is 'e4FEb-9dxdF-9N2wH-Dj7B8'.

$ crc console
Opening the OpenShift Web Console in the default browser...
```

![resources](images/crc-ui.png)