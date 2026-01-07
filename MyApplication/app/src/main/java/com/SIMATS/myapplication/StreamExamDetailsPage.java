package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.SIMATS.myapplication.network.VolleySingleton;

import org.json.JSONObject;

import com.SIMATS.myapplication.utils.RoadmapSessionManager;

/**
 * StreamExamDetailsPage Activity - Displays detailed information about an exam
 * from stream context.
 * Fetches data from exam_details.php API based on exam_id.
 * Navigation: Add to Roadmap → Success Page → Next Streams
 * Explore Next Step → Next Streams
 */
public class StreamExamDetailsPage extends AppCompatActivity {

    private static final String TAG = "StreamExamDetailsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private ProgressBar progressBar;
    private ScrollView contentScroll;
    private TextView examStageBadge;
    private TextView examName;
    private TextView examFullName;
    private TextView overviewText;
    private TextView conductingBody;
    private TextView eligibilityText;
    private LinearLayout eligibilityContainer;
    private TextView outcomeText;
    private TextView skipText;
    private Button exploreNextStepButton;
    private Button addToRoadmapButton;

    // Data
    private int examId = -1;
    private int streamId = -1;
    private int educationLevelId = -1;
    private String streamName = "";
    private String currentExamName = "";
    private boolean isFromLevelExams = false; // Flag to change button behavior
    private RoadmapSessionManager roadmapSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stream_exam_details_page);

        // Get data from intent
        examId = getIntent().getIntExtra("exam_id", -1);
        streamId = getIntent().getIntExtra("stream_id", -1);
        educationLevelId = getIntent().getIntExtra("education_level_id", 1);
        streamName = getIntent().getStringExtra("stream_name");
        isFromLevelExams = getIntent().getBooleanExtra("is_from_level_exams", false);

        if (examId == -1) {
            Toast.makeText(this, "Invalid exam", Toast.LENGTH_SHORT).show();
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

        // Fetch exam details from API
        fetchExamDetails();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        contentScroll = findViewById(R.id.contentScroll);
        examStageBadge = findViewById(R.id.examStageBadge);
        examName = findViewById(R.id.examName);
        examFullName = findViewById(R.id.examFullName);
        overviewText = findViewById(R.id.overviewText);
        conductingBody = findViewById(R.id.conductingBody);
        eligibilityText = findViewById(R.id.eligibilityText);
        eligibilityContainer = findViewById(R.id.eligibilityContainer);
        outcomeText = findViewById(R.id.outcomeText);
        skipText = findViewById(R.id.skipText);
        exploreNextStepButton = findViewById(R.id.exploreNextStepButton);
        addToRoadmapButton = findViewById(R.id.addToRoadmapButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        // Explore Next Step button - Navigate to NextStreamSuggestionsPage
        exploreNextStepButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NextStreamSuggestionsPage.class);
            intent.putExtra("stream_id", streamId);
            intent.putExtra("education_level_id", educationLevelId);
            startActivity(intent);
        });

        // Add to Roadmap button - Add exam to roadmap and show success page
        addToRoadmapButton.setOnClickListener(v -> {
            // Add exam step to roadmap session (inserted before last stream)
            roadmapSessionManager.insertExamBeforeLastStream(examId, currentExamName,
                    "Entrance exam for higher education");

            Intent intent = new Intent(this, StreamExamAddedSuccessPage.class);
            intent.putExtra("stream_id", streamId);
            intent.putExtra("education_level_id", educationLevelId);
            intent.putExtra("stream_name", streamName);
            intent.putExtra("exam_name", currentExamName);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Fetch exam details from API.
     */
    private void fetchExamDetails() {
        progressBar.setVisibility(View.VISIBLE);
        contentScroll.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "exam_details.php?exam_id=" + examId;

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
                            JSONObject exam = response.getJSONObject("data");
                            displayExamDetails(exam);
                        } else {
                            String message = response.optString("message", "Failed to fetch exam details");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(this, "Error loading exam details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching exam details: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Display exam details from API response.
     */
    private void displayExamDetails(JSONObject exam) {
        try {
            // Exam name parsing
            String name = exam.getString("exam_name");
            currentExamName = name; // Store for roadmap tracking
            examName.setText(getExamShortName(name));
            examFullName.setText(getExamFullDescription(name));

            // Exam stage badge
            String stage = exam.optString("exam_stage", "NATIONAL LEVEL");
            examStageBadge.setText(stage.toUpperCase());

            // Overview
            String overview = exam.optString("overview", "");
            if (!overview.isEmpty()) {
                overviewText.setText(overview);
            }

            // Conducting body
            String body = exam.optString("conducting_body", "N/A");
            conductingBody.setText(body);

            // Eligibility
            String eligibility = exam.optString("eligibility", "");
            if (!eligibility.isEmpty()) {
                eligibilityText.setText(eligibility);
                displayEligibilityPoints(eligibility);
            }

            // Outcome
            String outcome = exam.optString("outcome", "");
            if (!outcome.isEmpty()) {
                outcomeText.setText(outcome);
            }

            // Skip text - generate based on exam type
            skipText.setText(generateSkipText(name));

        } catch (Exception e) {
            Log.e(TAG, "Error displaying exam: " + e.getMessage());
        }
    }

    /**
     * Display eligibility as bullet points.
     */
    private void displayEligibilityPoints(String eligibility) {
        eligibilityContainer.removeAllViews();

        // Add some standard eligibility points
        addEligibilityPoint("Minimum education level: Class VIII.");
        addEligibilityPoint("Required stream: Any Stream.");
        addEligibilityPoint("Age limit: Specific criteria applies.");
        addEligibilityPoint("Parental income criteria must be met.");
    }

    /**
     * Add an eligibility bullet point.
     */
    private void addEligibilityPoint(String text) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = dpToPx(6);
        row.setLayoutParams(rowParams);

        TextView check = new TextView(this);
        check.setText("✓");
        check.setTextSize(14);
        check.setTextColor(getColor(R.color.black));

        TextView textView = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.leftMargin = dpToPx(8);
        textView.setLayoutParams(textParams);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setTextColor(0xFF374151);

        row.addView(check);
        row.addView(textView);
        eligibilityContainer.addView(row);
    }

    /**
     * Get short name for exam.
     */
    private String getExamShortName(String fullName) {
        if (fullName.contains("(") && fullName.contains(")")) {
            int start = fullName.indexOf("(");
            int end = fullName.indexOf(")");
            return fullName.substring(start + 1, end);
        }
        String[] words = fullName.split(" ");
        if (words.length > 2) {
            return words[0] + " " + words[1];
        }
        return fullName;
    }

    /**
     * Get full description for exam.
     */
    private String getExamFullDescription(String fullName) {
        if (fullName.contains("(")) {
            return fullName.substring(0, fullName.indexOf("(")).trim();
        }
        return fullName;
    }

    /**
     * Generate skip text based on exam type.
     */
    private String generateSkipText(String examName) {
        String lower = examName.toLowerCase();
        if (lower.contains("scholarship") || lower.contains("nmms") || lower.contains("ntse")) {
            return "Other state/private scholarships available. Focus on academic performance for future opportunities.";
        } else if (lower.contains("jee") || lower.contains("engineering")) {
            return "Consider state-level engineering exams or private institutions. Many alternative paths available.";
        } else if (lower.contains("neet") || lower.contains("medical")) {
            return "Consider alternative medical courses or state-level exams. AYUSH courses also available.";
        }
        return "Alternative opportunities available. Focus on academic performance and explore other options.";
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}

