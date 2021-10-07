package org.maproulette.client.api;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.maproulette.client.connection.IMapRouletteConnection;
import org.maproulette.client.connection.MapRouletteConfiguration;
import org.maproulette.client.connection.MapRouletteConnection;
import org.maproulette.client.connection.Query;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * That User service handles the API requests for Users
 *
 * @author pdevkota1
 */
@RequiredArgsConstructor
public class UserAPI
{

    private final ObjectMapper mapper = new ObjectMapper();
    private final IMapRouletteConnection connection;

    public UserAPI(final MapRouletteConfiguration configuration)
    {
        this(new MapRouletteConnection(configuration));
    }

    public Optional<User> getPublicFromId(final long identifier) throws MapRouletteException
    {
        final var query = Query.builder()
                .get(String.format(QueryConstants.URI_USER_PUBLIC_BY_ID, identifier)).build();
        return this.parseResponse(this.connection.execute(query).orElse(""));
    }

    private Optional<User> parseResponse(final String response) throws MapRouletteException
    {
        if (StringUtils.isEmpty(response))
        {
            return Optional.empty();
        }
        try
        {
            return Optional.of(this.mapper.readValue(response, User.class));
        }
        catch (final IOException e)
        {
            throw new MapRouletteException(e);
        }
    }
}
