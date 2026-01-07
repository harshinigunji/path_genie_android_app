package com.SIMATS.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.SIMATS.myapplication.network.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import com.SIMATS.myapplication.utils.RoadmapSessionManager;

/**
 * JobDetailsPage Activity - Displays detailed information about a job.
 * Fetches data from job_details.php API based on job_id.
 */
public class JobDetailsPage extends AppCompatActivity {

    private static final String TAG = "JobDetailsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private ProgressBar progressBar;
    private ScrollView contentScroll;
    private TextView jobTypeBadge;
    private ImageView jobTypeIcon;
    private TextView jobName;
    private TextView jobSubtitle;
    private TextView overviewText;
    private LinearLayout responsibilitiesContainer;
    private TextView requiredEducation;
    private TextView educationDetails;
    private GridLayout skillsContainer;
    private TextView careerGrowthText;
    private LinearLayout careerPathContainer;
    private Button bottomButton;

    // Data
    private int jobId = -1;
    private int streamId = -1;
    private String streamName = "";
    private String currentJobName = "";
    private String currentSalary = "";
    private RoadmapSessionManager roadmapSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job_details_page);

        // Get data from intent
        jobId = getIntent().getIntExtra("job_id", -1);
        streamId = getIntent().getIntExtra("stream_id", -1);
        streamName = getIntent().getStringExtra("stream_name");

        if (jobId == -1) {
            Toast.makeText(this, "Invalid job", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (streamName == null)
            streamName = "";

        // Setup edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initViews();

        // Initialize session manager
        roadmapSessionManager = new RoadmapSessionManager(this);

        // Setup click listeners
        setupClickListeners();

        // Fetch job details from API
        fetchJobDetails();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        contentScroll = findViewById(R.id.contentScroll);
        jobTypeBadge = findViewById(R.id.jobTypeBadge);
        jobTypeIcon = findViewById(R.id.jobTypeIcon);
        jobName = findViewById(R.id.jobName);
        jobSubtitle = findViewById(R.id.jobSubtitle);
        overviewText = findViewById(R.id.overviewText);
        responsibilitiesContainer = findViewById(R.id.responsibilitiesContainer);
        requiredEducation = findViewById(R.id.requiredEducation);
        educationDetails = findViewById(R.id.educationDetails);
        skillsContainer = findViewById(R.id.skillsContainer);
        careerGrowthText = findViewById(R.id.careerGrowthText);
        careerPathContainer = findViewById(R.id.careerPathContainer);
        bottomButton = findViewById(R.id.bottomButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        bottomButton.setOnClickListener(v -> {
            // Set target job and generate roadmap
            generateRoadmap();
        });
    }

    /**
     * Generate roadmap with this job as target.
     */
    private void generateRoadmap() {
        // Clear any previous job selection before setting new one
        roadmapSessionManager.removeStepsOfType("JOB");

        // Set the target job in session
        roadmapSessionManager.setTargetJob(jobId, currentJobName, currentSalary);

        // Navigate to generate page
        Intent intent = new Intent(this, UserGeneratePage.class);
        startActivity(intent);
    }

    /**
     * Fetch job details from API.
     */
    private void fetchJobDetails() {
        progressBar.setVisibility(View.VISIBLE);
        contentScroll.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "job_details.php?job_id=" + jobId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    contentScroll.setVisibility(View.VISIBLE);
                    try {
                        boolean status = response.getBoolean("status");
                        if (status) {
                            JSONObject job = response.getJSONObject("data");
                            displayJobDetails(job);
                        } else {
                            String message = response.optString("message", "Failed to fetch job details");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(this, "Error loading job details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching job details: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Display job details from API response.
     */
    private void displayJobDetails(JSONObject job) {
        try {
            // Job name
            currentJobName = job.getString("job_name");
            jobName.setText(currentJobName);

            // Capture salary for roadmap
            currentSalary = job.optString("salary_range", "Competitive Salary");

            // Job type
            String jobType = job.optString("job_type", "Private").toLowerCase();
            if (jobType.contains("government") || jobType.contains("govt") || jobType.contains("public")) {
                jobTypeBadge.setText("GOVERNMENT");
                jobTypeIcon.setImageResource(R.drawable.ic_building);
            } else {
                jobTypeBadge.setText("PRIVATE");
                jobTypeIcon.setImageResource(R.drawable.ic_briefcase);
            }

            // Job subtitle
            String subtitle = generateSubtitle(currentJobName, jobType);
            jobSubtitle.setText(subtitle);

            // Description / Overview
            String description = job.optString("description", "");
            if (!description.isEmpty()) {
                overviewText.setText(description);
            } else {
                overviewText.setText("Details about this position will be available soon.");
            }

            // Responsibilities - generate from description or use defaults
            displayResponsibilities(description, jobType);

            // Required education
            String education = job.optString("required_education", "");
            if (!education.isEmpty()) {
                requiredEducation.setText(education);
                educationDetails.setText("Relevant specialization required");
            } else {
                requiredEducation.setText(streamName.isEmpty() ? "Bachelor's Degree" : streamName);
                educationDetails.setText("Relevant educational background");
            }

            // Required exams (for skills)
            String requiredExams = job.optString("required_exams", "");
            displaySkills(requiredExams, jobType);

            // Career growth
            String careerGrowth = job.optString("career_growth", "");
            if (!careerGrowth.isEmpty()) {
                careerGrowthText.setText(careerGrowth);
            } else {
                careerGrowthText.setText(generateDefaultCareerGrowth(jobType));
            }

            // Career path
            displayCareerPath(currentJobName, jobType);

        } catch (Exception e) {
            Log.e(TAG, "Error displaying job: " + e.getMessage());
        }
    }

    /**
     * Generate subtitle for job.
     */
    private String generateSubtitle(String name, String jobType) {
        String lower = name.toLowerCase();
        if (lower.contains("ies")) {
            return "Indian Engineering Services (Class A)";
        } else if (lower.contains("psu") || lower.contains("gate")) {
            return "Public Sector Undertaking";
        } else if (lower.contains("rrb") || lower.contains("railway")) {
            return "Indian Railways Department";
        } else if (lower.contains("software")) {
            return "Information Technology Sector";
        } else if (lower.contains("design")) {
            return "Core Engineering & Manufacturing";
        }
        return jobType.contains("govt") ? "Government Position" : "Private Sector";
    }

    /**
     * Display responsibilities as bullet points.
     */
    private void displayResponsibilities(String description, String jobType) {
        responsibilitiesContainer.removeAllViews();

        // Generate default responsibilities based on job type
        if (jobType.contains("govt") || jobType.contains("government")) {
            addResponsibility("Managerial and technical supervision of large-scale government projects.");
            addResponsibility("Policy formulation and implementation for national infrastructure.");
            addResponsibility("Handling administration and public dealing in technical sectors.");
        } else {
            addResponsibility("Technical execution and project delivery.");
            addResponsibility("Collaboration with cross-functional teams.");
            addResponsibility("Continuous learning and skill development.");
        }
    }

    /**
     * Add a responsibility bullet point.
     */
    private void addResponsibility(String text) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = dpToPx(8);
        row.setLayoutParams(rowParams);

        // Check mark
        ImageView check = new ImageView(this);
        check.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(18), dpToPx(18)));
        check.setImageResource(R.drawable.ic_check_green);

        // Text
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.leftMargin = dpToPx(10);
        textView.setLayoutParams(textParams);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setTextColor(0xFF6B7280);
        textView.setLineSpacing(0, 1.3f);

        row.addView(check);
        row.addView(textView);
        responsibilitiesContainer.addView(row);
    }

    /**
     * Display skills as chips.
     */
    private void displaySkills(String requiredExams, String jobType) {
        skillsContainer.removeAllViews();

        // Add default skills
        String[] skills;
        if (jobType.contains("govt") || jobType.contains("government")) {
            skills = new String[] { "Technical Aptitude", "Problem Solving", "Leadership", "Project Management",
                    "Decision Making" };
        } else {
            skills = new String[] { "Technical Skills", "Communication", "Teamwork", "Problem Solving",
                    "Adaptability" };
        }

        for (String skill : skills) {
            addSkillChip(skill);
        }
    }

    /**
     * Add a skill chip to the grid.
     */
    private void addSkillChip(String skill) {
        TextView chip = new TextView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
        chip.setLayoutParams(params);
        chip.setText(skill);
        chip.setTextSize(13);
        chip.setTextColor(0xFF374151);
        chip.setBackgroundResource(R.drawable.bg_chip_light);
        chip.setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));

        skillsContainer.addView(chip);
    }

    /**
     * Generate default career growth text.
     */
    private String generateDefaultCareerGrowth(String jobType) {
        if (jobType.contains("govt") || jobType.contains("government")) {
            return "High job security and prestige. Career progression moves from technical execution to strategic leadership roles.";
        }
        return "Strong growth potential with opportunities for advancement based on performance and skill development.";
    }

    /**
     * Display career path.
     */
    private void displayCareerPath(String jobName, String jobType) {
        careerPathContainer.removeAllViews();

        String[] careerSteps;
        if (jobType.contains("govt") || jobType.contains("government")) {
            careerSteps = new String[] { "• Asst. Executive Engineer", "• Executive Engineer",
                    "↑ Chief Engineer / Secretary" };
        } else {
            careerSteps = new String[] { "• Entry Level", "• Senior Position", "↑ Leadership Role" };
        }

        for (int i = 0; i < careerSteps.length; i++) {
            TextView step = new TextView(this);
            step.setText(careerSteps[i]);
            step.setTextSize(14);

            // Last item is highlighted
            if (i == careerSteps.length - 1) {
                step.setTextColor(0xFF047857);
            } else {
                step.setTextColor(0xFF065F46);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0)
                params.topMargin = dpToPx(4);
            step.setLayoutParams(params);

            careerPathContainer.addView(step);
        }
    }

    /**
     * Save job to user's roadmap.
     */
    private void saveJobToRoadmap() {
        // Get user ID from shared preferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Please login to add jobs to roadmap", Toast.LENGTH_SHORT).show();
            return;
        }

        bottomButton.setEnabled(false);
        bottomButton.setText("Adding...");

        String url = ApiConfig.getBaseUrl() + "save_job.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean status = jsonResponse.getBoolean("status");
                        if (status) {
                            Toast.makeText(this, "Job added to roadmap!", Toast.LENGTH_SHORT).show();
                            bottomButton.setText("✓ Added to Roadmap");
                        } else {
                            String message = jsonResponse.optString("message", "Failed to add job");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            bottomButton.setEnabled(true);
                            bottomButton.setText("Add to Roadmap");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        bottomButton.setEnabled(true);
                        bottomButton.setText("Add to Roadmap");
                    }
                },
                error -> {
                    Log.e(TAG, "Error saving job: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                    bottomButton.setEnabled(true);
                    bottomButton.setText("Add to Roadmap");
                }) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject body = new JSONObject();
                    body.put("user_id", userId);
                    body.put("job_id", jobId);
                    body.put("stream_id", streamId);
                    return body.toString().getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}

