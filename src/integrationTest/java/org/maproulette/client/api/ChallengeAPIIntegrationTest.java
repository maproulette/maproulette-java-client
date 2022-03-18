package org.maproulette.client.api;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.maproulette.client.IntegrationBase;
import org.maproulette.client.TestConstants;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.Challenge;
import org.maproulette.client.model.ChallengeDifficulty;
import org.maproulette.client.model.ChallengePriority;
import org.maproulette.client.model.PriorityRule;
import org.maproulette.client.model.RuleList;
import org.maproulette.client.model.Task;

/**
 * @author mcuthbert
 */
public class ChallengeAPIIntegrationTest extends IntegrationBase
{
    @BeforeEach
    public void setup() throws MapRouletteException
    {
        super.setup();
    }

    @AfterEach
    public void teardown() throws MapRouletteException
    {
        super.teardown();
    }

    @Test
    public void basicAPINewConfigurationTest() throws MapRouletteException
    {
        final var toCreate = this.getBasicChallengeForNewConfiguration();
        final var createdChallengeIdentifier = this.getChallengeAPIForNewConfiguration()
                .create(toCreate).getId();
        Assertions.assertNotEquals(-1, createdChallengeIdentifier);
    }

    @Test
    public void basicAPITest() throws MapRouletteException
    {
        final var toCreate = this.getBasicChallenge();
        final var createdChallengeIdentifier = this.getChallengeAPI().create(toCreate).getId();
        Assertions.assertNotEquals(-1, createdChallengeIdentifier);

        final var challenge = this.getChallengeAPI().get(createdChallengeIdentifier);
        Assertions.assertTrue(challenge.isPresent());
        challenge.ifPresent(value -> this.compareChallenges(toCreate, value));

        // Test deletion
        Assertions.assertTrue(this.getChallengeAPI().delete(createdChallengeIdentifier));
        Assertions.assertTrue(this.getChallengeAPI().get(createdChallengeIdentifier).isPresent());

        // Force the deletion of the object
        Assertions.assertTrue(this.getChallengeAPI().forceDelete(createdChallengeIdentifier));
        Assertions.assertTrue(this.getChallengeAPI().get(createdChallengeIdentifier).isEmpty());

    }

    @Test
    public void updateTest() throws MapRouletteException
    {
        final var initialChallenge = this.getBasicChallenge();
        final var createdChallenge = this.getChallengeAPI().create(initialChallenge);
        // quick compare making sure our challenges are identical
        this.compareChallenges(initialChallenge, createdChallenge);

        final var toUpdateChallenge = createdChallenge.toBuilder().instruction("UpdatedInstruction")
                .difficulty(ChallengeDifficulty.EASY).blurb("UpdatedBlurb").enabled(true)
                .description("UpdatedDescription").featured(true)
                .checkinComment("UpdatedCheckinComment").checkinSource("UpdatedCheckinSource")
                .name("UpdatedName").defaultPriority(ChallengePriority.LOW)
                .highPriorityRule(this.getRuleList("OR", "pr.high"))
                .mediumPriorityRule(this.getRuleList("AND", "pr.medium"))
                .lowPriorityRule(this.getRuleList("or", "pr.low")).defaultZoom(11).minZoom(10)
                .maxZoom(12).defaultBasemapId("defaultBaseMap").defaultBasemap(67)
                .customBasemap("customBasemap").build();

        final var updatedChallenge = this.getChallengeAPI().update(toUpdateChallenge);

        // make sure that it has actually updated.
        this.compareChallenges(toUpdateChallenge, updatedChallenge);
        // now make sure that it is different from the original challenge
        Assertions.assertEquals(createdChallenge.getId(), updatedChallenge.getId());
        Assertions.assertEquals(createdChallenge.getParent(), updatedChallenge.getParent());
        Assertions.assertNotEquals(createdChallenge.getInstruction(),
                updatedChallenge.getInstruction());
        Assertions.assertNotEquals(createdChallenge.getBlurb(), updatedChallenge.getBlurb());
        Assertions.assertNotEquals(createdChallenge.getDefaultPriority(),
                updatedChallenge.getDefaultPriority());
        Assertions.assertNotEquals(createdChallenge.getDifficulty(),
                updatedChallenge.getDifficulty());
        Assertions.assertNotEquals(createdChallenge.isEnabled(), updatedChallenge.isEnabled());
        Assertions.assertNotEquals(createdChallenge.isFeatured(), updatedChallenge.isFeatured());
        Assertions.assertNotEquals(createdChallenge.getCheckinComment(),
                updatedChallenge.getCheckinComment());
        Assertions.assertNotEquals(createdChallenge.getCheckinSource(),
                updatedChallenge.getCheckinSource());
        Assertions.assertNotEquals(createdChallenge.getName(), updatedChallenge.getName());
        Assertions.assertNotEquals(createdChallenge.getDefaultZoom(),
                updatedChallenge.getDefaultZoom());
        Assertions.assertNotEquals(createdChallenge.getMinZoom(), updatedChallenge.getMinZoom());
        Assertions.assertNotEquals(createdChallenge.getMaxZoom(), updatedChallenge.getMaxZoom());
        Assertions.assertNotEquals(createdChallenge.getDefaultBasemapId(),
                updatedChallenge.getDefaultBasemapId());
        Assertions.assertNotEquals(createdChallenge.getDefaultBasemap(),
                updatedChallenge.getDefaultBasemap());
        Assertions.assertNotEquals(createdChallenge.getCustomBasemap(),
                updatedChallenge.getCustomBasemap());
    }

