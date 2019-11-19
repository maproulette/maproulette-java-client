package org.maproulette.client.model;

/**
 * A base object that is used as an interface for all the MapRoulette objects
 *
 * @author mcuthbert
 */
public interface IMapRouletteObject
{
    /**
     * The identifier for the object
     *
     * @return long identifier
     */
    long getId();

    /**
     * The parent for the object
     *
     * @return long identifier
     */
    long getParent();

    /**
     * Gets the name of the Object
     *
     * @return The name of the objet
     */
    String getName();
}
