package org.maproulette.client.connection;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.maproulette.client.exception.MapRouletteException;

import lombok.Getter;
import lombok.Setter;

/**
 * The MapRouletteConfiguration
 *
 * @author cuthbertm
 * @author mgostintsev
 * @author nachtm
 * @author bbreithaupt
 */
@Getter
public class MapRouletteConfiguration implements Serializable
{
    private static final int DEFAULT_BATCH_SIZE = 5000;
    private static final int API_KEY_INDEX = 3;
    private static final int NUMBER_OF_COMPONENTS = 4;
    private static final int PORT_INDEX = 1;
    private static final int PROJECT_NAME_INDEX = 2;
    private static final int SERVER_INDEX = 0;
    private static final long serialVersionUID = -1060265212173405828L;
    private static final String DELIMITER = "(?<!https|http):";
    private static final String SCHEME_DELIMITER = "://";
    private final String apiKey;
    private final int port;
    private final String scheme;
    private final String server;
    private final String defaultProjectName;
    @Setter
    private int batchSize;

    /**
     * Parses a map roulette configuration object from a string that follows one of these structures
     * [SERVER]:[PORT]:[PROJECT_NAME]:[API_KEY] [SCHEME]://[SERVER]:[PORT]:[PROJECT_NAME]:[API_KEY]
     *
     * @param configuration
     *            The configuration string to parse
     * @return A valid Map Roulette Configuration object, null returned if the configuration string
     *         is not valid
     * @throws MapRouletteException
     *             if invalid configuration string passed
     */
    public static MapRouletteConfiguration parse(final String configuration)
            throws MapRouletteException
    {
        if (StringUtils.isNotEmpty(configuration))
        {
            final String[] components = configuration.split(DELIMITER);
            if (components.length == NUMBER_OF_COMPONENTS)
            {
                final String scheme;
                final String server;
                final String[] splitServer = components[SERVER_INDEX].split(SCHEME_DELIMITER);
                if (splitServer.length < 2)
                {
                    scheme = HttpHost.DEFAULT_SCHEME_NAME;
                    server = components[SERVER_INDEX];
                }
                else
                {
                    scheme = splitServer[0];
                    server = splitServer[1];
                }
                return new MapRouletteConfiguration(scheme, server,
                        Integer.parseInt(components[PORT_INDEX]), components[PROJECT_NAME_INDEX],
                        components[API_KEY_INDEX]);
            }
        }
        throw new MapRouletteException(
                String.format("Map Roulette configuration not set, invalid string passed in. [%s]",
                        configuration));
    }

    public MapRouletteConfiguration(final String server, final int port, final String projectName,
            final String apiKey)
    {
        this(HttpHost.DEFAULT_SCHEME_NAME + "s", server, port, projectName, apiKey);
    }

    public MapRouletteConfiguration(final String server, final int port, final String apiKey)
    {
        this(HttpHost.DEFAULT_SCHEME_NAME + "s", server, port, apiKey);
    }

    public MapRouletteConfiguration(final String scheme, final String server, final int port,
            final String projectName, final String apiKey)
    {
        this.scheme = scheme;
        this.server = server;
        this.port = port;
        this.defaultProjectName = projectName;
        this.apiKey = apiKey;
        this.batchSize = DEFAULT_BATCH_SIZE;
    }

    public MapRouletteConfiguration(final String scheme, final String server, final int port,
            final String apiKey)
    {
        this.scheme = scheme;
        this.server = server;
        this.port = port;
        this.defaultProjectName = "";
        this.apiKey = apiKey;
        this.batchSize = DEFAULT_BATCH_SIZE;
    }

    @Override
    public String toString()
    {
        return String.format("%s://%s:%d:%s:%s", this.scheme, this.server, this.port,
                this.defaultProjectName, this.apiKey);
    }
}
