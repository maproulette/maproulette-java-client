package org.maproulette.client.serializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.maproulette.client.exception.MapRouletteRuntimeException;

/**
 * @author mcuthbert
 */
public final class SerializerUtilities
{
    public static String getResourceAsString(final String classResource)
    {
        try (var inputStream = new InputStreamReader(getResourceAsStream(classResource)))
        {
            final var textBuilder = new StringBuilder();
            try (var reader = new BufferedReader(inputStream))
            {
                int content = 0;
                while ((content = reader.read()) != -1)
                {
                    textBuilder.append((char) content);
                }
            }
            return textBuilder.toString();
        }
        catch (final IOException e)
        {
            throw new MapRouletteRuntimeException(e);
        }

    }

    public static InputStream getResourceAsStream(final String classResource)
    {
        final var classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null)
        {
            throw new MapRouletteRuntimeException("Context class loader could not be initialized.");
        }
        final var input = classLoader.getResourceAsStream(classResource);
        if (input == null)
        {
            throw new MapRouletteRuntimeException(
                    String.format("Resource, %s, not found.", classResource));
        }
        return input;
    }

    private SerializerUtilities()
    {

    }
}
