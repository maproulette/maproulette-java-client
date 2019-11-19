package org.maproulette.client.batch;

import static org.maproulette.client.utilities.ThrowingConsumer.throwingConsumerWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.maproulette.client.api.ProjectAPI;
import org.maproulette.client.connection.MapRouletteConfiguration;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.exception.MapRouletteRuntimeException;
import org.maproulette.client.model.Challenge;
import org.maproulette.client.model.Project;
import org.maproulette.client.model.Task;

/**
 * A class wrapping all the {@link ChallengeBatch}s for a specific project. There can be multiple
 * challenges per project.
 *
 * @author mcuthbert
 */
public class ProjectBatch
{
    private final Map<Long, ChallengeBatch> batch = new ConcurrentHashMap<>();
    private final MapRouletteConfiguration configuration;
    private final long projectId;

    public ProjectBatch(final long projectId, final MapRouletteConfiguration configuration)
    {
        this.projectId = projectId;
        this.configuration = configuration;
    }

    public ProjectBatch(final Project project, final MapRouletteConfiguration configuration)
    {
        this.configuration = configuration;
        final var projectAPI = new ProjectAPI(configuration);
        try
        {
            final var batchProject = projectAPI.get(project.getName());
            if (batchProject.isEmpty())
            {
                final var newProject = projectAPI.create(project);
                this.projectId = newProject.getId();
            }
            else
            {
                this.projectId = batchProject.get().getId();
            }
        }
        catch (final MapRouletteException e)
        {
            throw new MapRouletteRuntimeException(e);
        }
    }

    /**
     * Adds a task to a batch.
     *
     * @param challenge
     *            The parent challenge of the task, if the challenge identifier is -1 it will
     *            attempt to create the Challenge. If the parent id of the challenge is -1 it will
     *            set the parent id to the default project identifier.
     * @param task
     *            The task to push to the MapRoulette server
     * @return The identifier of the challenge that the task was added too. The user may already
     *         have this information, however if they expect the challenge to be created
     *         automatically.
     * @throws MapRouletteException
     *             Any exceptions thrown while pushing the task data to MapRoulette
     */
    public synchronized long addTask(final Challenge challenge, final Task task)
            throws MapRouletteException
    {
        challenge.setParent(this.projectId);
        final var challengeId = ChallengeBatch.getChallengeId(this.configuration, challenge);
        final var challengeBatch = this.batch.getOrDefault(challengeId,
                new ChallengeBatch(this.configuration, challenge));
        task.setParent(challengeId);
        challengeBatch.addTask(task);
        this.batch.put(challengeId, challengeBatch);
        return challengeId;
    }

    /**
     * Flush a specific challenge found in the project to MapRoulette
     *
     * @param challengeId
     *            The identifier of the challenge to flush
     * @throws MapRouletteException
     *             Any exceptions thrown while pushing the task data to MapRoulette
     */
    public synchronized void flush(final long challengeId) throws MapRouletteException
    {
        final var challengeBatch = this.batch.get(challengeId);
        if (challengeBatch != null)
        {
            challengeBatch.flush();
        }
    }

    /**
     * Flushes all the challenges to MapRoulette
     *
     * @throws MapRouletteException
     *             Any exceptions thrown while pushing the task data to MapRoulette
     */
    public synchronized void flush() throws MapRouletteException
    {
        this.batch.keySet().forEach(throwingConsumerWrapper(key -> this.batch.get(key).flush()));
    }
}
