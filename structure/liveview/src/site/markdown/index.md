# Structure : LiveView

This sample describes how to structure a LiveView fragment.

A LiveView fragment directory structure consists of :

* **pom.xml** - Maven Project Object Module that controls the build, tests, installation and deployment of the project.
* **src/main/liveview/lv-user-webapps/** - Directory containing LiveView web applications (such as lvweb.war).
* **src/main/liveview/lv-web/plugins/** - Directory containing LiveView web plugins.
* **src/main/liveview/lv-web/theme/** - Directory containing LiveView web theme.
* **src/main/eventflow/[package]/** - Directory containing any EventFlow applications.
* **src/main/java/[package]/** - Directory containing any Java source.
* **src/main/configurations/** - Directory containing any configuration files.  These are included in the built fragment.
* **src/main/resources/** - Directory containing any resources files.  These are included in the built fragment.
* **src/site/** - Directory containing any site documentation.  The maven build process can create html from these and publish to a web site.
* **src/test/java/[package]/** - Directory containing any junit test cases
* **src/test/configurations/** - Directory containing any configuration files used by test cases.  These are *not* included in the built fragment.
* **src/test/resources/** - Directory containing any resources files used by test cases.  These are *not* included in the built fragment.

This sample's structure is shown below :

```
├── pom.xml
└── src
    ├── main
    │   ├── configurations
    │   │   └── engine.conf
    │   ├── eventflow
    │   │   └── com
    │   │       └── tibco
    │   │           └── ep
    │   │               └── samples
    │   │                   └── liveview
    │   ├── java
    │   │   └── com
    │   │       └── tibco
    │   │           └── ep
    │   │               └── samples
    │   │                   └── liveview
    │   ├── liveview
    │   │   ├── lv-user-webapps
    │   │   │   └── lvweb.war
    │   │   └── lv-web
    │   │       ├── plugins
    │   │       └── theme
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
        │                   └── liveview
        └── resources
```
