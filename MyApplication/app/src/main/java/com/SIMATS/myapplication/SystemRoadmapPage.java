package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
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

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * SystemRoadmapPage - Displays the generated roadmap layout matching
 * UserRoadmapPage.
 */
public class SystemRoadmapPage extends AppCompatActivity {

    private static final String TAG = "SystemRoadmapPage";

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
    private int roadmapId;
    private String jobName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_system_roadmap_page);

        // Get data from intent
        roadmapId = getIntent().getIntExtra("roadmap_id", 0);
        jobName = getIntent().getStringExtra("job_name");
        if (jobName == null)
            jobName = "your dream role";

        // Setup edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        fetchRoadmapSteps();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        journeyTitle = findViewById(R.id.journeyTitle);
        stepsContainer = findViewById(R.id.stepsContainer);
        finalGoalTitle = findViewById(R.id.finalGoalTitle);
        finalGoalDescription = findViewById(R.id.finalGoalDescription);
        salaryText = findViewById(R.id.salaryText);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);

        // Hide save button initially
        saveButton.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        saveButton.setOnClickListener(v -> {
            // Navigate to saved page
            Intent intent = new Intent(this, SystemRoadmapSavedPage.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchRoadmapSteps() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);

        String url = ApiConfig.getBaseUrl() + "get_roadmap.php?roadmap_id=" + roadmapId;
        Log.d(TAG, "Fetching from: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Response: " + response.toString());
                    try {
                        boolean success = response.optBoolean("status", false);
                        if (success) {
                            JSONArray steps = response.optJSONArray("data");
                            if (steps != null && steps.length() > 0) {
                                displayTimeline(steps);
                            } else {
                                Toast.makeText(this, "No steps found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String message = response.optString("message", "Failed to load roadmap");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage(), e);
                        Toast.makeText(this, "Error loading roadmap", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Network error: " + error.getMessage(), error);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void displayTimeline(JSONArray steps) {
        stepsContainer.removeAllViews();
        saveButton.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(this);

        try {
            // Find target job step data (usually the last one or marked "JOB")
            JSONObject jobStep = null;
            String computedFirstStream = "";

            // Determine if job step exists appropriately
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                String stepType = step.optString("step_type", "STREAM").toUpperCase();
                if ("JOB".equals(stepType)) {
                    jobStep = step;
                }
                if ("STREAM".equals(stepType) && computedFirstStream.isEmpty()) {
                    computedFirstStream = step.optString("title", "");
                }
            }

            // Set Title
            String displayJobName = jobStep != null ? jobStep.optString("title", jobName) : jobName;
            if (!computedFirstStream.isEmpty()) {
                journeyTitle.setText(computedFirstStream + " to\n" + displayJobName);
            } else {
                journeyTitle.setText("Your Career\nRoadmap");
            }

            // Set Final Goal Card Data
            if (jobStep != null) {
                finalGoalTitle.setText(displayJobName);
                finalGoalDescription.setText(jobStep.optString("description", "Target Role at a Top Company."));
                // Attempt to parse salary/package from description if not separate field?
                // Or use default. System page logic usually has rigid structure.
                // get_roadmap.php probably returns title/description/icon.
                // We'll use a generic placeholder if salary not found.
                salaryText.setText("Avg. Package: Competitive");
            } else {
                finalGoalTitle.setText(jobName);
            }

            // Loop and add steps (Exclude JOB)
            int itemCount = steps.length();
            for (int i = 0; i < itemCount; i++) {
                JSONObject step = steps.getJSONObject(i);
                String stepType = step.optString("step_type", "STREAM").toUpperCase();
                String title = step.optString("title", "");
                String description = step.optString("description", "");

                if ("JOB".equals(stepType))
                    continue;

                View stepView = inflater.inflate(R.layout.item_roadmap_step, stepsContainer, false);

                // Set step type
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

                // Handle connector line
                // Logic: Hide if last step OR if it's the second to last step and the last step
                // is JOB (since we moved JOB out)
                boolean isActuallyLast = (i == itemCount - 1);
                boolean isLastBeforeJob = (i == itemCount - 2
                        && "JOB".equals(steps.getJSONObject(i + 1).optString("step_type")));

                if (isActuallyLast || isLastBeforeJob) {
                    View connectorLine = stepView.findViewById(R.id.connectorLine);
                    if (connectorLine != null)
                        connectorLine.setVisibility(View.INVISIBLE);
                }

                // Handle Multiple Exams Display
                LinearLayout examsContainer = stepView.findViewById(R.id.examsContainer);
                if ("EXAM".equals(stepType) && title.contains(" or ")) {
                    // Hide main title since we will show individual items
                    stepTitleView.setVisibility(View.GONE);
                    examsContainer.setVisibility(View.VISIBLE);
                    examsContainer.removeAllViews();

                    String[] exams = title.split(" or ");
                    for (int j = 0; j < exams.length; j++) {
                        String examName = exams[j].trim();

                        // Create a card for each exam
                        TextView examView = new TextView(this);
                        examView.setText(examName);
                        examView.setTextSize(14);
                        examView.setTextColor(0xFF1F2937);
                        examView.setTypeface(null, android.graphics.Typeface.BOLD);
                        examView.setBackgroundResource(R.drawable.bg_exam_item);

                        int padding = (int) (10 * getResources().getDisplayMetrics().density);
                        examView.setPadding(padding, padding, padding, padding);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (j > 0) {
                            params.topMargin = (int) (8 * getResources().getDisplayMetrics().density);

                            // Add "OR" label between them
                            TextView orText = new TextView(this);
                            orText.setText("OR");
                            orText.setTextSize(10);
                            orText.setTextColor(0xFF9CA3AF);
                            orText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); // Center text
                            orText.setGravity(android.view.Gravity.CENTER);
                            LinearLayout.LayoutParams orParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            orParams.topMargin = (int) (4 * getResources().getDisplayMetrics().density);
                            orParams.bottomMargin = (int) (4 * getResources().getDisplayMetrics().density);
                            orText.setLayoutParams(orParams);
                            examsContainer.addView(orText);
                        }

                        examView.setLayoutParams(params);
                        examsContainer.addView(examView);
                    }
                } else {
                    stepTitleView.setVisibility(View.VISIBLE);
                    examsContainer.setVisibility(View.GONE);
                }

                stepsContainer.addView(stepView);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error building timeline: " + e.getMessage());
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
}
