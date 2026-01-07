package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.SIMATS.myapplication.network.VolleySingleton;
import com.SIMATS.myapplication.utils.RoadmapSessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * UserRoadmapPage Activity - Displays the generated roadmap timeline.
 */
public class UserRoadmapPage extends AppCompatActivity {

    private static final String TAG = "UserRoadmapPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private TextView journeyTitle;
    private LinearLayout stepsContainer;
    private TextView finalGoalTitle;
    private TextView finalGoalDescription;
    private TextView salaryText;
    private Button saveButton;
    private ProgressBar progressBar;

    // Data
    private int roadmapId = -1;
    private int userId = -1;
    private boolean isValidRoadmap = false; // Track if roadmap is valid for saving

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_roadmap_page);

        // Get roadmap ID from intent
        roadmapId = getIntent().getIntExtra("roadmap_id", -1);

        // Get user ID
        // Use SessionManager to get user ID consistently
        com.SIMATS.myapplication.utils.SessionManager sessionMgr = new com.SIMATS.myapplication.utils.SessionManager(this);
        userId = sessionMgr.getUserId();

        // Setup edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Fetch and display roadmap
        if (roadmapId > 0) {
            fetchRoadmap();
        } else {
            // Display from session (for preview before save)
            displayFromSession();
        }
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        journeyTitle = findViewById(R.id.journeyTitle);
        stepsContainer = findViewById(R.id.stepsContainer);
        finalGoalTitle = findViewById(R.id.finalGoalTitle);
        finalGoalDescription = findViewById(R.id.finalGoalDescription);
        salaryText = findViewById(R.id.salaryText);
        saveButton = findViewById(R.id.saveButton);

        // Initially hide save button until we confirm valid roadmap
        saveButton.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> {
            // Only save if roadmap is valid
            if (!isValidRoadmap) {
                Toast.makeText(this, "Cannot save invalid roadmap", Toast.LENGTH_SHORT).show();
                return;
            }

            // Clear session and navigate to success
            RoadmapSessionManager sessionManager = new RoadmapSessionManager(this);
            sessionManager.clearSession();

            Intent intent = new Intent(this, UserRoadmapSuccessPage.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchRoadmap() {
        progressBar.setVisibility(View.VISIBLE);

        String url = ApiConfig.getBaseUrl() + "get_user_roadmap.php?roadmap_id=" + roadmapId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        boolean status = response.getBoolean("status");
                        if (status) {
                            JSONObject data = response.getJSONObject("data");
                            displayRoadmap(data);
                        } else {
                            Toast.makeText(this, "Failed to load roadmap", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(this, "Error loading roadmap", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching roadmap: " + error.toString());
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void displayFromSession() {
        RoadmapSessionManager sessionManager = new RoadmapSessionManager(this);
        JSONArray steps = sessionManager.getStepsArray();
        JSONObject targetJob = sessionManager.getTargetJob();

        // Validate roadmap - must have steps AND a target job
        if (steps == null || steps.length() == 0 || targetJob == null) {
            isValidRoadmap = false;
            saveButton.setVisibility(View.GONE);
            Toast.makeText(this, "No valid roadmap to display", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Mark as valid and show save button
        isValidRoadmap = true;
        saveButton.setVisibility(View.VISIBLE);

        try {
            // Display steps
            displaySteps(steps);

            // Display final goal
            if (targetJob != null) {
                finalGoalTitle.setText(targetJob.optString("job_name", "Your Goal"));
                salaryText.setText("Avg. Package: " + targetJob.optString("salary", "Competitive"));
            }

            // Build title
            String title = buildJourneyTitle(steps, targetJob);
            journeyTitle.setText(title);

        } catch (Exception e) {
            Log.e(TAG, "Error displaying from session: " + e.getMessage());
        }
    }

    private void displayRoadmap(JSONObject roadmapData) {
        try {
            // Fetched roadmaps are already saved/valid, show button
            isValidRoadmap = true;
            saveButton.setVisibility(View.VISIBLE);
            // Set title
            String title = roadmapData.optString("title", "My Career Roadmap");
            journeyTitle.setText(title.replace(" to ", " to\n"));

            // Set final goal
            String targetJobName = roadmapData.optString("target_job_name", "Your Goal");
            String targetSalary = roadmapData.optString("target_salary", "Competitive");

            finalGoalTitle.setText(targetJobName);
            finalGoalDescription.setText("Target Role at a Top Company.");
            salaryText.setText("Avg. Package: " + targetSalary);

            // Display steps
            JSONArray steps = roadmapData.getJSONArray("steps");
            displaySteps(steps);

        } catch (Exception e) {
            Log.e(TAG, "Error displaying roadmap: " + e.getMessage());
        }
    }

    private void displaySteps(JSONArray steps) {
        stepsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        try {
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                String stepType = step.optString("step_type", "STREAM");
                String title = step.optString("title", "");
                String description = step.optString("description", "");

                // Skip JOB type as it's shown in final goal card
                if ("JOB".equals(stepType)) {
                    continue;
                }

                View stepView = inflater.inflate(R.layout.item_roadmap_step, stepsContainer, false);

                // Set step type label
                TextView stepTypeView = stepView.findViewById(R.id.stepType);
                stepTypeView.setText(getStepTypeLabel(stepType));

                // Set title
                TextView stepTitleView = stepView.findViewById(R.id.stepTitle);
                stepTitleView.setText(title);

                // Set description
                TextView stepDescView = stepView.findViewById(R.id.stepDescription);
                if (!description.isEmpty()) {
                    stepDescView.setText(description);
                } else {
                    stepDescView.setText(getDefaultDescription(stepType, title));
                }

                // Set icon
                ImageView stepIcon = stepView.findViewById(R.id.stepIcon);
                stepIcon.setImageResource(getStepIcon(stepType));

                // Hide connector line for last non-job step
                if (i == steps.length() - 1 || (i == steps.length() - 2
                        && "JOB".equals(steps.getJSONObject(steps.length() - 1).optString("step_type")))) {
                    View connectorLine = stepView.findViewById(R.id.connectorLine);
                    connectorLine.setVisibility(View.INVISIBLE);
                }

                stepsContainer.addView(stepView);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying steps: " + e.getMessage());
        }
    }

    private String getStepTypeLabel(String stepType) {
        switch (stepType) {
            case "EDUCATION_LEVEL":
                return "FOUNDATION";
            case "STREAM":
                return "EDUCATION";
            case "EXAM":
                return "ENTRANCE EXAM";
            case "EXPERIENCE":
                return "EXPERIENCE";
            case "JOB_PREP":
                return "JOB PREP";
            default:
                return stepType;
        }
    }

    private int getStepIcon(String stepType) {
        switch (stepType) {
            case "EDUCATION_LEVEL":
                return R.drawable.ic_education;
            case "STREAM":
                return R.drawable.ic_cap;
            case "EXAM":
                return R.drawable.ic_exam;
            case "EXPERIENCE":
                return R.drawable.ic_code;
            case "JOB_PREP":
                return R.drawable.ic_briefcase;
            default:
                return R.drawable.ic_stream;
        }
    }

    private String getDefaultDescription(String stepType, String title) {
        switch (stepType) {
            case "EDUCATION_LEVEL":
                return "Selected education level for career path.";
            case "STREAM":
                return "Focus area: " + title;
            case "EXAM":
                return "Entrance exam for higher education.";
            default:
                return "";
        }
    }

    private String buildJourneyTitle(JSONArray steps, JSONObject targetJob) {
        try {
            String firstStream = "";
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                if ("STREAM".equals(step.getString("step_type"))) {
                    firstStream = step.getString("title");
                    break;
                }
            }

            String jobName = targetJob != null ? targetJob.optString("job_name", "") : "";

            if (!firstStream.isEmpty() && !jobName.isEmpty()) {
                return firstStream + " to\n" + jobName;
            } else if (!firstStream.isEmpty()) {
                return firstStream + "\nJourney";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error building title: " + e.getMessage());
        }
        return "Your Career\nRoadmap";
    }
}

