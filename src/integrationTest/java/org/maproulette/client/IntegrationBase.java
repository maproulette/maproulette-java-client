package org.maproulette.client;

import org.apache.commons.lang.StringUtils;
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
    public static final String DEFAULT_PROJECT_NAME = "IntegrationTestProject1";
    public static final String ENVIRONMENT_SCHEME = "scheme";
    public static final String ENVIRONMENT_HOST = "host";
    public static final String ENVIRONMENT_PORT = "port";
    public static final String ENVIRONMENT_API_KEY = "apiKey";
    private MapRouletteConfiguration configuration = null;
    private ProjectAPI projectAPI = null;
    private ChallengeAPI challengeAPI = null;
    private TaskAPI taskAPI = null;
    private final Project defaultProject = Project.builder().name(DEFAULT_PROJECT_NAME)
            .description("Project Description").displayName("Project Displayname").enabled(true)
            .build();
    private long defaultProjectIdentifier = -1;
    private long projectIdentifier = -1;
    private String scheme;
    private String host;
    private int port;
    private String apiKey;

    public long getDefaultProjectIdentifier()
    {
        return this.defaultProjectIdentifier;
    }

    public long getNewProjectIdentifier()
    {
        return this.projectIdentifier;
    }

    public Project getDefaultProject()
    {
        return this.defaultProject;
    }

    public TaskAPI getTaskAPI()
    {
        if (this.taskAPI == null)
        {
            this.taskAPI = new TaskAPI(this.getConfiguration());
        }
        return this.taskAPI;
    }

    public TaskAPI getTaskAPIForNewConfiguration()
    {
        if (this.taskAPI == null)
        {
            this.taskAPI = new TaskAPI(this.getConfigurationExcludingProject());
        }
        return this.taskAPI;
    }

    public ChallengeAPI getChallengeAPI()
    {
        if (this.challengeAPI == null)
        {
            this.challengeAPI = new ChallengeAPI(this.getConfiguration());
        }
        return this.challengeAPI;
    }

    public ChallengeAPI getChallengeAPIForNewConfiguration()
    {
        if (this.challengeAPI == null)
        {
            this.challengeAPI = new ChallengeAPI(this.getConfigurationExcludingProject());
        }
        return this.challengeAPI;
    }

    public MapRouletteConfiguration getConfigurationExcludingProject()
    {
        if (this.configuration == null)
        {
            this.configurationParamsSetUp();
            this.configuration = new MapRouletteConfiguration(this.scheme, this.host, this.port,
                    this.apiKey);
        }
        return this.configuration;
    }

    public ProjectAPI getProjectAPI()
    {
        if (this.projectAPI == null)
        {
            this.projectAPI = new ProjectAPI(this.getConfiguration());
        }
        return this.projectAPI;
    }

    public ProjectAPI getProjectAPIForNewConfiguration()
    {
        if (this.projectAPI == null)
        {
            this.projectAPI = new ProjectAPI(this.getConfigurationExcludingProject());
        }
        return this.projectAPI;
    }

    public MapRouletteConfiguration getConfiguration()
    {
        if (this.configuration == null)
        {
            this.configurationParamsSetUp();
            this.configuration = new MapRouletteConfiguration(this.scheme, this.host, this.port,
                    DEFAULT_PROJECT_NAME, this.apiKey);
        }
        return this.configuration;
    }

    private void configurationParamsSetUp()
    {
        this.scheme = System.getenv(ENVIRONMENT_SCHEME);
        if (StringUtils.isEmpty(this.scheme))
        {
            this.scheme = "https";
        }

        this.host = System.getenv(ENVIRONMENT_HOST);
        if (StringUtils.isEmpty(this.host))
        {
            this.host = "localhost";
        }
        try
        {
            this.port = Integer.parseInt(System.getenv(ENVIRONMENT_PORT));
        }
        catch (final NumberFormatException e)
        {
            this.port = 9000;
        }
        this.apiKey = System.getenv(ENVIRONMENT_API_KEY);
        if (StringUtils.isEmpty(this.apiKey))
        {
            this.apiKey = "test";
        }
    }

    public Project buildProject()
    {
        return Project.builder().name("Test project name").description("Project Description ")
                .displayName("Project Display name").enabled(true).build();
    }

    public void setup() throws MapRouletteException
    {
        // build the project that will be used to execute the integration tests for the challenges
        this.defaultProjectIdentifier = this.getProjectAPI().create(this.defaultProject).getId();
        this.projectIdentifier = this.getProjectAPIForNewConfiguration().create(this.buildProject())
                .getId();
    }

    public void teardown() throws MapRouletteException
    {
        // remove the project that is used to execute the integration tests for the challenges
        if (this.defaultProjectIdentifier != -1)
        {
            this.getProjectAPI().forceDelete(this.defaultProjectIdentifier);
        }
        if (this.getNewProjectIdentifier() != -1)
        {
            this.getProjectAPIForNewConfiguration().forceDelete(this.getNewProjectIdentifier());
        }
    }
}
