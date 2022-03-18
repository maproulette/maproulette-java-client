package org.maproulette.client.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
@Slf4j
public class RuleList implements Serializable
{
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
        // A condition is needed
        if (this.condition == null || this.condition.isEmpty())
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

        private void serializeRuleListAsObject(final RuleList value, final JsonGenerator gen,
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
            // When the serialized format lacks required values, just create an empty RuleList to
            // avoid NPE.
            if (node.get("condition") == null)
            {
                return RuleList.builder().build();
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
