# Maintenance Notes #

These samples illustrate a lot of functionality, which makes use of a
lot of different Maven plugins and external tools and
services. Because of the way Maven project construction works, some
projects are not active by default, because validating or running them
would require prerequisites that many users would not normally
have.

## Setup ##

A comprehensive audit of these samples require the following
tools that are often third party downloads or available only from
add-on package repositories.

* [Docker](https://www.docker.com)
* [Ansible](https://www.ansible.com)
* [Helm](https://helm.sh)

You need to install these tools so that at least some basic validation
can be done projects, which in turn allows Maven to validate the
projects.

## Basics ##

Each Spotfire Streaming release eventually gets a branch named for the
version. The latest release version becomes the default branch
for this repository.

The sample project versions don't change; instead, the versions of the
sample-specific parent POMs (which run parallel to the ordinary parent
POMs used by Streaming projects) are changed using the
[`versions:update-parent` goal](https://www.mojohaus.org/versions-maven-plugin/update-parent-mojo.html)
denoted by the `parentVersion` parameter for the
next version. Transitions to the next snapshot version need
`-DallowSnapshots=true` as well.

When transitioning to release versions, internal Spotfire developers need
to use special Maven settings that allow access to release canididate
artifact repositories.

## Enabling Specialized Projects ##

As of now, some skipping of feature-specific functionality is
implemented by using profiles to conditionally include the worker
modules in the reactor build. **If these modules are not included
during version maintenance operations, they will retain stale
version references.** So, we are currently obliged to add the
following `mvn` command line options during such operations:

* `-PactiveAnsible` for [docker/ef-2node-ansible](docker/ef-2node-ansible).

Which then leaves a base command of:

    mvn -PactiveAnsible versions:update-parent ...

## Regenerating Sample Indices ##

After a branch's parent versions are updated, you should update the
indices, which will make sure that all the sample documentation is linked
from the top, and the correct Streaming versions are mentioned.

Run this command:

    [ MVN_OPTIONS=...more mvn parameters... ] ./EPDev/update-indices
   
You must be able to build all the samples locally because the updates
require that all project depdendencies are available, including
dependencies between the projects. So, Docker and Ansible need to
be installed and functional because of the container technology
samples.

Then inspect the results, commit, and push.

## Futures ##

It would be nice to get rid of the reliance on profiles because they
cannot be specified in the preferences for Eclipse m2e, although
Maven launch configurations can specify them.

One approach would be always to always include modules, and skip
the specialized executions by other means.

Another approach is define the property `updatingVersionPhase` during
maintenance operations, and skip specialized executions based on that
property. But combining this with other techniques for end-users who
want to activate the specialized executions could be a challenge.
