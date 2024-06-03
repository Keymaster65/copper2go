# copper2go

## Open
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/Keymaster65/copper2go/blob/master/LICENSE)

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

## Quality and Security
[![Build](https://github.com/Keymaster65/copper2go/actions/workflows/build.yml/badge.svg)](https://github.com/Keymaster65/copper2go/actions/workflows/build.yml)
[![CodeQL](https://github.com/Keymaster65/copper2go/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/Keymaster65/copper2go/actions/workflows/codeql-analysis.yml)

[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Keymaster65_copper2go&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=Keymaster65_copper2go)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Keymaster65_copper2go&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Keymaster65_copper2go)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Keymaster65_copper2go&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Keymaster65_copper2go)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Keymaster65_copper2go&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Keymaster65_copper2go)

[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Keymaster65_copper2go&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Keymaster65_copper2go)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Keymaster65_copper2go&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Keymaster65_copper2go)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Keymaster65_copper2go&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Keymaster65_copper2go)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=Keymaster65_copper2go&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=Keymaster65_copper2go)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Keymaster65_copper2go&metric=alert_status)](https://sonarcloud.io/dashboard?id=Keymaster65_copper2go)

## API
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.keymaster65/copper2go-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.keymaster65/copper2go-api)

## Application
[![GitHub release](https://img.shields.io/github/release/Keymaster65/copper2go)](https://GitHub.com/Keymaster65/copper2go/releases/)
[![Docker Hub](https://shields.io/docker/pulls/keymaster65/copper2go)](https://hub.docker.com/r/keymaster65/copper2go/)

## Introduction
Implementation of a lightweight CI/CD pipeline using git repositories for changes in COPPER workflows.

One of the basic ideas of COPPER is to be able to perform software changes in business workflows at runtime. With
release 5.1 this now can be done by using git repositories.

copper2go makes use of this feature and should help to use COPPER in your project. All you need to do is, to start the
copper2go container with your configuration, that support your business workflows.

Changes in the technical part of the application in addition to the business workflows can be deployed by the using
traditional CI/CD pipelines.

This picture shows a first overview:
![This picture shows a first overview](copper2goOverview.svg)

## Quick Start

In your container you can run workflows that are accessible via git. Just start with the ones
in  https://github.com/Keymaster65/copper2go-workflows.

[![Docker Hub](https://shields.io/docker/pulls/keymaster65/copper2go)](https://hub.docker.com/r/keymaster65/copper2go/)

### Demo

* Start container with `hello` and `pricing` workflow 
    * `docker run -d -p 19666:19666 --pull always --name copper2go --rm registry.hub.docker.com/keymaster65/copper2go:latest`
* In Browser `client` you can GET and see the used licenses
    * `http://localhost:19666/`
*  In Browser `client` you can GET and see an exception
    * `http://localhost:19666/copper2go/3/api/twoway/2.0/Hello` will deliver a "IllegalArgumentException: A name must be
      specified."
* Use any other HTTP `client` and POST your name to the URL
    * Example: `curl --data Wolf http://localhost:19666/copper2go/3/api/twoway/2.0/Hello`
        * Will produce someting like `Hello Wolf! Please transfer 4 cent`

### Demo Diagram

The following diagram shows the workflows used in the Demo.

* The green `client` sends a HTTP request to the blue copper2go `hello` workflow and waits for the response
* The blue copper2go `hello` workflow send a request the red `pricing` backend and waits for the response
* The red `pricing` backend calculates a price send it in it's response. To keep the demo simple, this backend also is a 
copper2go workflow.

![Demo Diagram](engineHelloWorkflow.png)

### Change Workflows

You want to develop your own workflows? You may start with the existing ones.

* Clone or fork the copper2go-workflows **gradle** project: https://github.com/Keymaster65/copper2go-workflows
* Modify configuration and store it into environment variable C2G_CONFIG.
    * Start with file: https://github.com/Keymaster65/copper2go/blob/master/copper2go-application/src/main/resources/io/github/keymaster65/copper2go/application/config/config.json
    * store it in your local docker host `config.json`
    * Typically, modify workflowGitURI location
* Start Container with your configuration:
    * `docker run -d -p 19666:19666 -e C2G_CONFIG="$(cat config.json)" --pull always --name copper2go --rm registry.hub.docker.com/keymaster65/copper2go:latest`

### Starting with JMX and copper-monitoring Web Application

`host.docker.internal` works for windows.

* Start container with JMX on port 19665
  * `docker run -d -e JAVA_TOOL_OPTIONS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=19665 -Dcom.sun.management.jmxremote.rmi.port=19665 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.local.only=false -Djava.rmi.server.hostname=host.docker.internal" -p 19665:19665 -p 19666:19666 --pull always --name copper2go --rm registry.hub.docker.com/keymaster65/copper2go:latest`
  * Now you can visit the copper MBeas in tools like visualVM, JConsole etc.
* Start the copper-monitoring Web Application on port 29665 using same JMX port
  * `docker run -e JMX_HOST="host.docker.internal" -e JMX_PORT="19665" --name copperGui --rm -p 29665:8080 -d copperengine/copper-monitoring`
  * Login with admin/admin at http://localhost:29665
  * Submit this setting and the TransientEngine should be visible
  * More Details can be found here https://github.com/copper-engine/copper-monitoring

## More Motivation

COPPER was developed as an Orchestration Engine. For more than 10 years now, in 2021, many high performance systems are
in production. The online configuration capability of this workflow engine is used seldom. To fill this gap, by using
this feature as a main concept, copper2go was developed. By adding connectors, the development of Orchestration Services
will become easier for Java developers.

You can run copper2go as a "Plattform as a Service", if you want to enable your clients to write and support their own
COPPER workflows.

With an existing git repository, that contains all COPPER workflows of your orchestration services, copper2go is the
enabler for "Software as a Service" orchestration systems.

Of course, copper2go containers can be run wherever you want. So the container may run

* Internet Workflows, if hosted in the web
* Intranet Workflows, if hosted in a company
* Desktop Workflows, if run on your system

In times of automated build pipelines the needs for workflow systems are reduced, but a more lightweight **git** based
pipeline might even better fit your needs. Here are some more advantages:

* Lightweight pipeline form source code to deployment, because build is inside the copper2go container
* Additional quality steps can be integrated into the git workflow
* Using git and git workflows in the development teams
* Reuse of copper2go images might reduce costs for images in the cloud
* Unified copper2go images
* Secure copper2go images
* Easy extensions of copper2go images as forks on github
* Easy extensions of copper2go connectors as forks on github
* If the developers want to break the limits, they can use the vanilla-engines as forks on github

### Security and Quality

The copper2go application is checked using the https://plugins.gradle.org/plugin/org.owasp.dependencycheck, so engine 
does not contain any open known security issue, if a secure release of used 3rd party components are available.
The Dependabot helps to keep the 3rd party components up to date (see https://github.com/dependabot).
As workflows can not extend the used jars this check is sufficient for all workflow use cases.

High quality gates are define and used in sonarcloud. This is an important fact, if you want to stay on the secure side
using copper2go. Version 5 of copper2go does not support secure connectors, but they can easily be added on demand.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Keymaster65_copper2go&metric=alert_status)](https://sonarcloud.io/dashboard?id=Keymaster65_copper2go)

### Online Configuration and Revert

Using git only, and compiling the workflow inside the container, you can very easy change your system's behaviour
"online". That is what want many people dream of, if they talk about "configuration". As one use case you can simply
"revert" your changes, if something goes wrong. As the container is separated from the workflow, this "revert"
always works by concept.

### Reactive Applications

Non-blocking threads is one of the core concept of COPPER. That is the reason, why you can develop reactive high
performance applications using COPPER or copper2go. A good motivation can be found in the "reactor" reference
https://projectreactor.io/docs/core/release/reference/#intro-reactive

### Non-Blocking Threads but easy to Maintain

In spite of the non-blocking code with callback inside COPPER, the COPPER workflow Java code is readable and looks as
simple as blocking code. There is no "callback hell" in your project. You might have a look at the "Motivation" of the
Loom Project in
https://cr.openjdk.java.net/~rpressler/loom/Loom-Proposal.html.

### CRaC Support

It is possible to start copper2go at CRaC (https://openjdk.org/projects/crac/) checkpoints. If you want to use it, you
must create a fitting pipeline to create images containing a checkpoint. To see how this can be done,
there are some files in the `crac` directory.

The CRaC API is integrated in the HTTP connectors. Requirements to support Kafka and STDIN/STDOUT/STDERR can be found in
the Backlog.

### Long Running Workflows

Last but not least, COPPER workflows can be executed for an unlimited time. It depends on the resources you add to the
application. Transient workflows are supported in copper2go since release 0.1. Persistent workflows are supported by
COPPER and currently in the Backlog of copper2go and will be added on demand.

## Developer's Guide

copper2go bases on the COPPER (COmmon Persistable Process Excecution Runtime). To get more information about COPPER, you
might visit https://github.com/copper-engine or https://github.com/copper-engine.

### COPPER Details

COPPER workflows look like Synchronous Java Code. This code is instrumented at compile time on the server. If you want
to become more familiar with COPPER, that you might visit

* https://copper-engine.org/docs/content/copper.pptx
* https://copper-engine.org/docs/content/COPPER-best-practices_1.0.1-en.pdf
* https://copper-engine.org/docs/content/COPPER-WorkflowCompatibilityRules-1.2.0-en.pdf
* https://copper-engine.org/docs/unsorted/
* https://github.com/copper-engine
* https://github.com/copper-engine/copper-monitoring
* https://copper-engine.org/

### copper2go Details

The API described below is not much to read. I would suggest using the "Quick Start" and then start to develop a little
sample application.

You came to a point, where you are missing a feature? No Problem. You might ues a pull request, if you want to add it by
yourself. Of cource you can fork on GitHub. You may also add an item in
https://github.com/Keymaster65/copper2go/issues

I am looking for feedback.

#### copper2go Architecture

The Architecture overview

![The Architecture overview](copper2goComponents.svg)

shows the main packages, classes and infaces of copper2go. Beside the workflows and the workflow api, you should be
aware of the connector capabilities.

#### Connector Capabilities

TO DO ...

#### More Samples

https://github.com/Keymaster65/copper2go-tools-bridge

#### API and Versioning

##### Workflow API

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.keymaster65/copper2go-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.keymaster65/copper2go-api)

The copper2go Workflow's API is hosted in the Maven Central. It can be found at several places

* https://repo1.maven.org/maven2/io/github/keymaster65/copper2go-api/
* https://search.maven.org/search?q=copper2go
* https://mvnrepository.com/artifact/io.github.keymaster65/copper2go-api

Starting with Workflow API 3.1.0 it also contains some dependencies, that extend the API.

Changes will be listed here in the "Released" chapter. In addition, you have the API to the COPPER framework.

Last but not least, the Java API can be used and is contained in the copper2go container.

##### Workflow's API Detail

Visit the sources, tests, examples and JavaDocs:

| API       | Link to JavaDoc                                                                                       |
|-----------|-------------------------------------------------------------------------------------------------------|
| core      | [copper2go-api](https://www.javadoc.io/doc/io.github.keymaster65/copper2go-api/latest/index.html)     |
| extension | [copper-coreengine](https://www.javadoc.io/doc/org.copper-engine/copper-coreengine/latest/index.html) |
| extension | [slf4j-api](https://www.javadoc.io/doc/org.slf4j/slf4j-api/latest/org.slf4j/module-summary.html)      |
| JDK       | [Java 21 API](https://docs.oracle.com/en/java/javase/21/docs/api/index.html)                          |

##### Application API

[![GitHub release](https://img.shields.io/github/release/Keymaster65/copper2go)](https://GitHub.com/Keymaster65/copper2go/releases/)
[![Docker Hub](https://shields.io/docker/pulls/keymaster65/copper2go)](https://hub.docker.com/r/keymaster65/copper2go/)

The configuration of the application and the receiver's APIs will be listed here as Application API in the
"Released" chapter.

No Java code except of Workflow's API is released as an API. Of course, you can fork the project, if you want to make
extensions.

The releases are hosted at github:
https://github.com/Keymaster65/copper2go/releases

Docker images can be found here: https://hub.docker.com/r/keymaster65/copper2go

###### Application Configuration

###### Main Configuration

https://github.com/Keymaster65/copper2go/blob/master/copper2go-application/src/main/resources/io/github/keymaster65/copper2go/application/config/configSystemTestComplete.json

you find examples for the configuration of

* COPPER Workflows (workflowRepositoryConfig)
* HTTP Receiver (Server) (httpPort)
* HTTP Request/Response (httpRequestChannelConfigs)
* Kafka Server (kafkaHost, kafkaPort)
* Kafka Receiver (kafkaReceiverConfigs)
* Kafka Request/Response (kafkaRequestChannelConfigs)

###### Logging Configuration

The logback logging is defined in
https://github.com/Keymaster65/copper2go/blob/master/copper2go-application/src/main/resources/logback.xml

There you can find the environment variables, that can be used to control logging at container start.

###### HTTP Receiver API

URLs path should be "/copper2go/3/api/TYPE/MAJOR.MINOR/WORKFLOW-NAME

where

* The '3' relates to the HTTP Receiver API major version
* The '2' Application API major version is still supported but DEPRECATED
* TYPE can be
    * "twoway" if a body is expected in the reply. The HTTP reply is submitted during workflow processing.
    * "oneway if, no body is expected in the reply. The HTTP reply is submitted when workflow is initiated
* MAJOR.MINOR is the version of the workflow
* WORKFLOW-NAME is the target workflow of the request

URL "/" shows licence information.

###### Kafka Receiver API

TODO

###### StandardInOut Receiver API

TODO

### Missing Features?

If you want to add something, you may contribute with pull requests or forks. In a fork you might add 3rd party libs as
wished.

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

Forks or Pull Requests are always very welcome.

Issues are very welcome, too.

### Releasing and Maintenance

* The "master" branch is maintained and released as "latest" image on dockerhub. 
* The newest "release" branch is maintained and released as a tagged image for example "4.4.0"
* Because build pipeline is completely automated after committing to "master" or "release", these branches are protected on github and commits must be signed "verified". 
* The newest Application API should be used in this document
* For a "release", the following manual activities must be done
  1) Move "Ongoing" block in "master"
  1) Change "release" version in `.github/workflows/build.yml` in "master" on github (with a PR)
  1) merge "master" to "release" branch on github (with a PR)
  1) "Draft a new release on github" on "release" branch with a copy of the moved "Ongoing" block (look at older releases for details)

### Release a copper2go-api Library

1) Move "Ongoing" block in "master"
1) Update "version" in copper2go-api/build.gradle.kts
1) Push release branch
1) Publish artifact: `gradle :copper2go-api:publish`
1) Login to https://oss.sonatype.org/
1) Find artifact in "Staging Repositories" and "Close" it and "Release" it
1) "Draft a new release on github" on "release" branch with a copy of the moved "Ongoing" block (look at older releases for details)
1) Update SNAPSHOT "version" in copper2go-api/build.gradle.kts
1) Push release branch

#### More Helpful Tasks

1) `gradle dependencyUpdates`
1) Deprecated: `gradle dependencies :sync-application:dependencies :vanilla-application:dependencies :application-framework:dependencies :copper2go-app:dependencies :scotty-engine:dependencies :sync-engine:dependencies :vanilla-engine:dependencies  :copper2go-api:dependencies :connector-standardio:dependencies :connector-kafka-vertx:dependencies :connector-http-vertx:dependencies :connector-api:dependencies  :engine-api:dependencies :pricing-simulator:dependencies --write-locks`
1) Deprecated: Optional: `gradle dependencies :sync-application:dependencies :vanilla-application:dependencies :application-framework:dependencies :copper2go-app:dependencies :scotty-engine:dependencies :sync-engine:dependencies :vanilla-engine:dependencies  :copper2go-api:dependencies :connector-standardio:dependencies :connector-kafka-vertx:dependencies :connector-http-vertx:dependencies :connector-api:dependencies  :engine-api:dependencies :pricing-simulator:dependencies --write-locks --refresh-dependencies`
1) `gradle dependencyCheckAnalyze -x :dependencyCheckAnalyze --info`
1) `gradle clean build`
1) `gradle clean integrationTest`
1) `gradle -Pcopper2goVersion=tmp2 :copper2go-application:build :copper2go-application:jib `
1) `gradle systemTest`
1) `gradle :vanilla-application:build :vanilla-application:jib`
1) `gradle :sync-application:build :sync-application:jib`
1) `docker scout cves keymaster65/copper2go:latest`
1) Optional: `gradle pitest -x :pitest`
1) Optional: `gradle pitestReportAggregate`

