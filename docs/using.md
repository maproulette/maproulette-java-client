# Using the MapRoulette Java Client

## Configuration

Configuration is handled through the class [MapRouletteConfiguration](../src/main/java/org/maproulette/client/MapRouletteConfiguration.java) and [ProjectConfiguration](../src/main/java/org/maproulette/client/data/ProjectConfiguration.java). 

The MapRouletteConfiguration contains the following properties to be set:

- **apiKey** - The user's APIKey to authenticate the user to the MapRoulette server.
- **scheme** - Either HTTP or HTTPS.
- **server** - The hostname for the MapRoulette server.
- **projectConfiguration** - an Instance of the ProjectConfiguration class.

Th ProjectConfiguration contains the following properties to be set:

- **name** - The name of project. This would be the root project that you want to use to upload data too.
- **description** - The description for the project. In cases where the project does not exist already it will create a project with this description.
- **displayName** - The name used for display purposes of the project.
- **enabled** - Whether this project is enabled.

The project configuration is used to either find a project that exists with the same name or creates a new project with that name. 

The project can be initialized either through a parsable string as so:
```java
final var configuration = MapRouletteConfiguration.parse("https://maproulette.org:80:Project1:API_KEY_VALUE");
```

The configuration string can be in the following formats:
```
[SERVER]:[PORT]:[PROJECT_NAME]:[API_KEY]
OR
[SCHEME]://[SERVER]:[PORT]:[PROJECT_NAME]:[API_KEY]
```

You can also initialize the MapRouletteConfiguration through code using the following:
```java
final var configuration = new MapRouletteConfiguration("https", "maproulette.org", 80, new ProjectConfiguration("PROJECT_NAME", "PROJECT_DESCRIPTION", "PROJECT_DISPLAY_NAME", true), "API_KEY_VALUE");
```

### API

The API as mentioned previously focuses exclusively on building projects, challenges and tasks. So any MapRoulette API's that are using for retrieving data from MapRoulette are not included, although can be in the future.

In the documentation below I will use 3 constant variables:
- PROJECT_ID - which will be referenced any time I need to define a project identifier, usually this would be for the parent of a challenge.
- CHALLENGE_ID - the same as above except obviously for the challenge identifier and will usually be used for the parent of a task.
- TASK_ID - Used whenever I need to define a task identifier.

#### API Objects
The API objects map to the MapRoulette objects Project, Challenge and Tasks which together create a hierarchy of objects in that order in MapRoulette. The objects can be created through their constructors, however the recommended way of creating the objects is through the builder factory which is the method that will be shown in the examples.

[Project.java](../src/main/java/org/maproulette/client/data/Project.java)
- **id:Long** (Default: -1) - The id of the project. This field will be set by the client when it attempts to retrieve the Project based on the [ProjectConfiguration](../src/main/java/org/maproulette/client/data/ProjectConfiguration.java)
- **name:String** (Required) - The name of the project.
- **description:String** - The description for the project.
- **displayName:String** - The display name of the project that will used primarily for display purposes.
- **enabled:Boolean** (Default: false) - Whether or not the project is enabled or not.

Creating a Project:
```java
final var project = Project.builder().name("Project1")
                    .description("This is an example project")
                    .displayName("Example Project")
                    .enabled(true).build();
```

#### Challenge Object
[Challenge.java](../src/main/java/org/maproulette/client/data/Challenge.java)
- **id:Long** (Default: -1) - The id of the challenge. This field will be set by the client when it attempts to retrieve the Challenge. 
- **parent:Long** (Default: -1) - The identifier of the parent project for this challenge. This field will also be set when the Challenge is retrieved from the MapRoulette server.
- **blurb:String** - A short description for the Challenge.
- **enabled:boolean** - Whether this challenge is enabled or not.
- **description:String** - A full description for the Challenge.
- **difficulty:ChallengeDifficulty** (Default: NORMAL) - The challenge difficulty, either EASY, NORMAL or EXPERT.
- **instruction:String** - The challenge instruction. If an specific instruction is not set for the task then it will default to this instruction.
- **featured:boolean** (Default: false) - If set to true this will be a featured challenge in MapRoulette, this field can only be set by a Super user in MapRoulette otherwise it will be set to false.
- **checkinComment:String** (Default: "") - The default checkin comment associated with the Task.
- **checkinSource:String** (Default: "") - The source for the checkin.
- **name:String** (Required) - The name of the Challenge.
- **defaultPriority:ChallengePriority** - The default priority for any tasks that don't fit inside an of the priority rules.
- **highPriorityRule:RuleList*** - the string used to define the high priority rule. 
- **mediumPriorityRule:RuleList*** - the string used to define the medium priority rule.
- **lowPriorityRule:RuleList*** - the string used to define the low priority rule.
- **defaultZoom:Int** (Default: 13) - The default zoom level for the map when viewing tasks in the challenge.
- **minZoom:Int** (Default: 1) - The minimum zoom level that can be used in the map when viewing tasks in the challenge.
- **maxZoom:Int** (Default: 19) - The maximum zoom level that can be used in the map when viewing tasks in teh challenge.
- **defaultBasemap:Int** - The default base map identifier
- **defaultBasemapId:String** - 
- **customBasemap:String** - reference to a custom base map to use when viewing tasks.

