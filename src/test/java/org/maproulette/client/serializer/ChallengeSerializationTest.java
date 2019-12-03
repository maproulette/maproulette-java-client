package org.maproulette.client.serializer;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.Challenge;
import org.maproulette.client.model.ChallengeDifficulty;
import org.maproulette.client.model.ChallengePriority;
import org.maproulette.client.model.PriorityRule;
import org.maproulette.client.model.RuleList;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests whether a challenge can be read correctly from resources.
 *
 * @author cuthbertm
 */
public class ChallengeSerializationTest
{
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String BLURB = "BLURB";
    private static final String INSTRUCTION = "INSTRUCTION";
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void fullSerializationTest() throws IOException
    {
        final var full = Challenge.builder().parent(456L).name("TestChallenge")
                .instruction("TestInstruction").description("TestDescription").blurb("TestBlurb")
                .checkinComment("CheckinComment").checkinSource("CheckinSource")
                .customBasemap("customBaseMap").defaultBasemap(23)
                .defaultBasemapId("defaultBaseMap").defaultPriority(ChallengePriority.LOW)
                .defaultZoom(17).difficulty(ChallengeDifficulty.EXPERT).enabled(true).featured(true)
                .id(1234L).maxZoom(2).minZoom(3).build();

        final var serializedString = this.mapper.writeValueAsString(full);
        final var deserializedChallenge = this.mapper.readValue(serializedString, Challenge.class);
        Assertions.assertEquals(full.getName(), deserializedChallenge.getName());
        Assertions.assertEquals(full.getDescription(), deserializedChallenge.getDescription());
        Assertions.assertEquals(full.getBlurb(), deserializedChallenge.getBlurb());
        Assertions.assertEquals(full.getCheckinComment(),
                deserializedChallenge.getCheckinComment());
        Assertions.assertEquals(full.getCheckinSource(), deserializedChallenge.getCheckinSource());
        Assertions.assertEquals(full.getCustomBasemap(), deserializedChallenge.getCustomBasemap());
        Assertions.assertEquals(full.getDefaultBasemap(),
                deserializedChallenge.getDefaultBasemap());
        Assertions.assertEquals(full.getDefaultBasemapId(),
                deserializedChallenge.getDefaultBasemapId());
        Assertions.assertEquals(full.getDefaultPriority(),
                deserializedChallenge.getDefaultPriority());
        Assertions.assertEquals(full.getDefaultZoom(), deserializedChallenge.getDefaultZoom());
        Assertions.assertEquals(full.getDifficulty(), deserializedChallenge.getDifficulty());
        Assertions.assertEquals(full.isEnabled(), deserializedChallenge.isEnabled());
        Assertions.assertEquals(full.isFeatured(), deserializedChallenge.isFeatured());
        Assertions.assertEquals(full.getId(), deserializedChallenge.getId());
        Assertions.assertEquals(full.getInstruction(), deserializedChallenge.getInstruction());
        Assertions.assertEquals(full.getMaxZoom(), deserializedChallenge.getMaxZoom());
        Assertions.assertEquals(full.getMinZoom(), deserializedChallenge.getMinZoom());
        Assertions.assertEquals(full.getParent(), deserializedChallenge.getParent());
    }

    @Test
    public void defaultSerializationTest() throws IOException
    {
        final var value = Challenge.builder().parent(1L).name("TestChallenge")
                .instruction("TestInstruction").description("TestDescription").blurb("TestBlurb")
                .customBasemap("customBaseMap").defaultBasemap(56)
                .defaultBasemapId("defaultBaseMap").id(1234L).build();

        final var serializedString = this.mapper.writeValueAsString(value);
        final var deserializedChallenge = this.mapper.readValue(serializedString, Challenge.class);

        Assertions.assertEquals("", deserializedChallenge.getCheckinComment());
        Assertions.assertEquals("", deserializedChallenge.getCheckinSource());
        Assertions.assertEquals(ChallengePriority.MEDIUM,
                deserializedChallenge.getDefaultPriority());
        Assertions.assertEquals(13, deserializedChallenge.getDefaultZoom());
        Assertions.assertEquals(ChallengeDifficulty.NORMAL, deserializedChallenge.getDifficulty());
        Assertions.assertFalse(deserializedChallenge.isEnabled());
        Assertions.assertFalse(deserializedChallenge.isFeatured());
        Assertions.assertEquals(19, deserializedChallenge.getMaxZoom());
        Assertions.assertEquals(1, deserializedChallenge.getMinZoom());
        Assertions.assertEquals(1L, deserializedChallenge.getParent());
    }

