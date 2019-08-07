# Ansible-Docker : 2-node EventFlow

This sample describes how to deploy an application archive containing an EventFlow fragment to Docker.

* [Prerequisites](#prerequisites)
* [Creating an application archive project for Docker managed by Ansible from TIBCO StreamBase Studio&trade;](#creating-an-application-archive-project-for-docker-managed-by-Ansible-from-tibco-streambase-studio-trade)
* [Containers and nodes](#containers-and-nodes)
* [Changes to the default docker configurations](#changes-to-the-default-docker-configurations)
* [Building and running from TIBCO StreamBase Studio&trade;](#building-and-running-from-tibco-streambase-studio-trade)
* [Building this sample from the command line and running the integration test cases](#building-this-sample-from-the-command-line-and-running-the-integration-test-cases)
* [Example docker commands](#example-docker-commands)

See also [Docker section in TIBCO&reg; Streaming documentation](https://docs.tibco.com/pub/str/10.4.0/doc/html/admin/part-docker.html).

<a name="prerequisites"></a>

## Prerequisites

Docker must first be downloaded and installed - see https://www.docker.com/ for further details.  Any 
recent version of docker should suffice, but testing was initially with docker 2.1.0.0 on
MacOS 10.14.

Ansible managament server must be installed - see https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html for further details. Playbook from this sample was built and tested on a localhost with Ansible 2.8.1 on MacOS 10.14.

On MacOS, the resources available to docker may need to be increased beyond the default - see
CPUs and Memory settings on the Advanced tab of Docker preferences.

![resources](images/resources.png)

<a name="creating-an-application-archive-project-for-docker-from-tibco-streambase-studio-trade"></a>

## Creating an application archive project for Docker managed by Ansible from TIBCO StreamBase Studio&trade;

TIBCO StreamBase Studio&trade; can generate a project containing the necessary files to build and 
test a Docker image by selecting **Enable Docker support** when creating an application archive project :

![create](images/create.png)

Such a project includes :

* An Ansible [playbook file](../../main/ansible/project-playbook.yml) with set of tasks divided in three groups: build docker images, test docker images and application, clean (remove docker images build in this playbook).
* A [base Dockerfile](../../main/docker/base/Dockerfile) to build a base image containing Linux, utilities and the TIBCO StreamBase runtime
* A [start-node](../../main/docker/base/start-node) script to start a node
* An [application Dockerfile](../../main/docker/application/Dockerfile) to build an application image containing the application archive - this is based on the base image
* Steps in [pom.xml](../../../pom.xml) that uses [fabric8io/docker-maven-plugin](http://dmp.fabric8.io/) to build the Docker image and start Docker containers for basic testing
* [Trusted hosts HOCON configuration](../../main/configurations/security.conf) so that each container can run epadmin commands on the cluster
* [Application definition configuration](../../main/configurations/app.conf) that defines nodeType docker to use System V shared memory
* [Node deployment configuration](../../main/configurations/defaultnode.conf) that uses the above nodeType

Note that whilst this project will create a simple Docker image, changes to the project may be required for additional behaviours. 

<a name="ansible"></a>

## Ansible

In this sample we have one playbook with set of tasks.
When executing entire playbook, tasks in first section will prepare work directory and build docker images. Followed by a test section where environment will be set up, docker containers power up with the application nodes, run tests and power off entire environment. The last part will remove docker images created during this playbook execution.
If you prefer to skip the second and third part of this playbook please check skipTests box under SB Studio or when in the project folder execute **mvn -DskipTests=true install** in command line.

Please check [playbook tasksk](../../site/markdown/playbook-tasks.md) to see a selected Ansible tasks with brief description.


<a name="containers-and-nodes"></a>

## Containers and nodes

In this sample we name the docker container as **A.ef-2node-ansible-app**,  which hosts the StreamBase node **A.ef-2node-ansible-app**, and **B.ef-2node-ansible-app**, which hosts the StreamBase node **B.ef-2node-app**.  A Docker network **example.com** connects the nodes together :

![nodes](images/two-node-docker.svg)

The two containers have network access to each other, but not to the docker host.

<a name="changes-to-the-default-docker-configurations"></a>

## Changes to the default docker configurations

In this sample we still want to build the application archive if Ansible is not
installed, hence the maven [pom.xml](../../../pom.xml) file is updated to detect if ansible is installed :

```xml
    <properties>
        <dockerDomain>example.com</dockerDomain>
        <skipApplicationDocker>false</skipApplicationDocker>
        <skipStreamBaseDockerBase>false</skipStreamBaseDockerBase>
        <skipDockerTests>false</skipDockerTests>
        <skipTests>false</skipTests>
    </properties>
    ...
       <!-- if Ansible is available, run playbook and build docker images -->
    <profiles>
	<profile>
	    <id>Ansible-OSX</id>
	    <activation>
		<file>
		    <exists>/usr/local/bin/ansiblew</exists>
		</file>
	    </activation>
	    <modules>
    		<module>ef-2node-ansible-ef</module>
    		<module>ef-2node-ansible-app</module>
	    </modules>
	</profile>

	<profile>
	    <id>Ansible-Linux</id>
	    <activation>
		<file>
		    <exists>/usr/bin/ansible</exists>
		</file>
	    </activation>
	    <modules>
    		<module>ef-2node-ansible-ef</module>
    		<module>ef-2node-ansible-app</module>
	    </modules>
	</profile>
    </profiles>
``` 

<a name="building-and-running-from-tibco-streambase-studio-trade"></a>

## Building and running from TIBCO StreamBase Studio&trade;

Use the **Run As -> Maven install** menu option to build from TIBCO StreamBase Studio&trade; or Run As shortcut.  Tests can
be skipped if required by ticking the **Skip tests**. It is important to add **PATH** variable under Environment tab with value: **/bin:/usr/bin:/usr/local/bin:/usr/sbin**. Also in this place **TIBCO_EP_HOME** path can be set pointing to the working directory for this build project.

![maven](images/studio-conf-ansible.jpg)

Tasks info from ansible playbook will show up on a console tab.

![maven](images/studio-run-ansible.jpg)

<a name="building-this-sample-from-the-command-line-and-running-the-integration-test-cases"></a>

## Building this sample from the command line and running the integration test cases

Use the [maven](https://maven.apache.org) as **mvn install** to build from the command line or Continuous Integration system :

```
[INFO] PLAY [StreamBase create base and application docker image based on Centos7] ****
[INFO] 
[INFO] TASK [Gathering Facts] *********************************************************
[INFO] ok: [127.0.0.1]
[INFO] 
[INFO] TASK [Create work directory for base image] ************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Copy platform.zip file into work directory] ******************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Copy start-node script to base image work directory] *********************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Update dockerfile before building base image] ****************************
[INFO] changed: [127.0.0.1] => (item={u'search': u'(^LABEL build-image=)(.*)$', u'replace': u'LABEL build-image=1.0.0'})
[INFO] ok: [127.0.0.1] => (item={u'search': u'(^###Note:\\s)(.*)$', u'replace': u'###Note: LABEL statement build by Ansible playbook'})
[INFO] 
[INFO] TASK [Remove docker image if exist from previous build] ************************
[INFO] ok: [127.0.0.1]
[INFO] 
[INFO] TASK [Building SB base image - sbrt-base] **************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Clean up work directory for base image] **********************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Remove itermediate image with LABEL build-image=1.0.0] *******************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Create source directory for app image] ***********************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Create a copy of platform zip file into application docker image work directory] ***
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Update dockerfile before building app image] *****************************
[INFO] ok: [127.0.0.1] => (item={u'search': u'(^FROM\\s)(.*)$', u'replace': u'FROM sbrt-base:10.5.0-SNAPSHOT'})
[INFO] changed: [127.0.0.1] => (item={u'search': u'(^###Note:\\s)(.*)$', u'replace': u'###Note: FROM statement build by Ansible playbook'})
[INFO] changed: [127.0.0.1] => (item={u'search': u'(^LABEL build-image=)(.*)$', u'replace': u'LABEL build-image=1.0.0'})
[INFO] changed: [127.0.0.1] => (item={u'search': u'(^###Note:\\s)(.*)$', u'replace': u'###Note: LABEL statement build by Ansible playbook'})
[INFO] 
[INFO] TASK [Remove docker image if exist from previous build] ************************
[INFO] ok: [127.0.0.1]
[INFO] 
[INFO] TASK [Building StreamBase application image] ***********************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Clean up work directory for application image] ***************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Create example.com network] **********************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Start container A.ef-2node-ansible-app] **********************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Waiting for Node A to start] *********************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Start container B.ef-2node-ansible-app] **********************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Waiting for Node B to start] *********************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Run epadmin command on Node A] *******************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Node A] ******************************************************************
[INFO] ok: [127.0.0.1] => 
[INFO]   NodeAresults.stdout_lines:
[INFO]   - '[A.ef-2node-ansible-app] Node Name = B.ef-2node-ansible-app'
[INFO]   - '[A.ef-2node-ansible-app] Network Address = IPv4:B.example.com:5558,IPv4:B.example.com:5557'
[INFO]   - '[A.ef-2node-ansible-app] Current State = Up'
[INFO]   - '[A.ef-2node-ansible-app] Last State Change = 2019-08-02 14:38:37'
[INFO]   - '[A.ef-2node-ansible-app] Number of Connections = 3'
[INFO]   - '[A.ef-2node-ansible-app] Number of Queued PDUs = 0'
[INFO]   - '[A.ef-2node-ansible-app] Discovered = Dynamic'
[INFO]   - '[A.ef-2node-ansible-app] Location Code = 7382436235611343951'
[INFO]   - '[B.ef-2node-ansible-app] Node Name = A.ef-2node-ansible-app'
[INFO]   - '[B.ef-2node-ansible-app] Network Address = IPv4:A.example.com:5558,IPv4:A.example.com:5557'
[INFO]   - '[B.ef-2node-ansible-app] Current State = Up'
[INFO]   - '[B.ef-2node-ansible-app] Last State Change = 2019-08-02 14:38:37'
[INFO]   - '[B.ef-2node-ansible-app] Number of Connections = 3'
[INFO]   - '[B.ef-2node-ansible-app] Number of Queued PDUs = 0'
[INFO]   - '[B.ef-2node-ansible-app] Discovered = Dynamic'
[INFO]   - '[B.ef-2node-ansible-app] Location Code = 11636435185532938412'
[INFO] 
[INFO] TASK [Run epadmin command on Node B] *******************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Node B] ******************************************************************
[INFO] ok: [127.0.0.1] => 
[INFO]   NodeBresults.stdout_lines:
[INFO]   - '[B.ef-2node-ansible-app] Node Name = A.ef-2node-ansible-app'
[INFO]   - '[B.ef-2node-ansible-app] Network Address = IPv4:A.example.com:5558,IPv4:A.example.com:5557'
[INFO]   - '[B.ef-2node-ansible-app] Current State = Up'
[INFO]   - '[B.ef-2node-ansible-app] Last State Change = 2019-08-02 14:38:37'
[INFO]   - '[B.ef-2node-ansible-app] Number of Connections = 3'
[INFO]   - '[B.ef-2node-ansible-app] Number of Queued PDUs = 0'
[INFO]   - '[B.ef-2node-ansible-app] Discovered = Dynamic'
[INFO]   - '[B.ef-2node-ansible-app] Location Code = 11636435185532938412'
[INFO]   - '[A.ef-2node-ansible-app] Node Name = B.ef-2node-ansible-app'
[INFO]   - '[A.ef-2node-ansible-app] Network Address = IPv4:B.example.com:5558,IPv4:B.example.com:5557'
[INFO]   - '[A.ef-2node-ansible-app] Current State = Up'
[INFO]   - '[A.ef-2node-ansible-app] Last State Change = 2019-08-02 14:38:37'
[INFO]   - '[A.ef-2node-ansible-app] Number of Connections = 3'
[INFO]   - '[A.ef-2node-ansible-app] Number of Queued PDUs = 0'
[INFO]   - '[A.ef-2node-ansible-app] Discovered = Dynamic'
[INFO]   - '[A.ef-2node-ansible-app] Location Code = 7382436235611343951'
[INFO] 
[INFO] TASK [Stop and remove container A] *********************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Stop and remove container B] *********************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Remove example.com network] **********************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Platform check] **********************************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Remove Docker images [Linux]] ********************************************
[INFO] skipping: [127.0.0.1]
[INFO] 
[INFO] TASK [Remove Docker images] ****************************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] PLAY RECAP *********************************************************************
[INFO] 127.0.0.1                  : ok=29   changed=24   unreachable=0    failed=0    skipped=1    rescued=0    ignored=0

```

<a name="example-docker-commands"></a>

## Example docker commands

### Create the docker network

Use the [docker network](https://docs.docker.com/engine/reference/network/) command :

```
$ docker network create example.com
cc384acf3f6298253df72c61b8afcb27c7278a85d57e7c3ca5c907265fc0a30f
```

Network created with ansible task :
```
- name: Create example.com network
    docker_network:
        name: example.com
        state: present
```

### Start the containers

Use the [docker run](https://docs.docker.com/engine/reference/run/) command.  In this case the following options are used :

* **--detach** - run in the background
* **--hostname=A.example.com --network-alias=A.example.com --network=example.com** - set the container hostname and network name. This must match the docker network name and the [Trusted hosts HOCON configuration](../../main/configurations/security.conf)
* **--name=A.ef-2node-ansible-app** - container name
* **--env=NODENAME=A.ef-2node-ansible-app** - node name
* **docker/ef-2node-ansible-app:1.0.0** - Docker image name

```shell
$ docker run --detach --hostname=A.example.com --network-alias=A.example.com --name=A.ef-2node-ansible-app --network=example.com --env=NODENAME=A.ef-2node-ansible-app docker/ef-2node-ansible-app:1.0.0
156a2b2233f3b0fe987bddb9678c8dc1abf08a4ac6eb6fb0cd5e61f6478d8e35
$ docker run --detach --hostname=B.example.com --network-alias=B.example.com --name=B.ef-2node-ansible-app --network=example.com --env=NODENAME=B.ef-2node-ansible-app docker/ef-2node-ansible-app:1.0.0
6a1bb0aed2b9e97483aa1e0a290562a534815c447b72c8bc8207667eb1f2e0c4
```

Ansible tasks starting docker container A with options :
```
- name: Start container A.{{ projectId }}
    docker_container:
        name: A.{{ projectId }}
        image: docker/{{ projectId }}:{{ projectId_ver }}
        hostname: A.example.com
        networks: 
          - name: example.com
            aliases: 
              - A.example.com
        env:
          NODENAME: A.{{ projectId }}
        state: started
    when: skipTests == 'false'
``` 
variable and values passed by maven plugin to ansible playbook
- {{ projectId }} == ef-2node-ansible-app 
- {{ projectId_ver }} == 1.0.0

Ansible plugin in pom.xml file :
```xml
   <plugin>
        <groupId>co.escapeideas.maven</groupId>
        <artifactId>ansible-maven-plugin</artifactId>
        <version>1.3.0</version>
        <executions>
             <execution>
               <id>ansible-playbook</id>
                   <goals>
                       <goal>playbook</goal>
                   </goals>
                   <configuration>
                        <playbook>${project.basedir}/src/main/ansible/project-playbook.yml</playbook> 
                        <promoteDebugAsInfo>true</promoteDebugAsInfo> 
                        <failOnAnsibleError>true</failOnAnsibleError>
                        <extraVars>
                           <variable>platform=platform_linuxx86_64</variable>
                           <variable>sbrt_ver=${sbrt.version}</variable>   
                           <variable>projectId=${project.artifactId}</variable> 
                           <variable>projectId_ver=${project.version}</variable>
                           <variable>project_basedir=${project.basedir}</variable>
                           <variable>project_build_directory=${project.build.directory}</variable>
                           <variable>skipTests=${skipTests}</variable>
                        </extraVars>
                   </configuration>
             </execution>
          </executions>
    </plugin>
```

### View the running containers

Use the [docker ps](https://docs.docker.com/engine/reference/ps/) command :

```shell
$ docker ps
CONTAINER ID        IMAGE                               COMMAND                  CREATED             STATUS              PORTS               NAMES
6a1bb0aed2b9        docker/ef-2node-ansible-app:1.0.0   "/bin/sh -c ${PRODUC…"   22 seconds ago      Up 21 seconds                           B.ef-2node-ansible-app
156a2b2233f3        docker/ef-2node-ansible-app:1.0.0   "/bin/sh -c ${PRODUC…"   48 seconds ago      Up 47 seconds                           A.ef-2node-ansible-app
```

### View the container console logs

Use the [docker logs](https://docs.docker.com/engine/reference/commandline/logs/) command :

```shell
$ docker logs A.ef-2node-ansible-app
[A.ef-2node-ansible-app] 	Installing node
[A.ef-2node-ansible-app] 		PRODUCTION executables
[A.ef-2node-ansible-app] 		Memory shared memory
[A.ef-2node-ansible-app] 		4 concurrent allocation segments
[A.ef-2node-ansible-app] 		Host name A.example.com
[A.ef-2node-ansible-app] 		Container tibco/sb
[A.ef-2node-ansible-app] 		Starting container services
[A.ef-2node-ansible-app] 		Loading node configuration
[A.ef-2node-ansible-app] 		Auditing node security
[A.ef-2node-ansible-app] 		Deploying application
[A.ef-2node-ansible-app] 			Engine default-engine-for-com.tibco.ep.samples.docker.ef-2node-ansible-ef
[A.ef-2node-ansible-app] 		Application deployed
[A.ef-2node-ansible-app] 		Administration port is 2000
[A.ef-2node-ansible-app] 		Discovery Service running on port 54321
[A.ef-2node-ansible-app] 		Service name is A.ef-2node-ansible-app
[A.ef-2node-ansible-app] 	Node installed
[A.ef-2node-ansible-app] 	Starting node
[A.ef-2node-ansible-app] 		Engine application::default-engine-for-com.tibco.ep.samples.docker.ef-2node-ansible-ef started
[A.ef-2node-ansible-app] 		Loading node configuration
[A.ef-2node-ansible-app] 		Auditing node security
[A.ef-2node-ansible-app] 		Host name A.example.com
[A.ef-2node-ansible-app] 		Administration port is 2000
[A.ef-2node-ansible-app] 		Discovery Service running on port 54321
[A.ef-2node-ansible-app] 		Service name is A.ef-2node-ansible-app
[A.ef-2node-ansible-app] 	Node started
COMMAND FINISHED
```

### Include application logs to the container logs

A src/main/resources/logback.xml file is included in the application maven module to share application logs to docker console.

To make use of this add the --tty option when starting the container :

```shell
$ docker run --tty --detach --hostname=A.example.com --network-alias=A.example.com --name=A.ef-2node-ansible-app --network=example.com --env=NODENAME=A.ef-2node-ansible-app docker/ef-2node-ansible-app:1.0.0
f884cd838acbdb4be46055d303121e1e09387fb79139324845f652cff51a7ec6
$ docker run --tty --detach --hostname=B.example.com --network-alias=B.example.com --name=B.ef-2node-ansible-app --network=example.com --env=NODENAME=B.ef-2node-ansible-app docker/ef-2node-ansible-app:1.0.0
eef872633e6f8bcd4b1b05525ef918cf0453b5fce8702f7ebdd3329b75d5f2ed
```

```shell
$ docker logs A.ef-2node-ansible-app
...
20:04:05.075 adPool - 1 INFO  StreamBaseHTTPServer : sbd at A.example.com:10000; pid=168; version=10.5.0-SNAPSHOT_a9fed4da866b2db57849c1d6d81c1aec1ba07352; Listening
20:04:32.000        280 INFO  t.e.d.h.distribution : Node B.ef-2node-ansible-app has new interface: 'IPv4:B.example.com:5558,IPv4:B.example.com:5557, old interface: 'IPv4:B.example.com:5557'
```

### Run epadmin commands

Use the [docker exec](https://docs.docker.com/engine/reference/commandline/exec/) command on one of the containers :

```shell
$ docker exec A.ef-2node-ansible-app epadmin --servicename=ef-2node-ansible-app display cluster
[A.ef-2node-ansible-app] Node Name = B.ef-2node-ansible-app
[A.ef-2node-ansible-app] Network Address = IPv4:B.example.com:5558,IPv4:B.example.com:5557
[A.ef-2node-ansible-app] Current State = Up
[A.ef-2node-ansible-app] Last State Change = 2019-08-05 20:04:32
[A.ef-2node-ansible-app] Number of Connections = 2
[A.ef-2node-ansible-app] Number of Queued PDUs = 0
[A.ef-2node-ansible-app] Discovered = Dynamic
[A.ef-2node-ansible-app] Location Code = 7382436235611343951
[B.ef-2node-ansible-app] Node Name = A.ef-2node-ansible-app
[B.ef-2node-ansible-app] Network Address = IPv4:A.example.com:5558,IPv4:A.example.com:5557
[B.ef-2node-ansible-app] Current State = Up
[B.ef-2node-ansible-app] Last State Change = 2019-08-05 20:04:34
[B.ef-2node-ansible-app] Number of Connections = 2
[B.ef-2node-ansible-app] Number of Queued PDUs = 0
[B.ef-2node-ansible-app] Discovered = Dynamic
[B.ef-2node-ansible-app] Location Code = 11636435185532938412
```

Ansible tasks represents the docker command listed above :
- run docker command in shell
- list the results stored in NodeAresults veriable
```
- name: Run epadmin command on Node A
    shell: docker exec A.{{ projectId }} epadmin --servicename={{ projectId }} display cluster
    register: NodeAresults
    
- name: Node A
    debug: var=NodeAresults.stdout_lines
```
```
[INFO] TASK [Run epadmin command on Node A] *******************************************
[INFO] changed: [127.0.0.1]
[INFO] 

[INFO] TASK [Node A] ******************************************************************
[INFO] ok: [127.0.0.1] => 
[INFO]   NodeAresults.stdout_lines:
[INFO]   - '[A.ef-2node-ansible-app] Node Name = B.ef-2node-ansible-app'
[INFO]   - '[A.ef-2node-ansible-app] Network Address = IPv4:B.example.com:5558,IPv4:B.example.com:5557'
[INFO]   - '[A.ef-2node-ansible-app] Current State = Up'
[INFO]   - '[A.ef-2node-ansible-app] Last State Change = 2019-08-02 14:38:37'
[INFO]   - '[A.ef-2node-ansible-app] Number of Connections = 3'
[INFO]   - '[A.ef-2node-ansible-app] Number of Queued PDUs = 0'
[INFO]   - '[A.ef-2node-ansible-app] Discovered = Dynamic'
[INFO]   - '[A.ef-2node-ansible-app] Location Code = 7382436235611343951'
[INFO]   - '[B.ef-2node-ansible-app] Node Name = A.ef-2node-ansible-app'
[INFO]   - '[B.ef-2node-ansible-app] Network Address = IPv4:A.example.com:5558,IPv4:A.example.com:5557'
[INFO]   - '[B.ef-2node-ansible-app] Current State = Up'
[INFO]   - '[B.ef-2node-ansible-app] Last State Change = 2019-08-02 14:38:37'
[INFO]   - '[B.ef-2node-ansible-app] Number of Connections = 3'
[INFO]   - '[B.ef-2node-ansible-app] Number of Queued PDUs = 0'
[INFO]   - '[B.ef-2node-ansible-app] Discovered = Dynamic'
[INFO]   - '[B.ef-2node-ansible-app] Location Code = 11636435185532938412'
```

### Log in to the container to check node logs

Use the [docker exec](https://docs.docker.com/engine/reference/commandline/exec/) command to run bash :

```shell
$ docker exec -it A.ef-2node-ansible-app bash
[tibco@A /]$ cd /var/opt/tibco/streambase/node/A.ef-2node-ansible-app/logs/
[tibco@A logs]$ ls
System_administration.log  System_swcoordadmin.log  audit.log  bootstrap  deadlock.log  default-engine-for-com.tibco.ep.samples.docker.ef-2node-ansible-ef.log
```

Alternatively, use the [docker exec](https://docs.docker.com/engine/reference/commandline/exec/) command to run tail :

```shell
$ docker exec A.ef-2node-ansible-app tail -f /var/opt/tibco/streambase/node/A.ef-2node-ansible-app/logs/default-engine-for-com.tibco.ep.samples.docker.ef-2node-ansible-ef.log
2019-08-02 14:38:37.542000+0000 [171:main] INFO  com.tibco.ep.dtm.lifecycle: No user-defined Logback configuration, using product default configuration
...

```

### Stop and remove the containers

Use the [docker stop](https://docs.docker.com/engine/reference/commandline/stop/) and [docker rm](https://docs.docker.com/engine/reference/commandline/rm/) commands :

```shell
$ docker stop A.ef-2node-ansible-app
$ docker rm A.ef-2node-ansible-app
$ docker stop B.ef-2node-ansible-app
$ docker rm B.ef-2node-ansible-app
```

Ansible tasks corresponds to docker stop and dcoker rm commands listed above :
```
- name: Stop and remove container A
    docker_container:
      name: A.{{ projectId }}
      state: absent
    
- name: Stop and remove container B
    docker_container:
      name: B.{{ projectId }}
      state: absent
```

---
Copyright (c) 2018-2019, TIBCO Software Inc.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the copyright holder nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
