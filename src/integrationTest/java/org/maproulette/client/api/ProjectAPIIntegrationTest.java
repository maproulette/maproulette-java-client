package org.maproulette.client.api;

import static org.maproulette.client.utilities.ThrowingConsumer.throwingConsumerWrapper;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.IntegrationBase;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.Challenge;
import org.maproulette.client.model.Project;

/**
 * Tests the project service API
 *
 * @author mcuthbert
 */
public class ProjectAPIIntegrationTest extends IntegrationBase
{
    @Test
    public void basicAPINewConfigurationTest() throws MapRouletteException
    {
        final var project = this.buildProject();
        final var projectIdentifier = this.getProjectAPIForNewConfiguration().create(project)
                .getId();
        Assertions.assertNotEquals(-1, projectIdentifier);
    }

    @Test
    public void basicAPITest() throws MapRouletteException
    {
        final var project = this.getDefaultProject();
        final var identifier = this.getProjectAPI().create(project).getId();
        Assertions.assertNotEquals(-1, identifier);

        // Test basic retrieval functionality
        final var retrievedProject = this.getProjectAPI().get(identifier);
        Assertions.assertTrue(retrievedProject.isPresent());
        this.compare(project, retrievedProject.get());

        // Test retrieval by name
        final var retrievedNameProject = this.getProjectAPI().get(project.getName());
        Assertions.assertTrue(retrievedNameProject.isPresent());
        this.compare(project, retrievedNameProject.get());

        // Test deletion
        Assertions.assertTrue(this.getProjectAPI().delete(identifier));
        Assertions.assertTrue(this.getProjectAPI().get(identifier).isPresent());

        // Force the deletion of the object
        Assertions.assertTrue(this.getProjectAPI().forceDelete(identifier));
        Assertions.assertTrue(this.getProjectAPI().get(identifier).isEmpty());
    }

    @Test
    public void updateTest() throws MapRouletteException
    {
        final var project = this.getDefaultProject();
        final var createdProject = this.getProjectAPI().create(project);

        this.compare(project, createdProject);
        final var updateProject = createdProject.toBuilder().name("UpdatedProjectName")
                .description("UpdatedDescription").displayName("UpdatedDisplayName").enabled(false)
                .build();
        final var updatedProject = this.getProjectAPI().update(updateProject);
        this.compare(updatedProject, updatedProject);

        this.getProjectAPI().forceDelete(updatedProject.getId());
    }

    @Test
    public void findTest() throws MapRouletteException
    {
        final var prefix = "zzzzzz";
        final var projectIdentifierList = new ArrayList<Long>(5);
        for (var projectIndex = 0; projectIndex < 5; projectIndex++)
        {
            final var project = this.getProjectAPI()
                    .create(Project.builder().name(prefix + "Project" + projectIndex).build());
            projectIdentifierList.add(project.getId());
        }
        final var projects = this.getProjectAPI().find(prefix + "Project", -1, 10, 0);
        Assertions.assertEquals(5, projects.size());
        projects.forEach(project -> Assertions.assertTrue(
                StringUtils.startsWith(project.getName(), prefix + "Project"),
                String.format("Starts with invalid name %s", project.getName())));

        // clean up the projects
        projectIdentifierList.forEach(throwingConsumerWrapper(this.getProjectAPI()::forceDelete));
    }

    @Test
    public void childrenTest() throws MapRouletteException
    {
        final var parentIdentifier = this.getProjectAPI()
                .create(Project.builder().name("ZZZTestProject").build()).getId();

        final var numberOfChildren = 5;
        final var challengeList = new ArrayList<Long>();
        final var challengeAPI = new ChallengeAPI(this.getConfiguration());
        for (var challengeIndex = 0; challengeIndex < numberOfChildren; challengeIndex++)
        {
            final var challenge = challengeAPI.create(Challenge.builder().parent(parentIdentifier)
                    .name("Challenge" + challengeIndex).instruction("Default Instruction").build());
            challengeList.add(challenge.getId());
        }

        final var challenges = this.getProjectAPI().children(parentIdentifier, 10, 0);
        Assertions.assertEquals(numberOfChildren, challenges.size());
        challenges.forEach(
                challenge -> Assertions.assertTrue(challengeList.contains(challenge.getId())));
    }

    private void compare(final Project project1, final Project project2)
    {
        Assertions.assertEquals(project1.getName(), project2.getName());
        Assertions.assertEquals(project1.getDescription(), project2.getDescription());
        Assertions.assertEquals(project1.getDisplayName(), project2.getDisplayName());
        Assertions.assertEquals(project1.isEnabled(), project2.isEnabled());
    }
}