## Links

* https://repo1.maven.org/maven2/io/github/keymaster65/copper2go-api/
* https://hub.docker.com/r/keymaster65/copper2go
* https://github.com/Keymaster65/copper2go/releases
* https://github.com/copper-engine
* https://copper-engine.org/
* https://copper-engine.org/blog/2019-12-09-/copper-5.1-released/
* https://openjdk.org/projects/crac/

## Ongoing in latest/master

Of course, copper2go is ready use. Many more capabilities might be added. Here you find some of them ;-)

* [x] Update Application API to JDK 22
* [x] Use a Registered Ports in Applications

## Planning

### "Java 22" Release Application API 5.0.0

* [ ] Update to JDK 22

### "State Pattern" Release Application API 5.1.0

* [ ] Spike: BPMN Support
* [ ] Spike: Multi workflow repository support and REST level
* [ ] Spike: Workflow using State Pattern or other defined strategy in copper2go-engine

## Backlog

* STDIN/OUT support in config and container (or remove it)
* Load workflow subtree only from git
* Operating
  * configure thread pool size, client pool size and more
  * Add some performance analysis
  * Collect Statistics and other (may be useful for Tests like Bridge-Test)
* Remove version 2 of HTTP Receiver API
* Extract WorkflowTestRunner.java and Copper2goDependencyInjector.java
* Split copper2go-workflows; extract systemtest
* Redesign DefaultRequestChannel (like Kafka). Use WARN instead of ERROR?
* Redesign RequestChannel/EventChannel: Is the difference needed? Why 2 errorEvent (was inspired by STDOUT/ERR)?
* Add test coverage for workflows to copper2go-workflows
* Release internet workflow application as copper2go-webapp (see branch experiment/webapp)
* Finish support kafka events
* Add information "How Tos" to developer's guide
  * Overview
  * Request Channel Stores
  * Event Channel Stores
  * Configuration Reply Channel Store
  * Tickets
  * Workflow Development/Test
