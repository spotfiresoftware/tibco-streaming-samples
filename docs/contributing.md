# Contributing

As a public repository, we encourage and welcome contributions.  

## General

We follow the GitHub flow process, including :

* Repository is public - ie read-only to everyone.
* Collaborators are users with direct push access to the repository
    * Are from Spotfire Streaming engineering team
    * Are part of github team @streambasesamples
* Any user can submit a pull request
    * The request should mention @streambasesamples
    * One or more collaborators will engage with the aim of accepting the pull request
    * Pull requests can contain changes to existing samples or new samples
* All issues are tracked by github issues
    * See https://github.com/TIBCOSoftware/tibco-streaming-samples/issues
    * Optionally, any related internal jira issues can be referenced
    * Any user can create issues
    * Collaborators are expected to progress issues

## Sample requirements

* Samples may consist of one of :
  * A single fragment
  * A [maven aggregator](http://maven.apache.org/pom.html#Aggregation) + more than one fragment
  * A [maven aggregator](http://maven.apache.org/pom.html#Aggregation) + at least one application archive + at least one fragment
* Samples contain documentation in [markdown](https://guides.github.com/features/mastering-markdown/) format conforming to [maven site documentation rules](https://maven.apache.org/guides/mini/guide-site.html), containing :
    * Introduction
    * Code description or design notes, including studio screen shots if useful
    * For application archives, deployment configuration and instructions
    * Test case description and expected results
    * Links to main files ( such as configurations and pom.xml )
* Fragments must include junit test case(s)
* Application archives must include integration tests that at least start the application up
* Samples must import into studio with no errors or warnings (after following any manual instructions)
* Samples must also be buildable and testable from maven
  * Samples will be automatically built and tested under a jenkins-based continuous integration system
  * Maven site html documentation will be published to github pages
* Ideally samples shouldn't reference 3rd party dependencies not available publicly or part of the StreamBase release. 
    This allows samples to work without additional steps for both customers and automated builds.  However, when this isn't possible, the following applies : 
    * If the dependency is available on a vendor maintained maven repository, instructions are provided in that sample to use that repository.
    * If the dependency is only available with a manual download, instructions are provided in the sample to manually download the dependency, install into 
    the local maven repository and (optionally) deploy to a shared repository.
    * Internally, we do the same but deploy (or mirror) the dependency to a shared 3rd party repository. This repository is included in the sample builds.
    * Care must be taken to keep the metadata intact ( for example maintain copyright and license information ) so that the maven site info reports are correct.
* It must be possible for anyone to setup their own maven repository, build and execute all tests.
* Samples must go through standard development process including :
  * Requirements - reviewed by Spotfire engineering
  * Design - reviewed by Spotfire engineering
  * Implementation - reviewed by any collaborator
  * Testing
* Commit messages should be clear and concise, describing the change well and referencing jira or github issue number.  
    See https://chris.beams.io/posts/git-commit/? for a description of good commit messages.
* In order to support windows, samples should limit file lengths as much as possible :
  * Keep artifactId's short
  * In node deployment files, specify short engine name 

## Empty directories

Git doesn't store empty directories, so such directories that need to be part of the sample should include an empty .gitignore file.

## Images

We need to support viewing documents in github, studio and maven generated site documentation.  Unfortunately, the location of images for these cases differ.  Hence we should :

* Markdown files are stored in src/site/markdown
* Images are stored in src/site/markdown/images
* A soft link is added from src/site/resources/images to src/site/markdown/images
* Links to images in markdown files are of the format **\!\[Alt Text\]\(images/MyImage.png\)**

## Index

Jenkins will re-generate README.md files based on the pom.xml metadata. Therefore the README.md files should not be updated manually.
