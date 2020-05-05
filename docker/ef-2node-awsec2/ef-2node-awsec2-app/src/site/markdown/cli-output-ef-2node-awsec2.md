### Ansible Palybook - Plays # 1 .. 4 - example of command line output

The listing below shows Play #1 and #2 executed on Ansible management host (localhost / 127.0.0.1), 
Play #3 and #4 executed on EC2 instance (vm with IP: 54.159.20.34).

```ansible
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
[INFO] 
[INFO] AWS-EC2: 2-node EventFlow - EventFlow Fragment   [ep-eventflow-fragment]
[INFO] AWS-EC2: 2-node EventFlow - Application                 [ep-application]
[INFO] AWS-EC2: 2-node EventFlow                                          [pom]
[INFO] 
[INFO] -----------< com.tibco.ep.samples.docker:ef-2node-awsec2-ef >-----------
[INFO] Building AWS-EC2: 2-node EventFlow - EventFlow Fragment 1.0.0      [1/3]
[INFO] -----------------------[ ep-eventflow-fragment ]------------------------
...
[INFO] 
[INFO] PLAY [Play 1 -- Create StreamBase base and application docker image based on Centos7] ***
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
[INFO] changed: [127.0.0.1] => (item={u'search': u'(^LABEL build-image=)(.*)$', u'replace':
       u'LABEL build-image=1.0.0'})
[INFO] ok: [127.0.0.1] => (item={u'search': u'(^###Note:\\s)(.*)$', u'replace': u'###Note: 
       LABEL statement build by Ansible playbook'})
[INFO] 
[INFO] TASK [Remove docker image if exists from previous build] ***********************
[INFO] ok: [127.0.0.1]
[INFO] 
[INFO] TASK [Building SB base image - sbrt-base] **************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Clean up work directory for base image] **********************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Remove intermediate image with LABEL build-image=1.0.0] ******************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Create source directory for app image] ***********************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Create a copy of platform zip file into application docker image work directory] ***
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Update dockerfile before building app image] *****************************
[INFO] ok: [127.0.0.1] => (item={u'search': u'(^FROM\\s)(.*)$', u'replace': 
       u'FROM sbrt-base:11.0.0-SNAPSHOT'})
[INFO] changed: [127.0.0.1] => (item={u'search': u'(^###Note:\\s)(.*)$', u'replace': 
       u'###Note: FROM statement build by Ansible playbook'})
[INFO] changed: [127.0.0.1] => (item={u'search': u'(^LABEL build-image=)(.*)$', u'replace': 
       u'LABEL build-image=1.0.0'})
[INFO] changed: [127.0.0.1] => (item={u'search': u'(^###Note:\\s)(.*)$', u'replace': 
       u'###Note: LABEL statement build by Ansible playbook'})
[INFO] 
[INFO] TASK [Remove docker image if exists from previous build] ***********************
[INFO] ok: [127.0.0.1]
[INFO] 
[INFO] TASK [Building StreamBase application image] ***********************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Clean up work directory for application image] ***************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Update bash file before copying to EC2 instance (files in additional-scripts folder)] 
[INFO] changed: [127.0.0.1] => (item={u'search': u'\\bdocker\\/\\b', u'replace': u'<username>/'})
[INFO] 
[INFO] TASK [Create example.com network] **********************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Start container A.ef-2node-awsec2-app] ***********************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Waiting for Node A to start] *********************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Start container B.ef-2node-awsec2-app] ***********************************
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
[INFO]   - '[A.ef-2node-awsec2-app] Node Name = B.ef-2node-awsec2-app'
[INFO]   - '[A.ef-2node-awsec2-app] Network Address = IPv4:B.example.com:5558,IPv4:B.example.com:5557'
[INFO]   - '[A.ef-2node-awsec2-app] Current State = Up'
[INFO]   - '[A.ef-2node-awsec2-app] Last State Change = 2019-08-29 18:56:25'
[INFO]   - '[A.ef-2node-awsec2-app] Number of Connections = 2'
[INFO]   - '[A.ef-2node-awsec2-app] Number of Queued PDUs = 0'
[INFO]   - '[A.ef-2node-awsec2-app] Discovered = Dynamic'
[INFO]   - '[A.ef-2node-awsec2-app] Location Code = 6931605986115105920'
[INFO]   - '[B.ef-2node-awsec2-app] Node Name = A.ef-2node-awsec2-app'
[INFO]   - '[B.ef-2node-awsec2-app] Network Address = IPv4:A.example.com:5558,IPv4:A.example.com:5557'
[INFO]   - '[B.ef-2node-awsec2-app] Current State = Up'
[INFO]   - '[B.ef-2node-awsec2-app] Last State Change = 2019-08-29 18:56:25'
[INFO]   - '[B.ef-2node-awsec2-app] Number of Connections = 1'
[INFO]   - '[B.ef-2node-awsec2-app] Number of Queued PDUs = 0'
[INFO]   - '[B.ef-2node-awsec2-app] Discovered = Dynamic'
[INFO]   - '[B.ef-2node-awsec2-app] Location Code = 4265556644685878787'
[INFO] 
[INFO] TASK [Run epadmin command on Node B] *******************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Node B] ******************************************************************
[INFO] ok: [127.0.0.1] => 
[INFO]   NodeBresults.stdout_lines:
[INFO]   - '[B.ef-2node-awsec2-app] Node Name = A.ef-2node-awsec2-app'
[INFO]   - '[B.ef-2node-awsec2-app] Network Address = IPv4:A.example.com:5558,IPv4:A.example.com:5557'
[INFO]   - '[B.ef-2node-awsec2-app] Current State = Up'
[INFO]   - '[B.ef-2node-awsec2-app] Last State Change = 2019-08-29 18:56:25'
[INFO]   - '[B.ef-2node-awsec2-app] Number of Connections = 1'
[INFO]   - '[B.ef-2node-awsec2-app] Number of Queued PDUs = 0'
[INFO]   - '[B.ef-2node-awsec2-app] Discovered = Dynamic'
[INFO]   - '[B.ef-2node-awsec2-app] Location Code = 4265556644685878787'
[INFO]   - '[A.ef-2node-awsec2-app] Node Name = B.ef-2node-awsec2-app'
[INFO]   - '[A.ef-2node-awsec2-app] Network Address = IPv4:B.example.com:5558,IPv4:B.example.com:5557'
[INFO]   - '[A.ef-2node-awsec2-app] Current State = Up'
[INFO]   - '[A.ef-2node-awsec2-app] Last State Change = 2019-08-29 18:56:25'
[INFO]   - '[A.ef-2node-awsec2-app] Number of Connections = 2'
[INFO]   - '[A.ef-2node-awsec2-app] Number of Queued PDUs = 0'
[INFO]   - '[A.ef-2node-awsec2-app] Discovered = Dynamic'
[INFO]   - '[A.ef-2node-awsec2-app] Location Code = 6931605986115105920'
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
[INFO] TASK [Login to DockerHub remote private registry] ******************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Tag docker image and push to DockerHub] **********************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Logout from DockerHub] ***************************************************
[INFO] ok: [127.0.0.1]
[INFO] 
[INFO] TASK [Untagged successfully pushed image] **************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Platform check] **********************************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Remove Docker images [Linux]] ********************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Remove Docker images] ****************************************************
[INFO] skipping: [127.0.0.1]
[INFO] 
[INFO] PLAY [Play 2 -- Create security group and ec2 instance(s)] *********************
[INFO] 
[INFO] TASK [Assume an existing role] *************************************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Create a security group for SSH access ] *********************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Create EC2 instance -- Centos7 -- t2.small] ******************************
[INFO] changed: [127.0.0.1]
[INFO] 
[INFO] TASK [Update inventory list] ***************************************************
[INFO] changed: [127.0.0.1]
	 - instance(s) details are listed here -
[INFO] 
[INFO] TASK [Wait for SSH to be available] ********************************************
[INFO] ok: [127.0.0.1]
	 - instance(s) details are listed here -
[INFO] 
[INFO] PLAY [Play 3 -- Install Docker on ec2 instance(s) and pull docker image] *******
[INFO] 
[INFO] TASK [Install Docker CE on CentOs7 -- getting docker-ce repo] ******************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Install Docker CE on CentOs7 -- install docker-ce] ***********************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Install Docker CE on CentOs7 -- install epel-release] ********************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Install Docker CE on CentOs7 -- install python-pip] **********************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Install Docker CE on CentOs7 -- install python-py] ***********************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Install Docker CE on CentOs7 -- add user centos to docker group] *********
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Install Docker CE on CentOs7 -- start and enable docker service] *********
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Login to DockerHub remote private registry] ******************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Pull image from DockerHub] ***********************************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Logout from DockerHub] ***************************************************
[INFO] ok: [54.159.20.34]
[INFO] 
[INFO] TASK [Copy files to EC2 instance(s) (files in additional-scripts folder)] ******
[INFO] changed: [54.159.20.34] => (item=1-start_cluster.sh)
[INFO] changed: [54.159.20.34] => (item=2-validate_cluster.sh)
[INFO] changed: [54.159.20.34] => (item=3-stop_cluster.sh)
[INFO] 
[INFO] PLAY [Play 4 -- Start, validate and stop cluster] ******************************
[INFO] 
[INFO] TASK [Create example.com network] **********************************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Start container A.ef-2node-awsec2-app] ***********************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Waiting for Node A to start] *********************************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Start container B.ef-2node-awsec2-app] ***********************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Waiting for Node B to start] *********************************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Run epadmin command on Node A] *******************************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Node A] ******************************************************************
[INFO] ok: [54.159.20.34] => 
[INFO]   NodeAresults.stdout_lines:
[INFO]   - '[A.ef-2node-awsec2-app] Node Name = B.ef-2node-awsec2-app'
[INFO]   - '[A.ef-2node-awsec2-app] Network Address = IPv4:B.example.com:5558,IPv4:B.example.com:5557'
[INFO]   - '[A.ef-2node-awsec2-app] Current State = Up'
[INFO]   - '[A.ef-2node-awsec2-app] Last State Change = 2019-08-29 19:04:00'
[INFO]   - '[A.ef-2node-awsec2-app] Number of Connections = 4'
[INFO]   - '[A.ef-2node-awsec2-app] Number of Queued PDUs = 0'
[INFO]   - '[A.ef-2node-awsec2-app] Discovered = Dynamic'
[INFO]   - '[A.ef-2node-awsec2-app] Location Code = 6931605986115105920'
[INFO]   - '[B.ef-2node-awsec2-app] Node Name = A.ef-2node-awsec2-app'
[INFO]   - '[B.ef-2node-awsec2-app] Network Address = IPv4:A.example.com:5558,IPv4:A.example.com:5557'
[INFO]   - '[B.ef-2node-awsec2-app] Current State = Up'
[INFO]   - '[B.ef-2node-awsec2-app] Last State Change = 2019-08-29 19:04:01'
[INFO]   - '[B.ef-2node-awsec2-app] Number of Connections = 2'
[INFO]   - '[B.ef-2node-awsec2-app] Number of Queued PDUs = 0'
[INFO]   - '[B.ef-2node-awsec2-app] Discovered = Dynamic'
[INFO]   - '[B.ef-2node-awsec2-app] Location Code = 4265556644685878787'
[INFO] 
[INFO] TASK [Run epadmin command on Node B] *******************************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Node B] ******************************************************************
[INFO] ok: [54.159.20.34] => 
[INFO]   NodeBresults.stdout_lines:
[INFO]   - '[B.ef-2node-awsec2-app] Node Name = A.ef-2node-awsec2-app'
[INFO]   - '[B.ef-2node-awsec2-app] Network Address = IPv4:A.example.com:5558,IPv4:A.example.com:5557'
[INFO]   - '[B.ef-2node-awsec2-app] Current State = Up'
[INFO]   - '[B.ef-2node-awsec2-app] Last State Change = 2019-08-29 19:04:01'
[INFO]   - '[B.ef-2node-awsec2-app] Number of Connections = 2'
[INFO]   - '[B.ef-2node-awsec2-app] Number of Queued PDUs = 0'
[INFO]   - '[B.ef-2node-awsec2-app] Discovered = Dynamic'
[INFO]   - '[B.ef-2node-awsec2-app] Location Code = 4265556644685878787'
[INFO]   - '[A.ef-2node-awsec2-app] Node Name = B.ef-2node-awsec2-app'
[INFO]   - '[A.ef-2node-awsec2-app] Network Address = IPv4:B.example.com:5558,IPv4:B.example.com:5557'
[INFO]   - '[A.ef-2node-awsec2-app] Current State = Up'
[INFO]   - '[A.ef-2node-awsec2-app] Last State Change = 2019-08-29 19:04:00'
[INFO]   - '[A.ef-2node-awsec2-app] Number of Connections = 4'
[INFO]   - '[A.ef-2node-awsec2-app] Number of Queued PDUs = 0'
[INFO]   - '[A.ef-2node-awsec2-app] Discovered = Dynamic'
[INFO]   - '[A.ef-2node-awsec2-app] Location Code = 6931605986115105920'
[INFO] 
[INFO] TASK [Stop and remove container A] *********************************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Stop and remove container B] *********************************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] TASK [Remove example.com network] **********************************************
[INFO] changed: [54.159.20.34]
[INFO] 
[INFO] PLAY RECAP *********************************************************************
[INFO] 127.0.0.1	: ok=39  changed=32  unreachable=0  failed=0  skipped=1  rescued=0  ignored=0   
[INFO] 54.159.20.34	: ok=23  changed=20  unreachable=0  failed=0  skipped=0  rescued=0  ignored=0   
...
[INFO] Reactor Summary for AWS-EC2: 2-node EventFlow 1.0.0:
[INFO] 
[INFO] AWS-EC2: 2-node EventFlow - EventFlow Fragment ..... SUCCESS [  5.925 s]
[INFO] AWS-EC2: 2-node EventFlow - Application ............ SUCCESS [12:25 min]
[INFO] AWS-EC2: 2-node EventFlow .......................... SUCCESS [  0.508 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  12:33 min
[INFO] Finished at: 2019-08-29T15:04:38-04:00
[INFO] ------------------------------------------------------------------------
```
