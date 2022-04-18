package org.maproulette.client.batch;

import static org.maproulette.client.utilities.ThrowingConsumer.throwingConsumerWrapper;

import java.util.ArrayList;
import java.util.List;

import org.maproulette.client.api.ChallengeAPI;
import org.maproulette.client.api.QueryConstants;
import org.maproulette.client.connection.IMapRouletteConnection;
import org.maproulette.client.connection.MapRouletteConfiguration;
import org.maproulette.client.connection.MapRouletteConnection;
import org.maproulette.client.connection.Query;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.exception.MapRouletteRuntimeException;
import org.maproulette.client.model.Challenge;
import org.maproulette.client.model.Task;
import org.maproulette.client.utilities.ObjectMapperSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Batches tasks for a challenge
 *
 * @author mcuthbert
 */
@Getter
@RequiredArgsConstructor
public class ChallengeBatch
{
    private static final int MAXIMUM_BATCH_SIZE = 500;
    private final Logger logger = LoggerFactory.getLogger(ChallengeBatch.class);
    private final ObjectMapper mapper = ObjectMapperSingleton.getMapper();
    private final IMapRouletteConnection connection;
    private final long challengeId;
    private final int maxBatchSize;
    private final List<Task> batch = new ArrayList<>();

    public static long getChallengeId(final MapRouletteConfiguration configuration,
            final Challenge challenge)
    {
        final var challengeAPI = new ChallengeAPI(configuration);
        try
        {
            final var batchChallenge = challengeAPI.get(challenge.getParent(), challenge.getName());
            if (batchChallenge.isEmpty())
            {
                final var newChallenge = challengeAPI.create(challenge);
                return newChallenge.getId();
            }
            else
            {
                return batchChallenge.get().getId();
            }
        }
        catch (final MapRouletteException e)
        {
            throw new MapRouletteRuntimeException(e);
        }
    }

    public ChallengeBatch(final MapRouletteConfiguration configuration, final long challengeId)
    {
        this(new MapRouletteConnection(configuration), challengeId, configuration.getBatchSize());
    }

    /**
     * This constructor allows us to check whether the challenge has actually been created or not.
     *
     * @param configuration
     *            {@link MapRouletteConfiguration} object for the MapRoulette Server
     * @param challenge
     *            The challenge to create if not exists on the server
     */
    public ChallengeBatch(final MapRouletteConfiguration configuration, final Challenge challenge)
    {
        this.connection = new MapRouletteConnection(configuration);
        this.maxBatchSize = configuration.getBatchSize();
        this.challengeId = getChallengeId(configuration, challenge);
    }

    /**
     * Adds a set of Tasks to the batch. If the batch hits the maximum size it will automatically
     * flush the batch.
     *
     * @param tasks
     *            The tasks to add to the batch.
     * @throws MapRouletteException
     *             If it flushes, it may through a MapRouletteException when pushing data to
     *             MapRoulette
     */
    public synchronized void addTasks(final List<Task> tasks) throws MapRouletteException
    {
        tasks.forEach(throwingConsumerWrapper(this::addTask));
    }

    /**
     * Adds a task to the Challenge batch. If the batch hits the maximum size it will automatially
     * flush the batch.
     *
     * @param task
     *            Tasks are unique based on the Task identifier.
     * @throws MapRouletteException
     *             If it flushes, it may through a MapRouletteException when pushing data to
     *             MapRoulette
     */
    public synchronized void addTask(final Task task) throws MapRouletteException
    {
        task.setParent(this.challengeId);
        this.batch.add(task);
        if (this.batch.size() >= this.maxBatchSize)
        {
            this.logger.debug("FLUSHING queued tasks as batch size {} meets max {}",
                    this.batch.size(), this.maxBatchSize);
            this.flush();
        }
    }

    /**
     * Flushes all the tasks from the Challenge batch
     *
     * @throws MapRouletteException
     *             If there are any failures during the upload
     */
    public synchronized void flush() throws MapRouletteException
    {
        if (!this.batch.isEmpty())
        {
            this.uploadBatchTasks(this.challengeId, this.batch);
            this.batch.clear();
        }
    }

    private boolean uploadBatchTasks(final long challengeId, final List<Task> data)
            throws MapRouletteException
    {
        final var uniqueTasks = new ArrayList<Task>(data.size());
        uniqueTasks.addAll(data);
        // MAXIMUM batch size is 500, so if greater than 500, we need to make multiple
        // requests in groups of 500
        var succeeded = true;
        var startIndex = 0;
        int endIndex;
        do
        {
            endIndex = Math.min(startIndex + MAXIMUM_BATCH_SIZE, uniqueTasks.size());
            final var uploadList = uniqueTasks.subList(startIndex, endIndex);
            succeeded &= internalUploadBatchTasks(challengeId, uploadList);
            startIndex += MAXIMUM_BATCH_SIZE;
        }
        while (endIndex != uniqueTasks.size() - 1 && startIndex < uniqueTasks.size());
        return succeeded;
    }

    private boolean internalUploadBatchTasks(final long parentChallengeId, final List<Task> data)
            throws MapRouletteException
    {
        if (data.isEmpty())
        {
            return false;
        }
        this.logger.debug("Uploading batch of {} tasks for challenge {}", data.size(),
                parentChallengeId);
        try
        {
            final var postData = this.mapper.createArrayNode();
            data.forEach(task -> postData.add(this.mapper.convertValue(task, JsonNode.class)));
            final var postDataString = this.mapper.writeValueAsString(postData);
            final Query query = Query.builder().post(QueryConstants.URI_TASK_POST + "s")
                    .data(postDataString).build();
            this.connection.execute(query);
            return true;
        }
        catch (final JsonProcessingException e)
        {
            throw new MapRouletteException(e);
        }
    }
}
