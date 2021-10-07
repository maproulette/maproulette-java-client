package org.maproulette.client.serializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.User;

/**
 * @author pdevkota1
 */
public class UserSerializationTest
{
    @Test
    public void userSerialization() throws MapRouletteException
    {
        final var testUserString = "{\"id\":12345 ,\"name\":\"someOsmName\", \"created\": \"2016-08-16T12:34:36.372Z\"}";
        final User testUser = User.fromJson(testUserString);
        Assertions.assertEquals(12345, testUser.getId());
        Assertions.assertEquals("someOsmName", testUser.getName());
    }
}
