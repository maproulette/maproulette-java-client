package org.maproulette.client;

import org.apache.http.HttpHost;
import org.maproulette.client.api.ChallengeAPI;
import org.maproulette.client.api.ProjectAPI;
import org.maproulette.client.api.TaskAPI;
import org.maproulette.client.connection.MapRouletteConfiguration;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.Project;

/**
 * @author mcuthbert
 */
public class IntegrationBase
{
    public static final String DEFAULT_PROJECT_NAME = "TestProject";
    private final MapRouletteConfiguration configuration = new MapRouletteConfiguration(
            HttpHost.DEFAULT_SCHEME_NAME, "localhost", 9000, DEFAULT_PROJECT_NAME, "test");
    private final ProjectAPI projectAPI = new ProjectAPI(this.configuration);
    private final ChallengeAPI challengeAPI = new ChallengeAPI(this.configuration);
    private final TaskAPI taskAPI = new TaskAPI(this.configuration);
    private final Project defaultProject = Project.builder().name(DEFAULT_PROJECT_NAME)
            .description("Project Description").displayName("Project Displayname").enabled(true)
            .build();
    private long defaultProjectIdentifier = -1;

    public long getDefaultProjectIdentifier()
    {
        return this.defaultProjectIdentifier;
    }

    public Project getDefaultProject()
    {
        return this.defaultProject;
    }

    public TaskAPI getTaskAPI()
    {
        return this.taskAPI;
    }

    public ChallengeAPI getChallengeAPI()
    {
        return this.challengeAPI;
    }

    public ProjectAPI getProjectAPI()
    {
        return this.projectAPI;
    }

    public MapRouletteConfiguration getConfiguration()
    {
        return this.configuration;
    }

    public void setup() throws MapRouletteException
    {
        // build the project that will be used to execute the integration tests for the challenges
        this.defaultProjectIdentifier = this.projectAPI.create(this.defaultProject).getId();
    }

    public void teardown() throws MapRouletteException
    {
        // remove the project that is used to execute the integration tests for the challenges
        if (this.defaultProjectIdentifier != -1)
        {
            this.projectAPI.forceDelete(this.defaultProjectIdentifier);
        }
    }
}
