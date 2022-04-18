package org.maproulette.client.connection;

import static org.maproulette.client.utilities.ThrowingFunction.throwingFunctionWrapper;

import java.util.Optional;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.exception.MapRouletteRuntimeException;
import org.maproulette.client.http.ResourceFactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The connection class that actually makes the Rest request to the MapRoulette server.
 *
 * @author cuthbertm
 */
@Slf4j
public class MapRouletteConnection implements IMapRouletteConnection
{
    private static final int DEFAULT_CONNECTION_RETRIES = 3;
    private static final int DEFAULT_CONNECTION_WAIT = 5000;
    private static final String KEY_API_KEY = "apiKey";
    @Getter
    private final MapRouletteConfiguration configuration;
    private final URIBuilder uriBuilder;
    private final ResourceFactory resourceFactory;

    public MapRouletteConnection(final MapRouletteConfiguration configuration,
            final ResourceFactory factory)
    {
        if (configuration == null)
        {
            throw new IllegalArgumentException(
                    "configuration can't be null to create a connection.");
        }
        this.configuration = configuration;
        this.uriBuilder = new URIBuilder().setScheme(this.configuration.getScheme())
                .setHost(this.configuration.getServer()).setPort(this.configuration.getPort());
        this.resourceFactory = factory;
        if (!this.isAbleToConnectToMapRoulette())
        {
            throw new IllegalArgumentException(
                    "configuration must be able to connect to MapRouletteServers to create a connection.");
        }
    }

    public MapRouletteConnection(final MapRouletteConfiguration configuration)
    {
        this(configuration, new ResourceFactory());
    }

    @Override
    public Optional<String> execute(final Query query) throws MapRouletteException
    {
        log.debug("Request: {} {}", query.getMethodName(), query.getUri());
        if (log.isTraceEnabled())
        {
            log.trace("data={}", query.getData());
        }

        // add authentication to the query
        query.addHeader(KEY_API_KEY, this.configuration.getApiKey());
        return query.execute(this.resourceFactory, this.uriBuilder,
                throwingFunctionWrapper(resource ->
                {
                    final var statusCode = resource.getStatusCode();
                    log.trace("Response code: {} ", statusCode);

                    switch (statusCode)
                    {
                        case HttpStatus.SC_OK:
                        case HttpStatus.SC_CREATED:
                            final String ret = resource.getResponseBodyAsString();
                            log.trace("Response body: {}", ret);
                            return ret;
                        case HttpStatus.SC_NO_CONTENT:
                        case HttpStatus.SC_NOT_FOUND:
                            return "";
                        default:
                            throw new MapRouletteException(
                                    String.format("Invalid response status code %d - %s",
                                            statusCode, resource.getResponseBodyAsString()));
                    }
                }));
    }

    @Override
    public boolean isAbleToConnectToMapRoulette()
    {
        var retries = 0;
        while (true)
        {
            try
            {
                final var uri = this.uriBuilder.setPath("/ping").build();
                final var homepage = this.resourceFactory.resource(HttpGet.METHOD_NAME, uri);
                final var statusCode = homepage.getStatusCode();
                if (statusCode != HttpStatus.SC_OK)
                {
                    throw new MapRouletteException(String.format(
                            "Failed to connect to Map Roulette server [%s]. StatusCode: %d",
                            uri.toString(), statusCode));
                }
                else
                {
                    return true;
                }
            }
            catch (final Exception e)
            {
                log.error(
                        String.format("Failed to connect to MapRoulette [%s]", this.configuration),
                        e);
                retries++;
                if (retries > DEFAULT_CONNECTION_RETRIES)
                {
                    return false;
                }
                else
                {
                    try
                    {
                        Thread.sleep(DEFAULT_CONNECTION_WAIT);
                    }
                    catch (final InterruptedException exception)
                    {
                        throw new MapRouletteRuntimeException(exception);
                    }
                }
            }
        }
    }
}
