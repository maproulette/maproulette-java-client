package org.maproulette.client.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mcuthbert
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriorityRule implements Serializable
{
    private static final long serialVersionUID = -7443371611488972313L;
    private String value;
    private String type;
    private String operator;
}
