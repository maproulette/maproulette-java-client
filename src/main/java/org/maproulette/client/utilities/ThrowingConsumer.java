package org.maproulette.client.utilities;

import java.util.function.Consumer;

import org.maproulette.client.exception.MapRouletteRuntimeException;

/**
 * Wraps a consumer, so that we can throw exceptions properly when using lambda functions
 *
 * @param <T>
 *            The type of object that the consumer consumes
 * @author mcuthbert
 */
public interface ThrowingConsumer<T> extends Consumer<T>
{
    static <T> Consumer<T> throwingConsumerWrapper(ThrowingConsumer<T> throwingConsumer)
    {
        return throwingConsumer;
    }

    @Override
    default void accept(T input)
    {
        try
        {
            applyThrows(input);
        }
        catch (final Exception e)
        {
            throw new MapRouletteRuntimeException(e);
        }
    }

    void applyThrows(T input) throws Exception;
}
