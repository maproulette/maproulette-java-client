package org.maproulette.client.model;

import java.io.Serializable;

import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.utilities.Utilities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Very class defining the structure of user data
 *
 * @author pdevkota1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable
{
    @SuppressWarnings("checkstyle:memberName")
    private long id;
    private String name;

    public static User fromJson(final String json) throws MapRouletteException
    {
        return Utilities.fromJson(json, User.class);
    }
}
