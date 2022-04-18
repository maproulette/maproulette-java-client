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
import org.maproulette.client.model.Challenge;
import org.maproulette.client.model.Project;
import org.maproulette.client.utilities.ObjectMapperSingleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * The project service handles the API requests for projects
 *
 * @author mcuthbert
 */
@RequiredArgsConstructor
public class ProjectAPI implements IAPI<Project>
{
    private final ObjectMapper mapper = ObjectMapperSingleton.getMapper();
    private final IMapRouletteConnection connection;

    public ProjectAPI(final MapRouletteConfiguration configuration)
    {
        this(new MapRouletteConnection(configuration));
    }

    /**
     * Projects have no parents, so we create a function that will ignore the value
     *
     * @param name
     *            The name of the project to retrieve
     * @return An optional {@link Project} if found by name
     * @throws MapRouletteException
     *             If any exceptions occur while retrieving the project
     */
    public Optional<Project> get(final String name) throws MapRouletteException
    {
        return this.get(-1, name);
    }

    @Override
    public Optional<Project> get(final long parentIdentifier, final String name)
            throws MapRouletteException
    {
        // parentIdentifier
        final var query = Query.builder()
                .get(String.format(QueryConstants.URI_PROJECT_GET_BY_NAME, name)).build();
        return this.parseResponse(this.connection.execute(query).orElse(""));
    }

    @Override
    public Optional<Project> get(final long identifier) throws MapRouletteException
    {
        final var query = Query.builder()
                .get(String.format(QueryConstants.URI_PROJECT_BASE, identifier)).build();
        return this.parseResponse(this.connection.execute(query).orElse(""));
    }

    @Override
    public List<Project> find(final String matcher, final long parent, final int limit,
            final int page) throws MapRouletteException
    {
        final var query = Query.builder().get(QueryConstants.URI_PROJECT_FIND).build();
        query.addParameter(QueryConstants.QUERY_PARAMETER_Q, matcher + "%");
        query.addParameter(QueryConstants.QUERY_PARAMETER_PARENT_IDENTIFIER, "" + parent);
        query.addParameter(QueryConstants.QUERY_PARAMETER_LIMIT, "" + limit);
        query.addParameter(QueryConstants.QUERY_PARAMETER_PAGE, "" + page);
        query.addParameter(QueryConstants.QUERY_PARAMETER_ONLY_ENABLED, "false");
        final var response = this.connection.execute(query).orElse("[]");
        try
        {
            return Arrays.asList(this.mapper.readValue(response, Project[].class));
        }
        catch (final IOException e)
        {
            throw new MapRouletteException(e);
        }
    }

    /**
     * Retrieve the children of the Project
     *
     * @param identifier
     *            The identifier of the project
     * @param limit
     *            To limit the amount of returned tasks
     * @param page
     *            For paging
     * @return A list of {@link Challenge}s that belong to the provided challenge
     * @throws MapRouletteException
     *             If any exceptions occur during API Restful request
     */
    public List<Challenge> children(final long identifier, final int limit, final int page)
            throws MapRouletteException
    {
        final var query = Query.builder()
                .get(String.format(QueryConstants.URI_PROJECT_CHILDREN, identifier)).build();
        query.addParameter(QueryConstants.QUERY_PARAMETER_LIMIT, "" + limit);
        query.addParameter(QueryConstants.QUERY_PARAMETER_PAGE, "" + page);
        final var response = this.connection.execute(query).orElse("[]");
        try
        {
            return Arrays.asList(this.mapper.readValue(response, Challenge[].class));
        }
        catch (final IOException e)
        {
            throw new MapRouletteException(e);
        }
    }

    @Override
    public Project create(final Project project) throws MapRouletteException
    {
        try
        {
            final var query = Query.builder().post(QueryConstants.URI_PROJECT_POST)
                    .data(this.mapper.writeValueAsString(project)).build();
            return this.parseResponse(this.connection.execute(query).orElse("")).orElseThrow(
                    () -> new MapRouletteException("Invalid response provided by update query."));
        }
        catch (final JsonProcessingException e)
        {
            throw new MapRouletteException(e);
        }
    }

    @Override
    public Project update(final Project project) throws MapRouletteException
    {
        if (project.getId() < 0)
        {
            throw new MapRouletteException(
                    String.format("Invalid Id [%d] provided!", project.getId()));
        }
        try
        {
            final var query = Query.builder()
                    .put(String.format(QueryConstants.URI_PROJECT_BASE, project.getId()))
                    .data(this.mapper.writeValueAsString(project)).build();
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
                .delete(String.format(QueryConstants.URI_PROJECT_BASE, identifier)).build();
        this.connection.execute(query);
        return true;
    }

    @Override
    public boolean forceDelete(final long identifier) throws MapRouletteException
    {
        final var query = Query.builder()
                .delete(String.format(QueryConstants.URI_PROJECT_BASE, identifier)).build();
        query.addParameter(QueryConstants.FLAG_IMMEDIATE_DELETE, "true");
        this.connection.execute(query);
        return true;
    }

    private Optional<Project> parseResponse(final String response) throws MapRouletteException
    {
        if (StringUtils.isEmpty(response))
        {
            return Optional.empty();
        }
        try
        {
            return Optional.of(this.mapper.readValue(response, Project.class));
        }
        catch (final IOException e)
        {
            throw new MapRouletteException(e);
        }
    }
}
