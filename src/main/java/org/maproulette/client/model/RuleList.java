package org.maproulette.client.model;

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
import org.maproulette.client.exception.MapRouletteRuntimeException;
import org.maproulette.client.utilities.ObjectMapperSingleton;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        } catch (final JsonProcessingException e)
        {
            throw new MapRouletteRuntimeException(e);
        }
    }

    public static class RuleListSerializer extends StdSerializer<RuleList>
    {

        public RuleListSerializer()
        {
            this(null);
        }

        public RuleListSerializer(Class<RuleList> t)
        {
            super(t);
        }

        private static void serializeRuleListHelper(List<RuleList> ruleList, JsonGenerator gen) throws IOException
        {
            for (RuleList r : ruleList)
            {
                gen.writeStartObject();
                gen.writeStringField("condition", r.getCondition());
                gen.writeArrayFieldStart("rules");
                if (r.getRuleList() != null)
                {
                    for (RuleList it : r.getRuleList())
                    {
                        serializeRuleListHelper(it.getRuleList(), gen);
                    }
                }
                if (r.getRules() != null)
                {
                    for (PriorityRule p : r.getRules())
                    {
                        gen.writeObject(p);
                    }
                }
                gen.writeEndArray();
                gen.writeEndObject();
            }
        }

        @Override
        public void serialize(RuleList value, JsonGenerator gen, SerializerProvider serializers) throws IOException
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
                for (PriorityRule p : value.getRules())
                {
                    gen.writeObject(p);
                }
            }
            gen.writeEndArray();
            gen.writeEndObject();
        }
    }

    public static class RuleListDeserializer extends StdDeserializer<RuleList>
    {

        public RuleListDeserializer()
        {
            this(null);
        }

        public RuleListDeserializer(final Class<?> vc)
        {
            super(vc);
        }

        private static RuleList buildRuleListHelper(JsonNode node, DeserializationContext ctxt)
        {
            if (node.get("condition") == null)
            {
                return null;
            }
            final RuleList ret = RuleList.builder()
                    .condition(node.get("condition").asText())
                    .ruleList(new ArrayList<>())
                    .rules(new ArrayList<>())
                    .build();

            for (JsonNode it : node.withArray("rules"))
            {
                if (it.get("condition") != null)
                {
                    // If the child is a PriorityRule, do a recursive call to build the rule and add it to the list.
                    RuleList child = buildRuleListHelper(it, ctxt);
                    ret.getRuleList().add(child);
                } else
                {
                    PriorityRule priorityRule = PriorityRule.builder()
                            .type(it.get("type").asText())
                            .operator(it.get("operator").asText())
                            .value(it.get("value").asText())
                            .build();
                    ret.getRules().add(priorityRule);
                }
            }

            return ret;
        }

        @Override
        public RuleList deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
        {
            JsonNode node = p.getCodec().readTree(p);
            return buildRuleListHelper(node, ctxt);
        }
    }
}
