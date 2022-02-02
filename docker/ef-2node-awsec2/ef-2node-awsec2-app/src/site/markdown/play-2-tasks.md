# Play #2 - Selected Ansible playbook tasks


#### *** Task with [sts_assume_role](https://docs.ansible.com/ansible/latest/modules/sts_assume_role_module.html) module, to assume a role using AWS Security TokenService and obtain temporary credentials.
Project configuration files need to be updated.
``` 
  - name: Assume an existing role
    sts_assume_role:
      role_arn: "{{ role_arn }}"
      role_session_name: "{{ role_session_name }}"
      region: "{{ region }}"
    register: assumed_role
    when: sts

```

#### *** Task with [ec2_group](https://docs.ansible.com/ansible/latest/modules/ec2_group_module.html) module to create a security group in the specified region, based on rules listed below.
```
  - name: Create a security group for SSH access
    ec2_group:
      aws_access_key: "{{ assumed_role.sts_creds.access_key | default(omit) }}"
      aws_secret_key: "{{ assumed_role.sts_creds.secret_key | default(omit) }}"
      security_token: "{{ assumed_role.sts_creds.session_token | default(omit) }}"

      name: "{{ sg_name }}"
      description: "{{ sg_description }}"
      region: "{{ region }}"

      rules:
        - proto: tcp
          from_port: 22
          to_port: 22
          cidr_ip: "{{ cidr_ip }}"
          rule_desc: "{{ rule_desc }}"

```

#### *** Task with [ec2](https://docs.ansible.com/ansible/latest/modules/ec2_module.html) module to launch an ec2 instance based on details listed below. 
```
  - name: Create EC2 instance -- Almalinux -- {{ instance_type }}
    ec2:
      aws_access_key: "{{ assumed_role.sts_creds.access_key | default(omit) }}"
      aws_secret_key: "{{ assumed_role.sts_creds.secret_key  | default(omit) }}"
      security_token: "{{ assumed_role.sts_creds.session_token  | default(omit) }}"

      group: "{{ sg_name }}"
      instance_type: "{{ instance_type }}"
      image: "{{ image_id }}"
      wait: true
      region: "{{ region }}"
      keypair: "{{ keypair }}"
      count: "{{ count }}"
      instance_tags:
         Name: "ef-2node-awsec2-app"
         Type: "Docker_host"
    register: ec2
```

#### *** Task with [add_host](https://docs.ansible.com/ansible/latest/modules/add_host_module.html) module to add host to the ansible-playbook in-memory inventory.  
```
  - name: Update inventory list
    add_host:
      name: "{{ item.public_ip }}"
      groups: "AnsibleVms"
    with_items:
      - "{{ ec2.instances }}"
```

