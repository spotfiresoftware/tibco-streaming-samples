# Structure : Application

This sample describes how to structure the application archive module of a maven aggregator project.

A [maven aggregator project](http://maven.apache.org/pom.html#Aggregation) consists of at least one application archive module and at least one fragment module.  
With multiple maven modules in one project, maven can work out dependencies across maven modules and build and test in the right order.

The directory structure consists of :

* **pom.xml** - [Maven aggregator project object model](http://maven.apache.org/pom.html#Aggregation) that references all the contained modules
* **[application]/pom.xml** - Maven project object module that controls the build, tests, installation and deployment of this application archive.  Must list the fragment dependencies.
* **[application]/src/main/configurations/** - Directory containing any configuration files.  These are included in the built application archive.
* **[application]/src/main/docker/base** - Directory containing docker file to build base image.
* **[application]/src/main/docker/application** - Directory containing docker file to build application image.
* **[application]/src/main/resources/** - Directory containing any resources files.  These are included in the built application archive.
* **[application]/src/site/** - Directory containing any site documentation.  The maven build process can create html from these and publish to a web site.
* **[application]/src/test/java/[package]/** - Directory containing any junit test cases
* **[application]/src/test/configurations/** - Directory containing any configuration files used by test cases.  These are *not* included in the built fragment.
* **[application]/src/test/resources/** - Directory containing any resources files used by test cases.  These are *not* included in the built fragment.

This sample's structure is shown below :

```
├── pom.xml
├── one or more fragment modules
├── application-application
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── configurations
│       │   │   ├── app.conf
│       │   │   └── defaultnode.conf
│       │   └── docker
│       │       ├── application
│       │       │   └── Dockerfile
│       │       └── base
│       │           ├── Dockerfile
│       │           └── start-node
│       ├── site
│       │   ├── markdown
│       │   │   ├── index.md
│       │   │   └── using.md
│       │   └── site.xml
│       └── test
│           ├── configurations
│           ├── java
│           │   └── com
│           │       └── tibco
│           │           └── ep
│           │               └── samples
│           │                   └── application
│           └── resources
```
