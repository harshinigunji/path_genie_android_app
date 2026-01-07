package com.SIMATS.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * RoadmapSessionManager - Manages roadmap steps during user navigation.
 * Steps are stored locally until user clicks "Generate Roadmap".
 */
public class RoadmapSessionManager {

    private static final String TAG = "RoadmapSessionManager";
    private static final String PREF_NAME = "RoadmapSession";
    private static final String KEY_STEPS = "roadmap_steps";
    private static final String KEY_TARGET_JOB = "target_job";
    private static final String KEY_TARGET_SALARY = "target_salary";

    private final SharedPreferences prefs;

    public RoadmapSessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Clear all session data - call when starting a new roadmap journey.
     */
    public void clearSession() {
        prefs.edit().clear().apply();
        Log.d(TAG, "Session cleared");
    }

    /**
     * Add a step to the roadmap session.
     * 
     * @param stepType    Type: EDUCATION_LEVEL, STREAM, EXAM, JOB
     * @param referenceId ID of the referenced item (stream_id, exam_id, etc.)
     * @param title       Display title
     * @param description Description text
     * @param icon        Icon resource name
     */
    public void addStep(String stepType, int referenceId, String title, String description, String icon) {
        try {
            JSONArray steps = getStepsArray();

            JSONObject step = new JSONObject();
            step.put("step_type", stepType);
            step.put("reference_id", referenceId);
            step.put("title", title);
            step.put("description", description);
            step.put("icon", icon);

            steps.put(step);
            saveSteps(steps);

            Log.d(TAG, "Added step: " + stepType + " - " + title);
        } catch (JSONException e) {
            Log.e(TAG, "Error adding step: " + e.getMessage());
        }
    }

    /**
     * Add an education level step.
     */
    public void addEducationLevel(int levelId, String levelName) {
        addStep("EDUCATION_LEVEL", levelId, levelName, "Selected education level", "ic_education");
    }

    /**
     * Add a stream step.
     */
    public void addStream(int streamId, String streamName, String description) {
        addStep("STREAM", streamId, streamName, description, "ic_stream");
    }

    /**
     * Insert an exam BEFORE the last stream step.
     * This ensures exams appear as prerequisites to their associated stream.
     */
    public void insertExamBeforeLastStream(int examId, String examName, String description) {
        try {
            JSONArray steps = getStepsArray();

            // Find the last STREAM step index
            int lastStreamIndex = -1;
            for (int i = steps.length() - 1; i >= 0; i--) {
                JSONObject step = steps.getJSONObject(i);
                if ("STREAM".equals(step.getString("step_type"))) {
                    lastStreamIndex = i;
                    break;
                }
            }

            // Create exam step
            JSONObject examStep = new JSONObject();
            examStep.put("step_type", "EXAM");
            examStep.put("reference_id", examId);
            examStep.put("title", examName);
            examStep.put("description", description);
            examStep.put("icon", "ic_exam");

            // Insert before last stream or at current position
            if (lastStreamIndex >= 0) {
                // Rebuild array with exam inserted before last stream
                JSONArray newSteps = new JSONArray();
                for (int i = 0; i < steps.length(); i++) {
                    if (i == lastStreamIndex) {
                        newSteps.put(examStep);
                    }
                    newSteps.put(steps.getJSONObject(i));
                }
                saveSteps(newSteps);
            } else {
                // No stream found, just append
                steps.put(examStep);
                saveSteps(steps);
            }

            Log.d(TAG, "Inserted exam: " + examName);
        } catch (JSONException e) {
            Log.e(TAG, "Error inserting exam: " + e.getMessage());
        }
    }

