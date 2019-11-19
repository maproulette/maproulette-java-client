package org.maproulette.client.http;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.exception.MapRouletteRuntimeException;

/**
 * @author mcuthbert
 */
public class ResourceFactoryTest
{
    @Test
    public void resourceTest()
    {
        final var factory = new ResourceFactory();
        Assertions.assertTrue(factory.resource(HttpPost.METHOD_NAME, "") instanceof PostResource);
        Assertions.assertTrue(factory.resource(HttpGet.METHOD_NAME, "") instanceof GetResource);
        Assertions.assertTrue(factory.resource(HttpPut.METHOD_NAME, "") instanceof PutResource);
        Assertions
                .assertTrue(factory.resource(HttpDelete.METHOD_NAME, "") instanceof DeleteResource);
        Assertions.assertThrows(MapRouletteRuntimeException.class, () -> factory.resource("", ""));
    }
}
