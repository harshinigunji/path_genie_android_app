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
 * EducationLevelExamsPage Activity - Displays entrance exams for a specific
 * education level.
 * Fetches data from exams_by_level.php API based on education_level_id.
 */
public class EducationLevelExamsPage extends AppCompatActivity {

    private static final String TAG = "EducationLevelExamsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private TextView titleText;
    private LinearLayout examsContainer;
    private ProgressBar progressBar;
    private TextView noExamsText;
    private Button backToStreamsButton;

    // Data
    private int educationLevelId = -1;
    private String educationLevelName = "";

    // Education level names mapping
    private static final String[] LEVEL_NAMES = {
            "", "10th", "12th Science", "12th Commerce", "12th Arts", "Diploma", "Undergraduate", "Postgraduate"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_education_level_exams_page);

        // Get education_level_id from intent
        educationLevelId = getIntent().getIntExtra("education_level_id", -1);

        if (educationLevelId == -1) {
            Toast.makeText(this, "Invalid education level", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get level name
        if (educationLevelId > 0 && educationLevelId < LEVEL_NAMES.length) {
            educationLevelName = LEVEL_NAMES[educationLevelId];
        }

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

        // Set dynamic title
        titleText.setText("Entrance Exams After " + educationLevelName);

        // Fetch exams from API
        fetchExams();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        examsContainer = findViewById(R.id.examsContainer);
        progressBar = findViewById(R.id.progressBar);
        noExamsText = findViewById(R.id.noExamsText);
        backToStreamsButton = findViewById(R.id.backToStreamsButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        backToStreamsButton.setOnClickListener(v -> finish());
    }

    /**
     * Fetch exams from API based on education_level_id.
     */
    private void fetchExams() {
        progressBar.setVisibility(View.VISIBLE);
        examsContainer.removeAllViews();
        noExamsText.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "exams_by_level.php?education_level_id=" + educationLevelId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        boolean status = response.getBoolean("status");
                        if (status) {
                            JSONArray exams = response.getJSONArray("data");
                            if (exams.length() == 0) {
                                noExamsText.setVisibility(View.VISIBLE);
                            } else {
                                displayExams(exams);
                            }
                        } else {
                            String message = response.optString("message", "Failed to fetch exams");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(this, "Error loading exams", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching exams: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Display exams dynamically in the container.
     */
    private void displayExams(JSONArray exams) {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < exams.length(); i++) {
            try {
                JSONObject exam = exams.getJSONObject(i);
                int examId = exam.getInt("exam_id");
                String examName = exam.getString("exam_name");
                String conductingBody = exam.optString("conducting_body", "");
                String examStage = exam.optString("exam_stage", "");
                String overview = exam.optString("overview", "");

                // Inflate exam item
                View examView = inflater.inflate(R.layout.item_exam, examsContainer, false);

                // Set data
                TextView badgeView = examView.findViewById(R.id.examBadge);
                TextView nameView = examView.findViewById(R.id.examName);
                TextView bodyView = examView.findViewById(R.id.conductingBody);
                TextView detailsLink = examView.findViewById(R.id.viewDetailsLink);

                nameView.setText(examName);
                bodyView.setText(conductingBody);

                // Set badge based on exam type
                setBadgeStyle(badgeView, examName, examStage);

                // Click listener for details - navigate to exam details
                final int finalExamId = examId;
                detailsLink.setOnClickListener(v -> {
                    Intent intent = new Intent(this, EducationLevelExamDetailsPage.class);
                    intent.putExtra("exam_id", finalExamId);
                    intent.putExtra("education_level_id", educationLevelId);
                    startActivity(intent);
                });

                examsContainer.addView(examView);

            } catch (Exception e) {
                Log.e(TAG, "Error displaying exam: " + e.getMessage());
            }
        }
    }

    /**
     * Set badge style based on exam type/name.
     */
    private void setBadgeStyle(TextView badge, String examName, String examStage) {
        String name = examName.toLowerCase();

        if (name.contains("polytechnic") || name.contains("polycet") || name.contains("diploma")
                || name.contains("iti")) {
            badge.setText("Diploma Admission");
            badge.setTextColor(getColor(R.color.badge_blue));
            badge.setBackgroundResource(R.drawable.bg_chip_blue);
        } else if (name.contains("scholarship") || name.contains("nmms") || name.contains("ntse")
                || name.contains("talent")) {
            badge.setText("Scholarship");
            badge.setTextColor(getColor(R.color.badge_green));
            badge.setBackgroundResource(R.drawable.bg_chip_green);
        } else if (name.contains("jee") || name.contains("neet") || name.contains("bitsat") || name.contains("gate")) {
            badge.setText("Engineering/Medical");
            badge.setTextColor(getColor(R.color.badge_purple));
            badge.setBackgroundResource(R.drawable.bg_chip_purple);
        } else if (name.contains("cuet") || name.contains("clat") || name.contains("cat") || name.contains("ipmat")) {
            badge.setText("UG/PG Admission");
            badge.setTextColor(getColor(R.color.badge_blue));
            badge.setBackgroundResource(R.drawable.bg_chip_blue);
        } else if (name.contains("ca") || name.contains("chartered")) {
            badge.setText("Professional");
            badge.setTextColor(getColor(R.color.badge_orange));
            badge.setBackgroundResource(R.drawable.bg_chip_orange);
        } else if (name.contains("nata") || name.contains("architecture")) {
            badge.setText("Architecture");
            badge.setTextColor(getColor(R.color.badge_purple));
            badge.setBackgroundResource(R.drawable.bg_chip_purple);
        } else if (name.contains("lateral") || name.contains("leet")) {
            badge.setText("Lateral Entry");
            badge.setTextColor(getColor(R.color.badge_orange));
            badge.setBackgroundResource(R.drawable.bg_chip_orange);
        } else {
            // Default based on exam_stage
            badge.setText(examStage.isEmpty() ? "Entrance Exam" : examStage);
            badge.setTextColor(getColor(R.color.badge_blue));
            badge.setBackgroundResource(R.drawable.bg_chip_blue);
        }
    }
}

