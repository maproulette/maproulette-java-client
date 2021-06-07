package org.maproulette.client.model;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author mcuthbert
 */
public class ChallengeTest
{
    @Test
    public void challengeBuilderDefaultTest()
    {
        final var challenge = Challenge.builder().parent(12345).name("TestChallenge")
                .instruction("TestInstruction").build();
        Assertions.assertEquals(-1, challenge.getId());
        Assertions.assertEquals(12345, challenge.getParent());
        Assertions.assertEquals("TestChallenge", challenge.getName());
        Assertions.assertEquals(ChallengeDifficulty.NORMAL, challenge.getDifficulty());
        Assertions.assertEquals(ChallengePriority.MEDIUM, challenge.getDefaultPriority());
        Assertions.assertFalse(challenge.isEnabled());
        Assertions.assertFalse(challenge.isFeatured());
        Assertions.assertEquals("TestInstruction", challenge.getInstruction());
        Assertions.assertNull(challenge.getBlurb());
        Assertions.assertNull(challenge.getDescription());
        Assertions.assertEquals("", challenge.getCheckinComment());
        Assertions.assertEquals("", challenge.getCheckinSource());
        Assertions.assertNull(challenge.getHighPriorityRule());
        Assertions.assertNull(challenge.getMediumPriorityRule());
        Assertions.assertNull(challenge.getLowPriorityRule());
        Assertions.assertEquals(13, challenge.getDefaultZoom());
        Assertions.assertEquals(1, challenge.getMinZoom());
        Assertions.assertEquals(19, challenge.getMaxZoom());
        Assertions.assertNull(challenge.getDefaultBasemap());
        Assertions.assertNull(challenge.getDefaultBasemapId());
        Assertions.assertNull(challenge.getCustomBasemap());
    }

    @Test
    public void challengePriorityBuilderTest()
    {
        final var challenge = Challenge.builder().parent(12345).name("TestChallenge")
                .instruction("TestInstruction")
                .highPriorityRule(RuleList.builder().condition("AND")
                        .rules(Collections.singletonList(PriorityRule.builder().operator("equal")
                                .type("string").value("testValue").build()))
                        .build())
                .build();
        final var rule = challenge.getHighPriorityRule();
        Assertions.assertTrue(rule.isSet());
        Assertions.assertEquals("AND", rule.getCondition());
        final var ruleList = rule.getRules();
        Assertions.assertEquals(1, ruleList.size());
        final var onlyRule = ruleList.get(0);
        Assertions.assertEquals("equal", onlyRule.getOperator());
        Assertions.assertEquals("string", onlyRule.getType());
        Assertions.assertEquals("testValue", onlyRule.getValue());
    }

    @Test
    public void challengeTagsBuilderTest()
    {
        final var challenge = Challenge.builder().parent(12345).name("TestChallenge")
                .instruction("TestInstruction").preferredTags("preferredTag")
                .preferredReviewTags("preferredReviewTag").tags(new String[] { "tag1", "tag2" })
                .build();
        Assertions.assertEquals("preferredTag", challenge.getPreferredTags());
        Assertions.assertEquals("preferredReviewTag", challenge.getPreferredReviewTags());
        Assertions.assertEquals(2, challenge.getTags().length);
        Assertions.assertEquals("tag1", challenge.getTags()[0]);
    }
}
