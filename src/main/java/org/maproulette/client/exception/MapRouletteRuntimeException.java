package org.maproulette.client.exception;

/**
 * A MapRoulette wrapper for a runtime exceptions
 *
 * @author mcuthbert
 */
public class MapRouletteRuntimeException extends RuntimeException
{
    public MapRouletteRuntimeException(final Throwable e)
    {
        super(e);
    }

    public MapRouletteRuntimeException(final String message, final Throwable e)
    {
        super(message, e);
    }

    public MapRouletteRuntimeException(final String message)
    {
        super(message);
    }
}
