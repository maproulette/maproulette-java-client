package org.maproulette.client.model;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the Challenge Difficulty field
 *
 * @author cuthbertm
 */
public enum ChallengeDifficulty
{
    EASY(1),
    NORMAL(2),
    EXPERT(3);

    private final int value;

    @JsonCreator
    public static ChallengeDifficulty fromValue(final String value)
    {
        try
        {
            final var intValue = Integer.parseInt(value);
            return ChallengeDifficulty.fromValue(intValue);
        }
        catch (final NumberFormatException e)
        {
            for (final var challengeDifficulty : ChallengeDifficulty.values())
            {
                if (StringUtils.equalsIgnoreCase(challengeDifficulty.name(), value))
                {
                    return challengeDifficulty;
                }
            }
        }
        return null;
    }

    public static ChallengeDifficulty fromValue(final int value)
    {
        for (final var challengeDifficulty : ChallengeDifficulty.values())
        {
            if (challengeDifficulty.intValue() == value)
            {
                return challengeDifficulty;
            }
        }
        return null;
    }

    ChallengeDifficulty(final int value)
    {
        this.value = value;
    }

    @JsonValue
    public int intValue()
    {
        return this.value;
    }
}
