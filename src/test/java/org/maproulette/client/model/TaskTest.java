package org.maproulette.client.model;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.TestConstants;
import org.maproulette.client.exception.MapRouletteRuntimeException;

/**
 * @author mcuthbert
 */
public class TaskTest
{
    @Test
    public void taskBuilderDefaultTest()
    {
        final var task = Task.builder(1235, "TaskName").addGeojson("{}").build();
        Assertions.assertEquals(1235, task.getParent());
        Assertions.assertEquals("TaskName", task.getName());
        Assertions.assertEquals(-1, task.getId());
        Assertions.assertNull(task.getInstruction());
        Assertions.assertNull(task.getLocation());
        Assertions.assertNull(task.getStatus());
        Assertions.assertNull(task.getPriority());
    }

    @Test
    public void taskBuilderNoGeometryTest()
    {
        Assertions.assertThrows(MapRouletteRuntimeException.class,
                () -> Task.builder(12345, "TaskName").build());
    }

    @Test
    public void toBuilderTest()
    {
        final List<String> test_tags = Arrays.asList("fixtype=testing", "usecase=1");
        final var task = Task.builder(1234, "Task1")
                .addGeojson(String.format(TestConstants.FEATURE_STRING, 1.2, 4.5, "TestG"))
                .tags(test_tags).build();
        final var updatedGeo = String.format(TestConstants.FEATURE_STRING, 5.6, 7.8, "TestG2");
        final var updateTask = task.toBuilder(true).name("UTask1").addGeojson(updatedGeo).build();
        Assertions.assertEquals("UTask1", updateTask.getName());
        Assertions.assertEquals("{\"features\":[" + updatedGeo + "]}",
                updateTask.getGeometries().toString());
        Assertions.assertEquals(test_tags, updateTask.getTags());
    }

    @Test
    public void taskEqualsTest()
    {
        var task1 = Task.builder(12, "Task1").id(45).addGeojson("{}").build();
        var task2 = Task.builder(12, "Task1").id(45).addGeojson("{}").build();

        Assertions.assertEquals(task1, task2);
        // if I change the geometry they should still actually be equal
        task2 = task2.toBuilder(true).addGeojson("{\"test\":\"test\"}").build();
        Assertions.assertEquals(task1, task2);
        task2 = task2.toBuilder().id(56).build();
        Assertions.assertNotEquals(task1, task2);
        task2 = task2.toBuilder().id(45).parent(13).build();
        Assertions.assertEquals(task1, task2);
        task1 = task1.toBuilder().id(-1).build();
        task2 = task2.toBuilder().id(-1).parent(12).name("Task1").build();
        Assertions.assertEquals(task1, task2);
        task2 = task2.toBuilder().name("Task2").build();
        Assertions.assertNotEquals(task1, task2);
    }

    @Test
    public void taskHashcodeTest()
    {
        var task1 = Task.builder(12, "Task1").id(45).addGeojson("{}").build();
        var task2 = Task.builder(12, "Task1").id(45).addGeojson("{}").build();

        Assertions.assertEquals(task1, task2);
        // if I change the geometry they should still actually be equal
        task2 = task2.toBuilder(true).addGeojson("{\"test\":\"test\"}").build();
        Assertions.assertEquals(task1.hashCode(), task2.hashCode());
        task2 = task2.toBuilder().id(56).build();
        Assertions.assertNotEquals(task1.hashCode(), task2.hashCode());
        task2 = task2.toBuilder().id(45).parent(13).build();
        Assertions.assertEquals(task1.hashCode(), task2.hashCode());
        task1 = task1.toBuilder().id(-1).build();
        task2 = task2.toBuilder().id(-1).parent(12).name("Task1").build();
        Assertions.assertEquals(task1.hashCode(), task2.hashCode());
        task2 = task2.toBuilder().name("Task2").build();
        Assertions.assertNotEquals(task1.hashCode(), task2.hashCode());
    }
}
