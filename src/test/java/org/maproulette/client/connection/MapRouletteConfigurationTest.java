package org.maproulette.client.connection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.exception.MapRouletteException;

/**
 * Unit tests for MapRouletteConfiguration
 *
 * @author nachtm
 * @author bbreithaupt
 */
public class MapRouletteConfigurationTest
{
    private static final String SERVER = "server";
    private static final String SCHEME = "http";
    private static final String SCHEME2 = "https";
    private static final int PORT = 123;
    private static final String PROJECT_NAME = "project";
    private static final String API_KEY = "key";
    private static final String CONFIG = "server:123:project:key";
    private static final String CONFIG2 = "https://server:123:project:key";
    private static final String BAD_CONFIG = "server:123:project";

    @Test
    public void testBadParse()
    {
        Assertions.assertThrows(MapRouletteException.class,
                () -> MapRouletteConfiguration.parse(BAD_CONFIG));
    }

    @Test
    public void testFullURLParse() throws MapRouletteException
    {
        final MapRouletteConfiguration configuration = MapRouletteConfiguration.parse(CONFIG2);
        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(SCHEME2, configuration.getScheme());
        Assertions.assertEquals(SERVER, configuration.getServer());
        Assertions.assertEquals(PORT, configuration.getPort());
        Assertions.assertEquals(API_KEY, configuration.getApiKey());
        Assertions.assertEquals(PROJECT_NAME, configuration.getDefaultProjectName());
    }

    @Test
    public void testParse() throws MapRouletteException
    {
        final MapRouletteConfiguration configuration = MapRouletteConfiguration.parse(CONFIG);
        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(SCHEME, configuration.getScheme());
        Assertions.assertEquals(SERVER, configuration.getServer());
        Assertions.assertEquals(PORT, configuration.getPort());
        Assertions.assertEquals(API_KEY, configuration.getApiKey());
        Assertions.assertEquals(PROJECT_NAME, configuration.getDefaultProjectName());
    }
}
