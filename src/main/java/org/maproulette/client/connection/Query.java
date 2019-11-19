package org.maproulette.client.connection;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.http.HttpResource;
import org.maproulette.client.http.PostResource;
import org.maproulette.client.http.ResourceFactory;

import lombok.Builder;
import lombok.Getter;

/**
 * A class that handles any query for the MapRoulette connection, and has a handy execution function
 * that will execute it against the MapRoulette server.
 *
 * @author mcuthbert
 */
@Builder
@Getter
public class Query
{
    /**
     * Custom query builder, adding post/get/put/delete including URI
     */
    public static class QueryBuilder
    {
        public QueryBuilder post(final String uri)
        {
            this.uri(uri);
            this.methodName(HttpPost.METHOD_NAME);
            return this;
        }

        public QueryBuilder get(final String uri)
        {
            this.uri(uri);
            this.methodName(HttpGet.METHOD_NAME);
            return this;
        }

        public QueryBuilder put(final String uri)
        {
            this.uri(uri);
            this.methodName(HttpPut.METHOD_NAME);
            return this;
        }

        public QueryBuilder delete(final String uri)
        {
            this.uri(uri);
            this.methodName(HttpDelete.METHOD_NAME);
            return this;
        }
    }

    @Builder.Default
    private String methodName = HttpGet.METHOD_NAME;
    @Builder.Default
    private String data = "";
    @Builder.Default
    private ContentType dataContentType = ContentType.APPLICATION_JSON;
    private final String uri;
    private final Map<String, String> queryParameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    public void addParameter(final String key, final String value)
    {
        this.queryParameters.put(key, value);
    }

    public void addHeader(final String header, final String value)
    {
        this.headers.put(header, value);
    }

    public Optional<String> execute(final ResourceFactory factory, final URIBuilder uriBuilder,
            final Function<HttpResource, String> function) throws MapRouletteException
    {
        final var path = uriBuilder.setPath(this.uri);
        if (!this.queryParameters.isEmpty())
        {
            this.queryParameters.forEach(path::setParameter);
        }
        try (var resource = factory.resource(this.methodName, path.build()))
        {
            // add the post body data if methodName is Post or Put
            if (StringUtils.isNotEmpty(this.data) && resource instanceof PostResource)
            {
                ((PostResource) resource).setStringBody(this.data, this.dataContentType);
            }
            if (!this.headers.isEmpty())
            {
                this.headers.forEach(resource::setHeader);
            }
            final var result = function.apply(resource);
            if (result.isEmpty())
            {
                return Optional.empty();
            }
            else
            {
                return Optional.of(result);
            }
        }
        catch (final URISyntaxException e)
        {
            throw new MapRouletteException(e);
        }
    }
}
