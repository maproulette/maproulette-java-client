package org.maproulette.client.http;

import java.net.URI;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.maproulette.client.exception.MapRouletteRuntimeException;

/**
 * Factory for retrieving resource based on method
 *
 * @author mcuthbert
 */
public class ResourceFactory
{
    public ResourceFactory()
    {
        // no class variables required for class
    }

    public HttpResource resource(final String methodName, final String uri)
    {
        return this.resource(methodName, URI.create(uri));
    }

    public HttpResource resource(final String methodName, final URI uri)
    {
        switch (methodName)
        {
            case HttpGet.METHOD_NAME:
                return new GetResource(uri);
            case HttpDelete.METHOD_NAME:
                return new DeleteResource(uri);
            case HttpPost.METHOD_NAME:
                return new PostResource(uri);
            case HttpPut.METHOD_NAME:
                return new PutResource(uri);
            default:
                throw new MapRouletteRuntimeException(
                        String.format("Invalid method name %s provided", methodName));
        }
    }
}
