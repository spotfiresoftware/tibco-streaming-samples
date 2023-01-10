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

---
Copyright (c) 2018-2023 Cloud Software Group, Inc.

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
