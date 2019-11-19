package org.maproulette.client.http;

import static org.mockito.Mockito.mock;

import java.net.URI;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.maproulette.client.exception.MapRouletteRuntimeException;

/**
 * A factory that responds with mock resources, so that we can unit test while mocking the http
 * layer
 *
 * @author mcuthbert
 */
public class MockResourceFactory extends ResourceFactory
{
    private final GetResource mockGetResource = mock(GetResource.class);
    private final DeleteResource mockDeleteResource = mock(DeleteResource.class);
    private final PostResource mockPostResource = mock(PostResource.class);
    private final PutResource mockPutResource = mock(PutResource.class);

    public HttpResource resource(final String methodName)
    {
        return this.resource(methodName, "");
    }

    @Override
    public HttpResource resource(final String methodName, final URI uri)
    {
        switch (methodName)
        {
            case HttpGet.METHOD_NAME:
                return this.mockGetResource;
            case HttpDelete.METHOD_NAME:
                return this.mockDeleteResource;
            case HttpPost.METHOD_NAME:
                return this.mockPostResource;
            case HttpPut.METHOD_NAME:
                return this.mockPutResource;
            default:
                throw new MapRouletteRuntimeException(
                        String.format("Invalid method name %s provided", methodName));
        }
    }
}
