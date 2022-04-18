package org.maproulette.client.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.maproulette.client.IntegrationBase;
import org.maproulette.client.TestConstants;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.Challenge;
import org.maproulette.client.model.ChallengePriority;
import org.maproulette.client.model.Task;
import org.maproulette.client.model.TaskStatus;
import org.maproulette.client.utilities.ObjectMapperSingleton;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author mcuthbert
 */
public class TaskAPIIntegrationTest extends IntegrationBase
{
    private Challenge createdChallenge = null;
    private Challenge createdChallengeForNewConfiguration = null;
    public static final String DEFAULT_GEOMETRY = "TestGeometry";
    public static final String UPDATED_GEOMETRY = "UpdateGeometry";
    public static final String TEST_INSTRUCTION = "TestInstruction";
    public static final String UPDATED_INSTRUCTION = "UpdatedInstruction";
    public static final String TASK_NAME = "TestTask";
    public static final String UPDATED_TASK_NAME = "UpdateTaskName";
    public static final String CHALLENGE_NAME = "TestChallenge";
    public static final String TASK_FEATURES = "features";

    @BeforeEach
    public void setup() throws MapRouletteException
    {
        super.setup();
        // create challenge to run the task api tests in
        this.createdChallenge = this.getChallengeAPI()
                .create(Challenge.builder().parent(this.getDefaultProjectIdentifier())
                        .name(CHALLENGE_NAME).instruction(TEST_INSTRUCTION)
                        .defaultPriority(ChallengePriority.HIGH).build());

        // create challenge to run the task api tests for new configuration
        this.createdChallengeForNewConfiguration = this.getChallengeAPIForNewConfiguration()
                .create(Challenge.builder().parent(this.getNewProjectIdentifier())
                        .name(CHALLENGE_NAME).instruction(TEST_INSTRUCTION)
                        .defaultPriority(ChallengePriority.HIGH).build());

    }

    @AfterEach
    public void teardown() throws MapRouletteException
    {
        // This will remove the project and all child objects, so clean up everything
        super.teardown();
    }

    @Test
    public void basicAPINewConfigurationTest() throws MapRouletteException
    {
        final var defaultTask = this.getDefaultTaskForNewConfiguration(TASK_NAME);
        final var createdTaskIdentifier = this.getTaskAPIForNewConfiguration().create(defaultTask)
                .getId();
        Assertions.assertNotEquals(-1, createdTaskIdentifier);
    }

    @Test
    public void basicAPITest() throws MapRouletteException
    {
        final var toCreate = this.getDefaultTask(TASK_NAME);
        final var createdTaskIdentifier = this.getTaskAPI().create(toCreate).getId();
        Assertions.assertNotEquals(-1, createdTaskIdentifier);

        final var task = this.getTaskAPI().get(createdTaskIdentifier);
        Assertions.assertTrue(task.isPresent());
        task.ifPresent(value -> this.compare(toCreate, value));

        // Test deletion
        Assertions.assertTrue(this.getTaskAPI().delete(createdTaskIdentifier));
        Assertions.assertTrue(this.getTaskAPI().get(createdTaskIdentifier).isEmpty());
    }

    @Test
    public void resetGeometryTest() throws MapRouletteException
    {
        final var toCreate = this.getDefaultTaskForNewConfiguration(TASK_NAME);
        final var createdIdentifier = this.getTaskAPIForNewConfiguration().create(toCreate).getId();
        final var created = this.getTaskAPIForNewConfiguration().get(createdIdentifier);
        Assertions.assertTrue(created.isPresent());
        Assertions.assertNotNull(created.get().getGeometries());
        final var update = created.get().toBuilder().instruction(UPDATED_INSTRUCTION)
                .status(TaskStatus.FIXED).name(UPDATED_TASK_NAME).resetGeometry()
                .addGeojson(String.format(TestConstants.FEATURE_STRING, 3.1, 4.2, UPDATED_GEOMETRY))
                .build();
        Assertions.assertNotNull(update);

        final var updatedTask = this.getTaskAPIForNewConfiguration().update(update);
        final var retrievedUpdatedTask = this.getTaskAPIForNewConfiguration()
                .get(updatedTask.getId());
        Assertions.assertTrue(retrievedUpdatedTask.isPresent());

        Assertions.assertEquals("{\"features\":["
                + String.format(TestConstants.FEATURE_STRING, 3.1, 4.2, UPDATED_GEOMETRY) + "]}",
                retrievedUpdatedTask.get().getGeometries().toString());
    }