* Support of HTTP callbacks
* Add callback in WorkflowData ("replychannel")
* Use vanilla-engine without a github fork
* Extend connectors without a github fork
* Extend copper2go image without a github fork
* Retire vertx
  * Replace vertx HTTP components with simpler implementation
  * Replace vertx Kafka components with simpler implementation
* On demand only
  * HTTP Security
  * Kafka Security
  * CRaC Support for STDIN/STDOUT/STDERR
  * Support Binary data
  * Binary Binding
  * Kafka choreography example
  * CRaC Support for Kafka
  * PostgreSQL for workflow persistent instances
  * Async idempotent DB API
  * PostgreSQL support for business resources
  * JMS support (may be IBM MQ, ActiveMQ or ...)
  * factoryfx integration
  * extend workflow attributes to a MultiMap
* Withdrawn
  * Add new Workflow Repository for Performancetest
  * Delete .copper on start (if still problems occurs)
  * Workflow with XML binding (may be not ;-)
  * Vertx Bus Connector

## Released

### "Maintenance" Release Application API 4.7.0

* [x] Dependabot updates 02.12.2023
* [x] Dependabot updates 10.12.2023
* [x] Dependabot updates 15.12.2023
* [x] Dependabot updates 23.12.2023
* [x] Dependabot updates 27.12.2023
* [x] Dependabot updates 06.01.2024
* [x] Dependabot updates 12.01.2024
* [x] Dependabot updates 20.01.2024
* [x] Dependabot updates 05.02.2024
* [x] Dependabot updates 24.02.2024
* [x] Dependabot updates 02.03.2024
* [x] Dependabot updates 24.03.2024
* [x] Dependabot updates 26.03.2024
* [x] Dependabot updates 29.03.2024
* [x] Dependabot updates 30.03.2024
* [x] No more gradle.lockfile usage
* [x] Dependabot updates 14.04.2024
* [x] Dependabot updates 20.04.2024
* [x] Dependabot updates 20.05.2024
* [x] Dependabot updates 23.05.2024
* [x] Dependabot updates 02.06.2024
* [x] Toolchain Update to JDK 22

