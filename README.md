# copper2go
 Implementation of a lightweight CI/CD pipeline using git repositories for changes 
 in COPPER workflows.
 
 One of the basic ideas of COPPER is to be able to perform software changes in business
 workflows at runtime. With release 5.1 this now can be done by using git repositories.
 
copper2go makes use of this feature and should help to use COPPER in your project. 
All you need to do is, to start the copper2go container with your configuration,
that support your business workflow.
 
 As one of the later steps, there might follow a factoryfx integration. 
 This will enable copper2go to make runtime changes in the technical part of the application 
 outside the business workflows without usage of the traditional pipeline.
 
 This picture shows a first overview:
 ![This picture shows a first overview](copper2goOverview.svg)

## Quick Start
In your container you can run workflows that are accessible via git. 
Just start with the ones in  https://github.com/Keymaster65/copper2go-workflows.

 * Start container
       * `docker run -d -p 59665:59665 -d --pull always --name copper2go --rm registry.hub.docker.com/keymaster65/copper2go:v2.0`
 * In Browser you can see the used licenses
   * `http://localhost:59665/`
   * `http://localhost:59665/request/2.0/Hello` will deliver a "IllegalArgumentException: A name must be specified."
 * Use any other HTTP-Client and POST your name to the URL
   * Example: `curl --data Wolf http://localhost:59665/request/2.0/Hello`
   * Will produce someting like `Hello Wolf! Please transfer 4 cent`

### Change Workflows
You want to develop your own workflows? You may start with the existing ones.
  * Clone or fork the copper2go-workflows **gradle** project: https://github.com/Keymaster65/copper2go-workflows
  * Modify configuration and store it into environment variable C2G_CONFIG.
    * Start with file: https://github.com/Keymaster65/copper2go/blob/release/2.0/src/main/resources/io.github.keymaster65/copper2go/application/config/config.json
    * store it in your local docker host `config.json`
    * Typically, modify workflowGitURI location
  * Start Container with your configuration:
    * `docker run -d -p 59665:59665 -e C2G_CONFIG="$(cat config.json)" -d --pull always --name copper2go --rm registry.hub.docker.com/keymaster65/copper2go:v2.0`

## Developer's Guide
copper2go bases on the COPPER (COmmon Persistable Process Excecution Runtime). To get more information about
COPPER, you might visit https://github.com/copper-engine or https://github.com/copper-engine.

### Motivation
COPPER was developed as an Orchestration Engine. For more that 10 years now, in 2021, many high performance
systems are in production. The Online Configuration capability of this workflow engine is used seldom.
To fill this gap, by using this feature as a main concept, copper2go was developed. By adding connectors,
the development of Orchestration Services will become more efficient for Java developers.

You can see copper2go as "Something as a Service", somewhere in the area of Paas and SaaS.
copper2go containers can be run wherever you want. So the container may run

* Internet Workflows, if hosted in the web
* Intranet Workflows, if hosted in a company
* Desktop Workflows, if run on your system

In times of automated build pipelines the needs for Workflow systems reduced, but a more lightweight **git** based
pipeline might even better fit your needs.

### Online Configuration
Using git only, and compiling the workflow inside the container, you can very easy change your system's behaviour
"online". That is what want many people dream of, if they talk about "configuration". As one use case you can simply
"revert" your changes, if something goes wrong. As the container is separated from the workflow, this "revert"
always works by concept.

### Reactive Applications
Asynchronicity is one of the core concept of COPPER. That is the reason, why you can develop reactive
high performance applications using COPPER or copper2go.

### Synchronous Code
In spite of the asynchronicity inside COPPER, the workflow Java code is Synchronous Code. You might
have a look at the "Motivation" of the Loom Project in https://cr.openjdk.java.net/~rpressler/loom/Loom-Proposal.html.

### Long Running Workflows
Last but not least, COPPER workflows can be executed for an unlimited time. It depends on the resources you add
to the application. Transient workflows are supported in copper2go since release 0.1. Persistent workflows are
supported by COPPER and currently in the Backlog of copper2go.

