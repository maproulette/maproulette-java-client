package org.maproulette.client.utilities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A basic Tuple class
 *
 * @param <X>
 *            The first object type that will be stored in the Tuple
 * @param <Y>
 *            The second object type that will be stored in the Tuple
 * @author mcuthbert
 */
@Getter
@EqualsAndHashCode
@ToString
public class Tuple<X, Y>
{
    private final X first;
    private final Y second;

    public Tuple(final X first, final Y second)
    {
        this.first = first;
        this.second = second;
    }
}
