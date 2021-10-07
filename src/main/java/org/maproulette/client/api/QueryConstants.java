package org.maproulette.client.api;

/**
 * @author mcuthbert
 */
public final class QueryConstants
{
    // Query String parameters
    public static final String QUERY_PARAMETER_Q = "q";
    public static final String QUERY_PARAMETER_PARENT_IDENTIFIER = "parentId";
    public static final String QUERY_PARAMETER_LIMIT = "limit";
    public static final String QUERY_PARAMETER_PAGE = "page";
    public static final String QUERY_PARAMETER_ONLY_ENABLED = "onlyEnabled";
    // COMMON URIS
    public static final String API_VERSION = "/api/v2";
    public static final String URI_FIND = "s/find";
    // PROJECT URIS
    public static final String URI_PROJECT_GET_BY_NAME = API_VERSION + "/projectByName/%s";
    public static final String URI_PROJECT_BASE = API_VERSION + "/project/%s";
    public static final String URI_PROJECT_POST = API_VERSION + "/project";
    public static final String URI_PROJECT_FIND = URI_PROJECT_POST + URI_FIND;
    public static final String URI_PROJECT_CHILDREN = URI_PROJECT_BASE + "/challenges";
    // CHALLENGE URIS
    public static final String URI_CHALLENGE_GET_BY_NAME = API_VERSION + "/project/%d/challenge/%s";
    public static final String URI_CHALLENGE_BASE = API_VERSION + "/challenge/%s";
    public static final String URI_CHALLENGE_POST = API_VERSION + "/challenge";
    public static final String URI_CHALLENGE_FIND = URI_CHALLENGE_POST + URI_FIND;
    public static final String URI_CHALLENGE_CHILDREN = URI_CHALLENGE_BASE + "/tasks";
    // TASK URIS
    public static final String URI_TASK_GET_BY_NAME = API_VERSION + "/challenge/%d/task/%s";
    public static final String URI_TASK_BASE = API_VERSION + "/task/%s";
    public static final String URI_TASK_POST = API_VERSION + "/task";
    public static final String URI_TASK_FIND = URI_TASK_POST + URI_FIND;
    // USER URIS
    public static final String URI_USER_PUBLIC_BY_ID = API_VERSION + "/user/%d/public";
    // Flags
    public static final String FLAG_IMMEDIATE_DELETE = "immediate";

    private QueryConstants()
    {

    }
}
