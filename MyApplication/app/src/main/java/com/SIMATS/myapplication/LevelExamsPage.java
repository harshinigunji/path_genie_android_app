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
 * LevelExamsPage Activity - Displays entrance exams for a specific education
 * level.
 * Uses exams_by_level.php API.
 * 
 * Flow: After viewing next stream suggestions, this shows exams for that next
 * level
 * Example: After 10th streams → Next suggestions are 12th level → Show 12th
 * level exams
 */
public class LevelExamsPage extends AppCompatActivity {

    private static final String TAG = "LevelExamsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private TextView titleText;
    private TextView levelBadge;
    private TextView subtitleText;
    private ProgressBar progressBar;
    private TextView noExamsText;
    private LinearLayout examsContainer;
    private Button exploreNextButton;

    // Data
    private int educationLevelId = -1;
    private int streamId = -1;
    private String levelName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level_exams_page);

        // Get data from intent
        educationLevelId = getIntent().getIntExtra("education_level_id", -1);
        streamId = getIntent().getIntExtra("stream_id", -1);
        levelName = getIntent().getStringExtra("level_name");

        if (educationLevelId == -1) {
            Toast.makeText(this, "Invalid education level", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (levelName == null) {
            levelName = getLevelName(educationLevelId);
        }

        // Setup edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initViews();

        // Set dynamic titles
        titleText.setText("Entrance Exams");
        levelBadge.setText("AFTER " + levelName.toUpperCase());
        subtitleText.setText("Explore entrance examinations available after " + levelName + ".");

        // Setup click listeners
        setupClickListeners();

        // Fetch exams from API
        fetchLevelExams();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        levelBadge = findViewById(R.id.levelBadge);
        subtitleText = findViewById(R.id.subtitleText);
        progressBar = findViewById(R.id.progressBar);
        noExamsText = findViewById(R.id.noExamsText);
        examsContainer = findViewById(R.id.examsContainer);
        exploreNextButton = findViewById(R.id.exploreNextButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        // Back to Streams button - just go back to previous page
        exploreNextButton.setOnClickListener(v -> finish());
    }

    /**
     * Fetch exams for this education level from API.
     */
    private void fetchLevelExams() {
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
                            noExamsText.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        noExamsText.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching exams: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                    noExamsText.setVisibility(View.VISIBLE);
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Display exams in the container.
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

                // Inflate exam item (reuse existing item_stream_exam layout)
                View examView = inflater.inflate(R.layout.item_stream_exam, examsContainer, false);

                // Set data
                TextView conductingBodyView = examView.findViewById(R.id.conductingBody);
                TextView examNameView = examView.findViewById(R.id.examName);
                TextView overviewView = examView.findViewById(R.id.examOverview);
                TextView categoryTagView = examView.findViewById(R.id.categoryTag);
                ImageView categoryIconView = examView.findViewById(R.id.categoryIcon);

                conductingBodyView.setText(conductingBody.toUpperCase());
                examNameView.setText(getExamShortName(examName));
                overviewView.setText(overview);

                // Set category tag
                setCategoryTag(categoryTagView, categoryIconView, examName, examStage);

                // Click listener - navigate to exam details
                final int clickedExamId = examId;
                examView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, StreamExamDetailsPage.class);
                    intent.putExtra("exam_id", clickedExamId);
                    intent.putExtra("stream_id", streamId);
                    intent.putExtra("education_level_id", educationLevelId);
                    intent.putExtra("is_from_level_exams", true); // Flag to change button behavior
                    startActivity(intent);
                });

                examsContainer.addView(examView);

            } catch (Exception e) {
                Log.e(TAG, "Error displaying exam: " + e.getMessage());
            }
        }
    }

    /**
     * Get level name from ID.
     */
    private String getLevelName(int levelId) {
        switch (levelId) {
            case 1:
                return "10th Pass";
            case 2:
                return "12th Science";
            case 3:
                return "12th Commerce";
            case 4:
                return "12th Arts";
            case 5:
                return "Diploma";
            case 6:
                return "Undergraduate";
            case 7:
                return "Postgraduate";
            default:
                return "Education Level";
        }
    }

    /**
     * Set category tag and icon based on exam type.
     */
    private void setCategoryTag(TextView tagView, ImageView iconView, String examName, String examStage) {
        String name = examName.toLowerCase();

        if (name.contains("scholarship") || name.contains("nmms") || name.contains("ntse")) {
            tagView.setText("Scholarship Exam");
            iconView.setImageResource(R.drawable.ic_cap);
        } else if (name.contains("engineering") || name.contains("jee") || name.contains("cet")) {
            tagView.setText("Engineering Entrance");
            iconView.setImageResource(R.drawable.ic_science);
        } else if (name.contains("medical") || name.contains("neet")) {
            tagView.setText("Medical Entrance");
            iconView.setImageResource(R.drawable.ic_microscope);
        } else if (name.contains("architecture") || name.contains("nata")) {
            tagView.setText("Architecture");
            iconView.setImageResource(R.drawable.ic_arts);
        } else if (name.contains("management") || name.contains("cat") || name.contains("ipmat")) {
            tagView.setText("Management");
            iconView.setImageResource(R.drawable.ic_commerce);
        } else if (name.contains("law") || name.contains("clat")) {
            tagView.setText("Law Entrance");
            iconView.setImageResource(R.drawable.ic_undergraduate);
        } else {
            tagView.setText(examStage.isEmpty() ? "Entrance Exam" : examStage);
            iconView.setImageResource(R.drawable.ic_exam);
        }
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
        if (words.length > 3) {
            return words[0] + " " + words[1];
        }
        return fullName;
    }
}

