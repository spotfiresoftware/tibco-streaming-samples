# Web: Using WebSocket WAR in EventFlow project

This sample describes how to use a WebSocket WAR in EventFlow project.

## EventFlow under test

In this sample the following simple EventFlow fragment is used :

![Density](images/Density.png)

## Create a WAR file which has web socket endpoint

See [websocket-war](../../../../websocket-war/src/main/java/com/tibco/ep/samples/web/websocket/websocketwar).
## Declare the WAR as a dependency

A WAR can be used in the same way as any other maven dependency:

```xml
    <dependency>
        <groupId>com.tibco.ep.samples.web</groupId>
        <artifactId>websocketwar</artifactId>
        <version>1.0.0</version>
        <type>war</type>
    </dependency>
``` 

## Provide a engine configuration for EventFlow use

A engine configuration is required to declare the functions available :

```scala
name = "javafunction-engine"
version = "1.0.0"
type = "com.tibco.ep.streambase.configuration.sbengine"

configuration = {
    StreamBaseEngine = {
        streamBase = {
            pluginFunctions = {
                java = {
                    Density = {
                        type = "simple"
                        alias = "density"
                        className = "com.tibco.ep.samples.javafunction.UtilFunctions"
                        autoArguments = true
                    }
                }
            }
        }
    }
}

```

## Running this sample from TIBCO StreamBase Studio&trade;

Use the **Run As -> EventFlow Fragment** menu option to run in TIBCO StreamBase Studio&trade;, and then enqueue tuples :

![RunFromStudio](images/studio.gif)

## Building this sample from TIBCO StreamBase Studio&trade; and running the unit test cases

Use the **Run As -> EventFlow Fragment Unit Test** menu option to build from TIBCO StreamBase Studio&trade; :

![RunFromStudio](images/studiounit.gif)

## Building this sample from the command line and running the unit test cases

Use the [maven](https://maven.apache.org) as **mvn install** to build from the command line or Continuous Integration system :

![maven](images/maven.gif)
