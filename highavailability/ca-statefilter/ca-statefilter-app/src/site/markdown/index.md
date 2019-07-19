# HA : Cluster aware state filter

This sample describes how to deploy an EventFlow fragment in a 2-node cluster aware configuration.

* [Machines and nodes](#machines-and-nodes)
* [Define the node deployment configuration](#define-the-node-deployment-configuration)
* [Design notes](#design-notes)
* [Failure scenarios](#failure-scenarios)
* [Building this sample from the command line and running the integration test cases](#building-this-sample-from-the-command-line-and-running-the-integration-test-cases)

<a name="machines-and-nodes"></a>

## Machines and nodes

In this sample we name the machines as **A**,  which hosts the StreamBase node **A**, 
and **B**, which hosts the StreamBase node **B**.

![nodes](images/two-node-cluster-aware.svg)

<a name="define-the-node-deployment-configuration"></a>

## Define the node deployment configuration

This sample uses no special node deployment configuration, so we have : :

```scala
name = "ca-statefilter-node"
version = "1.0.0"
type = "com.tibco.ep.dtm.configuration.node"

configuration = {
    NodeDeploy = {
        nodes = {
            "${EP_NODE_NAME}" = {
                engines = {
                    ca-statefilter-app = {
                        fragmentIdentifier = "com.tibco.ep.samples.highavailability.ca-statefilter-ef"
                    }
                }
            }
        }
    }
}
```

<a name="design-notes"></a>

## Design notes

* The event flow fragment defines that only one state filter operator is running in the cluster

<a name="failure-scenarios"></a>

## Failure scenarios

The main failure cases for this deployment are outlined below :

Failure case   | Behaviour on failure | Steps to resolve | Notes
--- | --- | --- | ---
Machine A fails | State filter operator on node B is automatically started  | 1 Fix machine A<br/>2 Use **epadmin install node** and **epadmin start node**<br/>3 Note that the operator remains running on node B | 1 No data loss<br/>2 No service loss
Machine B fails | No impact | 1 Fix machine B<br/>2 Use **epadmin install node** and **epadmin start node** | 1 No data loss<br/>2 No service loss

With a 2 node configuration node quorums don't apply hence a multi-master scenario is possible on network failure.
Multiple network paths ( such as network bonding ) is recommended.

<a name="building-this-sample-from-the-command-line-and-running-the-integration-test-cases"></a>

## Building this sample from the command line and running the integration test cases

In this sample, some HA integration test cases are defined in the pom.xml that :

* start nodes A & B
* use **epadmin display operator** to check the operator status on both nodes ( state filter operator is started on node A only )
* stop node A
* use **epadmin display operator** to check the operator status on both nodes ( state filter operator is now started on node B )
* stop node B

:warning: This does not constitute an exhaustive non-functional test plan

Use the [maven](https://maven.apache.org) as **mvn install** to build from the command line or Continuous Integration system :

![maven](images/maven.gif)
