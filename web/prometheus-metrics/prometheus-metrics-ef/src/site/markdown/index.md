# Web : Prometheus Metrics - EventFlow project

In this sample, the Metronome and Map send out a tuple with a random length (< 10 characters) string every second, 
then the stream passes the StringLengthCounter which integrates a counter with a property *length*, this counter records 
the count of strings in different length.

## Adding a dependency to the Prometheus Metrics WAR

The EventFlow POM is updated with a dependency to the WAR:

```xml
    <dependencies>
        <dependency>
            <groupId>com.tibco.ep.samples.web</groupId>
            <artifactId>prometheus-metrics-war</artifactId>
            <version>1.0.0</version>
            <type>war</type>
        </dependency>
    </dependencies>
```

## Running this sample from TIBCO StreamBase Studio&trade;

Use the **Run As -> EventFlow Fragment** menu option to run in TIBCO StreamBase Studio&trade;

See this the following capture from the NAR EventFlow build:

![studio](../../../../../../nativelibrary/nar/nar-eventflow/src/site/resources/images/studiounit.gif)


## Building this sample from the command line

Use the [maven](https://maven.apache.org) as **mvn install** to build from the command line or Continuous Integration system :

See this the following capture from the NAR EventFlow build:

![maven](../../../../../../nativelibrary/nar/nar-eventflow/src/site/resources/images/maven.gif)

---
Copyright (c) 2020, TIBCO Software Inc.

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