### COPPER Details
COPPER workflows look like Synchronous Java Code. This code is instrumented at compile time on the server.
If you want to become more familiar with COPPER, that you might visit

* https://copper-engine.org/docs/content/copper.pptx
* https://copper-engine.org/docs/content/COPPER-best-practices_1.0.1-en.pdf
* https://copper-engine.org/docs/content/COPPER-WorkflowCompatibilityRules-1.2.0-en.pdf
* https://copper-engine.org/docs/unsorted/
* https://github.com/copper-engine
* https://copper-engine.org/

### copper2go Details
The API described below is not much to read. I would suggest using the "Quick Start" and then
start to develop a little sample application.

You came to a point, where you are missing a feature?
No Problem. You might ues a pull request, if you want to add it by yourself. You
may also add an item in https://github.com/Keymaster65/copper2go/issues

I am looking for feedback.

### Versioning and API
#### Workflow's API
The copper2go Workflow's API is hosted in the Maven Central. It can be found at several places
* https://repo1.maven.org/maven2/io/github/keymaster65/copper2go-api/
* https://search.maven.org/search?q=copper2go
* https://mvnrepository.com/artifact/io.github.keymaster65/copper2go-api

Changes will be listed here. In addition, you have the API to the COPPER framework.

#### Application API
The configuration of the application and the shipped dependencies will be listed here as Application API.
The releases are hosted at github: https://github.com/Keymaster65/copper2go/tags

If you want to add something, you may contribute with pull requests or forks. In a fork you might
add 3rd party libs as wished.

Forks or Pull Requests are always very welcome.

#### COPPER Docker
Docker images can be found here: https://hub.docker.com/r/keymaster65/copper2go

### Links
  * https://repo1.maven.org/maven2/io/github/keymaster65/copper2go-api/
  * https://hub.docker.com/r/keymaster65/copper2go
  * https://github.com/Keymaster65/copper2go/releases
  * https://github.com/copper-engine
  * https://copper-engine.org/  
  * https://copper-engine.org/blog/2019-12-09-/copper-5.1-released/
  * https://github.com/factoryfx
 
## Planning
Of course, copper2go is ready use. Many more capabilities might be added.
Here you find some of them ;-)
   
### Developer Release Plan Application API 2.0
* publish to public artifact repository
* Add How to develop Workflows for IDEA
* Add How to develop Workflows for gradle
* Draft development Guide

### License Release Plan Application API 2.0.1
* Add licence info more files and update year
* Add licence URL in non Docker apps

### Kafka Release Plan Application API 2.1
* Kafka Connectors

### Binding Release Plan Application API 2.2
* Workflow with Json binding
* Workflow with XML binding

### State Pattern Release Plan Application API 2.3
* Spike: Workflow using State Pattern

### Operator Release Plan Application API 2.3
* configure thread pool size, client pool size and more
* Support of COPPER core GUI
* JMX usage in Container

### Backlog
* Multi workflow support and REST level
* Support URL parameter (in and out)
* Load workflow subtree only from git
* Delete .copper on start (if still problems occurs)
* Kafka choreography example
* Support Binary data
* Binary Binding
* JMS support (may be IBM MQ, ActiveMQ or ...)
* PostgreSQL for workflow persistent instances
    * Add callback in WorkflowData
    * Support of callbacks
* factoryfx integration
* PostgreSQL support for business resources
* Async idempotent DB API

## Released

### HTTP Container Release Notes Workflow API 1.0 and Application API 1.0
* http server support
* http client support
* non-business config
* Multi workflow
* docker image distribution
* add license info
* Add Application and Workflow path 
* Event consuming workflows (no reply body, state 202 only)
* quick start
  
### MVP Release Notes Workflow API 0.1 and Application API 0.1
* 0.1 A first MVP (Minimum Viable Product)
