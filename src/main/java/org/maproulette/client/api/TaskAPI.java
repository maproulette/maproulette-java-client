package org.maproulette.client.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.maproulette.client.connection.IMapRouletteConnection;
import org.maproulette.client.connection.MapRouletteConfiguration;
import org.maproulette.client.connection.MapRouletteConnection;
import org.maproulette.client.connection.Query;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.Task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * That Task service handles the API requests for Tasks
 *
 * @author mcuthbert
 */
@RequiredArgsConstructor
public class TaskAPI implements IAPI<Task>
{
    private final ObjectMapper mapper = new ObjectMapper();
    private final IMapRouletteConnection connection;

    public TaskAPI(final MapRouletteConfiguration configuration)
    {
        this(new MapRouletteConnection(configuration));
    }

    @Override
    public Optional<Task> get(final long parentIdentifier, final String name)
            throws MapRouletteException
    {
        final var query = Query.builder()
                .get(String.format(QueryConstants.URI_TASK_GET_BY_NAME, parentIdentifier, name))
                .build();
        return this.parseResponse(this.connection.execute(query).orElse(""));
    }

    @Override
    public Optional<Task> get(final long identifier) throws MapRouletteException
    {
        final var query = Query.builder()
                .get(String.format(QueryConstants.URI_TASK_BASE, identifier)).build();
        return this.parseResponse(this.connection.execute(query).orElse(""));
    }

    @Override
    public List<Task> find(final String matcher, final long parent, final int limit, final int page)
            throws MapRouletteException
    {
        final var query = Query.builder().get(QueryConstants.URI_TASK_FIND).build();
        query.addParameter(QueryConstants.QUERY_PARAMETER_Q, matcher + "%");
        query.addParameter(QueryConstants.QUERY_PARAMETER_PARENT_IDENTIFIER, "" + parent);
        query.addParameter(QueryConstants.QUERY_PARAMETER_LIMIT, "" + limit);
        query.addParameter(QueryConstants.QUERY_PARAMETER_PAGE, "" + page);
        query.addParameter(QueryConstants.QUERY_PARAMETER_ONLY_ENABLED, "false");
        final var response = this.connection.execute(query).orElse("[]");
        try
        {
            return Arrays.asList(this.mapper.readValue(response, Task[].class));
        }
        catch (final IOException e)
        {
            throw new MapRouletteException(e);
        }
    }

    @Override
    public Task create(final Task task) throws MapRouletteException
    {
        try
        {
            final var query = Query.builder().post(QueryConstants.URI_TASK_POST)
                    .data(this.mapper.writeValueAsString(task)).build();

            return this.parseResponse(this.connection.execute(query).orElse("")).orElseThrow(
                    () -> new MapRouletteException("Invalid response provided by update query."));
        }
        catch (final JsonProcessingException e)
        {
            throw new MapRouletteException(e);
        }
    }

    @Override
    public Task update(final Task task) throws MapRouletteException
    {
        try
        {
            final var query = Query.builder()
                    .put(String.format(QueryConstants.URI_TASK_BASE, task.getId()))
                    .data(this.mapper.writeValueAsString(task)).build();
            return this.parseResponse(this.connection.execute(query).orElse("")).orElseThrow(
                    () -> new MapRouletteException("Invalid response provided by update query."));
        }
        catch (final JsonProcessingException e)
        {
            throw new MapRouletteException(e);
        }
    }

    @Override
    public boolean delete(final long identifier) throws MapRouletteException
    {
        final var query = Query.builder()
                .delete(String.format(QueryConstants.URI_TASK_BASE, identifier)).build();
        this.connection.execute(query);
        return true;
    }

    /**
     * For a Task there is no delayed deletion available, so a delete and forceDelete amounts to the
     * same thing.
     *
     * @param identifier
     *            The identifier for the object to delete
     * @return true if succeeds
     * @throws MapRouletteException
     *             If there is any exceptions thrown while executing the Restful API delete request
     */
    @Override
    public boolean forceDelete(final long identifier) throws MapRouletteException
    {
        return this.delete(identifier);
    }

    private Optional<Task> parseResponse(final String response) throws MapRouletteException
    {
        if (StringUtils.isEmpty(response))
        {
            return Optional.empty();
        }
        try
        {
            return Optional.of(this.mapper.readValue(response, Task.class));
        }
        catch (final IOException e)
        {
            throw new MapRouletteException(e);
        }
    }
}
