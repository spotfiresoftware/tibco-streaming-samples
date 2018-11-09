# Structure : Java

This sample describes how to structure a Java fragment.

A Java fragment directory structure consists of :

* **pom.xml** - Maven Project Object Module that controls the build, tests, installation and deployment of the project.
* **src/main/java/[package]/** - Directory containing Java source.
* **src/main/configurations/** - Directory containing any configuration files.  These are included in the built fragment.
* **src/main/resources/** - Directory containing any resources files.  These are included in the built fragment.
* **src/site/** - Directory containing any site documentation.  The maven build process can create html from these and publish to a web site.
* **src/test/java/[package]/** - Directory containing any junit test cases
* **src/test/configurations/** - Directory containing any configuration files used by test cases.  These are *not* included in the built fragment.
* **src/test/resources/** - Directory containing any resources files used by test cases.  These are *not* included in the built fragment.

This sample's structure is shown below :

```
.
├── pom.xml
└── src
    ├── main
    │   ├── configurations
    │   │   └── engine.conf
    │   ├── java
    │   │   └── com
    │   │       └── tibco
    │   │           └── ep
    │   │               └── samples
    │   │                   └── java
    │   │                       └── Main.java
    │   └── resources
    ├── site
    │   ├── markdown
    │   │   ├── index.md
    │   │   └── using.md
    │   └── site.xml
    └── test
        ├── configurations
        ├── java
        │   └── com
        │       └── tibco
        │           └── ep
        │               └── samples
        │                   └── java
        │                       └── TestCase.java
        └── resources
```
