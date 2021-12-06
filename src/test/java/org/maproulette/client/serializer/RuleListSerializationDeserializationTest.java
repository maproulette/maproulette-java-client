package org.maproulette.client.serializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.model.PriorityRule;
import org.maproulette.client.model.RuleList;
import org.maproulette.client.utilities.ObjectMapperSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class RuleListSerializationDeserializationTest
{
    private static RuleList stringJsonToRuleList(final String in) throws IOException
    {
        return ObjectMapperSingleton.getMapper().readValue(in, RuleList.class);
    }

    private static RuleList resourceToRuleList(String resource) throws IOException
    {
        return stringJsonToRuleList(SerializerUtilities.getResourceAsString(resource));
    }

    @Test
    public void empty() throws IOException
    {
        Assertions.assertNull(stringJsonToRuleList("{}"));
    }

    @Test
    public void noRules() throws IOException
    {
        RuleList expected = RuleList.builder().condition("OR").rules(new ArrayList<>()).ruleList(new ArrayList<>()).build();
        RuleList actual = resourceToRuleList("rulelist/rulelist_condition_no_rules.json");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void simpleRules() throws IOException
    {
        RuleList expected = RuleList.builder()
                .condition("AND")
                .rules(
                        Collections.singletonList(
                                PriorityRule.builder()
                                        .value("priority_pd.3")
                                        .type("string")
                                        .operator("equal")
                                        .build()))
                .ruleList(new ArrayList<>())
                .build();
        RuleList actual = resourceToRuleList("rulelist/rulelist_priorityrule_not_nested.json");
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(stringJsonToRuleList(ObjectMapperSingleton.getMapper().writeValueAsString(expected)), expected);
    }

    @Test
    public void nestedRuleList() throws IOException
    {
        RuleList expected = RuleList.builder()
                .condition("AND")
                .rules(new ArrayList<>())
                .ruleList(Collections.singletonList(
                        RuleList.builder()
                                .condition("OR")
                                .ruleList(new ArrayList<>())
                                .rules(Collections.singletonList(
                                        PriorityRule.builder()
                                                .value("a.b")
                                                .type("string")
                                                .operator("is_not_empty")
                                                .build()))
                                .build()))
                .build();
        RuleList actual = resourceToRuleList("rulelist/rulelist_no_priorityrule_with_nested_rulelist.json");
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(stringJsonToRuleList(ObjectMapperSingleton.getMapper().writeValueAsString(expected)), expected);
    }

    @Test
    public void nestedWithPriorityRuleAndRuleList() throws IOException
    {
        RuleList expected = RuleList.builder()
                .condition("AND")
                .rules(Collections.singletonList(
                        PriorityRule.builder()
                                .value("t.u")
                                .type("string")
                                .operator("is_empty")
                                .build()
                ))
                .ruleList(Collections.singletonList(
                        RuleList.builder()
                                .condition("OR")
                                .ruleList(new ArrayList<>())
                                .rules(Collections.singletonList(
                                        PriorityRule.builder()
                                                .value("a.b")
                                                .type("string")
                                                .operator("is_not_empty")
                                                .build()))
                                .build()))
                .build();
        RuleList actual = resourceToRuleList("rulelist/rulelist_priorityrule_with_nested_rulelist.json");
        RuleList actual_reordered = resourceToRuleList("rulelist/rulelist_priorityrule_with_nested_rulelist_reordered.json");
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(stringJsonToRuleList(ObjectMapperSingleton.getMapper().writeValueAsString(expected)), expected);
        Assertions.assertEquals(expected, actual_reordered);
    }

}
