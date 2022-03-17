package org.maproulette.client.connection;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.maproulette.client.api.QueryConstants;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.exception.MapRouletteRuntimeException;
import org.maproulette.client.http.MockResourceFactory;
import org.maproulette.client.http.ResourceFactory;

/**
 * @author mcuthbert
 */
@Disabled
public class MapRouletteConnectionTest
{
    @Test
    public void nullConfigurationTest()
    {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MapRouletteConnection(null));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            final var factory = new MockResourceFactory();
            final var getResource = factory.resource(HttpGet.METHOD_NAME);
            when(getResource.getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);
            new MapRouletteConnection(new MapRouletteConfiguration("", 90, "", ""), factory);
        });
    }

    @Test
    public void isAbleToConnectTest() throws MapRouletteException
    {
        final var factory = new MockResourceFactory();
        final var connection = this.getConnection(factory);
        final var getResource = factory.resource(HttpGet.METHOD_NAME);
        when(getResource.getStatusCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .thenReturn(HttpStatus.SC_OK).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        Assertions.assertTrue(connection.isAbleToConnectToMapRoulette());
        Assertions.assertFalse(connection.isAbleToConnectToMapRoulette());
    }

    @Test
    public void deleteTest() throws MapRouletteException
    {
        final var factory = new MockResourceFactory();
        final var connection = this.getConnection(factory);
        final var deleteResource = factory.resource(HttpDelete.METHOD_NAME);
        when(deleteResource.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND)
                .thenReturn(HttpStatus.SC_NO_CONTENT).thenReturn(HttpStatus.SC_OK)
                .thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(deleteResource.getRequestBodyAsString()).thenReturn("test");
        final var query = Query.builder().delete("").build();
        Assertions.assertTrue(connection.execute(query).isEmpty());
        Assertions.assertTrue(connection.execute(query).isEmpty());
        query.addParameter(QueryConstants.FLAG_IMMEDIATE_DELETE, "true");
        Assertions.assertEquals("test", connection.execute(query).get());
        Assertions.assertThrows(MapRouletteRuntimeException.class, () -> connection.execute(query));
    }

    @Test
    public void retrieveTest() throws MapRouletteException
    {
        final var factory = new MockResourceFactory();
        final var connection = this.getConnection(factory);
        final var getResource = factory.resource(HttpGet.METHOD_NAME);
        when(getResource.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND)
                .thenReturn(HttpStatus.SC_NO_CONTENT).thenReturn(HttpStatus.SC_OK);
        final var responseString = "{\"test\":\"test\"}";
        when(getResource.getRequestBodyAsString()).thenReturn(responseString);

        final var query = Query.builder().get("").build();
        // First request will respond with NOT_FOUND
        Assertions.assertTrue(connection.execute(query).isEmpty());
        // Second request will respond with NO_CONTENT
        Assertions.assertTrue(connection.execute(query).isEmpty());

        final var response = connection.execute(query);
        Assertions.assertTrue(response.isPresent());
        Assertions.assertEquals(responseString, response.get());
    }

    @Test
    public void createNewTest() throws MapRouletteException
    {
        final var factory = new MockResourceFactory();
        final var connection = this.getConnection(factory);
        final var getResource = factory.resource(HttpGet.METHOD_NAME);
        when(getResource.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);
        final var postResource = factory.resource(HttpPost.METHOD_NAME);
        when(postResource.getStatusCode()).thenReturn(HttpStatus.SC_CREATED)
                .thenReturn(HttpStatus.SC_OK).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(postResource.getRequestBodyAsString()).thenReturn("{\"id\":1234}")
                .thenReturn("{\"id\":6543}");
        final var query = Query.builder().post("").build();
        Assertions.assertEquals(Optional.of("{\"id\":1234}"), connection.execute(query));
        Assertions.assertEquals(Optional.of("{\"id\":6543}"), connection.execute(query));
    }

    private IMapRouletteConnection getConnection(final ResourceFactory factory)
            throws MapRouletteException
    {
        final var getResource = factory.resource(HttpGet.METHOD_NAME, "");
        when(getResource.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        return new MapRouletteConnection(
                new MapRouletteConfiguration("localhost", 80, "test", "test"), factory);
    }
}
