package org.maproulette.client.model;

import java.io.Serializable;

import lombok.Data;

/**
 * Simple object representing a Point that can be displayed on the map. The point can contain
 * information that then can be displayed on the map when clicked.
 *
 * @author mcuthbert
 */
@Data
public class PointInformation implements Serializable
{
    private static final long serialVersionUID = -6243145816167442948L;
    private final double latitude;
    private final double longitude;
    private final String description;

    public PointInformation(final double latitude, final double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = "";
    }
}
