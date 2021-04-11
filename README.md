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

## Quick Start (DRAFT)
In your container you can run workflows that are accessible via git. 
Just start with the ones in  https://github.com/Keymaster65/copper2go-workflows.

 * Start container
       * `docker run -d -p 59665:59665 -d --pull always --name copper2go --rm registry.hub.docker.com/keymaster65/copper2go`
 * In Browser you can see the used licenses
   * `http://localhost:59665/`
   * `http://localhost:59665/request/2.0/Hello` will deliver a "IllegalArgumentException: A name must be specified."
 * Use any other HTTP-Client and POST your name to the URL
   * Example: `curl --data Wolf http://localhost:59665/request/2.0/Hello`
   * Will produce someting like `Hello Wolf7! Please transfer 4 cent`

## Change Workflows
You develop your own workflows. You may start with the existing ones.
  * Clone or fork the copper2go-workflows: https://github.com/Keymaster65/copper2go-workflows
  * Modify configuration and store it into environment variable C2G_CONFIG. Example:
    * Start with file: https://github.com/Keymaster65/copper2go/blob/master/src/main/resources/de/wolfsvl/copper2go/application/config/config.json
    * store in in your local docker host `config.json`
    * Typically modify workflowGitURI location
  * Start Container with your configuration:
    * `docker run -d -p 59665:59665 -e C2G_CONFIG="$(cat config.json)" -d --pull always --name copper2go --rm registry.hub.docker.com/keymaster65/copper2go`

### Versioning
The Workflows API in contained in https://github.com/Keymaster65/copper2go/tree/master/src/main/java/de/wolfsvl/copper2go/workflowapi
Changes will be listed here.

The configuration of the application and the shipped dependencies will be listed here as Application API
  
## Links
  * https://github.com/Keymaster65/copper2go/releases
  * https://github.com/copper-engine
  * https://copper-engine.org/blog/2019-12-09-/copper-5.1-released/
  * https://github.com/factoryfx
 
## Planning
   
### Release Plan Application API 1.1
 * Add How to develop Workflows for IDEA
 * Add How to develop Workflows for gradle
 * Workflow with Json binding
 * Support of COPPER core GUI
 * Workflow with XML binding
 * JMX usage in Container

### Release Notes Workflow API 1.0 and Application API 1.0
 * http server support
 * http client support
 * non-business config
 * Multi workflow
 * docker image distribution
 * add license info
 * Add Application and Workflow path 
 * Event consuming workflows (no reply body, state 202 only)
 * quick start
  
### Release Notes Workflow API 0.1 and Application API 0.1
 * 0.1 A first MVP (Minimum Viable Product)

### Backlog
 * configure thread pool size, client pool size and more
 * Multi workflow support and REST level
 * Kafka support and choreography example
 * publish to public artifact repository
 * JMS support 
 * PostgreSQL for workflow instances
   * Add callback in WorkflowData
   * Support of callbacks
 * factoryfx integration
 * PostgreSQL support for business resources

