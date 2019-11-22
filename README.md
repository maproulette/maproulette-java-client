# MapRoulette Java Client

[![Build](https://github.com/osmlab/maproulette-java-client/workflows/Build/badge.svg)](#)
[![quality gate](https://sonarcloud.io/api/project_badges/measure?project=org.maproulette.client%3Amaproulette-java-client&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.maproulette.client%3Amaproulette-java-client)
[![Github Release](https://img.shields.io/github/v/release/osmlab/maproulette-java-client)](https://github.com/osmlab/maproulette-java-client/packages/60203)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The MapRoulette Java Client is a java library that makes it easy to create Projects, Challenges and Tasks. This library focuses primarily on creation, and does not currently implement any other API endpoints.

### Contributing

For contributing guidelines see [CONTRIBUTING.md](CONTRIBUTING.md).

### API

For information on the API Library see the [documentation](docs/using.md).

## Getting Started

Here is examples of adding the library into a project.

- <img src="https://search.maven.org/assets/images/gradle.png" width="30" height="30"/> Gradle Groovy DSL:
```
dependencies
{
    implementation "org.maproulette:maproulette-java-client:0.3.0"
}
```

- <img src="https://search.maven.org/assets/images/mvn.png" width="30" height="30"/> Maven:
```
<dependency>
    <groupId>org.maproulette</groupId>
    <artifactId>maproulette-java-client</artifactId>
    <version>0.3.0</version>
</dependency>
```

- <img src="https://search.maven.org/assets/images/sbt.svg" width="30" height="30"/> Scala SBT:
```
libraryDependencies += "org.maproulette" % "maproulette-java-client" % "0.3.0"
```

- <img src="https://search.maven.org/assets/images/ivy.png" width="30" height="30"/> Apache Ivy:
```
<dependency org="org.maproulette" name="maproulette-java-client" rev="0.3.0"/>
```

