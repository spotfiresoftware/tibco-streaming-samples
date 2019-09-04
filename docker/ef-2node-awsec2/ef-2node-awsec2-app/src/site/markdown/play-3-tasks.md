# Play #3 - Selected Ansible playbook tasks


### Below you can find selected ansible playbook tasks with brief description. 

#### *** Task with module [get_url](https://docs.ansible.com/ansible/latest/modules/get_url_module.html), to download files from HTTP, HTTPS, or FTP to ec2 instance.
``` 
  - name: Install Docker CE on CentOs7 -- geting docker-ce repo
    get_url:
      url: https://download.docker.com/linux/centos/docker-ce.repo
      dest: /etc/yum.repos.d/docker-ce.repo
```

#### *** Task with module [package](https://docs.ansible.com/ansible/latest/modules/package_module.html) to install docker-ce on ec2 instance.
```
  - name: Install Docker CE on CentOs7 -- install docker-ce
    package:
      name: docker-ce
      state: latest
```

#### *** Task with module [copy](https://docs.ansible.com/ansible/latest/modules/copy_module.html) to copy a selected files (list of files in \[ ... ] ) to ec2 instance. Source and destination folder needs to be defined. Destination folder will be created if not exist. Also you can specify new owner and group for the copied files as well as set new permisions. 
```
  - name: Copy files to EC2 instance (files in additional-scripts folder)
    copy:
      src: "{{ project_basedir }}/target/classes/{{ item }}"
      dest: "/home/centos/additional-scripts/"
      owner: centos
      group: centos
      mode: 0744
    with_items:
      [ 1-start_cluster.sh, 2-validate_cluster.sh, 3-stop_cluster.sh ]
```