    /**
     * Add an exam step after education level (for level exams like NMMS, NTSE).
     */
    public void addExamAfterEducationLevel(int examId, String examName, String description) {
        try {
            JSONArray steps = getStepsArray();

            // Find the education level step index
            int eduLevelIndex = -1;
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                if ("EDUCATION_LEVEL".equals(step.getString("step_type"))) {
                    eduLevelIndex = i;
                    break;
                }
            }

            // Create exam step
            JSONObject examStep = new JSONObject();
            examStep.put("step_type", "EXAM");
            examStep.put("reference_id", examId);
            examStep.put("title", examName);
            examStep.put("description", description);
            examStep.put("icon", "ic_exam");

            // Insert after education level
            if (eduLevelIndex >= 0) {
                JSONArray newSteps = new JSONArray();
                for (int i = 0; i < steps.length(); i++) {
                    newSteps.put(steps.getJSONObject(i));
                    if (i == eduLevelIndex) {
                        newSteps.put(examStep);
                    }
                }
                saveSteps(newSteps);
            } else {
                // No education level found, just append
                steps.put(examStep);
                saveSteps(steps);
            }

            Log.d(TAG, "Added exam after education: " + examName);
        } catch (JSONException e) {
            Log.e(TAG, "Error adding exam: " + e.getMessage());
        }
    }

    /**
     * Set the target job (final goal).
     */
    public void setTargetJob(int jobId, String jobName, String salary) {
        try {
            JSONObject job = new JSONObject();
            job.put("job_id", jobId);
            job.put("job_name", jobName);
            job.put("salary", salary);
            prefs.edit().putString(KEY_TARGET_JOB, job.toString()).apply();

            // Also add as final step
            addStep("JOB", jobId, jobName, "Target Role", "ic_briefcase");

            Log.d(TAG, "Set target job: " + jobName);
        } catch (JSONException e) {
            Log.e(TAG, "Error setting target job: " + e.getMessage());
        }
    }

    /**
     * Get target job info.
     */
    public JSONObject getTargetJob() {
        try {
            String jobStr = prefs.getString(KEY_TARGET_JOB, null);
            if (jobStr != null) {
                return new JSONObject(jobStr);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error getting target job: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all steps as JSONArray.
     */
    public JSONArray getStepsArray() {
        try {
            String stepsStr = prefs.getString(KEY_STEPS, "[]");
            return new JSONArray(stepsStr);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    /**
     * Get the count of steps.
     */
    public int getStepsCount() {
        return getStepsArray().length();
    }

    /**
     * Check if session has any steps.
     */
    public boolean hasSteps() {
        return getStepsCount() > 0;
    }

    /**
     * Save steps array to preferences.
     */
    private void saveSteps(JSONArray steps) {
        prefs.edit().putString(KEY_STEPS, steps.toString()).apply();
    }

    /**
     * Get the hierarchy level of a step type.
     * Higher number = later in the flow.
     */
    private int getStepLevel(String stepType) {
        switch (stepType) {
            case "EDUCATION_LEVEL":
                return 1;
            case "STREAM":
                return 2;
            case "EXAM":
                return 3;
            case "JOB":
                return 4;
            default:
                return 0;
        }
    }

    /**
     * Clear all steps from a given type onwards.
     * Used when user navigates back and makes a different selection.
     * E.g., clearStepsFromType("STREAM") removes all STREAM, EXAM, and JOB steps.
     */
    public void clearStepsFromType(String stepType) {
        try {
            int clearLevel = getStepLevel(stepType);
            JSONArray steps = getStepsArray();
            JSONArray newSteps = new JSONArray();

            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                String type = step.getString("step_type");
                if (getStepLevel(type) < clearLevel) {
                    newSteps.put(step);
                }
            }

            saveSteps(newSteps);

            // Also clear target job if clearing from STREAM or earlier
            if (clearLevel <= 2) {
                prefs.edit().remove(KEY_TARGET_JOB).apply();
            }

            Log.d(TAG, "Cleared steps from type: " + stepType + ", remaining: " + newSteps.length());
        } catch (JSONException e) {
            Log.e(TAG, "Error clearing steps from type: " + e.getMessage());
        }
    }

    /**
     * Remove all steps of a specific type only.
     */
    public void removeStepsOfType(String stepType) {
        try {
            JSONArray steps = getStepsArray();
            JSONArray newSteps = new JSONArray();

            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                String type = step.getString("step_type");
                if (!type.equals(stepType)) {
                    newSteps.put(step);
                }
            }

            saveSteps(newSteps);

            // Also clear target job if removing JOB type
            if ("JOB".equals(stepType)) {
                prefs.edit().remove(KEY_TARGET_JOB).apply();
            }

            Log.d(TAG, "Removed steps of type: " + stepType);
        } catch (JSONException e) {
            Log.e(TAG, "Error removing steps of type: " + e.getMessage());
        }
    }
}
