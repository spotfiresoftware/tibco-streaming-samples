# Play #1 - Selected Ansible playbook tasks


#### *** Task with [copy](https://docs.ansible.com/ansible/latest/modules/copy_module.html) module, where source folder and destination folder need to be defined.
``` 
- name: Copy start-node script to base image work directory
  copy:
    src:  "{{ project_basedir }}/target/dependencies/{{ platform }}.zip"
    dest: "{{ project_build_directory }}/docker/base/maven/"
```

#### *** Task with [replace](https://docs.ansible.com/ansible/latest/modules/replace_module.html) module and loop. This is ansible recommended method to customize config/settings/text files during the playbook execution. Removing a string from file in specific path is also available in this module.
Search is based on defined regular expressions in the list below which is executed in a loop.  
```
- name: Update dockerfile before building base image
  replace:
    path: "{{ project_build_directory }}/docker/base/Dockerfile"
    regexp: "{{ item.search }}"
    replace: "{{ item.replace }}"
  with_items:
    - { search: '(^LABEL build-image=)(.*)$', replace: "LABEL build-image={{ projectId_ver }}" }
    - { search: '(^###Note:\s)(.*)$', replace: '###Note: LABEL statement build by Ansible playbook' }
```

#### *** Task with [docker_image](https://docs.ansible.com/ansible/latest/modules/docker_image_module.html) module to build an image. 
```
- name: Building SB base image - sbrt-base
  docker_image:
    build:
      path: "{{ project_build_directory }}/docker/base/"
      pull: no
    push: no
    name: sbrt-base
    repository: sbrt-base
    tag: "{{ sbrt_ver }}"
    source: build
```

#### *** Task with [docker_container](https://docs.ansible.com/ansible/latest/modules/docker_container_module.html) module to start a container based on a defined image. 
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
```

#### *** Task with [shell](https://docs.ansible.com/ansible/latest/modules/shell_module.html) module can execute shell commands on targets. In the task below, docker exec … command is run and results are registered in NodeAresults variable. After this is completed successfully, the next task displays the results based on module [debug](https://docs.ansible.com/ansible/latest/modules/debug_module.html).
```
- name: Run epadmin command on Node A
  shell: docker exec A.{{ projectId }} epadmin --servicename={{ projectId }} display cluster
  register: NodeAresults
  when: skipTests == 'false'
- name: Node A
  debug: var=NodeAresults.stdout_lines
```

#### *** Task with [docker_container](https://docs.ansible.com/ansible/latest/modules/docker_container_module.html) module to stop and remove container A.
```
- name: Stop and remove container A
  docker_container:
    name: A.{{ projectId }}
    state: absent
```

#### *** Task with [docker_login](https://docs.ansible.com/ansible/latest/modules/docker_login_module.html) module to login to DockerHub.
```
  - name: Login to DockerHub remote private registry
    docker_login:
      username: "{{ dockerhub_username }}"
      password: "{{ dockerhub_password }}"
      email: "{{ email_address }}"
```

#### *** Task with [docker_image](https://docs.ansible.com/ansible/latest/modules/docker_image_module.html) module to tag image and push to DockerHub. 
```
  - name: Tag docker image and push to DockerHub
    docker_image:
      name: docker/{{ projectId }}:{{ projectId_ver }}
      repository: "{{ dockerhub_username }}/{{ projectId }}:{{ projectId_ver }}"
      build:
        pull: no
      source: local
      push: yes
      state: present
    register: push
```