### "Update copper-coreengine, slf4j-api and jackson-databind" Workflow API 3.3.0

* [x] Update jackson-databind from 2.15.3 to 2.17.1
* [x] Update slf4j-api from 2.0.10 to 2.0.13
* [x] Update copper-coreengine from 5.4.2 to 5.5.0

### "Binding" Release Application API 4.6.0

* [x] Workflow with Json binding
* [x] Use org.crac:crac instead of io.github.crac:org-crac
* [x] New ENV for logback $LOG_LEVEL_ROOT
* [x] Dependabot updates 21.10.2023
* [x] Dependabot updates 01.11.2023
* [x] Dependabot updates 04.11.2023
* [x] Dependabot updates 12.11.2023
* [x] Dependabot updates 18.11.2023
* [x] Build with gradle 8.5 and use JDK 21 for gradle
* [x] Much faster gradle INIT on Windows with org.gradle.vfs.watch=true
* [x] Separate OWASP github workflow

### "copper-coreengine, slf4j-api and jackson-databind" Workflow API 3.2.1

* [x] Update copper-coreengine from 5.4.1 to 5.4.2
* [x] Update slf4j-api from 2.0.6 to 2.0.9
* [x] Update jackson-databind from 2.14.1 to 2.15.3

### "CRaC ready" Release Application API 4.5.0