    @Test
    public void findTest() throws MapRouletteException
    {
        final var prefix = "zzzzzzChallenge";
        final var challengeIdentifierList = new ArrayList<Long>(5);
        for (var challengeIndex = 0; challengeIndex < 5; challengeIndex++)
        {
            final var challenge = this.getChallengeAPI()
                    .create(Challenge.builder().parent(this.getDefaultProjectIdentifier())
                            .name(prefix + challengeIndex).instruction("Default Instruction")
                            .build());
            challengeIdentifierList.add(challenge.getId());
        }
        final var challenges = this.getChallengeAPI().find(prefix, -1, 10, 0);
        Assertions.assertEquals(5, challenges.size());
        challenges.forEach(challenge -> Assertions.assertTrue(
                StringUtils.startsWith(challenge.getName(), prefix),
                String.format("Starts with invalid name %s", challenge.getName())));
    }

    @Test
    public void childrenTest() throws MapRouletteException
    {
        final var parentIdentifier = this.getChallengeAPI()
                .create(Challenge.builder().parent(this.getDefaultProjectIdentifier())
                        .name("TestChallenge").instruction("Default Instruction").build())
                .getId();

        final var numberOfChildren = 5;
        final var taskList = new ArrayList<Long>();
        final var taskAPI = new TaskAPI(this.getConfiguration());
        for (var taskIndex = 0; taskIndex < numberOfChildren; taskIndex++)
        {
            final var task = taskAPI.create(Task.builder(parentIdentifier, "Task" + taskIndex)
                    .addGeojson(String.format(TestConstants.FEATURE_STRING, 5.4, 6.7, "TestValue"))
                    .build());
            taskList.add(task.getId());
        }

        final var tasks = this.getChallengeAPI().children(parentIdentifier, 10, 0);
        Assertions.assertEquals(numberOfChildren, tasks.size());
        tasks.forEach(task -> Assertions.assertTrue(taskList.contains(task.getId())));
    }

    private RuleList getRuleList(final String condition, final String value)
    {
        return RuleList.builder().condition(condition).rules(Collections.singletonList(
                PriorityRule.builder().operator("equals").type("string").value(value).build()))
                .build();
    }

    private void compareChallenges(final Challenge challenge1, final Challenge challenge2)
    {
        Assertions.assertEquals(challenge1.getName(), challenge2.getName());
        Assertions.assertEquals(challenge1.getParent(), challenge2.getParent());
        Assertions.assertEquals(challenge1.getInstruction(), challenge2.getInstruction());
        Assertions.assertEquals(challenge1.getDifficulty(), challenge2.getDifficulty());
        Assertions.assertEquals(challenge1.getDescription(), challenge2.getDescription());
        Assertions.assertEquals(challenge1.getBlurb(), challenge2.getBlurb());
        Assertions.assertEquals(challenge1.isEnabled(), challenge2.isEnabled());
        Assertions.assertEquals(challenge1.isFeatured(), challenge2.isFeatured());
        Assertions.assertEquals(challenge1.getCheckinComment(), challenge2.getCheckinComment());
        Assertions.assertEquals(challenge1.getCheckinSource(), challenge2.getCheckinSource());
        Assertions.assertEquals(challenge1.getDefaultPriority(), challenge2.getDefaultPriority());
        Assertions.assertEquals(challenge1.getHighPriorityRule().isSet(),
                challenge2.getHighPriorityRule().isSet());
        Assertions.assertEquals(challenge1.getMediumPriorityRule().isSet(),
                challenge2.getMediumPriorityRule().isSet());
        Assertions.assertEquals(challenge1.getLowPriorityRule().isSet(),
                challenge2.getLowPriorityRule().isSet());
        Assertions.assertEquals(challenge1.getDefaultZoom(), challenge2.getDefaultZoom());
        Assertions.assertEquals(challenge1.getMinZoom(), challenge2.getMinZoom());
        Assertions.assertEquals(challenge1.getMaxZoom(), challenge2.getMaxZoom());
        Assertions.assertEquals(challenge1.getDefaultBasemap(), challenge2.getDefaultBasemap());
        Assertions.assertEquals(challenge1.getDefaultBasemapId(), challenge2.getDefaultBasemapId());
        Assertions.assertEquals(challenge1.getCustomBasemap(), challenge2.getCustomBasemap());
    }

    private Challenge getBasicChallenge()
    {
        return Challenge.builder().name("challengeTest").instruction("TestInstruction")
                .description("Testing challenge creation").blurb("Testing challenge creation blurb")
                .difficulty(ChallengeDifficulty.EXPERT).parent(this.getDefaultProjectIdentifier())
                .build();
    }

    private Challenge getBasicChallengeForNewConfiguration()
    {
        return Challenge.builder().parent(1234).name("challengeTest").instruction("TestInstruction")
                .description("Testing challenge creation").blurb("Testing challenge creation blurb")
                .difficulty(ChallengeDifficulty.EXPERT).parent(this.getNewProjectIdentifier())
                .build();
    }
}
