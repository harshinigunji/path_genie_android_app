package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONObject;

/**
 * NextStreamsListDetailsPage Activity - Displays details of a stream selected
 * from next streams.
 * Clicking "Explore Next Streams" loops back to NextStreamsListPage.
 * 
 * Navigation Flow (Loop):
 * NextStreamsListPage → NextStreamsListDetailsPage → NextStreamsListPage
 * (repeat)
 */
public class NextStreamsListDetailsPage extends AppCompatActivity {

    private static final String TAG = "NextStreamsListDetailsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private TextView streamName;
    private TextView streamDescription;
    private TextView streamSubjects;
    private TextView durationValue;
    private TextView durationLevel;
    private LinearLayout whoShouldChooseContainer;
    private ImageView streamIcon;
    private Button btnExplore;
    private ProgressBar progressBar;

    // Data
    private int streamId = -1;
    private String streamNameStr = "";
    private int educationLevelId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_next_streams_list_details_page);

        streamId = getIntent().getIntExtra("stream_id", -1);
        streamNameStr = getIntent().getStringExtra("stream_name");
        educationLevelId = getIntent().getIntExtra("education_level_id", -1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        fetchStreamDetails();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        streamName = findViewById(R.id.streamName);
        streamDescription = findViewById(R.id.streamDescription);
        streamSubjects = findViewById(R.id.streamSubjects);
        durationValue = findViewById(R.id.durationValue);
        durationLevel = findViewById(R.id.durationLevel);
        whoShouldChooseContainer = findViewById(R.id.whoShouldChooseContainer);
        streamIcon = findViewById(R.id.streamIcon);
        btnExplore = findViewById(R.id.btnExplore);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        // Loop back to NextStreamsListPage with the current stream to find its next
        // streams
        btnExplore.setOnClickListener(v -> {
            Intent intent = new Intent(this, NextStreamsListPage.class);
            intent.putExtra("stream_id", streamId);
            intent.putExtra("stream_name", streamNameStr);
            intent.putExtra("education_level_id", educationLevelId);
            startActivity(intent);
        });
    }

    private void fetchStreamDetails() {
        if (streamId == -1) {
            Toast.makeText(this, "Invalid stream", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        String url = ApiConfig.getBaseUrl() + "stream_details.php?stream_id=" + streamId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    try {
                        boolean status = response.getBoolean("status");
                        if (status) {
                            JSONObject data = response.getJSONObject("data");
                            updateUI(data);
                        } else {
                            String message = response.optString("message", "Failed to fetch stream details");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            updateBasicUI();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        updateBasicUI();
                    }
                },
                error -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Log.e(TAG, "Error fetching stream details: " + error.toString());
                    updateBasicUI();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void updateUI(JSONObject data) {
        try {
            // Stream name
            String name = data.optString("stream_name", streamNameStr);
            streamName.setText(name);
            streamNameStr = name;

            // Description
            String description = data.optString("description", "");
            if (!description.isEmpty() && streamDescription != null) {
                streamDescription.setText(description);
            }

            // Subjects / What is this stream
            String subjects = data.optString("subjects", "");
            if (!subjects.isEmpty() && streamSubjects != null) {
                streamSubjects.setText(subjects);
            }

            // Duration
            String duration = data.optString("duration", "2 Years");
            if (durationValue != null) {
                durationValue.setText(duration);
            }

            // Difficulty level
            String difficulty = data.optString("difficulty_level", "Intermediate");
            if (durationLevel != null) {
                durationLevel.setText(difficulty);
            }

            // Who should choose
            String whoShouldChoose = data.optString("who_should_choose", "");
            if (!whoShouldChoose.isEmpty() && whoShouldChooseContainer != null) {
                updateWhoShouldChoose(whoShouldChoose);
            }

            // Set icon with blue background and white tint
            streamIcon.setImageResource(getStreamIcon(name));
            streamIcon.setColorFilter(android.graphics.Color.parseColor("#2563EB"),
                    android.graphics.PorterDuff.Mode.SRC_IN);

        } catch (Exception e) {
            Log.e(TAG, "Error updating UI: " + e.getMessage());
        }
    }

    private void updateBasicUI() {
        if (streamNameStr != null && !streamNameStr.isEmpty()) {
            streamName.setText(streamNameStr);
        }
        streamIcon.setImageResource(getStreamIcon(streamNameStr));
        streamIcon.setColorFilter(android.graphics.Color.parseColor("#2563EB"),
                android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void updateWhoShouldChoose(String whoShouldChoose) {
        whoShouldChooseContainer.removeAllViews();

        String[] points = whoShouldChoose.split("[\\n,]");

        for (String point : points) {
            String trimmedPoint = point.trim();
            if (!trimmedPoint.isEmpty()) {
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = dpToPx(8);
                textView.setLayoutParams(params);
                textView.setText("✓ " + trimmedPoint);
                textView.setTextSize(14);
                textView.setTextColor(android.graphics.Color.parseColor("#16A34A"));
                whoShouldChooseContainer.addView(textView);
            }
        }
    }

    private int getStreamIcon(String name) {
        if (name == null)
            return R.drawable.ic_education;
        String lowerName = name.toLowerCase();

        // === SCIENCE STREAMS ===
        if (lowerName.contains("pcm") || (lowerName.contains("science") && lowerName.contains("math"))) {
            return R.drawable.ic_science;
        } else if (lowerName.contains("pcb") || (lowerName.contains("science") && lowerName.contains("bio"))) {
            return R.drawable.ic_microscope;
        } else if (lowerName.contains("biology") || lowerName.contains("life science")) {
            return R.drawable.ic_biology;
        } else if (lowerName.contains("physics")) {
            return R.drawable.ic_bulb;
        } else if (lowerName.contains("chemistry")) {
            return R.drawable.ic_science;
        }

        // === COMMERCE & BUSINESS ===
        else if (lowerName.contains("commerce") && !lowerName.contains("e-commerce")) {
            return R.drawable.ic_commerce;
        } else if (lowerName.contains("accounting") || lowerName.contains("finance")) {
            return R.drawable.ic_rupee;
        } else if (lowerName.contains("business") || lowerName.contains("bba") || lowerName.contains("mba")) {
            return R.drawable.ic_briefcase;
        } else if (lowerName.contains("marketing") || lowerName.contains("sales")) {
            return R.drawable.ic_trend_up;
        } else if (lowerName.contains("economics")) {
            return R.drawable.ic_balance;
        }

        // === ARTS & HUMANITIES ===
        else if (lowerName.contains("arts") && !lowerName.contains("fine art")) {
            return R.drawable.ic_arts;
        } else if (lowerName.contains("literature") || lowerName.contains("english")) {
            return R.drawable.ic_book;
        } else if (lowerName.contains("history") || lowerName.contains("political")) {
            return R.drawable.ic_history;
        } else if (lowerName.contains("psychology") || lowerName.contains("sociology")) {
            return R.drawable.ic_user;
        } else if (lowerName.contains("humanities") || lowerName.contains("liberal")) {
            return R.drawable.ic_school;
        } else if (lowerName.contains("journalism") || lowerName.contains("media")) {
            return R.drawable.ic_mic;
        }

        // === ENGINEERING & TECHNOLOGY ===
        else if (lowerName.contains("computer") || lowerName.contains("cse") || lowerName.contains("software")) {
            return R.drawable.ic_computer;
        } else if (lowerName.contains("mechanical") || lowerName.contains("automobile")) {
            return R.drawable.ic_engineering;
        } else if (lowerName.contains("electrical") || lowerName.contains("electronics")) {
            return R.drawable.ic_bulb;
        } else if (lowerName.contains("civil") || lowerName.contains("construction")) {
            return R.drawable.ic_building;
        } else if (lowerName.contains("engineering") || lowerName.contains("b.tech") || lowerName.contains("btech")) {
            return R.drawable.ic_engineering;
        } else if (lowerName.contains("data science") || lowerName.contains("ai")
                || lowerName.contains("machine learning")) {
            return R.drawable.ic_ai_brain;
        }

        // === MEDICAL & HEALTH ===
        else if (lowerName.contains("mbbs") || lowerName.contains("medicine") || lowerName.contains("doctor")) {
            return R.drawable.ic_medical;
        } else if (lowerName.contains("nursing") || lowerName.contains("healthcare")) {
            return R.drawable.ic_heart_filled;
        } else if (lowerName.contains("pharmacy") || lowerName.contains("pharma")) {
            return R.drawable.ic_science;
        } else if (lowerName.contains("dental") || lowerName.contains("bds")) {
            return R.drawable.ic_medical;
        }

        // === LAW & LEGAL ===
        else if (lowerName.contains("law") || lowerName.contains("llb") || lowerName.contains("legal")) {
            return R.drawable.ic_law;
        }

        // === DESIGN & CREATIVE ===
        else if (lowerName.contains("design") || lowerName.contains("graphic")) {
            return R.drawable.ic_design;
        } else if (lowerName.contains("architecture") || lowerName.contains("interior")) {
            return R.drawable.ic_architecture;
        } else if (lowerName.contains("fashion")) {
            return R.drawable.ic_star;
        } else if (lowerName.contains("animation") || lowerName.contains("multimedia")) {
            return R.drawable.ic_camera;
        }

        // === AVIATION & TRAVEL ===
        else if (lowerName.contains("aviation") || lowerName.contains("pilot") || lowerName.contains("aerospace")) {
            return R.drawable.ic_aviation;
        } else if (lowerName.contains("hotel") || lowerName.contains("hospitality") || lowerName.contains("tourism")) {
            return R.drawable.ic_building;
        }

        // === VOCATIONAL & DIPLOMA ===
        else if (lowerName.contains("vocational") || lowerName.contains("skill") || lowerName.contains("iti")) {
            return R.drawable.ic_vocational;
        } else if (lowerName.contains("diploma") || lowerName.contains("polytechnic")) {
            return R.drawable.ic_diploma;
        }

        // === GOVERNMENT & COMPETITIVE ===
        else if (lowerName.contains("civil service") || lowerName.contains("upsc") || lowerName.contains("ias")) {
            return R.drawable.ic_govt;
        } else if (lowerName.contains("bank") || lowerName.contains("ssc")) {
            return R.drawable.ic_rupee;
        }

        // === RESEARCH & ACADEMIA ===
        else if (lowerName.contains("research") || lowerName.contains("phd") || lowerName.contains("doctorate")) {
            return R.drawable.ic_research;
        } else if (lowerName.contains("teaching") || lowerName.contains("education") || lowerName.contains("b.ed")) {
            return R.drawable.ic_school;
        }

        // === DEGREE LEVELS ===
        else if (lowerName.contains("undergraduate") || lowerName.contains("bachelor")) {
            return R.drawable.ic_undergraduate;
        } else if (lowerName.contains("postgraduate") || lowerName.contains("master")) {
            return R.drawable.ic_postgraduate;
        } else if (lowerName.contains("graduation") || lowerName.contains("graduate")) {
            return R.drawable.ic_graduation;
        }

        // === DEFAULT ===
        else {
            return R.drawable.ic_education;
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