    @Test
    public void updateTest() throws MapRouletteException
    {
        final var toCreate = this.getDefaultTask(TASK_NAME);
        final var createdIdentifier = this.getTaskAPI().create(toCreate).getId();
        final var created = this.getTaskAPI().get(createdIdentifier);
        Assertions.assertTrue(created.isPresent());
        this.compare(toCreate, created.get());

        final var update = created.get().toBuilder().instruction(UPDATED_INSTRUCTION)
                .status(TaskStatus.FIXED).name(UPDATED_TASK_NAME).resetGeometry()
                .addGeojson(String.format(TestConstants.FEATURE_STRING, 3.1, 4.2, UPDATED_GEOMETRY))
                .build();

        final var mapper = ObjectMapperSingleton.getMapper();
        final ArrayNode arrayNode = mapper.createArrayNode();
        Assertions.assertNotNull(created.get().getGeometries().get(TASK_FEATURES).get(0));
        Assertions.assertNotNull(update.getGeometries().get(TASK_FEATURES).get(0));
        arrayNode.add(created.get().getGeometries().get(TASK_FEATURES).get(0));
        arrayNode.add(update.getGeometries().get(TASK_FEATURES).get(0));
        final var res = mapper.createObjectNode().set(TASK_FEATURES, arrayNode);
        update.setGeometries(res);

        final var updatedTask = this.getTaskAPI().update(update);
        final var retrievedUpdatedTask = this.getTaskAPI().get(updatedTask.getId());
        Assertions.assertTrue(retrievedUpdatedTask.isPresent());
        this.compare(updatedTask, retrievedUpdatedTask.get());
        Assertions
                .assertEquals(
                        "{\"features\":["
                                + String.format(
                                        TestConstants.FEATURE_STRING, 1.1, 2.2, DEFAULT_GEOMETRY)
                                + ","
                                + String.format(TestConstants.FEATURE_STRING, 3.1, 4.2,
                                        UPDATED_GEOMETRY)
                                + "]}",
                        retrievedUpdatedTask.get().getGeometries().toString());
    }

    private void compare(final Task task1, final Task task2)
    {
        Assertions.assertEquals(task1.getName(), task2.getName());
        // Assertions.assertEquals(TaskStatus.CREATED, task2.getStatus());
        Assertions.assertEquals(ChallengePriority.HIGH, task2.getPriority());
        Assertions.assertEquals(task1.getParent(), task2.getParent());
        Assertions.assertEquals(task1.getInstruction(), task2.getInstruction());
        Assertions.assertEquals(task1.getGeometries(), task2.getGeometries());
    }

    private Task getDefaultTask(final String name)
    {
        return Task.builder(this.createdChallenge.getId(), name).instruction(TEST_INSTRUCTION)
                .priority(ChallengePriority.HIGH)
                .addGeojson(String.format(TestConstants.FEATURE_STRING, 1.1, 2.2, DEFAULT_GEOMETRY))
                .build();
    }

    private Task getDefaultTaskForNewConfiguration(final String name)
    {
        return Task.builder(this.createdChallengeForNewConfiguration.getId(), name)
                .instruction(TEST_INSTRUCTION).priority(ChallengePriority.HIGH)
                .addGeojson(String.format(TestConstants.FEATURE_STRING, 1.1, 2.2, DEFAULT_GEOMETRY))
                .build();
    }
}