`*` The priority strings are built in the following format:

TODO

Creating a Challenge:
```java
// Not all properties are set, but follow the same pattern if they are need to be set.
final var challenge = Challenge.builder().parent(PROJECT_ID).name("Challenge1")
                        .instruction("Challenge Instruction")
                        .difficulty(ChallengeDifficulty.EASY)
                        .priority(ChallengePriority.LOW)
                        .maxZoom(17).build();
```

#### Task Object
[Task.java](../src/main/java/org/maproulette/client/data/Task.java)
- **taskIdentifier:String** - The identifier for the task, it can be 
- **projectName:String** - The name of the project in the task hierarchy. This value will be set by the MapRouletteClient when uploading to MapRoulette.
- **challengeName:String** - The name of the challenge in the task hierarchy. This value will be set by the MapRouletteClient when uploading to MapRoulette.
- **instruction:String** - The instruction for the specific task.
- **geoJson:JsonArray** - The geometry (FeatureCollection) for the task.
- **points:Set\<PointInformation\>** - Extra points that can be added to the Task geometry. Usually this is to emphasis something in the task geometry.

Creating a Task:
```java
final var geoJson = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[1.0,2.0]},\"properties\":{\"name\":\"property\"}}";
final var task = Task.builder().parent(CHALLENGE_ID).name("ExampleTask")
                    .instruction("Example Task instruction")
                    .addGeojson(geoJson)
                    .addPoint(new PointInformation(5.0, 6.0))
                    .build();
```

### Working with the API

The API is currently split into 3 categories, ProjectAPI, ChallengeAPI and TaskAPI. And as expected each object deals with the APIs that match the specific object that it is working with. Additionally there is a BatchUploader that will allow you to easily upload batches of Tasks. Currently the API focuses on object creation, so other APIs in MapRoulette are not currently supported. However the structure of the code is built in such a way as to make the ability to extend to these new APIs quite easy.

#### ProjectAPI
Below are the functions that are available to use with the ProjectAPI object. To initialize the ProjectAPI you do the following:
```java
final var configuration = MapRouletteConfiguration.parse("https://maproulette.org:80:Project1:API_KEY_VALUE");
final var projectAPI = new ProjectAPI(configuration);
```

**FUNCTIONS**

---
```java
// GET a specific project by name
final Optional<Project> project = projectAPI.get("NameOfProject");
```
```java
// GET a specific project by it's identifier
final Optional<Project> project = projectAPI.get(PROJECT_ID);
```
```java
// CREATE a new project
final var newProject = Project.builder().name("ExampleProject").build();
final Project project = projectAPI.create(newProject);
```
```java
// UPDATE an existing project
final var updateProject = Project.builder().name("ExampleProject")
                            .id(PROJECT_ID)
                            .description("Updated Description!")
                            .build();
final Project project = projectAPI.update(updateProject);
```
```java
// DELETE an existing project. This API will really only set the project up for later deletion, so flags for deletion, but doesn't actually delete it. A scheduled job that runs daily will delete this later.
projectAPI.delete(PROJECT_ID);
```
```java
// DELETE an existing project.
projectAPI.forceDelete(PROJECT_ID);
```

#### ChallengeAPI
Below are the functions that are available to use with the ChallengeAPI object. To initialize the ChallengeAPI you do the following:
```java
final var configuration = MapRouletteConfiguration.parse("https://maproulette.org:80:Project1:API_KEY_VALUE");
final var challengeAPI = new ChallengeAPI(configuration);
```

**FUNCTIONS**

