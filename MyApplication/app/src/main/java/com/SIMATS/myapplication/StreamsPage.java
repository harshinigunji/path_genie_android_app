package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import com.SIMATS.myapplication.utils.RoadmapSessionManager;

/**
 * StreamsPage Activity - Displays streams available for the selected education
 * level.
 * Fetches data from streams.php API based on education_level_id.
 */
public class StreamsPage extends AppCompatActivity {

    private static final String TAG = "StreamsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private LinearLayout streamsContainer;
    private ProgressBar progressBar;

    // Data
    private int educationLevelId = -1;
    private RoadmapSessionManager roadmapSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_streams_page);

        // Get education_level_id from intent
        educationLevelId = getIntent().getIntExtra("education_level_id", -1);

        if (educationLevelId == -1) {
            Toast.makeText(this, "Invalid education level", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

        // Clear any previous stream/exam/job selections when entering this page
        // This ensures fresh start if user navigated back
        roadmapSessionManager.clearStepsFromType("STREAM");

        // Setup click listeners
        setupClickListeners();

        // Fetch streams from API
        fetchStreams();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        streamsContainer = findViewById(R.id.streamsContainer);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Fetch streams from API based on education_level_id.
     */
    private void fetchStreams() {
        progressBar.setVisibility(View.VISIBLE);
        streamsContainer.removeAllViews();

        String url = ApiConfig.getBaseUrl() + "streams.php?education_level_id=" + educationLevelId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        boolean status = response.getBoolean("status");
                        if (status) {
                            JSONArray streams = response.getJSONArray("data");
                            displayStreams(streams);
                        } else {
                            String message = response.optString("message", "Failed to fetch streams");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(this, "Error loading streams", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching streams: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Display streams dynamically in the container.
     */
    private void displayStreams(JSONArray streams) {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < streams.length(); i++) {
            try {
                JSONObject stream = streams.getJSONObject(i);
                int streamId = stream.getInt("stream_id");
                String streamName = stream.getString("stream_name");
                String description = stream.optString("description", "");
                String difficulty = stream.optString("difficulty_level", "Medium");

                // Inflate stream item
                View streamView = inflater.inflate(R.layout.item_stream, streamsContainer, false);

                // Set data
                TextView nameView = streamView.findViewById(R.id.streamName);
                TextView descView = streamView.findViewById(R.id.streamDescription);
                ImageView iconView = streamView.findViewById(R.id.streamIcon);

                nameView.setText(streamName);
                descView.setText(truncateText(description, 30));

                // Set icon based on stream name
                iconView.setImageResource(getStreamIcon(streamName));

                // Click listener - navigate to stream details
                final int clickedStreamId = streamId;
                final String clickedStreamName = streamName;
                final String clickedDescription = description;
                streamView.setOnClickListener(v -> {
                    // Add stream step to roadmap session
                    roadmapSessionManager.addStream(clickedStreamId, clickedStreamName, clickedDescription);

                    Intent intent = new Intent(this, StreamDetailsPage.class);
                    intent.putExtra("stream_id", clickedStreamId);
                    intent.putExtra("stream_name", clickedStreamName);
                    intent.putExtra("education_level_id", educationLevelId);
                    startActivity(intent);
                });

                streamsContainer.addView(streamView);

            } catch (Exception e) {
                Log.e(TAG, "Error displaying stream: " + e.getMessage());
            }
        }
    }

    /**
     * Get appropriate icon for stream based on name.
     */
    private int getStreamIcon(String streamName) {
        if (streamName == null)
            return R.drawable.ic_education;
        String name = streamName.toLowerCase();

        // === SCIENCE STREAMS ===
        if (name.contains("pcm") || (name.contains("science") && name.contains("math"))) {
            return R.drawable.ic_science;
        } else if (name.contains("pcb") || (name.contains("science") && name.contains("bio"))) {
            return R.drawable.ic_microscope;
        } else if (name.contains("biology") || name.contains("life science")) {
            return R.drawable.ic_biology;
        } else if (name.contains("physics")) {
            return R.drawable.ic_bulb;
        } else if (name.contains("chemistry")) {
            return R.drawable.ic_science;
        }

        // === COMMERCE & BUSINESS ===
        else if (name.contains("commerce") && !name.contains("e-commerce")) {
            return R.drawable.ic_commerce;
        } else if (name.contains("accounting") || name.contains("finance")) {
            return R.drawable.ic_rupee;
        } else if (name.contains("business") || name.contains("bba") || name.contains("mba")) {
            return R.drawable.ic_briefcase;
        } else if (name.contains("marketing") || name.contains("sales")) {
            return R.drawable.ic_trend_up;
        } else if (name.contains("economics")) {
            return R.drawable.ic_balance;
        }

        // === ARTS & HUMANITIES ===
        else if (name.contains("arts") && !name.contains("fine art")) {
            return R.drawable.ic_arts;
        } else if (name.contains("literature") || name.contains("english")) {
            return R.drawable.ic_book;
        } else if (name.contains("history") || name.contains("political")) {
            return R.drawable.ic_history;
        } else if (name.contains("psychology") || name.contains("sociology")) {
            return R.drawable.ic_user;
        } else if (name.contains("humanities") || name.contains("liberal")) {
            return R.drawable.ic_school;
        } else if (name.contains("journalism") || name.contains("media")) {
            return R.drawable.ic_mic;
        }

        // === ENGINEERING & TECHNOLOGY ===
        else if (name.contains("computer") || name.contains("cse") || name.contains("software")) {
            return R.drawable.ic_computer;
        } else if (name.contains("mechanical") || name.contains("automobile")) {
            return R.drawable.ic_engineering;
        } else if (name.contains("electrical") || name.contains("electronics")) {
            return R.drawable.ic_bulb;
        } else if (name.contains("civil") || name.contains("construction")) {
            return R.drawable.ic_building;
        } else if (name.contains("engineering") || name.contains("b.tech") || name.contains("btech")) {
            return R.drawable.ic_engineering;
        } else if (name.contains("data science") || name.contains("ai") || name.contains("machine learning")) {
            return R.drawable.ic_ai_brain;
        }

        // === MEDICAL & HEALTH ===
        else if (name.contains("mbbs") || name.contains("medicine") || name.contains("doctor")) {
            return R.drawable.ic_medical;
        } else if (name.contains("nursing") || name.contains("healthcare")) {
            return R.drawable.ic_heart_filled;
        } else if (name.contains("pharmacy") || name.contains("pharma")) {
            return R.drawable.ic_science;
        } else if (name.contains("dental") || name.contains("bds")) {
            return R.drawable.ic_medical;
        }

        // === LAW & LEGAL ===
        else if (name.contains("law") || name.contains("llb") || name.contains("legal")) {
            return R.drawable.ic_law;
        }

        // === DESIGN & CREATIVE ===
        else if (name.contains("design") || name.contains("graphic")) {
            return R.drawable.ic_design;
        } else if (name.contains("architecture") || name.contains("interior")) {
            return R.drawable.ic_architecture;
        } else if (name.contains("fashion")) {
            return R.drawable.ic_star;
        } else if (name.contains("animation") || name.contains("multimedia")) {
            return R.drawable.ic_camera;
        }

        // === AVIATION & TRAVEL ===
        else if (name.contains("aviation") || name.contains("pilot") || name.contains("aerospace")) {
            return R.drawable.ic_aviation;
        } else if (name.contains("hotel") || name.contains("hospitality") || name.contains("tourism")) {
            return R.drawable.ic_building;
        }

        // === VOCATIONAL & DIPLOMA ===
        else if (name.contains("vocational") || name.contains("skill") || name.contains("iti")) {
            return R.drawable.ic_vocational;
        } else if (name.contains("diploma") || name.contains("polytechnic")) {
            return R.drawable.ic_diploma;
        }

        // === GOVERNMENT & COMPETITIVE ===
        else if (name.contains("civil service") || name.contains("upsc") || name.contains("ias")) {
            return R.drawable.ic_govt;
        } else if (name.contains("bank") || name.contains("ssc")) {
            return R.drawable.ic_rupee;
        }

        // === RESEARCH & ACADEMIA ===
        else if (name.contains("research") || name.contains("phd") || name.contains("doctorate")) {
            return R.drawable.ic_research;
        } else if (name.contains("teaching") || name.contains("education") || name.contains("b.ed")) {
            return R.drawable.ic_school;
        }

        // === DEGREE LEVELS ===
        else if (name.contains("undergraduate") || name.contains("bachelor")) {
            return R.drawable.ic_undergraduate;
        } else if (name.contains("postgraduate") || name.contains("master")) {
            return R.drawable.ic_postgraduate;
        } else if (name.contains("graduation") || name.contains("graduate")) {
            return R.drawable.ic_graduation;
        }

        // === DEFAULT ===
        else {
            return R.drawable.ic_education;
        }
    }

    /**
     * Truncate text with ellipsis.
     */
    private String truncateText(String text, int maxLength) {
        if (text == null)
            return "";
        if (text.length() <= maxLength)
            return text;
        return text.substring(0, maxLength) + "...";
    }
}

