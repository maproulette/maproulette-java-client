package org.maproulette.client.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.maproulette.client.api.ProjectAPI;
import org.maproulette.client.connection.MapRouletteConfiguration;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.exception.MapRouletteRuntimeException;
import org.maproulette.client.model.Challenge;
import org.maproulette.client.model.Project;
import org.maproulette.client.model.Task;
import org.maproulette.client.utilities.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A BatchUploader that will add tasks to a batch and then upload them to MapRoulette
 *
 * @author mcuthbert
 */
public class BatchUploader
{
    private static final Logger logger = LoggerFactory.getLogger(BatchUploader.class);
    private final Map<Long, ProjectBatch> projectBatchMap = new HashMap<>();
    private final ProjectAPI projectAPI;
    private MapRouletteConfiguration configuration;
    private long defaultProjectIdentifier = -1;

    /**
     * Default constructor that will build the default project configuration when initialized.
     *
     * @param configuration
     *            The configuration to connect to MapRoulette
     */
    public BatchUploader(final MapRouletteConfiguration configuration)
    {
        this.configuration = configuration;
        this.projectAPI = new ProjectAPI(configuration);
    }

    /**
     * Adds a set of tasks to the batch
     *
     * @param challenge
     *            The challenge to add the tasks under
     * @param tasks
     *            The tasks to add to the batch
     * @return A tuple containing the identifiers first for the project that the task was created
     *         in, and then the challenge.
     * @throws MapRouletteException
     *             If there are any failures while adding tasks to the batch. This will only occur
     *             if you add more tasks than the match batch size and it automatically tries to
     *             flush the tasks to the maproulette server
     */
    public Tuple<Long, Long> addTasks(final Challenge challenge, final List<Task> tasks)
            throws MapRouletteException
    {
        var parents = new Tuple<>(-1L, -1L);
        for (final Task task : tasks)
        {
            parents = this.addTask(challenge, task);
        }
        return parents;
    }

    /**
     * This will add tasks to the batch, it will use the default project found in the configuration
     *
     * @param challenge
     *            The Challenge you are adding the task too. The project will be either based on the
     *            parent identifier in the challenge object or the default project in the
     *            configuration.
     * @param task
     *            The task to add to the batch.
     * @return A tuple containing the identifiers first for the project that the task was created
     *         in, and then the challenge.
     * @throws MapRouletteException
     *             if there are any failures adding the task to the batch
     */
    public Tuple<Long, Long> addTask(final Challenge challenge, final Task task)
            throws MapRouletteException
    {
        var identifier = challenge.getParent();
        if (identifier == -1)
        {
            identifier = this.getDefaultProjectIdentifier();
        }

        final long finalIdentifier = identifier;
        final var projectBatch = this.projectBatchMap.computeIfAbsent(identifier,
                k -> new ProjectBatch(finalIdentifier, this.configuration));

        final var challengeId = projectBatch.addTask(challenge, task);
        this.projectBatchMap.put(identifier, projectBatch);
        return new Tuple<>(identifier, challengeId);
    }

    /**
     * Flushes a single project batch
     *
     * @param identifier
     *            The identifier of the project that you want to flush
     * @throws MapRouletteException
     *             Any exceptions that occur while flushing the project
     */
    public void flush(final long identifier) throws MapRouletteException
    {
        final var projectBatch = this.projectBatchMap.get(identifier);
        if (projectBatch != null)
        {
            projectBatch.flush();
        }
    }

    /**
     * Flushes all project batches
     */
    public void flushAll()
    {
        this.projectBatchMap.forEach((identifier, batch) ->
        {
            try
            {
                logger.info("Flushing project {}", identifier);
                batch.flush();
            }
            catch (final MapRouletteException e)
            {
                throw new MapRouletteRuntimeException(e);
            }
        });
    }

    /**
     * Get the project id for the configuration's default project name. If the project id is not yet
     * known, HTTP requests will be make to (1) get the default project and (2) if the default
     * project doesn't exist create it. <br>
     * THIS METHOD MAY MAKE EXTERNAL HTTP REQUESTS. <br>
     *
     * @return project id
     * @throws MapRouletteException
     *             when the requests fail
     */
    private long getDefaultProjectIdentifier() throws MapRouletteException
    {
        if (this.defaultProjectIdentifier == -1)
        {
            final Optional<Project> defaultProject = this.projectAPI
                    .get(this.configuration.getDefaultProjectName());
            if (defaultProject.isEmpty())
            {
                this.defaultProjectIdentifier = this.projectAPI.create(
                        Project.builder().name(this.configuration.getDefaultProjectName()).build())
                        .getId();
            }
            else
            {
                this.defaultProjectIdentifier = defaultProject.get().getId();
            }
        }
        return this.defaultProjectIdentifier;
    }
}
