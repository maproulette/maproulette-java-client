package org.maproulette.client.utilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author mcuthbert
 */
public class TupleTest
{
    @Test
    public void basicTupleTest()
    {
        final var tuple = new Tuple<>("Test", true);
        Assertions.assertEquals("Test", tuple.getFirst());
        Assertions.assertTrue(tuple.getSecond());
        final var tuple2 = new Tuple<>("Test", true);
        Assertions.assertEquals(tuple, tuple2);
        Assertions.assertEquals(tuple.hashCode(), tuple2.hashCode());
        Assertions.assertEquals(tuple.toString(), tuple2.toString());
        final var tuple3 = new Tuple<>("TEst", false);
        Assertions.assertNotEquals(tuple, tuple3);
        Assertions.assertNotEquals(tuple.hashCode(), tuple3.hashCode());
        Assertions.assertNotEquals(tuple.toString(), tuple3.toString());
    }
}
