package org.maproulette.client.utilities;

import org.maproulette.client.exception.MapRouletteException;

import java.io.IOException;

/**
 * A generic helper class
 *
 * @author mcuthbert
 */
public final class Utilities
{
    public static <T> T fromJson(final String json, final Class<T> clazz)
            throws MapRouletteException
    {
        try
        {
            return ObjectMapperSingleton.getMapper().readValue(json, clazz);
        }
        catch (final IOException e)
        {
            throw new MapRouletteException(e);
        }
    }

    private Utilities()
    {
        // Don't need to the constructor as this a utilities class
    }
}
