package org.maproulette.client.connection;

import java.util.Optional;

import org.maproulette.client.exception.MapRouletteException;

/**
 * @author mcuthbert
 */
public interface IMapRouletteConnection
{
    /**
     * Function used to check whether you can actually make a connection to the a MapRoulette server
     *
     * @return True if connection is successful
     */
    boolean isAbleToConnectToMapRoulette();

    /**
     * A generic execute function that will
     *
     * @param query
     *            The query to execute against the MapRoulette Server
     * @return A list of strings that would then need to be transformed by the caller
     * @throws MapRouletteException
     *             Any exceptions that occur while trying to list the data
     */
    Optional<String> execute(Query query) throws MapRouletteException;
}
