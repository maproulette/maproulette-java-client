package org.maproulette.client.batch;

import static org.maproulette.client.utilities.ThrowingConsumer.throwingConsumerWrapper;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.IntegrationBase;
import org.maproulette.client.TestConstants;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.Challenge;
import org.maproulette.client.model.Project;
import org.maproulette.client.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mcuthbert
 */
public class BatchUploaderIntegrationTest extends IntegrationBase
{
    private static final Logger LOG = LoggerFactory.getLogger(BatchUploaderIntegrationTest.class);
    private static final int NUMBER_PROJECTS = 4;
    private static final int NUMBER_CHALLENGES = 3;
    private static final int NUMBER_TASKS = 10;

    @Test
    public void batchTest() throws MapRouletteException
    {
        final var batchUploader = new BatchUploader(this.getConfiguration());
        // By setting the parent identifier to -1 it will force the challenge to use the default
        // configuration for MapRoulette. Alternatively you can just create the Project beforehand
        // and pass that information in.
        final var challenge = Challenge.builder().parent(-1).name("TestChallenge")
                .instruction("TestInstruction").build();

        final var tasks = new ArrayList<Task>();
        for (int index = 0; index < NUMBER_TASKS; index++)
        {
            tasks.add(this.getDefaultTask(challenge.getId(), "Task" + index));
        }
        final var parents = batchUploader.addTasks(challenge, tasks);
        batchUploader.flushAll();

        // retrieve each task that was created
        for (int index = 0; index < NUMBER_TASKS; index++)
        {
            final var task = this.getTaskAPI().get(parents.getSecond(), "Task" + index);
            Assertions.assertTrue(task.isPresent());
        }
    }

    @Test
    public void multipleChallengeBatchTest() throws MapRouletteException
    {
        final var batchUploader = new BatchUploader(this.getConfiguration());
        this.addDefaultTasks(batchUploader, -1);
        batchUploader.flushAll();

        final var parentProject = this.getProjectAPI().get(DEFAULT_PROJECT_NAME);
        Assertions.assertTrue(parentProject.isPresent());
        final var challengeList = this.getProjectAPI().children(parentProject.get().getId(),
                NUMBER_CHALLENGES, 0);

        // find all the tasks
        challengeList.forEach(throwingConsumerWrapper(challenge ->
        {
            final var taskChildren = this.getChallengeAPI().children(challenge.getId(),
                    NUMBER_TASKS, 0);
            Assertions.assertEquals(NUMBER_TASKS, taskChildren.size());
        }));

        // clean up
        this.getProjectAPI().forceDelete(parentProject.get().getId());
    }

    @Test
    public void multipleProjectBatchTest() throws MapRouletteException
    {
        final var batchUploader = new BatchUploader(this.getConfiguration());
        final var prefix = "zzzzzzProject";
        final var projectList = new ArrayList<Long>();
        for (int projectIndex = 0; projectIndex < NUMBER_PROJECTS; projectIndex++)
        {
            final var projectIdentifier = this.getProjectAPI()
                    .create(Project.builder().name(prefix + projectIndex).build()).getId();
            projectList.add(projectIdentifier);
            LOG.debug("Starting task add for project id={}", projectIdentifier);
            this.addDefaultTasks(batchUploader, projectIdentifier);
        }
        batchUploader.flushAll();

        Assertions.assertEquals(projectList.size(), NUMBER_PROJECTS);

        projectList.forEach(throwingConsumerWrapper(identifier ->
        {
            final var children = this.getProjectAPI().children(identifier, NUMBER_TASKS, 0);
            Assertions.assertEquals(NUMBER_CHALLENGES, children.size());
            children.forEach(throwingConsumerWrapper(child ->
            {
                final var tasks = this.getChallengeAPI().children(child.getId(), NUMBER_TASKS, 0);
                Assertions.assertEquals(NUMBER_TASKS, tasks.size());
            }));
        }));
    }

    private void addDefaultTasks(final BatchUploader uploader, final long parentIdentifier)
            throws MapRouletteException
    {
        for (int challengeIndex = 0; challengeIndex < NUMBER_CHALLENGES; challengeIndex++)
        {
            final var newChallenge = Challenge.builder().parent(parentIdentifier)
                    .name("Challenge" + challengeIndex)
                    .instruction(String.format("Challenge %d instruction!", challengeIndex))
                    .build();
            for (int taskIndex = 0; taskIndex < NUMBER_TASKS; taskIndex++)
            {
                uploader.addTask(newChallenge, this.getDefaultTask(-1, "Task" + taskIndex));
                LOG.debug("Added task name=Task{} parentId={} challengeName={}", taskIndex,
                        parentIdentifier, newChallenge.getName());
            }
        }
    }

    private Task getDefaultTask(final long parent, final String name)
    {
        return Task.builder(parent, name)
                .addGeojson(String.format(TestConstants.FEATURE_STRING, 1.0, 2.0, "TestGeometry"))
                .build();
    }
}
