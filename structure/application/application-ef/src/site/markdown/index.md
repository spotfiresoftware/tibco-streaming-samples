# Structure : Application

This sample describes how to structure the fragment module of a maven aggregator project.

A [maven aggregator project](http://maven.apache.org/pom.html#Aggregation) consists of at least one application archive module and at least one fragment module.  
With multiple maven modules in one project, maven can work out dependencies across maven modules and build and test in the right order.

The directory structure consists of :

* **pom.xml** - [Maven aggregator project object model](http://maven.apache.org/pom.html#Aggregation) that references all the contained modules
* **[fragment]/pom.xml** - Maven project object module that controls the build, tests, installation and deployment of this fragment.
* **[fragment]/src/main/eventflow/[package]/** - Directory containing EventFlow applications.
* **[fragment]/src/main/java/[package]/** - Directory containing any Java source.
* **[fragment]/src/main/configurations/** - Directory containing any configuration files.  These are included in the built fragment.
* **[fragment]/src/main/resources/** - Directory containing any resources files.  These are included in the built fragment.
* **[fragment]/src/site/** - Directory containing any site documentation.  The maven build process can create html from these and publish to a web site.
* **[fragment]/src/test/java/[package]/** - Directory containing any junit test cases
* **[fragment]/src/test/configurations/** - Directory containing any configuration files used by test cases.  These are *not* included in the built fragment.
* **[fragment]/src/test/resources/** - Directory containing any resources files used by test cases.  These are *not* included in the built fragment.

```
.
├── pom.xml
├── application-eventflowfragment
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── configurations
│       │   ├── eventflow
│       │   │   └── com
│       │   │       └── tibco
│       │   │           └── ep
│       │   │               └── samples
│       │   │                   └── application
│       │   │                       ├── application_eventflowfragment.sbapp
│       │   │                       └── application_eventflowfragment.sblayout
│       │   ├── java
│       │   │   └── com
│       │   │       └── tibco
│       │   │           └── ep
│       │   │               └── samples
│       │   │                   └── application
│       │   └── resources
│       ├── site
│       │   ├── markdown
│       │   │   ├── index.md
│       │   │   └── using.md
│       │   └── site.xml
│       └── test
│           ├── configurations
│           ├── java
│           │   └── com
│           │       └── tibco
│           │           └── ep
│           │               └── samples
│           │                   └── application
│           │                       └── TestCase.java
│           └── resources
├── application archive module
```
