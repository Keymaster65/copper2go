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
       * `docker run -d -p 59665:59665 -d --pull always --name copper2go --rm registry.hub.docker.com/keymaster65/copper2go:v1.0.1`
 * In Browser you can see the used licenses
   * `http://localhost:59665/`
   * `http://localhost:59665/request/2.0/Hello` will deliver a "IllegalArgumentException: A name must be specified."
 * Use any other HTTP-Client and POST your name to the URL
   * Example: `curl --data Wolf http://localhost:59665/request/2.0/Hello`
   * Will produce someting like `Hello Wolf! Please transfer 4 cent`

## Change Workflows
You want to develop your own workflows? You may start with the existing ones.
  * Clone or fork the copper2go-workflows: https://github.com/Keymaster65/copper2go-workflows
  * Modify configuration and store it into environment variable C2G_CONFIG. Example:
    * Start with file: https://github.com/Keymaster65/copper2go/blob/release/1.0/src/main/resources/de/wolfsvl/copper2go/application/config/config.json
    * store in in your local docker host `config.json`
    * Typically modify workflowGitURI location
  * Start Container with your configuration:
    * `docker run -d -p 59665:59665 -e C2G_CONFIG="$(cat config.json)" -d --pull always --name copper2go --rm registry.hub.docker.com/keymaster65/copper2go:v1.0`

## Use Cases
* Orchestration
* Synchronous Code (see "Motivtaion" of the Loom Prject) https://cr.openjdk.java.net/~rpressler/loom/Loom-Proposal.html)  
* Async (reactive!?)
* Long Running (limited transient since version 0.1, unlimited persistent in backlog)
* Online Configuration (incl. undo/rollback)

* Internet Workflows
* Intranet Worksflows
* Desktop Workflows

## Developer's Guide (DRAFT)
* COPPER
* Links for COPPER

### Versioning
#### Workflow's API
The Workflow's API is hosted in the Maven Central. It can be found at several places
* https://repo1.maven.org/maven2/io/github/keymaster65/copper2go-api/
* https://search.maven.org/search?q=copper2go
* https://mvnrepository.com/artifact/io.github.keymaster65/copper2go-api

Changes will be listed here.

#### Application API
The configuration of the application and the shipped dependencies will be listed here as Application API.
The releases are hosted at github: https://github.com/Keymaster65/copper2go/tags
* Forks are welcome

#### COPPER Docker
* Something as a Service
Docker images kan be found here: https://hub.docker.com/r/keymaster65/copper2go

### Links
  * https://repo1.maven.org/maven2/io/github/keymaster65/copper2go-api/
  * https://hub.docker.com/r/keymaster65/copper2go
  * https://github.com/Keymaster65/copper2go/releases
  * https://github.com/copper-engine
  * https://copper-engine.org/blog/2019-12-09-/copper-5.1-released/
  * https://github.com/factoryfx
 
## Planning
   
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

### Backlog
* Multi workflow support and REST level
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

