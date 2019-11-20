# MapRoulette Java Client

[![Build Status](https://github.com/osmlab/maproulette-java-client/workflows/build.yml/badge.svg)](https://github.com/osmlab/maproulette-java-client/actions)
[![quality gate](https://sonarcloud.io/api/project_badges/measure?project=org.maproulette.client%3Amaproulette-java-client&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.maproulette.client%3Amaproulette-java-client)
[![Maven Central](https://img.shields.io/maven-central/v/org.maproulette.client/maproulette-java-client.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.maproulette.client%22%20AND%20a:%22maproulette-java-client%22)

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
    implementation "org.maproulette.client:maproulette-java-client:1.0.0"
}
```

- <img src="https://search.maven.org/assets/images/mvn.png" width="30" height="30"/> Maven:
```
<dependency>
    <groupId>org.maproulette.client</groupId>
    <artifactId>maproulette-java-client</artifactId>
    <version>0.1.0</version>
</dependency>
```

- <img src="https://search.maven.org/assets/images/sbt.svg" width="30" height="30"/> Scala SBT:
```
libraryDependencies += "org.maproulette.client" % "maproulette-java-client" % "0.1.0"
```

- <img src="https://search.maven.org/assets/images/ivy.png" width="30" height="30"/> Apache Ivy:
```
<dependency org="org.maproulette.client" name="maproulette-java-client" rev="0.1.0"/>
```

