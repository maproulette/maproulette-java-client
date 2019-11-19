package org.maproulette.client.model;

import java.io.Serializable;
import java.util.List;

import org.maproulette.client.exception.MapRouletteRuntimeException;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class RuleList implements Serializable
{
    private static final String KEY_CONDITION = "condition";
    private static final String KEY_RULES = "rules";
    private static final long serialVersionUID = -1085774480815117637L;

    private String condition;
    private List<PriorityRule> rules;

    public boolean isSet()
    {
        return this.condition != null && this.rules != null && !this.rules.isEmpty();
    }

    @JsonValue
    public String toJson()
    {
        final var mapper = new ObjectMapper();
        try
        {
            return String.format("{\"%s\":\"%s\",\"%s\":%s}", KEY_CONDITION, this.getCondition(),
                    KEY_RULES, mapper.writeValueAsString(this.getRules()));
        }
        catch (final JsonProcessingException e)
        {
            throw new MapRouletteRuntimeException(e);
        }

    }
}
