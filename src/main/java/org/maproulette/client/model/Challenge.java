package org.maproulette.client.model;

import java.io.Serializable;

import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.utilities.Utilities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * An Java object representing a MapRoulette Challenge object
 *
 * @author cuthbertm
 */
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Challenge implements IMapRouletteObject, Serializable
{
    private static final long serialVersionUID = -8034692909431083341L;
    private static final int DEFAULT_ZOOM = 13;
    private static final int MINIMUM_ZOOM = 1;
    private static final int MAXIMUM_ZOOM = 19;
    @SuppressWarnings("checkstyle:memberName")
    @Builder.Default
    private long id = -1;
    private long parent;
    @NonNull
    private String instruction;
    @Builder.Default
    private ChallengeDifficulty difficulty = ChallengeDifficulty.NORMAL;
    private String blurb;
    @Builder.Default
    private boolean enabled = false;
    private String description;
    @Builder.Default
    private boolean featured = false;
    @Builder.Default
    private String checkinComment = "";
    @Builder.Default
    private String checkinSource = "";
    @NonNull
    private String name;

    @Builder.Default
    private ChallengePriority defaultPriority = ChallengePriority.MEDIUM;
    @Builder.Default
    private RuleList highPriorityRule = RuleList.builder().build();
    @Builder.Default
    private RuleList mediumPriorityRule = RuleList.builder().build();
    @Builder.Default
    private RuleList lowPriorityRule = RuleList.builder().build();

    @Builder.Default
    private int defaultZoom = DEFAULT_ZOOM;
    @Builder.Default
    private int minZoom = MINIMUM_ZOOM;
    @Builder.Default
    private int maxZoom = MAXIMUM_ZOOM;
    private Integer defaultBasemap;
    private String defaultBasemapId;
    private String customBasemap;
    private String preferredTags;
    private String preferredReviewTags;
    private String[] tags;
    @Builder.Default
    private boolean changesetUrl = false;

    public static Challenge fromJson(final String json) throws MapRouletteException
    {
        return Utilities.fromJson(json, Challenge.class);
    }
}
