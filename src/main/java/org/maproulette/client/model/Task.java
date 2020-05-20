package org.maproulette.client.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.exception.MapRouletteRuntimeException;
import org.maproulette.client.utilities.Utilities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A task is a single unit of work in the MapRoulette Challenge
 *
 * @author cuthbertm
 * @author mgostintsev
 */
@Builder(builderMethodName = "taskBuilder", toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task implements IMapRouletteObject, Serializable
{
    /**
     * The task builder customizes the builder object to hide some of the details away from the
     * user.
     */
    public static class TaskBuilder
    {
        private static final String TASK_TYPE = "type";
        private static final String TASK_FEATURES = "features";
        private static final String TASK_FEATURE_COORDINATES = "coordinates";
        private static final String TASK_FEATURE_GEOMETRY = "geometry";
        private static final String KEY_DESCRIPTION = "description";
        private static final String TASK_FEATURE_PROPERTIES = "properties";
        private static final String FEATURE = "feature";
        private static final String POINT = "point";
        private final ObjectMapper mapper = new ObjectMapper();
        private final Set<PointInformation> points = new HashSet<>();
        private ArrayNode geoJson = this.mapper.createArrayNode();

        public TaskBuilder locationGeojson(final String geojson)
        {
            try
            {
                this.location = this.mapper.readTree(geojson);
            }
            catch (final IOException e)
            {
                throw new MapRouletteRuntimeException(e);
            }
            return this;
        }

        public TaskBuilder addPoint(final PointInformation point)
        {
            this.points.add(point);
            return this;
        }

        public TaskBuilder addPoints(final List<PointInformation> points)
        {
            this.points.addAll(points);
            return this;
        }

        public TaskBuilder addGeojson(final String geojson)
        {
            try
            {
                this.geoJson.add(this.mapper.readTree(geojson));
            }
            catch (final IOException e)
            {
                throw new MapRouletteRuntimeException(e);
            }
            return this;
        }

        public TaskBuilder resetGeometry()
        {
            this.geoJson = this.mapper.createArrayNode();
            this.geometries = null;
            return this;
        }

        public TaskBuilder addGeojson(final List<String> geojson)
        {
            geojson.forEach(this.geoJson::add);
            return this;
        }

        public Task build()
        {
            try
            {
                if (this.geometries == null)
                {
                    this.geometries(this.buildGeometries());
                }
            }
            catch (final MapRouletteException e)
            {
                throw new MapRouletteRuntimeException(e);
            }
            // make sure that the default is set correctly
            if (this.id < 1)
            {
                this.id(-1);
            }
            if (this.parent < 1)
            {
                this.parent(-1);
            }
            return new Task(this.id, this.parent, this.name, this.instruction, this.location,
                    this.status, this.priority, this.geometries);
        }

        protected ArrayNode generateTaskFeatures(final Set<PointInformation> source,
                final ArrayNode geoJson) throws MapRouletteException
        {
            final var features = this.mapper.createArrayNode();
            if (source.isEmpty() && geoJson.size() == 0)
            {
                throw new MapRouletteException(String
                        .format("Could not find any features for the task [%s].", this.toString()));
            }
            source.forEach(point ->
            {
                final var feature = this.mapper.createObjectNode();
                final var geometry = this.mapper.createObjectNode();
                final var coordinates = this.mapper.createArrayNode();
                coordinates.add(point.getLongitude());
                coordinates.add(point.getLatitude());
                geometry.put(TASK_TYPE, POINT);
                geometry.set(TASK_FEATURE_COORDINATES, coordinates);
                feature.set(TASK_FEATURE_GEOMETRY, geometry);
                feature.put(TASK_TYPE, FEATURE);
                final var pointInformation = this.mapper.createObjectNode();
                if (StringUtils.isEmpty(point.getDescription()))
                {
                    pointInformation.put(KEY_DESCRIPTION, point.getDescription());
                }
                feature.set(TASK_FEATURE_PROPERTIES, pointInformation);
                features.add(feature);
            });

            if (geoJson != null)
            {
                geoJson.forEach(features::add);
            }

            return features;
        }

        private TaskBuilder location(final JsonNode value)
        {
            this.location = value;
            return this;
        }

        private TaskBuilder geometries(final JsonNode value)
        {
            this.geometries = value;
            return this;
        }

        private JsonNode buildGeometries() throws MapRouletteException
        {
            final var result = this.mapper.createObjectNode();
            result.set(TASK_FEATURES, generateTaskFeatures(this.points, this.geoJson));
            return result;
        }
    }

    private static final int CONSTANT_HASHCODE = 31;
    private static final long serialVersionUID = 3111348272637323920L;
    @SuppressWarnings("checkstyle:memberName")
    private long id;
    private long parent;
    private String name;
    private String instruction;
    private JsonNode location;
    private TaskStatus status;
    private ChallengePriority priority;
    private JsonNode geometries;

    public static TaskBuilder builder(final long parentIdentifier, final String name)
    {
        return taskBuilder().parent(parentIdentifier).name(name);
    }

    public static Task fromJson(final String json) throws MapRouletteException
    {
        return Utilities.fromJson(json, Task.class);
    }

    public TaskBuilder toBuilder(final boolean resetGeometry)
    {
        if (resetGeometry)
        {
            return this.toBuilder().resetGeometry();
        }
        else
        {
            return this.toBuilder();
        }
    }

    /**
     * What defines a task as unique is its task identifier and its challenge name. So even if the
     * geometry or description or other member variables are different, it will be defined as equal
     * if those two values are equal
     *
     * @param obj
     *            The object to compare it against
     * @return whether it matches the supplied object
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof Task)
        {
            final var task2 = (Task) obj;
            if (this.getId() == -1 || task2.getId() == -1)
            {
                return StringUtils.equals(this.name, task2.getName())
                        && this.getParent() == task2.getParent();
            }
            else if (this.getId() == task2.getId())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        if (this.id != -1)
        {
            return Long.hashCode(this.id);
        }
        else
        {
            final var result = CONSTANT_HASHCODE + this.name.hashCode();
            return CONSTANT_HASHCODE * result + Long.hashCode(this.parent);
        }
    }
}
