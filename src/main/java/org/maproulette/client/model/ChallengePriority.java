package org.maproulette.client.model;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * An enum representing the Challenge priority
 *
 * @author cuthbertm
 */
public enum ChallengePriority
{
    @Deprecated
    NONE(-1),
    HIGH(0),
    MEDIUM(1),
    LOW(2);

    private final int value;

    @JsonCreator
    public static ChallengePriority fromValue(final String value)
    {
        try
        {
            final var intValue = Integer.parseInt(value);
            return ChallengePriority.fromValue(intValue);
        }
        catch (final NumberFormatException e)
        {
            for (final var challengePriority : ChallengePriority.values())
            {
                if (StringUtils.equalsIgnoreCase(challengePriority.name(), value))
                {
                    return challengePriority;
                }
            }
        }
        return null;
    }

    public static ChallengePriority fromValue(final int value)
    {
        for (final var challengePriority : ChallengePriority.values())
        {
            if (challengePriority.intValue() == value)
            {
                return challengePriority;
            }
        }
        return ChallengePriority.LOW;
    }

    ChallengePriority(final int value)
    {
        this.value = value;
    }

    @JsonValue
    public int intValue()
    {
        return this.value;
    }
}
