package org.maproulette.client.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author mcuthbert
 */
public class ChallengePriorityTest
{
    @Test
    public void fromValueTest()
    {
        // Successful use cases
        Assertions.assertEquals(ChallengePriority.NONE, ChallengePriority.fromValue(-1));
        Assertions.assertEquals(ChallengePriority.HIGH, ChallengePriority.fromValue(0));
        Assertions.assertEquals(ChallengePriority.MEDIUM, ChallengePriority.fromValue(1));
        Assertions.assertEquals(ChallengePriority.LOW, ChallengePriority.fromValue(2));

        // Failure use cases
        Assertions.assertEquals(ChallengePriority.LOW, ChallengePriority.fromValue(-2));
        Assertions.assertEquals(ChallengePriority.LOW, ChallengePriority.fromValue(3));
    }

    @Test
    public void intValueTest()
    {
        Assertions.assertEquals(-1, ChallengePriority.NONE.intValue());
        Assertions.assertEquals(0, ChallengePriority.HIGH.intValue());
        Assertions.assertEquals(1, ChallengePriority.MEDIUM.intValue());
        Assertions.assertEquals(2, ChallengePriority.LOW.intValue());
    }
}
