package org.maproulette.client.serializer;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.model.Project;
import org.maproulette.client.utilities.ObjectMapperSingleton;

/**
 * Tests serialization and deserialization of the {@link Project} object
 *
 * @author mcuthbert
 */
public class ProjectSerializationTest
{
    @Test
    public void projectSerializationTest() throws IOException
    {
        final var mapper = ObjectMapperSingleton.getMapper();
        final var project = Project.builder().name("TestProject").description("TestDescription")
                .displayName("TestDisplayName").enabled(true).id(6875L).build();
        final var projectJson = mapper.writeValueAsString(project);
        final var deserializedProject = mapper.readValue(projectJson, Project.class);

        this.verifyProjects(project, deserializedProject);
    }

    @Test
    public void fromJsonTest() throws Exception
    {
        final var mapper = ObjectMapperSingleton.getMapper();
        final var project = Project.builder().name("TestProject").description("TestDescription")
                .displayName("TestDisplayName").enabled(true).id(6875L).build();
        final var projectJson = mapper.writeValueAsString(project);
        final var deserializedProject = Project.fromJson(projectJson);

        this.verifyProjects(project, deserializedProject);
    }

    private void verifyProjects(final Project project1, final Project project2)
    {
        Assertions.assertEquals(project1.getName(), project2.getName());
        Assertions.assertEquals(project1.getId(), project2.getId());
        Assertions.assertEquals(project1.getDescription(), project2.getDescription());
        Assertions.assertEquals(project1.getDisplayName(), project2.getDisplayName());
        Assertions.assertEquals(project1.isEnabled(), project2.isEnabled());
    }
}
