package org.maproulette.client.connection;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author mcuthbert
 */
public class QueryTest
{
    @Test
    public void fullBuilderTest()
    {
        final var query = Query.builder().get("TEST_URI").data("TEST_DATA")
                .dataContentType(ContentType.MULTIPART_FORM_DATA).methodName("TEST_METHOD").build();
        Assertions.assertEquals("TEST_URI", query.getUri());
        Assertions.assertEquals("TEST_DATA", query.getData());
        Assertions.assertEquals(ContentType.MULTIPART_FORM_DATA, query.getDataContentType());
        Assertions.assertEquals("TEST_METHOD", query.getMethodName());

        query.addParameter("testKey", "testValue");
        final var parameters = query.getQueryParameters();
        Assertions.assertEquals(1, parameters.size());
        Assertions.assertEquals("testValue", parameters.get("testKey"));

        query.addHeader("testHeader", "testValue");
        final var headers = query.getHeaders();
        Assertions.assertEquals(1, headers.size());
        Assertions.assertEquals("testValue", headers.get("testHeader"));
    }

    @Test
    public void getTest()
    {
        final var query = Query.builder().get("").build();
        Assertions.assertEquals(HttpGet.METHOD_NAME, query.getMethodName());
    }

    @Test
    public void putTest()
    {
        final var query = Query.builder().put("").build();
        Assertions.assertEquals(HttpPut.METHOD_NAME, query.getMethodName());
    }

    @Test
    public void postTest()
    {
        final var query = Query.builder().post("").build();
        Assertions.assertEquals(HttpPost.METHOD_NAME, query.getMethodName());
    }

    @Test
    public void deleteTest()
    {
        final var query = Query.builder().delete("").build();
        Assertions.assertEquals(HttpDelete.METHOD_NAME, query.getMethodName());
    }
}
