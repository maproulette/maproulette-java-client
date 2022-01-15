package org.maproulette.client.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.maproulette.client.exception.MapRouletteRuntimeException;
import org.maproulette.client.utilities.ObjectMapperSingleton;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

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
@JsonDeserialize(using = RuleList.RuleListDeserializer.class)
@JsonSerialize(using = RuleList.RuleListSerializer.class)
public class RuleList implements Serializable
{
    private static final String KEY_CONDITION = "condition";
    private static final String KEY_RULES = "rules";
    private static final long serialVersionUID = -1085774480815117637L;

    private String condition;
    private List<RuleList> ruleList;
    private List<PriorityRule> rules;

    public boolean isSet()
    {
        return this.condition != null && this.rules != null && !this.rules.isEmpty();
    }

    @JsonValue
    public String toJson()
    {
        try
        {
            return ObjectMapperSingleton.getMapper().writeValueAsString(this);
        }
        catch (final JsonProcessingException e)
        {
            throw new MapRouletteRuntimeException(e);
        }
    }

    /**
     * Serialize a {@code RuleList}
     */
    public static class RuleListSerializer extends StdSerializer<RuleList>
    {
        public RuleListSerializer()
        {
            this(null);
        }

        public RuleListSerializer(final Class<RuleList> clazzRuleList)
        {
            super(clazzRuleList);
        }

        private static void serializeRuleListHelper(final List<RuleList> ruleListList,
                final JsonGenerator gen) throws IOException
        {
            for (final RuleList ruleList : ruleListList)
            {
                gen.writeStartObject();
                gen.writeStringField("condition", ruleList.getCondition());
                gen.writeArrayFieldStart("rules");
                if (ruleList.getRuleList() != null)
                {
                    for (final RuleList nestedRuleList : ruleList.getRuleList())
                    {
                        serializeRuleListHelper(nestedRuleList.getRuleList(), gen);
                    }
                }
                if (ruleList.getRules() != null)
                {
                    for (final PriorityRule priorityRule : ruleList.getRules())
                    {
                        gen.writeObject(priorityRule);
                    }
                }
                gen.writeEndArray();
                gen.writeEndObject();
            }
        }

        @Override
        public void serialize(final RuleList value, final JsonGenerator gen,
                final SerializerProvider serializers) throws IOException
        {
            gen.writeStartObject();
            gen.writeStringField("condition", value.getCondition());
            gen.writeArrayFieldStart("rules");
            if (value.getRuleList() != null)
            {
                serializeRuleListHelper(value.getRuleList(), gen);
            }

            if (value.getRules() != null)
            {
                for (final PriorityRule priorityRule : value.getRules())
                {
                    gen.writeObject(priorityRule);
                }
            }
            gen.writeEndArray();
            gen.writeEndObject();
        }
    }

    /**
     * Deserialize a {@code RuleList}
     */
    public static class RuleListDeserializer extends StdDeserializer<RuleList>
    {
        public RuleListDeserializer()
        {
            this(null);
        }

        public RuleListDeserializer(final Class<?> valueClass)
        {
            super(valueClass);
        }

        private static RuleList buildRuleListHelper(final JsonNode node,
                final DeserializationContext ctxt)
        {
            if (node.get("condition") == null)
            {
                return null;
            }
            final RuleList ret = RuleList.builder().condition(node.get("condition").asText())
                    .ruleList(new ArrayList<>()).rules(new ArrayList<>()).build();

            for (final JsonNode jsonNode : node.withArray("rules"))
            {
                if (jsonNode.get("condition") != null)
                {
                    // If the child is a PriorityRule, do a recursive call to build the rule and add
                    // it to the list.
                    final RuleList child = buildRuleListHelper(jsonNode, ctxt);
                    ret.getRuleList().add(child);
                }
                else
                {
                    final PriorityRule priorityRule = PriorityRule.builder()
                            .type(jsonNode.get("type").asText())
                            .operator(jsonNode.get("operator").asText())
                            .value(jsonNode.get("value").asText()).build();
                    ret.getRules().add(priorityRule);
                }
            }

            return ret;
        }

        @Override
        public RuleList deserialize(final JsonParser jsonParser, final DeserializationContext ctxt)
                throws IOException
        {
            final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            return buildRuleListHelper(node, ctxt);
        }
    }
}
