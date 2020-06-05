# Web: Using OpenAPI generated WAR in EventFlow project

This sample describes how to use a WAR which is created with OpenAPI Code Generation tool in an EventFlow project.

* [Create a WAR file with OpenAPI Code Generation tool](#create-war)
* [Create a no-op EventFlow fragment and declare the WAR as a dependency](#declare-the-war-as-a-dependency)
* [Running this sample from TIBCO StreamBase Studio&trade;](#running-this-sample-from-tibco-streambase-studiotrade)
* [Using "epadmin display web" command to retrieve information about web server](#using-epadmin-display-web-command-to-retrieve-information)
* [Using HelpUI to send request to test endpoint](#using-helpui)
* [Building this sample from the command line and running the integration test cases](#building-this-sample-from-the-command-line-and-running-the-integration-test-cases)


<a name="create-war"></a>

## Create a WAR file with OpenAPI Code Generation tool
See [openapi-server-war](../../../../openapi-server-war/src/site/markdown/index.md).
The WAR provides a GET endpoint which path is **http://{webserver-hostname}:{webserver-port-number}/openapi-server-war/test**.


<a name="declare-the-war-as-a-dependency"></a>

## Create an EventFlow fragment and declare the WAR as a dependency
This sample contains [a no-op EventFlow file](../../main/eventflow/com/tibco/ep/samples/web/openapi/server/eventflow/Demo.sbapp),  
that represent a simply runnable Eventflow fragment.  To use the WAR, just add the WAR into fragment's pom.xml in the same 
way as any other maven dependency:

```xml
    <dependency>
        <groupId>com.tibco.ep.samples.web</groupId>
        <artifactId>openapi-server-war</artifactId>
        <version>1.0.0</version>
        <type>war</type>
    </dependency>
``` 

<a name="running-this-sample-from-tibco-streambase-studiotrade"></a>

## Running this sample from TIBCO StreamBase Studio&trade;
Use the **Run As -> EventFlow Fragment** menu option to run in TIBCO StreamBase Studio&trade;:


<a name="using-epadmin-display-web-command-to-retrieve-information"></a>

## Using "epadmin display web" command to retrieve Web Help UI Address
The information we need is **Web Help UI Address**
![DisplayWeb](images/epadmin.gif)


<a name="using-helpui"></a>
## Send request to the /test endpoint provided by the WAR
Open a web browser, enter **Web Help UI Address**, select **openapi-server-war** from the **Select a Web Service** drop-down list, 
choose the **GET /test** endpoint and click **Try it out**. Then click **Execute** button, a popping up window will ask for user name and password. 
Since we use default-realm in this sample, which does NOT require password when a connection originates from a trusted address, 
just enter computer username as the username, and no password is needed, then "Hello, TIBCO!" shows up in the **Responses** section with Code 200.
![Help UI](images/helpui.gif)


<a name="building-this-sample-from-the-command-line-and-running-the-integration-test-cases"></a>

## Building this sample from the command line and running the integration test cases

In this sample, an integration test is defined in the **pom.xml** file. The test will:

* Start node A
* Trigger [OpenAPIWARTest](../../test/java/com/tibco/ep/samples/web/openapi/server/OpenAPIWARTest.java): it uses a Jersey web client which sends a request to the **/openapi-server-war/test** endpoint 
of OpenAPI Generated WAR, and validates the response is **200_OK** with a message **Hello, TIBCO!**.
* Stop node A

Use the [maven](https://maven.apache.org) as **mvn install** to build from the command line or Continuous Integration system:

![maven](images/maven.gif)

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
