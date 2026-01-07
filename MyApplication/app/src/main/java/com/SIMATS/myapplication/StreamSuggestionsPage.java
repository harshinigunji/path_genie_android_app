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
 * StreamSuggestionsPage Activity - Displays next stream suggestions based on
 * current stream.
 * Fetches data from next_streams.php API based on stream_id.
 */
public class StreamSuggestionsPage extends AppCompatActivity {

    private static final String TAG = "StreamSuggestionsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private TextView titleText;
    private TextView subtitleText;
    private ProgressBar progressBar;
    private TextView noStreamsText;
    private LinearLayout streamsContainer;
    private Button bottomButton;

    // Data
    private int streamId = -1;
    private int educationLevelId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stream_suggestions_page);

        // Get data from intent
        streamId = getIntent().getIntExtra("stream_id", -1);
        educationLevelId = getIntent().getIntExtra("education_level_id", 1);

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

        // Fetch next streams from API
        if (streamId != -1) {
            fetchNextStreams();
        } else {
            noStreamsText.setVisibility(View.VISIBLE);
            noStreamsText.setText("Select a stream to see next steps.");
        }
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        subtitleText = findViewById(R.id.subtitleText);
        progressBar = findViewById(R.id.progressBar);
        noStreamsText = findViewById(R.id.noStreamsText);
        streamsContainer = findViewById(R.id.streamsContainer);
        bottomButton = findViewById(R.id.bottomButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        bottomButton.setOnClickListener(v -> {
            // Navigate to home page
            Intent intent = new Intent(this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Fetch next streams from API.
     */
    private void fetchNextStreams() {
        progressBar.setVisibility(View.VISIBLE);
        streamsContainer.removeAllViews();
        noStreamsText.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "next_streams.php?stream_id=" + streamId;

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
                            if (streams.length() == 0) {
                                noStreamsText.setVisibility(View.VISIBLE);
                            } else {
                                displayStreams(streams);
                            }
                        } else {
                            String message = response.optString("message", "Failed to fetch next streams");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            noStreamsText.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        noStreamsText.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching next streams: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                    noStreamsText.setVisibility(View.VISIBLE);
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Display streams in the container.
     */
    private void displayStreams(JSONArray streams) {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < streams.length(); i++) {
            try {
                JSONObject stream = streams.getJSONObject(i);
                int nextStreamId = stream.getInt("stream_id");
                String streamName = stream.getString("stream_name");
                String description = stream.optString("description", "");
                String duration = stream.optString("duration", "");
                String difficultyLevel = stream.optString("difficulty_level", "");

                // Inflate stream item
                View streamView = inflater.inflate(R.layout.item_stream_suggestion, streamsContainer, false);

                // Set data
                ImageView iconView = streamView.findViewById(R.id.streamIcon);
                TextView nameView = streamView.findViewById(R.id.streamName);
                TextView durationView = streamView.findViewById(R.id.streamDuration);
                TextView descView = streamView.findViewById(R.id.streamDescription);
                TextView difficultyView = streamView.findViewById(R.id.difficultyLevel);

                nameView.setText(streamName);
                durationView.setText(duration);
                descView.setText(description);
                difficultyView.setText(difficultyLevel);

                // Set icon based on stream name
                iconView.setImageResource(getStreamIcon(streamName));

                // Click listener - navigate to stream details
                final int clickedStreamId = nextStreamId;
                streamView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, StreamDetailsPage.class);
                    intent.putExtra("stream_id", clickedStreamId);
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
     * Get appropriate icon for stream.
     */
    private int getStreamIcon(String name) {
        if (name == null)
            return R.drawable.ic_education;
        String lower = name.toLowerCase();

        // === SCIENCE STREAMS ===
        if (lower.contains("pcm") || (lower.contains("science") && lower.contains("math"))) {
            return R.drawable.ic_science;
        } else if (lower.contains("pcb") || (lower.contains("science") && lower.contains("bio"))) {
            return R.drawable.ic_microscope;
        } else if (lower.contains("biology") || lower.contains("life science")) {
            return R.drawable.ic_biology;
        } else if (lower.contains("physics")) {
            return R.drawable.ic_bulb;
        } else if (lower.contains("chemistry") || lower.contains("bsc")) {
            return R.drawable.ic_science;
        }

        // === COMMERCE & BUSINESS ===
        else if (lower.contains("commerce") && !lower.contains("e-commerce")) {
            return R.drawable.ic_commerce;
        } else if (lower.contains("accounting") || lower.contains("finance")) {
            return R.drawable.ic_rupee;
        } else if (lower.contains("business") || lower.contains("bba") || lower.contains("mba")) {
            return R.drawable.ic_briefcase;
        } else if (lower.contains("marketing") || lower.contains("sales")) {
            return R.drawable.ic_trend_up;
        } else if (lower.contains("economics")) {
            return R.drawable.ic_balance;
        }

        // === ARTS & HUMANITIES ===
        else if (lower.contains("arts") && !lower.contains("fine art")) {
            return R.drawable.ic_arts;
        } else if (lower.contains("literature") || lower.contains("english")) {
            return R.drawable.ic_book;
        } else if (lower.contains("history") || lower.contains("political")) {
            return R.drawable.ic_history;
        } else if (lower.contains("psychology") || lower.contains("sociology")) {
            return R.drawable.ic_user;
        } else if (lower.contains("humanities") || lower.contains("liberal") || lower.contains("ba")) {
            return R.drawable.ic_arts;
        } else if (lower.contains("journalism") || lower.contains("media")) {
            return R.drawable.ic_mic;
        }

        // === ENGINEERING & TECHNOLOGY ===
        else if (lower.contains("computer") || lower.contains("cse") || lower.contains("software")) {
            return R.drawable.ic_computer;
        } else if (lower.contains("mechanical") || lower.contains("automobile")) {
            return R.drawable.ic_engineering;
        } else if (lower.contains("electrical") || lower.contains("electronics")) {
            return R.drawable.ic_bulb;
        } else if (lower.contains("civil") || lower.contains("construction")) {
            return R.drawable.ic_building;
        } else if (lower.contains("engineering") || lower.contains("b.tech") || lower.contains("btech")) {
            return R.drawable.ic_engineering;
        } else if (lower.contains("data science") || lower.contains("ai") || lower.contains("machine learning")) {
            return R.drawable.ic_ai_brain;
        }

        // === MEDICAL & HEALTH ===
        else if (lower.contains("mbbs") || lower.contains("medicine") || lower.contains("doctor")) {
            return R.drawable.ic_medical;
        } else if (lower.contains("nursing") || lower.contains("healthcare")) {
            return R.drawable.ic_heart_filled;
        } else if (lower.contains("pharmacy") || lower.contains("pharma")) {
            return R.drawable.ic_science;
        } else if (lower.contains("dental") || lower.contains("bds")) {
            return R.drawable.ic_medical;
        }

        // === LAW & LEGAL ===
        else if (lower.contains("law") || lower.contains("llb") || lower.contains("legal")) {
            return R.drawable.ic_law;
        }

        // === DESIGN & CREATIVE ===
        else if (lower.contains("design") || lower.contains("graphic")) {
            return R.drawable.ic_design;
        } else if (lower.contains("architecture") || lower.contains("interior")) {
            return R.drawable.ic_architecture;
        } else if (lower.contains("fashion")) {
            return R.drawable.ic_star;
        } else if (lower.contains("animation") || lower.contains("multimedia")) {
            return R.drawable.ic_camera;
        }

        // === AVIATION & TRAVEL ===
        else if (lower.contains("aviation") || lower.contains("pilot") || lower.contains("aerospace")) {
            return R.drawable.ic_aviation;
        } else if (lower.contains("hotel") || lower.contains("hospitality") || lower.contains("tourism")) {
            return R.drawable.ic_building;
        }

        // === VOCATIONAL & DIPLOMA ===
        else if (lower.contains("vocational") || lower.contains("skill") || lower.contains("iti")) {
            return R.drawable.ic_vocational;
        } else if (lower.contains("diploma") || lower.contains("polytechnic")) {
            return R.drawable.ic_diploma;
        }

        // === GOVERNMENT & COMPETITIVE ===
        else if (lower.contains("civil service") || lower.contains("upsc") || lower.contains("ias")) {
            return R.drawable.ic_govt;
        } else if (lower.contains("bank") || lower.contains("ssc")) {
            return R.drawable.ic_rupee;
        }

        // === RESEARCH & ACADEMIA ===
        else if (lower.contains("research") || lower.contains("phd") || lower.contains("doctorate")) {
            return R.drawable.ic_research;
        } else if (lower.contains("teaching") || lower.contains("education") || lower.contains("b.ed")) {
            return R.drawable.ic_school;
        }

        // === DEGREE LEVELS ===
        else if (lower.contains("undergraduate") || lower.contains("bachelor")) {
            return R.drawable.ic_undergraduate;
        } else if (lower.contains("postgraduate") || lower.contains("master")) {
            return R.drawable.ic_postgraduate;
        } else if (lower.contains("graduation") || lower.contains("graduate")) {
            return R.drawable.ic_graduation;
        }

        // === DEFAULT ===
        else {
            return R.drawable.ic_education;
        }
    }
}

