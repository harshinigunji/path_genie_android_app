package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

/**
 * EducationLevelExamDetailsPage Activity - Displays exam details for entrance
 * exams.
 * 
 * Navigation Flow:
 * HomePage → ExamsEducationLevelsPage → EducationLevelExamsPage →
 * EducationLevelExamDetailsPage → HomePage
 * 
 * Fetches exam details from exam_details.php API based on exam_id.
 */
public class EducationLevelExamDetailsPage extends AppCompatActivity {

    private static final String TAG = "ExamDetailsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private ProgressBar progressBar;
    private ScrollView contentScroll;
    private TextView examStageTag;
    private TextView examName;
    private TextView examSubtitle;
    private TextView examOverview;
    private TextView conductingBody;
    private TextView eligibilityEducation;
    private TextView applicationPeriod;
    private TextView examPattern;
    private TextView examOutcome;
    private Button btnHome;

    // Data
    private int examId = -1;
    private int educationLevelId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_education_level_exam_details_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from intent
        examId = getIntent().getIntExtra("exam_id", -1);
        educationLevelId = getIntent().getIntExtra("education_level_id", 1);

        if (examId == -1) {
            Toast.makeText(this, "Invalid exam", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();
        fetchExamDetails();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        contentScroll = findViewById(R.id.contentScroll);
        examStageTag = findViewById(R.id.examStageTag);
        examName = findViewById(R.id.examName);
        examSubtitle = findViewById(R.id.examSubtitle);
        examOverview = findViewById(R.id.examOverview);
        conductingBody = findViewById(R.id.conductingBody);
        eligibilityEducation = findViewById(R.id.eligibilityEducation);
        applicationPeriod = findViewById(R.id.applicationPeriod);
        examPattern = findViewById(R.id.examPattern);
        examOutcome = findViewById(R.id.examOutcome);
        btnHome = findViewById(R.id.btnHome);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        btnHome.setOnClickListener(v -> navigateToHome());
    }

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
                            populateUI(exam);
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
                    contentScroll.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Error fetching exam: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void populateUI(JSONObject exam) {
        try {
            // Exam name
            String name = exam.optString("exam_name", "Exam Details");
            examName.setText(name);

            // Exam stage tag
            String stage = exam.optString("exam_stage", "ENTRANCE");
            examStageTag.setText(stage.toUpperCase());

            // Subtitle (conducting body short)
            String body = exam.optString("conducting_body", "");
            examSubtitle.setText(body);
            conductingBody.setText(body.isEmpty() ? "Various Bodies" : body);

            // Overview
            String overview = exam.optString("overview", "No overview available.");
            examOverview.setText(overview);

            // Eligibility
            String eligibility = exam.optString("eligibility", "");
            if (!eligibility.isEmpty()) {
                eligibilityEducation.setText(eligibility);
            } else {
                eligibilityEducation.setText("Check website");
            }

            // Application period
            String appPeriod = exam.optString("application_period", "");
            if (!appPeriod.isEmpty()) {
                applicationPeriod.setText(appPeriod);
            } else {
                applicationPeriod.setText("Varies");
            }

            // Exam pattern
            String pattern = exam.optString("exam_pattern", "");
            if (!pattern.isEmpty()) {
                examPattern.setText(pattern);
            } else {
                examPattern.setText("Please check official website for exam pattern details.");
            }

            // Outcome
            String outcome = exam.optString("outcome", "");
            if (!outcome.isEmpty()) {
                examOutcome.setText("✓ " + outcome);
            } else {
                examOutcome.setText("✓ Successful candidates will be eligible for admission to related programs.");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error populating UI: " + e.getMessage());
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