* [x] CRaC integration in VertxHttpClient
* [x] CRaC integration in Copper2GoGitWorkflowRepository
* [x] Support faster startup using CRaC (https://openjdk.org/projects/crac/)

### "Service" Release Application API 4.4.1

* [x] Update netty-handler to 4.1.100.Final (Continue suppressing CVE-2023-4586)
* [x] Fix race condition in WorkflowHandler for oneway requests
* [x] Dependabot updates 14.10.2023
* [x] Full automated build pipelines into dockerhub releases for "latest/master" and "release"
* [x] CRaC integration in VertxHttpServer
* 
### "Operator" Release Application API 4.4.0

* [x] JMX usage in Container
* [x] Support of COPPER core GUI
* [x] (Security) Updates
* [x] Update to JDK 21

#### "slf4j-api and jackson-databind" Workflow API 3.2.0

* [x] Update slf4j-api from 2.0.0-alpha5 to 2.0.6
* [x] Update jackson-databind from 2.13.2.2 to 2.14.1

### "Loom" Release Application API 4.3.0

* [x] Add pitest support (https://pitest.org/)
* [x] Add a sync-engine and application with blocking code
* [x] Run sync-application on JDK 19 with Loom enabled
* [x] Security updates
* [x] Additional updates
* [x] Add licenses to sync-application
* [x] Do not use root as user to run copper2go

### "Vanilla" Release Application API 4.2.0

* [x] Add license info "vanilla" engine implementation
* [x] Add "vanilla" engine implementation
* [x] Add "vanilla" engine implementation as incubating feature
* [x] Fix CVE-2022-38752 by excluding snakeyaml
* [x] Security updates
* [x] Version updates

### "Service" Release Application API 4.1.1

* [x] Security Updates
* [x] Version Updates

### "3rd Party" Release Workflow API 3.1.0

* [x] Add 3rd party libs to supported Workflow API

### "Log Config" Release Application API 4.1

* [x] Make logLevel accessible via environment
* [x] Add log configuration here and changed defaults

### "Security" Release

#### Application API 4

* [x] Remove vulnerabilities in dependencies
* [x] Upgrade to higher libs, copper2go depends on

### "Kafka/Http Bridge, Quality and Java 17" Release

#### Application API 3.0

* [x] Support for Java 17
* [x] Add Bridge Workflow
* [x] Add quality badges
* [x] Support HTTP URL parameter (in but no out)
* [x] Support Kafka Header (parameter) (in and out)
* [x] Improve unit test coverage up to 95% or more
* [x] Update many used jars
* [x] Add use case in Developer's Guide
* [x] Refactor engine subproject for tests and extracting engine-api and connector-api
* [x] Refactor connector subproject for tests and extracting more modules
* [x] Fix memory leak in ReplyChannelStore
* [x] UUID might not be set in WorkflowData (breaking change)
* [x] Use term "oneway" instead of (incoming) "event" in code (breaking change)
* [x] Use term "oneway" instead of (incoming) "event" in this README

URLs path should be "/copper2go/3/api/TYPE/MAJOR.MINOR/WORKFLOW-NAME

where

* The '3' relates to the Application API major version
* The '2' Application API major version is still supported but DEPRECATED
* TYPE can be
    * "twoway" if a body is expected in the reply. The HTTP reply is submitted during workflow processing.
    * "oneway if, no body is expected in the reply. The HTTP reply is submitted when workflow is initiated
* MAJOR.MINOR is the version of the workflow
* WORKFLOW-NAME is the target workflow of the request

URL "/" shows licence information.

#### Release Workflow API 3.0

* [x] Support HTTP URL parameter (in but no out)
* [x] Support Kafka Header (parameter) (in and out)
* [x] Remove payload member from WorkflowData (breaking change)
* [x] Require Java 17 (breaking change)
* [x] Restructure packages (breaking change)

#### Release Bridge Workflow 1.0

* [x] New Repository for Bridge Workflow
* [x] System tests
* [x] Add use case in Developer's Guide

### "Kafka" Release Application API 2.1

* [x] Kafka Connectors
* [x] Separate System- and Integration- testing in Build Pipeline

### "License" Release Application API 2.0.1

* [x] Add licence info more files and update year
* [x] Add licence URL in non Docker apps
* [x] Add API release info
* [x] Correct URLs in Quick Start
* [x] Replace the lax Application API by a stricter validation

### "Bugfix POM" Release Workflow API 2.0.1

* [x] No source change, but correction in POM file versions

### "Developer" Release Workflow API 2.0 and Application API 2.0

* publish to public artifact repository
* Add How to develop Workflows for IDEA
* Add How to develop Workflows for gradle
* Draft development Guide

#### Workflow API 2.0

Now the API is available in maven. Due to the OSSRH process, the package were refactored.
https://repo1.maven.org/maven2/io/github/keymaster65/copper2go-api/

#### Application API 2.0

URLs path must be "/copper2go/2/api/TYPE/MAJOR.MINOR/WORKFLOW-NAME

where

* The '2' relates to the Application API major version
* TYPE can be
    * "request" if a body is expected in the reply
    * "event" if, no body is expected in the reply
* MAJOR.MINOR is the version of the workflow
* WORKFLOW-NAME is the target workflow of the request

URL "/" shows licence information.

### "HTTP Container" Release Notes Workflow API 1.0 and Application API 1.0

* http server support
* http client support
* non-business config
* Multi workflow
* docker image distribution
* add license info
* Add Application and Workflow path
* Event consuming workflows (no reply body, state 202 only)
* quick start

#### Workflow API 1.0

The payload is now part of the WorkflowData
https://github.com/Keymaster65/copper2go/tree/release/1.0/src/main/java/de/wolfsvl/copper2go/workflowapi

#### Application API 1.0

Support for configuration of the HTTP server and
https://github.com/Keymaster65/copper2go/blob/release/1.0/src/main/resources/de/wolfsvl/copper2go/application/config/config.json

URLs must end with /MAJOR.MINOR/WORKFLOW-NAME

where

* MAJOR.MINOR is the version of the workflow
* WORKFLOW-NAME is the target workflow of the request

### "MVP" Release Notes Workflow API 0.1 and Application API 0.1

* 0.1 A first MVP (Minimum Viable Product)

#### Workflow API 0.1

https://github.com/Keymaster65/copper2go/tree/release/0.1/src/main/java/de/wolfsvl/copper2go/workflowapi

#### Application API 0.1

No configuration support and all payloads are processed by the "Hello" workflow.
