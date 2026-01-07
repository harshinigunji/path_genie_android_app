package com.SIMATS.myapplication.network;

/**
 * API Configuration class for Education Stream Advisor App.
 * Contains all API endpoints and configuration constants.
 * 
 * All endpoints are database-driven - no static content.
 */
public class ApiConfig {

    // ========================================
    // 🔧 BASE CONFIGURATION
    // ========================================

    // TODO: Update this to your actual server URL before production
    private static final String BASE_URL = "http://10.0.2.2/education_stream_advisor_api/";
    // Use "http://10.0.2.2/" for Android Emulator (maps to localhost)
    // Use "http://YOUR_IP_ADDRESS/" for physical device testing
    // Use "https://yourdomain.com/" for production

    // Request timeout in milliseconds
    public static final int REQUEST_TIMEOUT_MS = 30000;

    // Retry policy
    public static final int MAX_RETRIES = 2;
    public static final float BACKOFF_MULTIPLIER = 1.0f;

    // ========================================
    // 🔐 AUTHENTICATION APIs
    // ========================================

    /** User registration - returns success message */
    public static final String REGISTER = BASE_URL + "register.php";

    /** User login - returns user_id and token on success */
    public static final String LOGIN = BASE_URL + "login.php";

    /** Logout - invalidates auth_token */
    public static final String LOGOUT = BASE_URL + "logout.php";

    /**
     * Login after Firebase auth - returns user_id and token (no password
     * verification)
     */
    public static final String LOGIN_FIREBASE = BASE_URL + "login_firebase.php";

    /** Forgot password - sends reset email */
    public static final String FORGOT_PASSWORD = BASE_URL + "forgot_password.php";

    /** Save FCM token for push notifications */
    public static final String SAVE_FCM_TOKEN = BASE_URL + "save_fcm_token.php";

    /** Get user profile */
    public static final String GET_PROFILE = BASE_URL + "get_profile.php";

    /** Update user profile */
    public static final String UPDATE_PROFILE = BASE_URL + "update_profile.php";

    // ========================================
    // 📚 EDUCATION LEVELS APIs
    // ========================================

    /** Get all education levels */
    public static final String GET_EDUCATION_LEVELS = BASE_URL + "get_education_levels.php";

    /** Get education level details by ID */
    public static final String GET_EDUCATION_LEVEL_DETAILS = BASE_URL + "get_education_level_details.php";

    // ========================================
    // 🎓 STREAMS APIs
    // ========================================

    /** Get streams by education level - respects education-stage validation */
    public static final String GET_STREAMS_BY_LEVEL = BASE_URL + "get_streams_by_level.php";

    /** Get stream details by ID */
    public static final String GET_STREAM_DETAILS = BASE_URL + "get_stream_details.php";

    /** Get next possible streams - follows stream_progression table */
    public static final String GET_NEXT_STREAMS = BASE_URL + "get_next_streams.php";

    /** Get all valid streams for user's current education */
    public static final String GET_VALID_STREAMS = BASE_URL + "get_valid_streams.php";

    // ========================================
    // 📝 ENTRANCE EXAMS APIs
    // ========================================

    /** Get exams by stream - follows stream_exams relationship */
    public static final String GET_EXAMS_BY_STREAM = BASE_URL + "get_exams_by_stream.php";

    /** Get exam details by ID */
    public static final String GET_EXAM_DETAILS = BASE_URL + "get_exam_details.php";

    /** Get all exams for an education level */
    public static final String GET_EXAMS_BY_LEVEL = BASE_URL + "get_exams_by_level.php";

    /** Save exam to user's saved list */
    public static final String SAVE_EXAM = BASE_URL + "save_exam.php";

    /** Remove exam from saved list */
    public static final String UNSAVE_EXAM = BASE_URL + "unsave_exam.php";

    /** Get user's saved exams */
    public static final String GET_SAVED_EXAMS = BASE_URL + "get_saved_exams.php";

    // ========================================
    // 💼 JOBS APIs
    // ========================================

    /** Get jobs by stream - follows stream_jobs relationship */
    public static final String GET_JOBS_BY_STREAM = BASE_URL + "get_jobs_by_stream.php";

    /** Get job details by ID */
    public static final String GET_JOB_DETAILS = BASE_URL + "get_job_details.php";

    /** Get all jobs as career targets */
    public static final String GET_ALL_JOBS = BASE_URL + "get_all_jobs.php";

    /** Search jobs by keyword */
    public static final String SEARCH_JOBS = BASE_URL + "search_jobs.php";

    // ========================================
    // 🗺️ ROADMAP APIs
    // ========================================

    /** Generate roadmap - creates dynamic path from current to target */
    public static final String GENERATE_ROADMAP = BASE_URL + "generate_roadmap.php";

    /** Get roadmap details with all steps */
    public static final String GET_ROADMAP_DETAILS = BASE_URL + "get_roadmap_details.php";

    /** Save roadmap to user's collection */
    public static final String SAVE_ROADMAP = BASE_URL + "save_roadmap.php";

    /** Remove roadmap from saved collection */
    public static final String UNSAVE_ROADMAP = BASE_URL + "unsave_roadmap.php";

    /** Get user's saved roadmaps */
    public static final String GET_SAVED_ROADMAPS = BASE_URL + "get_saved_roadmaps.php";

    /** Get roadmap steps by roadmap ID */
    public static final String GET_ROADMAP_STEPS = BASE_URL + "get_roadmap_steps.php";

    // ========================================
    // 🤖 AI CHAT APIs
    // ========================================

    /** Send message to AI assistant - context-aware */
    public static final String AI_SEND_MESSAGE = BASE_URL + "ai_send_message.php";

    /** Get chat history for user */
    public static final String AI_GET_HISTORY = BASE_URL + "ai_get_history.php";

    /** Clear chat history */
    public static final String AI_CLEAR_HISTORY = BASE_URL + "ai_clear_history.php";

    // ========================================
    // 🔧 HELPER METHODS
    // ========================================

    /**
     * Get the base URL for API calls.
     * 
     * @return Base URL string
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * Build URL with query parameter.
     * 
     * @param endpoint   The API endpoint
     * @param paramName  Parameter name
     * @param paramValue Parameter value
     * @return Complete URL with query string
     */
    public static String buildUrl(String endpoint, String paramName, String paramValue) {
        return endpoint + "?" + paramName + "=" + paramValue;
    }

    /**
     * Build URL with multiple query parameters.
     * 
     * @param endpoint The API endpoint
     * @param params   Key-value pairs of parameters
     * @return Complete URL with query string
     */
    public static String buildUrl(String endpoint, String... params) {
        if (params.length == 0 || params.length % 2 != 0) {
            return endpoint;
        }

        StringBuilder url = new StringBuilder(endpoint);
        url.append("?");

        for (int i = 0; i < params.length; i += 2) {
            if (i > 0) {
                url.append("&");
            }
            url.append(params[i]).append("=").append(params[i + 1]);
        }

        return url.toString();
    }
}
