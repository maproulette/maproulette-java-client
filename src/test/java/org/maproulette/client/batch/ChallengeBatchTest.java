package org.maproulette.client.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.api.QueryConstants;
import org.maproulette.client.connection.MapRouletteConnection;
import org.maproulette.client.connection.Query;
import org.maproulette.client.model.Task;
import org.maproulette.client.utilities.ObjectMapperSingleton;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author mcuthbert
 */
public class ChallengeBatchTest
{
    @Test
    public void addTasksTest() throws Exception
    {
        final var mockConnection = mock(MapRouletteConnection.class);
        final var challengeBatch = new ChallengeBatch(mockConnection, 12, 10);
        challengeBatch.addTask(this.task("Task1", -1));
        challengeBatch.addTask(this.task("Task2", -1));
        challengeBatch.addTask(this.task("Task3", -1));
        final var multipleTasks = Arrays.asList(this.task("Task4", -1), this.task("task5", -1));
        challengeBatch.addTasks(multipleTasks);
        challengeBatch.flush();

        final var mapper = ObjectMapperSingleton.getMapper();
        final var postData = mapper.createArrayNode();
        final var tasks = Arrays.asList(this.task("Task1", 12), this.task("Task2", 12),
                this.task("Task3", 12), this.task("Task4", 12), this.task("task5", 12));
        tasks.forEach(task -> postData.add(mapper.convertValue(task, JsonNode.class)));
        final var postDataString = mapper.writeValueAsString(postData);
        final var captor = ArgumentCaptor.forClass(Query.class);
        verify(mockConnection).execute(captor.capture());
        final var queryArgument = captor.getValue();
        Assertions.assertEquals(HttpPost.METHOD_NAME, queryArgument.getMethodName());
        Assertions.assertEquals(QueryConstants.URI_TASK_POST + "s", queryArgument.getUri());
        Assertions.assertEquals(ContentType.APPLICATION_JSON, queryArgument.getDataContentType());
        Assertions.assertEquals(postDataString, queryArgument.getData());
    }

    @Test
    public void maxBatchTest() throws Exception
    {
        final var mockConnection = mock(MapRouletteConnection.class);
        final var challengeBatch = new ChallengeBatch(mockConnection, 12, 2);
        challengeBatch.addTask(this.task("Task1", -1));
        verify(mockConnection, never()).execute(any());
        challengeBatch.addTask(this.task("Task2", -1));
        verify(mockConnection, atLeastOnce()).execute(any());
    }

    private Task task(final String name, final long parent)
    {
        return Task.taskBuilder().name(name).parent(parent).addGeojson("{}").build();
    }
}
