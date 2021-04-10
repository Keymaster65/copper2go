# copper2go
 Implementation of a lightweight CI/CD pipeline using git repositories for changes in copper workflows.
 
 One of the basic ideas of copper is to be able to perform software changes in business workflows at runtime. With release 5.1 this now can be done by using git repositories.
 
 copper2go makes use of this feature and should help to use copper in your project. The first release will be a very, very small implementation. The future will show, if there will follow more. It depends on the feedback.
 
 As one of the next steps, there might follow a factoryfx integration. This will enable copper2go to make runtime changes in the technical part of the application outside the business workflows.
 
 This picture shows a first overview:
 ![This picture shows a first overview](copper2goOverview.svg)
 
 ## Release plan 1.0
  * http server support
  * http client support
  * non-business config
  * Multi workflow
  * docker image distribution
  * add license info
   
## Release notes
 * 0.1 A first MVP (Minimum Viable Product)
 
 ## Possible features

 * quick start
 * Multi workflow support and REST level
 * Kafka support and choreography example
 * publish to public artifact repository
 * JMS support 
 * PostgreSQL for workflow instances
 * factoryfx integration
 
 ## Links
 * https://github.com/Keymaster65/copper2go/releases
 * https://github.com/copper-engine
 * https://copper-engine.org/blog/2019-12-09-/copper-5.1-released/
 * https://github.com/factoryfx
