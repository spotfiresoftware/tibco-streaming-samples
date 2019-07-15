# HA : Cluster aware polling

This sample describes how to build an EventFlow fragment with a cluster-aware adapter polling the filesystem.

* [Enable cluster aware settings in adapter](#enable-cluster-aware-settings-in-adapter)
* [Design notes](#design-notes)
* [Running this sample from TIBCO StreamBase Studio&trade;](#running-this-sample-from-tibco-streambase-studio-trade)
* [Building this sample from TIBCO StreamBase Studio&trade; and running the unit test cases](#building-this-sample-from-tibco-streambase-studio-trade-and-running-the-unit-test-cases)
* [Building this sample from the command line and running the unit test cases](#building-this-sample-from-the-command-line-and-running-the-unit-test-cases)

<a name="enable-cluster-aware-settings-in-adapter"></a>

## Enable cluster aware settings in adapter

A file monitor adapter is used to poll the filesystem for new files.  This adapter is configured
to be **cluster aware** - that is, the adapter is started and stopped depending on the state of the cluster.

![Clusteraware settings](images/studioclusterawaresettings.png)

<a name="design-notes"></a>

## Design notes

The cluster aware option **Active on a single node in the cluster** was selected, hence :

* Only one node in the cluster will poll the filesystem regardless of the number of nodes
* Should the node polling the filesystem fail, another node in the cluster will start polling

<a name="running-this-sample-from-tibco-streambase-studio-trade"></a>

## Running this sample from TIBCO StreamBase Studio&trade;

Use the **Run As -> EventFlow Fragment** menu option to run in TIBCO StreamBase Studio&trade;, and then write some files
into the polling directory.

Note that here we are unit testing the business logic rather than cluster aware - in this sample we test cluster aware in
the application archive integration test cases.

![RunFromStudio](images/studio.gif)

<a name="building-this-sample-from-tibco-streambase-studio-trade-and-running-the-unit-test-cases"></a>

## Building this sample from TIBCO StreamBase Studio&trade; and running the unit test cases

Use the **Run As -> EventFlow Fragment Unit Test** menu option to build from TIBCO StreamBase Studio&trade; :

![RunFromStudio](images/studiounit.gif)

<a name="building-this-sample-from-the-command-line-and-running-the-unit-test-cases"></a>

## Building this sample from the command line and running the unit test cases

Use the [maven](https://maven.apache.org) as **mvn install** to build from the command line or Continuous Integration system :

![maven](images/maven.gif)