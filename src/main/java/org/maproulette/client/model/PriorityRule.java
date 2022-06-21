package org.maproulette.client.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.maproulette.client.exception.MapRouletteRuntimeParseException;

import lombok.Builder;
import lombok.Value;

/**
 * @author mcuthbert
 */
@Builder
@Value
public class PriorityRule implements Serializable
{
    private static final Set<String> NUMBER_COMPARISON_OPERATORS = Set.of("==", "!=", "<", "<=",
            ">", ">=");
    private static final Set<String> STRING_COMPARISON_OPERATORS = Set.of("equal", "not_equal",
            "contains", "not_contains", "is_empty", "is_not_empty");
    private static final Map<String, Set<String>> TYPE_TO_OPERATORS = Map.of("bounds",
            Set.of("contains", "not_contains"), "string", STRING_COMPARISON_OPERATORS, "integer",
            NUMBER_COMPARISON_OPERATORS, "long", NUMBER_COMPARISON_OPERATORS, "double",
            NUMBER_COMPARISON_OPERATORS);
    private static final long serialVersionUID = -7443371611488972313L;
    private String value;
    private String type;
    private String operator;

    public static PriorityRuleBuilder builder()
    {
        return new PriorityRuleBuilderCustom();
    }

    /**
     * Extend the generated PriorityRuleBuilder for additional validation of the fields.
     */
    private static class PriorityRuleBuilderCustom extends PriorityRuleBuilder
    {
        public PriorityRule build()
        {
            if (!TYPE_TO_OPERATORS.containsKey(super.type))
            {
                throw new MapRouletteRuntimeParseException(
                        String.format("Type '%s' is not supported by Priority Rules", super.type));
            }
            if (!TYPE_TO_OPERATORS.get(super.type).contains(super.operator))
            {
                throw new MapRouletteRuntimeParseException(String.format(
                        "Operator '%s' is not supported by type '%s'", super.operator, super.type));
            }
            if (super.value.split("\\.").length != 2)
            {
                throw new MapRouletteRuntimeParseException(
                        String.format("value '%s' must contain exactly one '.'", super.value));
            }

            return new PriorityRule(super.value, super.type, super.operator);
        }
    }
}
