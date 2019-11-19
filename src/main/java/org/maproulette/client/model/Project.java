package org.maproulette.client.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Very basic class defining the structure of the MapRoulette Project
 *
 * @author cuthbertm
 * @author nachtm
 */
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project implements IMapRouletteObject, Serializable
{
    private static final long serialVersionUID = 6310166267361870242L;
    @Setter
    @SuppressWarnings("checkstyle:MemberName")
    @Builder.Default
    private long id = -1;
    @NonNull
    private String name;
    private String description;
    private String displayName;
    @Builder.Default
    private boolean enabled = false;

    /**
     * Projects have no parents, so will automatically return -1
     *
     * @return -1 always
     */
    public long getParent()
    {
        return -1;
    }
}
