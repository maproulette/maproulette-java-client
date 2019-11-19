package org.maproulette.client.http;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.maproulette.client.exception.MapRouletteException;

import lombok.Getter;
import lombok.Setter;

/**
 * Base Http resource object that will handle most of the http request information. Sub classes
 * generally will set the type of request and possibly a couple of request-specific parameters. For
 * instance POST will require to post body data in the request. Example Usage: URI uri = new
 * URIBuilder("http://localhost:2020/path/to/location").build(); HttpResource post = new GetResource
 * // get t(uri, body); //read the response post.lines().foreach(System.out.println(x)); //get
 * status code int code = post.getStatusCode();
 *
 * @author cuthbertm
 */
public abstract class HttpResource implements Closeable
{
    @Setter
    @Getter
    private HttpRequestBase request;
    @Getter
    private final URI uri;
    private CloseableHttpResponse response = null;
    private UsernamePasswordCredentials creds = null;
    @Setter
    private HttpHost proxy = null;

    private static HttpClientContext createBasicAuthCache(final HttpHost target,
            final HttpClientContext context)
    {
        // Create AuthCache instance
        final var authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local
        // auth cache
        final var basicAuth = new BasicScheme();
        authCache.put(target, basicAuth);
        // Add AuthCache to the execution context
        context.setAuthCache(authCache);
        return context;
    }

    public HttpResource(final String uri)
    {
        this(URI.create(uri));
    }

    public HttpResource(final URI uri)
    {
        this.uri = uri;
    }

    public void close()
    {
        HttpClientUtils.closeQuietly(this.response);
    }

    /**
     * If you want to execute the request, call this. All other attempts in an HttpResource will
     * first check to see if the response object has been retrieved. This will null out the response
     * object and execute it again.
     *
     * @throws MapRouletteException
     *             if any exceptions occur while trying to get a response
     */
    public void execute() throws MapRouletteException
    {
        this.response = null;
        this.response();
    }

    public Header[] getHeader(final String headerKey) throws MapRouletteException
    {
        // make sure that a connection attempt has been made
        this.response();
        return this.response.getHeaders(headerKey);
    }

    public String getRequestBodyAsString() throws MapRouletteException
    {
        try
        {
            return new String(this.response().readAllBytes());
        }
        catch (final IOException e)
        {
            throw new MapRouletteException(e);
        }
    }

    public CloseableHttpResponse getResponse() throws MapRouletteException
    {
        // make sure that a connection attempt has been made
        this.response();
        return this.response;
    }

    // ---- HTTP Helper Functions ---------//
    public int getStatusCode() throws MapRouletteException
    {
        // make sure that a connection attempt has been made
        this.response();
        return this.response.getStatusLine().getStatusCode();
    }

    public void setAuth(final String user, final String pass)
    {
        this.creds = new UsernamePasswordCredentials(user, pass);
    }

    public void setHeader(final String name, final String value)
    {
        this.request.setHeader(name, value);
    }

    private InputStream response() throws MapRouletteException
    {
        try
        {
            if (this.response == null)
            {
                final var target = new HttpHost(this.uri.getHost(), this.uri.getPort(),
                        this.uri.getScheme());
                final var context = HttpClientContext.create();
                final var clientBuilder = HttpClients.custom();
                if (this.creds != null)
                {
                    final var credsProvider = new BasicCredentialsProvider();
                    credsProvider.setCredentials(
                            new AuthScope(target.getHostName(), target.getPort()), this.creds);
                    clientBuilder.setDefaultCredentialsProvider(credsProvider);
                }
                if (this.proxy != null)
                {
                    clientBuilder.setProxy(this.proxy);
                }
                final var client = clientBuilder.build();
                createBasicAuthCache(target, context);
                this.response = client.execute(target, this.request, context);
            }
            if (this.response.getEntity() == null)
            {
                return new ByteArrayInputStream("".getBytes());
            }
            return this.response.getEntity().getContent();
        }
        catch (final IOException ioe)
        {
            throw new MapRouletteException(ioe.getMessage(), ioe);
        }
    }
}
