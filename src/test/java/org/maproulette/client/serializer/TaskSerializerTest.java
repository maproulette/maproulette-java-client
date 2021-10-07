package org.maproulette.client.serializer;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.ChallengePriority;
import org.maproulette.client.model.PointInformation;
import org.maproulette.client.model.Task;
import org.maproulette.client.model.TaskStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author mcuthbert
 */
public class TaskSerializerTest
{
    @Test
    public void serializationTest() throws IOException
    {
        final var testFeatureString = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[%s, %s]},\"properties\": {\"name\":\"%s\"}}";
        final var mapper = new ObjectMapper();
        final var pointList = Arrays.asList(new PointInformation(1.0, 2.0),
                new PointInformation(5.4, 8.7));

        final var task = Task.builder(343444454, "TestTask").id(12355655)
                .instruction("TestInstruction").priority(ChallengePriority.HIGH)
                .status(TaskStatus.DELETED).addPoints(pointList)
                .addGeojson(String.format(testFeatureString, 1.0, 2.0, "Feature1"))
                .addGeojson(String.format(testFeatureString, 5.0, 6.0, "Feature2")).build();
        final var taskJson = mapper.writeValueAsString(task);
        final var deserializedTask = mapper.readValue(taskJson, Task.class);

        this.verifyTask(task, deserializedTask);
    }

    @Test
    public void geometriesSerializationTest() throws IOException
    {
        final var testFeatureString = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[%s, %s]},\"properties\": {\"name\":\"%s\"}}";
        final var mapper = new ObjectMapper();
        final var task = Task.builder(343444454, "TestTask").id(12355655)
                .instruction("TestInstruction").priority(ChallengePriority.HIGH)
                .status(TaskStatus.DELETED)
                .addGeojson(String.format(testFeatureString, 1.0, 2.0, "Feature1")).build();
        final var taskJson = mapper.writeValueAsString(task);
        final var deserializedTask = mapper.readValue(taskJson, Task.class);

        this.verifyTask(task, deserializedTask);
    }

    @Test
    public void fromJsonTest() throws Exception
    {
        final var testFeatureString = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[%s, %s]},\"properties\": {\"name\":\"%s\"}}";
        final var mapper = new ObjectMapper();
        final var pointList = Arrays.asList(new PointInformation(1.0, 2.0),
                new PointInformation(5.4, 8.7));

        final var task = Task.builder(343444454, "TestTask").id(12355655)
                .instruction("TestInstruction").priority(ChallengePriority.HIGH)
                .status(TaskStatus.DELETED).addPoints(pointList)
                .addGeojson(String.format(testFeatureString, 1.0, 2.0, "Feature1"))
                .addGeojson(String.format(testFeatureString, 5.0, 6.0, "Feature2")).build();
        final var taskJson = mapper.writeValueAsString(task);

        final var deserializedTask = Task.fromJson(taskJson);
        this.verifyTask(task, deserializedTask);
    }

    private void verifyTask(final Task task1, final Task task2)
    {
        Assertions.assertEquals(task1.getId(), task2.getId());
        Assertions.assertEquals(task1.getName(), task2.getName());
        Assertions.assertEquals(task1.getGeometries(), task2.getGeometries());
        Assertions.assertEquals(task1.getInstruction(), task2.getInstruction());
        Assertions.assertEquals(task1.getLocation(), task2.getLocation());
        Assertions.assertEquals(task1.getParent(), task2.getParent());
        Assertions.assertEquals(task1.getPriority(), task2.getPriority());
        Assertions.assertEquals(task1.getStatus(), task2.getStatus());
    }

    @Test
    public void taskWithStatusFroJson() throws MapRouletteException
    {
        final Task task = Task
                .fromJson(SerializerUtilities.getResourceAsString("task/testTask.json"));
        Assertions.assertEquals(999, task.getCompletedBy());
        Assertions.assertEquals(58871, task.getCompletedTimeSpent());
        Assertions.assertEquals("2021-09-15T18:30:57.652Z", task.getMappedOn());
    }
}
