package org.maproulette.client.model;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The status of a Task.
 *
 * @author mcuthbert
 */
public enum TaskStatus
{
    CREATED(0),
    FIXED(1),
    FALSE_POSITIVE(2),
    SKIPPED(3),
    DELETED(4),
    ALREADY_FIXED(5),
    TOO_HARD(6),
    ANSWERED(7),
    VALIDATED(8),
    DISABLED(9);

    private final int value;

    @JsonCreator
    public static TaskStatus fromValue(final String value)
    {
        try
        {
            final var intValue = Integer.parseInt(value);
            return TaskStatus.fromValue(intValue);
        }
        catch (final NumberFormatException e)
        {
            for (final var taskStatus : TaskStatus.values())
            {
                if (StringUtils.equalsIgnoreCase(taskStatus.name(), value))
                {
                    return taskStatus;
                }
            }
        }
        return null;
    }

    public static TaskStatus fromValue(final int value)
    {
        for (final var status : TaskStatus.values())
        {
            if (status.intValue() == value)
            {
                return status;
            }
        }
        return null;
    }

    TaskStatus(final int value)
    {
        this.value = value;
    }

    @JsonValue
    public int intValue()
    {
        return this.value;
    }
}
