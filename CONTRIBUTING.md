# Contributing to MapRoulette-Java-Client

Thanks for taking the time to contribute!

## Where to ask a question

TODO

## Suggestions and bug reports

### Reporting bugs

If you have found a bug to report, that is great! Please search for similar bugs in GitHub issues, as your bug might have been filed already. If not, filing a GitHub issue is the next thing to do!

### Filing a Github issue

When submitting an issue, here is the information to include:

* Title: Use a title that is as self explanatory as possible
* Summary: Detailed description of the issue, including screenshots and/or stack traces
* Steps to reproduce: Do not forget to include links to data samples that can help in reproducing the issue
* Actual vs. Expected: Describe the results and how they differ from the expected behavior
* Workaround: If you have found a temporary workaround the issue, please also include it there!

### Suggesting enhancements

Enhancements are also handled with GitHub issues. Make sure to include the following:

* Title: Use a title that is as self explanatory as possible
* Summary: Use-case description of the proposed enhancement
* Desired: Describe the desired behavior of the proposed enhancement
* Implementation proposal: If you have an idea of how to implement the enhancement

## Submitting code

### Requirements

* Open JDK 11
* Gradle

### First contribution

The first step would be to fork the project in your own github account, then clone the project locally using `git clone`. Then use gradle to build the code, and run tests:

```
cd <my clone location>
./gradlew build
```

To start contributing to your fork, the best way is to make sure that your IDE is setup to follow the [code formatting template](config/format/code_format.xml).

Once you have fixed an issue or added a new feature, it is time to submit [a pull request](#pull-request-guidelines)!

### Building

TODO

#### Log4j Properties

Make sure you first have a `log4j.properties` file in `src/main/resources`. 
Alternatively, you can have as a VM parameter:

``` 
-Dlog4j.configuration=file://<path_to_config>
```

https://github.com/osmlab/maproulette-java-client/blob/dev/config/log4j/log4j.properties

#### IntelliJ Setup

IntellJ IDEA works pretty much out of the box. You can just open a new project with the checked out folder and select it as a gradle project.

### Code formatting

The project's code is checked by Checkstyle as part of the `gradle check` step.

There also is an eclipse code formatting template [here](config/format/code_format.xml) that is used by [Spotless](https://github.com/diffplug/spotless) to check that the formatting rules are being followed. In case those are not, the `gradle spotlessCheck` step will fail and the build will not pass. Spotless provides an easy fix though, with `gradle spotlessApply` which will refactor your code so it follows the formatting guidelines.

### Testing

The codebase contains an extensive range of unit tests. Unit tests are supposed to run fairly fast. All the tests will be run for every pull request build, so make sure that it doesn't take an exceptionally long time to run! When contributing new code, make sure to not break existing tests (or modify them and explain why the modification is needed) and to add new tests for new features.

### Pull Request Guidelines

Pull requests comments should follow the template below:

* An as extensive as reasonable description of the change proposed, in easy to read [Markdown](https://guides.github.com/features/mastering-markdown/), with as many code snippet examples, screen captures links and diagrams as possible
* A Benefit/Drawback analysis: what does this improve, and at what cost? Is the performance impacted or improved?
* Label: If applicable, apply one of the available labels to the pull request