    /**
     * Tests that a challenge with no defaultPriority specified gets loaded as defaultPriority=LOW.
     *
     * @throws Exception
     *             any failure when reading the challenge resource file
     */
    @Test
    public void serializationNoDefaultPrioritySpecifiedTest() throws Exception
    {
        final var deserializedChallenge = this.getChallenge("challenges/testChallenge4.json");

        Assertions.assertEquals(DESCRIPTION, deserializedChallenge.getDescription());
        Assertions.assertEquals(BLURB, deserializedChallenge.getBlurb());
        Assertions.assertEquals(INSTRUCTION, deserializedChallenge.getInstruction());
        Assertions.assertEquals(ChallengeDifficulty.NORMAL, deserializedChallenge.getDifficulty());
        Assertions.assertEquals(ChallengePriority.MEDIUM,
                deserializedChallenge.getDefaultPriority());
        Assertions.assertNull(deserializedChallenge.getHighPriorityRule());
        Assertions.assertNull(deserializedChallenge.getMediumPriorityRule());
        Assertions.assertNull(deserializedChallenge.getLowPriorityRule());
    }

    /**
     * Test if a challange can be deserialized from a test JSON file. The challenge resource json
     * contains no MapRoulette priority information.
     *
     * @throws Exception
     *             any failure when reading the challenge resource file
     */
    @Test
    public void serializationNoPriorityTest() throws Exception
    {
        // This line will deserialize the challenge, and if it fails we know it didn't work.
        final var deserializedChallenge = this.getChallenge("challenges/testChallenge2.json");
        Assertions.assertEquals(DESCRIPTION, deserializedChallenge.getDescription());
        Assertions.assertEquals(BLURB, deserializedChallenge.getBlurb());
        Assertions.assertEquals(INSTRUCTION, deserializedChallenge.getInstruction());
        Assertions.assertEquals("", deserializedChallenge.getCheckinComment());
        Assertions.assertEquals(ChallengeDifficulty.NORMAL, deserializedChallenge.getDifficulty());
        Assertions.assertEquals(ChallengePriority.NONE, deserializedChallenge.getDefaultPriority());
        Assertions.assertNull(deserializedChallenge.getHighPriorityRule());
        Assertions.assertNull(deserializedChallenge.getMediumPriorityRule());
        Assertions.assertNull(deserializedChallenge.getLowPriorityRule());
    }

    /**
     * Test if a challenge can be deserialized from a test JSON file. The challenge contains
     * MapRoulette priority information for high and medium priority but not for low priority.
     *
     * @throws Exception
     *             any failure when reading the challenge resource file
     */
    @Test
    public void serializationTest() throws Exception
    {
        // This line will deserialize the challenge, and if it fails we know it didn't work.
        final var deserializedChallenge = this.getChallenge("challenges/testChallenge.json");
        final var highPriority = RuleList.builder().condition("AND")
                .rules(Collections.singletonList(PriorityRule.builder().operator("equal")
                        .type("string").value("priority_pd.3").build()))
                .build();
        final var mediumPriority = RuleList.builder().condition("OR")
                .rules(Collections.singletonList(PriorityRule.builder().operator("equal")
                        .type("string").value("priority_pd.2").build()))
                .build();

        Assertions.assertEquals(DESCRIPTION, deserializedChallenge.getDescription());
        Assertions.assertEquals(BLURB, deserializedChallenge.getBlurb());
        Assertions.assertEquals(INSTRUCTION, deserializedChallenge.getInstruction());
        Assertions.assertEquals("", deserializedChallenge.getCheckinComment());
        Assertions.assertEquals(ChallengeDifficulty.NORMAL, deserializedChallenge.getDifficulty());
        Assertions.assertEquals(ChallengePriority.LOW, deserializedChallenge.getDefaultPriority());
        Assertions.assertNotNull(deserializedChallenge.getHighPriorityRule());
        Assertions.assertEquals(highPriority, deserializedChallenge.getHighPriorityRule());
        Assertions.assertNotNull(deserializedChallenge.getMediumPriorityRule());
        Assertions.assertEquals(mediumPriority, deserializedChallenge.getMediumPriorityRule());
        Assertions.assertNull(deserializedChallenge.getLowPriorityRule());
    }

    /**
     * Helper function that converts the resource file into a {@link Challenge}
     *
     * @param resource
     *            The path to the resource file
     * @return A {@link Challenge} object representing the provided resource.
     * @throws MapRouletteException
     *             any failure when reading the challenge resource file
     */
    private Challenge getChallenge(final String resource) throws MapRouletteException
    {
        return Challenge.fromJson(SerializerUtilities.getResourceAsString(resource));
    }
}
