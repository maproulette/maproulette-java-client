package org.maproulette.client.exception;

/**
 * @author mcuthbert
 */
public class MapRouletteException extends Exception
{
    public MapRouletteException(final Throwable e)
    {
        super(e);
    }

    public MapRouletteException(final String message, final Throwable e)
    {
        super(message, e);
    }

    public MapRouletteException(final String message)
    {
        super(message);
    }
}
