package org.maproulette.client.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author mcuthbert
 */
public class ProjectTest
{
    @Test
    public void projectBuilderDefaultTest()
    {
        final var project = Project.builder().name("ProjectName").build();

        Assertions.assertEquals(-1, project.getId());
        Assertions.assertEquals(-1, project.getParent());
        Assertions.assertEquals("ProjectName", project.getName());
        Assertions.assertNull(project.getDisplayName());
        Assertions.assertNull(project.getDescription());
        Assertions.assertFalse(project.isEnabled());
    }
}
