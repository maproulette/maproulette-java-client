package org.maproulette.client.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.maproulette.client.exception.MapRouletteRuntimeParseException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mcuthbert
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = RuleList.RuleListDeserializer.class)
@JsonSerialize(using = RuleList.RuleListSerializer.class)
@Value
@Slf4j
public class RuleList implements Serializable
{
    public static final String CONDITION_AND = "AND";
    public static final String CONDITION_OR = "OR";
    private static final Set<String> VALID_CONDITIONS = Set.of(CONDITION_AND, CONDITION_OR);

    private static final String KEY_CONDITION = "condition";
    private static final String KEY_RULES = "rules";
    private static final long serialVersionUID = -1085774480815117637L;

    @Builder.Default
    @NonNull
    private String condition = "";

    @Builder.Default
    @NonNull
    private List<RuleList> ruleList = new ArrayList<>();

    @Builder.Default
    @NonNull
    private List<PriorityRule> rules = new ArrayList<>();

    public boolean isSet()
    {
        // The rule list is "set" if the condition is empty, and there are no rules, and there are
        // no nested priority rules
        if (this.condition.isEmpty() && this.rules.isEmpty() && this.ruleList.isEmpty())
        {
            return false;
        }

        // It is possible to have an empty 'rules' and a non-empty nested rule list.
        // It is invalid for both to be empty at the same time.
        if (this.rules.isEmpty() && this.ruleList.isEmpty())
        {
            return false;
        }

        return true;
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
                final String condition = ruleList.getCondition();
                // For nested rule lists (eg rule lists that have a parent), these REQUIRE the
                // condition to be non-empty and valid.
                if (!CONDITION_AND.equals(condition) && !CONDITION_OR.equals(condition))
                {
                    throw new MapRouletteRuntimeParseException(
                            String.format("Condition '%s' is not known", condition));
                }

                gen.writeStartObject();
                gen.writeStringField(KEY_CONDITION, ruleList.getCondition());
                gen.writeArrayFieldStart(KEY_RULES);
                for (final RuleList nestedRuleList : ruleList.getRuleList())
                {
                    serializeRuleListHelper(nestedRuleList.getRuleList(), gen);
                }
                for (final PriorityRule priorityRule : ruleList.getRules())
                {
                    gen.writeObject(priorityRule);
                }
                gen.writeEndArray();
                gen.writeEndObject();
            }
        }

        private void serializeRuleListAsObject(final RuleList value, final JsonGenerator gen,
                final SerializerProvider serializers) throws IOException
        {
            final String condition = value.getCondition();
            // A condition must be valid
            if (!VALID_CONDITIONS.contains(condition))
            {
                throw new MapRouletteRuntimeParseException(
                        String.format("Condition '%s' is not known", condition));
            }

            gen.writeStartObject();
            gen.writeStringField(KEY_CONDITION, condition);
            gen.writeArrayFieldStart(KEY_RULES);

            serializeRuleListHelper(value.getRuleList(), gen);
            for (final PriorityRule priorityRule : value.getRules())
            {
                gen.writeObject(priorityRule);
            }

            gen.writeEndArray();
            gen.writeEndObject();
            gen.flush();
        }

        /**
         * Serialize a RuleList in a format that is compatible with the existing scala backend
         * service. The end format must be a json escaped string and not an object. The deserializer
         * supports either format: a json string or an object. <br>
         * <br>
         * For example:
         * "highPriorityRule":"{\"condition\":\"AND\",\"rules\":[{\"value\":\"priority_pd.3\",\"type\":\"string\",\"operator\":\"equal\"}]}"
         * {@inheritDoc}
         */
        @Override
        public void serialize(final RuleList value, final JsonGenerator gen,
                final SerializerProvider serializers) throws IOException
        {
            // If the RuleList is not "set", write an empty object string.
            if (!value.isSet())
            {
                gen.writeString("{}");
                return;
            }

            // First create a temporary jsongenerator to hold the serialized nested RuleList object.
            final StringWriter stringWriter = new StringWriter();
            final JsonGenerator tempGen = new JsonFactory().setCodec(gen.getCodec())
                    .createGenerator(stringWriter);
            serializeRuleListAsObject(value, tempGen, serializers);

            // Now get the serialized RuleList as a string and write it as a plain string.
            final String ruleListAsString = stringWriter.toString();
            gen.writeString(ruleListAsString);
            stringWriter.close();
            tempGen.close();
        }
    }

    /**
     * Deserialize a {@code RuleList}. The serialized format may be either a json escaped string or
     * an object.
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
            final JsonNode conditionNode = node.get(KEY_CONDITION);
            final JsonNode rulesNode = node.get(KEY_RULES);

            // If NEITHER a condition nor rules is in the object, return a dummy RuleList which will
            // serialize to '{}'.
            if (conditionNode == null && rulesNode == null)
            {
                return RuleList.builder().build();
            }

            // Both the 'condition' and 'rules' must appear, otherwise it is an invalid object.
            if (conditionNode == null || rulesNode == null)
            {
                throw new MapRouletteRuntimeParseException(
                        "parse error: the rulelist requires both a 'condition' and 'rules' field");
            }

            if (!VALID_CONDITIONS.contains(conditionNode.asText()))
            {
                throw new MapRouletteRuntimeParseException(
                        String.format("Condition '%s' is not known", conditionNode.asText()));
            }

            final RuleListBuilder ret = RuleList.builder().condition(conditionNode.asText())
                    .ruleList(new ArrayList<>()).rules(new ArrayList<>());

            for (final JsonNode jsonNode : node.withArray(KEY_RULES))
            {
                if (jsonNode.get(KEY_CONDITION) != null || jsonNode.get(KEY_RULES) != null)
                {
                    // If the child is a PriorityRule, do a recursive call to build the rule and add
                    // it to the list.
                    final RuleList child = buildRuleListHelper(jsonNode, ctxt);
                    ret.ruleList$value.add(child);
                }
                else
                {
                    if (jsonNode.get("type") == null || jsonNode.get("operator") == null
                            || jsonNode.get("value") == null)
                    {
                        throw new MapRouletteRuntimeParseException(
                                "Nested object is not a PriorityRule nor a RuleList! Parsing failed!");
                    }
                    final PriorityRule priorityRule = PriorityRule.builder()
                            .type(jsonNode.get("type").asText())
                            .operator(jsonNode.get("operator").asText())
                            .value(jsonNode.get("value").asText()).build();
                    ret.rules$value.add(priorityRule);
                }
            }

            return ret.build();
        }

        /**
         * Deserialize the escaped json string or object into a RuleList. <br>
         * <br>
         * For example the escaped json string looks like this
         * "{\"condition\":\"AND\",\"rules\":[{\"value\":\"priority_pd.3\",\"type\":\"string\",\"operator\":\"equal\"}]}"
         * or like this
         * {"condition":"AND","rules":[{"value":"priority_pd.3","type":"string","operator":"equal"}]}
         * {@inheritDoc}
         */
        @Override
        public RuleList deserialize(final JsonParser jsonParser, final DeserializationContext ctxt)
                throws IOException
        {
            // The json could be an escaped string representation or an object representation of a
            // RuleList
            final JsonNode tree = jsonParser.readValueAsTree();

            if (tree.isContainerNode())
            {
                // It's an object representation, pass it on for parsing
                return buildRuleListHelper(tree, ctxt);
            }
            else
            {
                // First read out the string which is the escaped json of the RuleList
                final String ruleListString = jsonParser.getCodec().treeToValue(tree, String.class);

                // Take the string and convert it to a JsonNode
                final JsonNode ruleListNode = ((ObjectMapper) jsonParser.getCodec())
                        .readTree(ruleListString);

                // Recursively parse the node tree
                return buildRuleListHelper(ruleListNode, ctxt);
            }
        }
    }
}
