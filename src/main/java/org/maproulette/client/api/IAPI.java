package org.maproulette.client.api;

import java.util.List;
import java.util.Optional;

import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.IMapRouletteObject;

/**
 * The service interface
 *
 * @param <T>
 *            Type of service, so either {@link org.maproulette.client.model.Project},
 *            {@link org.maproulette.client.model.Challenge} or
 *            {@link org.maproulette.client.model.Task}
 * @author mcuthbert
 */
public interface IAPI<T extends IMapRouletteObject>
{
    /**
     * Gets the object type associated with the service based on the object name
     *
     * @param identifier
     *            The identifier of parent of the object
     * @param name
     *            The name of the object
     * @return The object of Optional if not found
     * @throws MapRouletteException
     *             If there are any exceptions while retrieving the object
     */
    Optional<T> get(long identifier, String name) throws MapRouletteException;

    /**
     * Gets the object type associated with the service.
     *
     * @param identifier
     *            The identifier of the object
     * @return The object or Optional if not found
     * @throws MapRouletteException
     *             If there are any exceptions while retrieving the object
     */
    Optional<T> get(long identifier) throws MapRouletteException;

    /**
     * Finds a list of elements
     *
     * @param matcher
     *            A string query to match based on names
     * @param parent
     *            The parent identifier, use -1 if want to ignore it.
     * @param limit
     *            How many elements to limit the results by
     * @param page
     *            For paging the limited results
     * @return A list of elements
     * @throws MapRouletteException
     *             Any API exceptions
     */
    List<T> find(String matcher, long parent, int limit, int page) throws MapRouletteException;

    /**
     * Creates an object in MapRoulette. For creation if the object is in the cache it will just
     * respond with the cached object.
     *
     * @param object
     *            The object to create of type T
     * @return The newly created identifier of the object
     * @throws MapRouletteException
     *             If there are any exceptions while creating/updating or retrieving the objects
     */
    T create(T object) throws MapRouletteException;

    /**
     * Updates an object in MapRoulette.
     *
     * @param object
     *            The object to update of type T
     * @return Whether the update succeeded or not
     * @throws MapRouletteException
     *             If there are any exceptions while creating/updating or retrieving the objects
     */
    T update(T object) throws MapRouletteException;

    /**
     * Will create a challenge if it has not already been created. This function will also check to
     * see if the object exists first and if it does it will attempt to update it.
     *
     * @param object
     *            The object to create or update
     * @return The id of the created or updated object
     * @throws MapRouletteException
     *             If there are any exceptions while creating/updating or retrieving the objects
     */
    default T createUpdate(T object) throws MapRouletteException
    {
        final var getResponse = this.get(object.getParent(), object.getName());
        if (getResponse.isEmpty())
        {
            return this.create(object);
        }
        else
        {
            return this.update(object);
        }
    }

    /**
     * Deletes an object in MapRoulette based on the provided identifier
     *
     * @param identifier
     *            The identifier for the object to delete
     * @return true if object was deleted
     * @throws MapRouletteException
     *             If there are any exceptions while deleting the object
     */
    boolean delete(long identifier) throws MapRouletteException;

    /**
     * By default MapRoulette will not actually delete any object, but rather flag the object for
     * deletion. This function will add a flag to the URI that will force the deletion of an object.
     *
     * @param identifier
     *            The identifier for the object to delete
     * @return true if object was deleted
     * @throws MapRouletteException
     *             if there are any exceptions while deleting the object
     */
    boolean forceDelete(long identifier) throws MapRouletteException;
}
