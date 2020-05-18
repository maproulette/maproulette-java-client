package org.maproulette.client.api;

import java.util.ArrayList;
import java.util.List;

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

/**
 * @author mcuthbert
 */
public class TaskAPIIntegrationTest extends IntegrationBase
{
    private Challenge createdChallenge = null;
    private Challenge createdChallengeForNewConfiguration = null;
    public static final String DEFAULT_GEOMETRY = "TestGeometry";
    public static final String TEST_INSTRUCTION = "TestInstruction";
    public static final String TASK_NAME = "TestTask";
    public static final String CHALLENGE_NAME = "TestChallenge";

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
        final var resetGeometry = created.get().toBuilder().resetGeometry();
        Assertions.assertNull(resetGeometry.getGeometries());
    }

    @Test
    public void updateTest() throws MapRouletteException
    {
        final var toCreate = this.getDefaultTask(TASK_NAME);
        final var createdIdentifier = this.getTaskAPI().create(toCreate).getId();
        final var created = this.getTaskAPI().get(createdIdentifier);
        Assertions.assertTrue(created.isPresent());
        this.compare(toCreate, created.get());

        final Task.TaskBuilder taskBuilder = created.get().toBuilder();
        final List<String> geoJsons = new ArrayList<>();
        Assertions.assertNotNull(taskBuilder.getGeometries().get("features").get(0));
        geoJsons.add(taskBuilder.getGeometries().get("features").get(0).toString());
        geoJsons.add(String.format(TestConstants.FEATURE_STRING, 3.1, 4.2, "UpdateGeometry"));
        final var update = taskBuilder.instruction("UpdatedInstruction").status(TaskStatus.FIXED)
                .name("UpdateTaskName").addGeojson(geoJsons).build();
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
                                        "UpdateGeometry")
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
