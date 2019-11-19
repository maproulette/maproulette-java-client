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
import org.maproulette.client.model.Task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * The service handling all the API requests for Challenges
 *
 * @author mcuthbert
 */
@RequiredArgsConstructor
public class ChallengeAPI implements IAPI<Challenge>
{
    private final ObjectMapper mapper = new ObjectMapper();
    private final IMapRouletteConnection connection;

    public ChallengeAPI(final MapRouletteConfiguration configuration)
    {
        this(new MapRouletteConnection(configuration));
    }

    /**
     * The name is a dash delimited string [PROJECT_ID]-[CHALLENGE_NAME]
     *
     * @param parentIdentifier
     *            The identifier of the parent object
     * @param name
     *            The name of the object
     * @return An optional {@link Challenge object}
     * @throws MapRouletteException
     *             If any exceptions occur while trying to retrieve the object
     */
    @Override
    public Optional<Challenge> get(final long parentIdentifier, final String name)
            throws MapRouletteException
    {
        final var query = Query.builder().get(
                String.format(QueryConstants.URI_CHALLENGE_GET_BY_NAME, parentIdentifier, name))
                .build();
        return this.parseResponse(this.connection.execute(query).orElse(""));
    }

    @Override
    public Optional<Challenge> get(final long identifier) throws MapRouletteException
    {
        final var query = Query.builder()
                .get(String.format(QueryConstants.URI_CHALLENGE_BASE, identifier)).build();
        return this.parseResponse(this.connection.execute(query).orElse(""));
    }

    @Override
    public List<Challenge> find(final String matcher, final long parent, final int limit,
            final int page) throws MapRouletteException
    {
        final var query = Query.builder().get(QueryConstants.URI_CHALLENGE_FIND).build();
        query.addParameter(QueryConstants.QUERY_PARAMETER_Q, matcher + "%");
        query.addParameter(QueryConstants.QUERY_PARAMETER_PARENT_IDENTIFIER, "" + parent);
        query.addParameter(QueryConstants.QUERY_PARAMETER_LIMIT, "" + limit);
        query.addParameter(QueryConstants.QUERY_PARAMETER_PAGE, "" + page);
        query.addParameter(QueryConstants.QUERY_PARAMETER_ONLY_ENABLED, "false");
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

    /**
     * Retrieve the children of the Challenge
     *
     * @param identifier
     *            The identifier of the challenge
     * @param limit
     *            To limit the amount of returned tasks
     * @param page
     *            For paging
     * @return A list of {@link Task}s that belong to the provided challenge
     * @throws MapRouletteException
     *             If any exceptions occur during API Restful request
     */
    public List<Task> children(final long identifier, final int limit, final int page)
            throws MapRouletteException
    {
        final var query = Query.builder()
                .get(String.format(QueryConstants.URI_CHALLENGE_CHILDREN, identifier)).build();
        query.addParameter(QueryConstants.QUERY_PARAMETER_LIMIT, "" + limit);
        query.addParameter(QueryConstants.QUERY_PARAMETER_PAGE, "" + page);
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
    public Challenge create(final Challenge challenge) throws MapRouletteException
    {
        try
        {
            final var query = Query.builder().post(QueryConstants.URI_CHALLENGE_POST)
                    .data(this.mapper.writeValueAsString(challenge)).build();
            return this.parseResponse(this.connection.execute(query).orElse("")).orElseThrow(
                    () -> new MapRouletteException("Invalid response provided by update query."));
        }
        catch (final JsonProcessingException e)
        {
            throw new MapRouletteException(e);
        }
    }

    @Override
    public Challenge update(final Challenge challenge) throws MapRouletteException
    {
        if (challenge.getId() < 0)
        {
            throw new MapRouletteException(
                    String.format("Invalid Id [%d] provided!", challenge.getId()));
        }
        try
        {
            final var query = Query.builder()
                    .put(String.format(QueryConstants.URI_CHALLENGE_BASE, challenge.getId()))
                    .data(this.mapper.writeValueAsString(challenge)).build();
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
                .delete(String.format(QueryConstants.URI_CHALLENGE_BASE, identifier)).build();
        this.connection.execute(query);
        return true;
    }

    @Override
    public boolean forceDelete(final long identifier) throws MapRouletteException
    {
        final var query = Query.builder()
                .delete(String.format(QueryConstants.URI_CHALLENGE_BASE, identifier)).build();
        query.addParameter(QueryConstants.FLAG_IMMEDIATE_DELETE, "true");
        this.connection.execute(query);
        return true;
    }

    private Optional<Challenge> parseResponse(final String response) throws MapRouletteException
    {
        if (StringUtils.isEmpty(response))
        {
            return Optional.empty();
        }
        try
        {
            return Optional.of(this.mapper.readValue(response, Challenge.class));
        }
        catch (final IOException e)
        {
            throw new MapRouletteException(e);
        }
    }
}
