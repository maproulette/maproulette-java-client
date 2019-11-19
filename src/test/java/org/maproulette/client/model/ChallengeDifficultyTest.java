package org.maproulette.client.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author mcuthbert
 */
public class ChallengeDifficultyTest
{
    @Test
    public void fromValueTest()
    {
        // Successful use cases
        Assertions.assertEquals(ChallengeDifficulty.EASY, ChallengeDifficulty.fromValue(1));
        Assertions.assertEquals(ChallengeDifficulty.NORMAL, ChallengeDifficulty.fromValue(2));
        Assertions.assertEquals(ChallengeDifficulty.EXPERT, ChallengeDifficulty.fromValue(3));

        // Failure use cases
        Assertions.assertNull(ChallengeDifficulty.fromValue(-1));
        Assertions.assertNull(ChallengeDifficulty.fromValue(4));
    }

    @Test
    public void intValueTest()
    {
        Assertions.assertEquals(1, ChallengeDifficulty.EASY.intValue());
        Assertions.assertEquals(2, ChallengeDifficulty.NORMAL.intValue());
        Assertions.assertEquals(3, ChallengeDifficulty.EXPERT.intValue());
    }
}
