package org.maproulette.client.utilities;

import java.util.function.Function;

import org.maproulette.client.exception.MapRouletteRuntimeException;

/**
 * Wraps a consumer, so that we can throw exceptions properly when using lambda functions
 *
 * @param <T>
 *            The type of object that the function consumes
 * @param <R>
 *            The type of object that the function produces
 * @author mcuthbert
 */
public interface ThrowingFunction<T, R> extends Function<T, R>
{
    static <T, R> Function<T, R> throwingFunctionWrapper(ThrowingFunction<T, R> throwingFunction)
    {
        return throwingFunction;
    }

    @Override
    default R apply(final T input)
    {
        try
        {
            return applyThrows(input);
        }
        catch (final Exception e)
        {
            throw new MapRouletteRuntimeException(e);
        }
    }

    R applyThrows(T input) throws Exception;
}
