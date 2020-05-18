# Web : Using generated web client

This sample describes how to use generated web client to communication with Health check web service.
Health check web service provides a GET endpoint for monitoring node status.  After the node started, 
the **Health check web service** is automatically deployed on the node web server.  

* [Create an EventFlow fragment and declare the web client as a test dependency](#declare-the-client-as-a-dependency)
* [Building this sample from TIBCO StreamBase Studio&trade; and running the unit test cases](#building-this-sample-from-tibco-streambase-studio-trade-and-running-the-unit-test-cases)
* [Building this sample from the command line and running the unit test cases](#building-this-sample-from-the-command-line-and-running-the-unit-test-cases)

<a name="declare-the-client-as-a-dependency"></a>

## Create an EventFlow fragment and declare the web client as a test dependency
Since the aim is demonstrating how to use the generated web client, this sample contains [a no-op EventFlow file](../../main/eventflow/com/tibco/ep/samples/web/openapi/eventflow/Demo.sbapp),  
that represent a simply runnable Eventflow fragment and not related with the demo.  To use the generated web client, just add the jar into fragment's pom.xml in the same 
way as any other maven dependency:

```xml
    <dependency>
        <groupId>com.tibco.ep.samples.web</groupId>
        <artifactId>openapi-client</artifactId>
        <version>1.0.0</version>
        <scope>test</scope>
    </dependency>
``` 
Since we are going to use it in the demo test [OpenAPIClientTest](../../../src/test/java/com/tibco/ep/samples/web/openapi/OpenAPIClientTest.java),  we mark the scope of the dependency is **test**.  
In the **com.tibco.ep.samples.web.openapi.OpenAPIClientTest.getStatusTest**, we uses the generated API client talk to Health check web service */status* endpoint, and validate the response.  
This web service is integrated with the authentication services provided by the node web server, which means a valid username:password pair must to be provided for requesting the endpoints.  
In this example, we use the default authentication and system username as the username, no password is needed.


<a name="building-this-sample-from-tibco-streambase-studio-trade-and-running-the-unit-test-cases"></a>

## Building this sample from TIBCO StreamBase Studio&trade; and running the unit test cases

Use the **Run As -> EventFlow Fragment Unit Test** menu option to build from TIBCO StreamBase Studio&trade; :

![RunTestFromStudio](images/studio.gif)

Results are displayed in the console and junit windows.

<a name="building-this-sample-from-the-command-line-and-running-the-unit-test-cases"></a>


## Building this sample from the command line and running the unit test cases

Use the [maven](https://maven.apache.org) as **mvn install** to build from the command line or Continuous Integration system:

![Maven](images/maven.gif)


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
