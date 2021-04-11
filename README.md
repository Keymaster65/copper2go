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
 * Clone the workflows
 * modify configuration
 * Start container
   * docker pull registry.hub.docker.com/keymaster65/copper2go
   * docker run -d -p 59665:59665 -p 9000:9000 -d --name copper2go --rm registry.hub.docker.com/keymaster65/copper2go
   * docker run -d -p 59665:59665 -p 9000:9000 -d --pull always --name copper2go --rm registry.hub.docker.com/keymaster65/copper2go
 * Use browser
   * http://localhost:59665/
 * Use HTTP-Client
 * Use JMX
 
## Links
  * https://github.com/Keymaster65/copper2go/releases
  * https://github.com/copper-engine
  * https://copper-engine.org/blog/2019-12-09-/copper-5.1-released/
  * https://github.com/factoryfx
 
## Planning

### Versioning (DRAFT)
API + Config

   
### Release Plan 1.1
 * Support of COPPER core GUI
 * Workflow with Json binding
 * Workflow with XML binding

### Release Plan 1.0
 * http server support
 * http client support
 * non-business config
 * Multi workflow
 * docker image distribution
 * add license info
 * Add Application and Workflow path 
 * quick start
 * JMX usage in Container
 * Event consuming workflows (no reply body, state only)
  
### Release Notes
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

