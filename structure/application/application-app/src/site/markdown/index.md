
[comment]: # (  Copyright \(C\) 2018-2019, TIBCO Software Inc.                               )

[comment]: # (                                                                               )

[comment]: # (  Redistribution and use in source and binary forms, with or without           )

[comment]: # (  modification, are permitted provided that the following conditions are met:  )

[comment]: # (                                                                               )

[comment]: # (  1. Redistributions of source code must retain the above copyright notice,    )

[comment]: # (     this list of conditions and the following disclaimer.                     )

[comment]: # (                                                                               )

[comment]: # (  2. Redistributions in binary form must reproduce the above copyright notice, )

[comment]: # (     this list of conditions and the following disclaimer in the documentation )

[comment]: # (     and/or other materials provided with the distribution.                    )

[comment]: # (                                                                               )

[comment]: # (  3. Neither the name of the copyright holder nor the names of its contributors)

[comment]: # (     may be used to endorse or promote products derived from this software     )

[comment]: # (     without specific prior written permission.                                )

[comment]: # (                                                                               )

[comment]: # (  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  )

[comment]: # (  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE    )

[comment]: # (  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE   )

[comment]: # (  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE    )

[comment]: # (  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR          )

[comment]: # (  CONSEQUENTIAL DAMAGES \(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF        )

[comment]: # (  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS     )

[comment]: # (  INTERRUPTION\) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN     )

[comment]: # (  CONTRACT, STRICT LIABILITY, OR TORT \(INCLUDING NEGLIGENCE OR OTHERWISE\)    )

[comment]: # (  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE   )

[comment]: # (  POSSIBILITY OF SUCH DAMAGE.                                                  )

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
