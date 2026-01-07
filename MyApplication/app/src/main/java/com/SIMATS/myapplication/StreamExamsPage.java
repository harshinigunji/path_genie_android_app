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
 * StreamExamsPage Activity - Displays entrance exams for a specific stream.
 * Fetches data from exams.php API based on stream_id.
 */
public class StreamExamsPage extends AppCompatActivity {

    private static final String TAG = "StreamExamsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private TextView titleText;
    private TextView streamBadge;
    private TextView subtitleText;
    private ProgressBar progressBar;
    private TextView noExamsText;
    private LinearLayout examsContainer;
    private Button exploreNextButton;
    private Button exploreJobsButton;

    // Data
    private int streamId = -1;
    private int educationLevelId = -1;
    private String streamName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stream_exams_page);

        // Get data from intent
        streamId = getIntent().getIntExtra("stream_id", -1);
        educationLevelId = getIntent().getIntExtra("education_level_id", 1);
        streamName = getIntent().getStringExtra("stream_name");

        if (streamId == -1) {
            Toast.makeText(this, "Invalid stream", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (streamName == null) {
            streamName = "Science (PCM)";
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
        titleText.setText("Entrance Exams for " + getShortStreamName(streamName));
        streamBadge.setText("STREAM: " + streamName.toUpperCase());
        subtitleText
                .setText("Explore key entrance examinations for scholarships and professional course admissions after "
                        + streamName + ".");

        // Setup click listeners
        setupClickListeners();

        // Fetch exams from API
        fetchStreamExams();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        streamBadge = findViewById(R.id.streamBadge);
        subtitleText = findViewById(R.id.subtitleText);
        progressBar = findViewById(R.id.progressBar);
        noExamsText = findViewById(R.id.noExamsText);
        examsContainer = findViewById(R.id.examsContainer);
        exploreNextButton = findViewById(R.id.exploreNextButton);
        exploreJobsButton = findViewById(R.id.exploreJobsButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        // Explore Next button - Navigate to NextStreamSuggestionsPage
        exploreNextButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NextStreamSuggestionsPage.class);
            intent.putExtra("stream_id", streamId);
            intent.putExtra("education_level_id", educationLevelId);
            startActivity(intent);
        });

        // Job Opportunities button - Navigate to JobsPage
        exploreJobsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, JobsPage.class);
            intent.putExtra("stream_id", streamId);
            intent.putExtra("stream_name", streamName);
            startActivity(intent);
        });
    }

    /**
     * Fetch exams for this stream from API.
     */
    private void fetchStreamExams() {
        progressBar.setVisibility(View.VISIBLE);
        examsContainer.removeAllViews();
        noExamsText.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "exams.php?stream_id=" + streamId;

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
                String eligibility = exam.optString("eligibility", "");

                // Inflate exam item
                View examView = inflater.inflate(R.layout.item_stream_exam, examsContainer, false);

                // Set data
                TextView conductingBodyView = examView.findViewById(R.id.conductingBody);
                TextView examNameView = examView.findViewById(R.id.examName);
                TextView overviewView = examView.findViewById(R.id.examOverview);
                TextView categoryTagView = examView.findViewById(R.id.categoryTag);
                ImageView categoryIconView = examView.findViewById(R.id.categoryIcon);

                conductingBodyView.setText(conductingBody.toUpperCase());
                examNameView.setText(getExamShortName(examName));
                overviewView.setText(overview.isEmpty() ? eligibility : overview);

                // Set category tag based on exam type
                setCategoryTag(categoryTagView, categoryIconView, examName, examStage);

                // Click listener - navigate to stream exam details
                final int clickedExamId = examId;
                examView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, StreamExamDetailsPage.class);
                    intent.putExtra("exam_id", clickedExamId);
                    intent.putExtra("stream_id", streamId);
                    intent.putExtra("education_level_id", educationLevelId);
                    intent.putExtra("stream_name", streamName);
                    startActivity(intent);
                });

                examsContainer.addView(examView);

            } catch (Exception e) {
                Log.e(TAG, "Error displaying exam: " + e.getMessage());
            }
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
            tagView.setText("Engineering & Pharmacy");
            iconView.setImageResource(R.drawable.ic_science);
        } else if (name.contains("medical") || name.contains("neet")) {
            tagView.setText("Medical Entrance");
            iconView.setImageResource(R.drawable.ic_microscope);
        } else if (name.contains("architecture") || name.contains("nata")) {
            tagView.setText("Architecture");
            iconView.setImageResource(R.drawable.ic_arts);
        } else if (name.contains("agriculture") || name.contains("agri")) {
            tagView.setText("Engg, Agri & Pharm");
            iconView.setImageResource(R.drawable.ic_science);
        } else if (name.contains("professional") || name.contains("admission")) {
            tagView.setText("Prof. Courses Admission");
            iconView.setImageResource(R.drawable.ic_cap);
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
        // Return first few words
        String[] words = fullName.split(" ");
        if (words.length > 3) {
            return words[0] + " " + words[1];
        }
        return fullName;
    }

    /**
     * Get short stream name.
     */
    private String getShortStreamName(String name) {
        if (name.contains("(") && name.contains(")")) {
            int start = name.indexOf("(");
            int end = name.indexOf(")");
            return name.substring(start + 1, end);
        }
        return name.split(" ")[0];
    }
}

