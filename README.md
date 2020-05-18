# MapRoulette Java Client

![Build](https://github.com/osmlab/maproulette-java-client/workflows/Build/badge.svg?branch=master)
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

<img src="https://search.maven.org/assets/images/gradle.png" width="30" height="30"/> Gradle Groovy DSL:

```
repositories 
{
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/osmlab/maproulette-java-client")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getProperty("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getProperty("PASSWORD")
        }
    }
}

dependencies
{
    implementation "org.maproulette:maproulette-java-client:0.3.0"
}
```

This project is published using Github Package Registry. Unfortunately one of the drawbacks of using Github Package Registry is that to download a package you have to be authenticated and have permissions to read packages. Hopefully this will change in the future, however for now you will have to supply a username and token to access the package. 

The "credentials" section above shows that it will look for any properties found in gradle with `gpr.user` for your Github username and if not found it will look at any passed in system environment variables called `USERNAME`. Likewise it will do the same for password, except obviously looking for `gpr.key` and `PASSWORD` respectively.