---
```java
// GET a specific challenge by name within a specific project by project ID
final Optional<Challenge> challenge = challengeAPI.get(PROJECT_ID, "ExampleChallenge");
```
```java
// GET a specific challenge by it's identifier
final Optional<Challenge> challenge = challengeAPI.get(CHALLENGE_ID);
```
```java
// CREATE a new challenge
final var challenge = Challenge.builder().parent(PROJECT_ID)
                        .name("ExampleChallenge")
                        .instruction("Example Instruction").build();
final var newChallenge = challengeAPI.create(challenge);
```
```java
// UPDATE a challenge
final var challenge = Challenge.builder().parent(PROJECT_ID)
                        .name("ExampleChallenge")
                        .instruction("Example Instruction")
                        .id(CHALLENGE_ID).blurb("Updated Blurb").build();
final var updatedChallenge = challengeAPI.update(challenge);
```
```java
// DELETES a challenge - This API will really only set the challenge up for later deletion, so flags for deletion, but doesn't actually delete it. A scheduled job that runs daily will delete this later.
challengeAPI.delete(CHALLENGE_ID);
```
```java
// DELETE a challenge immediately out of the database
challengeAPI.forceDelete(CHALLENGE_ID);
```

#### TaskAPI
Below are the functions that are available to use with the TaskAPI object. To initialize the TaskAPI you do the following:
```java
final var configuration = MapRouletteConfiguration.parse("https://maproulette.org:80:Project1:API_KEY_VALUE");
final var taskAPI = new TaskAPI(configuration);
```

**FUNCTIONS**

---
```java
// GET a specific task by name within a specific challenge by challenge ID
final Optional<Task> task = taskAPI.get(CHALLENGE_ID, "ExampleTask");
```
```java
// GET a specific task by it's identifier
final Optional<Task> task = taskAPI.get(TASK_ID);
```
```java
// CREATE a new task
final var geojson = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[1.0,2.0]},\"properties\":{\"name\":\"property\"}}";
final var task = Task.builder().parent(CHALLENGE_ID)
                    .name("ExampleTask").addGeojson(geojson).build();
final var newTask = taskAPI.create(task);
```
```java
// UPDATE a task
final var geojson = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[1.0,2.0]},\"properties\":{\"name\":\"property\"}}";
final var task = Task.builder().parent(CHALLENGE_ID)
                    .name("ExampleTask")
                    .instruction("Updated Instruction")
                    .id(TASK_ID).geojson(geojson).build();
final var updatedTask = taskAPI.update(task);
```
```java
// DELETES a task
taskAPI.delete(TASK_ID);
```
```java
// DELETE a task - the interface requires a forceDelete function so it is implemented but Tasks do not have a delayed deletion mechanism so forceDelete is identical to delete.
taskAPI.forceDelete(TASK_ID);
```

#### BatchUploader
The BatchUploader object is for the most part a wrapper around the API objects allowing the user to upload tasks in a batch easily. Using the BatchUploader is very simple and below is an example of how it works.

Example adding tasks to a single project and challenge. By setting -1 for all the parent identifiers for the challenge and children tasks it will automatically setup the object hierarchy and set the identifiers.
```java
final var configuration = new MapRouletteConfiguration("maproulette.org", 80, "DefaultProject", "API_KEY");
final var uploader = new BatchUploader(configuration);
// By setting the parent project identifier to -1 it will use the default project from the configuration
final var challenge = Challenge.builder().name("PrimaryChallenge")
                        .instruction("Primary Challenge Instruction").build();
final var geojson = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[1.0,2.0]},\"properties\":{\"name\":\"property\"}}";
for (final var i = 0; i < 10; i++) {
    uploader.addTask(Task.builder().name("Task" + i).addGeojson(geojson).build());
}
uploader.flush();
```

Example adding tasks to multiple projects and multiple challenges.
```java
final var configuration = new MapRouletteConfiguration("maproulette.org", 80, "DefaultProject", "API_KEY");
final var uploader = new BatchUploader(configuration);
final var batchUploader = new BatchUploader(configuration);
        final var prefix = "zzzzzzProject";
        final var projectList = new ArrayList<Long>();
        for (int projectIndex = 0; projectIndex < NUMBER_PROJECTS; projectIndex++)
        {
            final var projectIdentifier = this.getProjectAPI()
                    .create(Project.builder().name(prefix + projectIndex).build()).getId();
            projectList.add(projectIdentifier);
            this.addDefaultTasks(batchUploader, projectIdentifier);
        }
        batchUploader.flushAll();
```

For more examples you can look at the integration tests, specifically [BatchUploaderIntegrationTest](../src/integrationTest/java/org/maproulette/client/batch/BatchUploaderIntegrationTest.java)


