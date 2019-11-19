package org.maproulette.client.serializer;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.maproulette.client.model.Project;

import com.fasterxml.jackson.databind.ObjectMapper;

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
        final var mapper = new ObjectMapper();
        final var project = Project.builder().name("TestProject").description("TestDescription")
                .displayName("TestDisplayName").enabled(true).id(6875L).build();
        final var projectJson = mapper.writeValueAsString(project);
        final var deserializedProject = mapper.readValue(projectJson, Project.class);

        Assertions.assertEquals(project.getName(), deserializedProject.getName());
        Assertions.assertEquals(project.getId(), deserializedProject.getId());
        Assertions.assertEquals(project.getDescription(), deserializedProject.getDescription());
        Assertions.assertEquals(project.getDisplayName(), deserializedProject.getDisplayName());
        Assertions.assertEquals(project.isEnabled(), deserializedProject.isEnabled());
    }
}
