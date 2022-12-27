package org.maproulette.client.exception;

/**
 * An exception class used to identify, mostly, json parsing errors.
 * 
 * @author ljdelight
 */
public class MapRouletteRuntimeParseException extends MapRouletteRuntimeException
{
    public MapRouletteRuntimeParseException(final Throwable e)
    {
        super(e);
    }

    public MapRouletteRuntimeParseException(final String message, final Throwable e)
    {
        super(message, e);
    }

    public MapRouletteRuntimeParseException(final String message)
    {
        super(message);
    }
}
