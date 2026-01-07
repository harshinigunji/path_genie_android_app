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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * NextStreamsListPage Activity - Displays next available streams after
 * selecting a stream.
 * 
 * Navigation Flow:
 * StreamsListDetailsPage → NextStreamsListPage → NextStreamsListDetailsPage →
 * NextStreamsListPage (repeat)
 */
public class NextStreamsListPage extends AppCompatActivity {

    private static final String TAG = "NextStreamsListPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private TextView titleText;
    private LinearLayout streamsContainer;
    private ProgressBar progressBar;
    private TextView noStreamsText;
    private Button btnContinue;

    // Data
    private int currentStreamId = -1;
    private String currentStreamName = "";
    private int educationLevelId = -1;
    private List<JSONObject> streamsList = new ArrayList<>();
    private int selectedStreamId = -1;
    private String selectedStreamName = "";
    private View selectedStreamView = null;
    private boolean noStreamsAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_next_streams_list_page);

        currentStreamId = getIntent().getIntExtra("stream_id", -1);
        currentStreamName = getIntent().getStringExtra("stream_name");
        educationLevelId = getIntent().getIntExtra("education_level_id", -1);

        if (currentStreamId == -1) {
            Toast.makeText(this, "Invalid stream", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        fetchNextStreams();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        streamsContainer = findViewById(R.id.streamsContainer);
        progressBar = findViewById(R.id.progressBar);
        noStreamsText = findViewById(R.id.noStreamsText);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        btnContinue.setOnClickListener(v -> {
            if (noStreamsAvailable) {
                // Navigate to HomePage when no streams
                Intent intent = new Intent(this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                // Normal flow - navigate to stream details
                if (selectedStreamId == -1) {
                    Toast.makeText(this, "Please select a stream", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, NextStreamsListDetailsPage.class);
                intent.putExtra("stream_id", selectedStreamId);
                intent.putExtra("stream_name", selectedStreamName);
                intent.putExtra("education_level_id", educationLevelId);
                startActivity(intent);
            }
        });
    }

    private void fetchNextStreams() {
        progressBar.setVisibility(View.VISIBLE);
        streamsContainer.removeAllViews();
        noStreamsText.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "next_streams.php?stream_id=" + currentStreamId;

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
                            streamsList.clear();
                            for (int i = 0; i < streams.length(); i++) {
                                streamsList.add(streams.getJSONObject(i));
                            }

                            if (streamsList.isEmpty()) {
                                showNoStreamsState();
                            } else {
                                displayStreams();
                            }
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

    private void showNoStreamsState() {
        noStreamsAvailable = true;
        noStreamsText.setVisibility(View.VISIBLE);
        // Change Continue button to Back to Home
        btnContinue.setText("Back to Home");
        btnContinue.setEnabled(true);
        btnContinue.setBackgroundResource(R.drawable.bg_button_primary);
    }

    private void displayStreams() {
        LinearLayout currentRow = null;
        int columnCount = 0;

        for (int i = 0; i < streamsList.size(); i++) {
            JSONObject stream = streamsList.get(i);

            if (columnCount == 0) {
                currentRow = new LinearLayout(this);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                currentRow.setWeightSum(2);
                LinearLayout.LayoutParams rowParams = (LinearLayout.LayoutParams) currentRow.getLayoutParams();
                if (i > 0)
                    rowParams.topMargin = dpToPx(16);
                currentRow.setLayoutParams(rowParams);
            }

            try {
                int streamId = stream.getInt("stream_id");
                String streamName = stream.getString("stream_name");

                View cardView = createStreamCard(streamId, streamName);

                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        0, dpToPx(140), 1f);
                if (columnCount == 0) {
                    cardParams.setMarginEnd(dpToPx(8));
                } else {
                    cardParams.setMarginStart(dpToPx(8));
                }
                cardView.setLayoutParams(cardParams);

                currentRow.addView(cardView);
                columnCount++;

                if (columnCount == 2) {
                    streamsContainer.addView(currentRow);
                    columnCount = 0;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error displaying stream: " + e.getMessage());
            }
        }

        if (columnCount > 0) {
            View emptyView = new View(this);
            LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(0, dpToPx(140), 1f);
            emptyParams.setMarginStart(dpToPx(8));
            emptyView.setLayoutParams(emptyParams);
            currentRow.addView(emptyView);
            streamsContainer.addView(currentRow);
        }
    }

    private View createStreamCard(int streamId, String streamName) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(android.view.Gravity.CENTER);
        card.setBackgroundResource(R.drawable.bg_card_selectable);
        card.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Icon with light background and blue tint (matching HomePage pattern)
        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(44), dpToPx(44)));
        icon.setBackgroundResource(R.drawable.bg_icon_circle_light);
        icon.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        icon.setImageResource(getStreamIcon(streamName));
        icon.setColorFilter(android.graphics.Color.parseColor("#2563EB"), android.graphics.PorterDuff.Mode.SRC_IN);
        card.addView(icon);

        TextView nameView = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = dpToPx(12);
        nameView.setLayoutParams(nameParams);
        nameView.setText(streamName);
        nameView.setTextSize(14);
        nameView.setTypeface(null, android.graphics.Typeface.BOLD);
        nameView.setTextColor(android.graphics.Color.parseColor("#111827"));
        nameView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        card.addView(nameView);

        card.setOnClickListener(v -> {
            if (selectedStreamView != null) {
                selectedStreamView.setSelected(false);
            }
            card.setSelected(true);
            selectedStreamView = card;
            selectedStreamId = streamId;
            selectedStreamName = streamName;

            btnContinue.setEnabled(true);
            btnContinue.setBackgroundResource(R.drawable.bg_button_primary);
        });

        return card;
    }

    private int getStreamIcon(String streamName) {
        if (streamName == null)
            return R.drawable.ic_education;
        String name = streamName.toLowerCase();

        // === SCIENCE STREAMS ===
        if (name.contains("pcm") || (name.contains("science") && name.contains("math"))) {
            return R.drawable.ic_science; // Flask for PCM
        } else if (name.contains("pcb") || (name.contains("science") && name.contains("bio"))) {
            return R.drawable.ic_microscope; // Microscope for PCB/Biology
        } else if (name.contains("biology") || name.contains("life science")) {
            return R.drawable.ic_biology;
        } else if (name.contains("physics")) {
            return R.drawable.ic_bulb; // Lightbulb for Physics
        } else if (name.contains("chemistry")) {
            return R.drawable.ic_science;
        }

        // === COMMERCE & BUSINESS ===
        else if (name.contains("commerce") && !name.contains("e-commerce")) {
            return R.drawable.ic_commerce; // Commerce specific
        } else if (name.contains("accounting") || name.contains("finance")) {
            return R.drawable.ic_rupee; // Rupee for finance
        } else if (name.contains("business") || name.contains("bba") || name.contains("mba")) {
            return R.drawable.ic_briefcase; // Briefcase for business
        } else if (name.contains("marketing") || name.contains("sales")) {
            return R.drawable.ic_trend_up; // Trend for marketing
        } else if (name.contains("economics")) {
            return R.drawable.ic_balance; // Balance for economics
        }

        // === ARTS & HUMANITIES ===
        else if (name.contains("arts") && !name.contains("fine art")) {
            return R.drawable.ic_arts; // Arts generic
        } else if (name.contains("literature") || name.contains("english")) {
            return R.drawable.ic_book; // Book for literature
        } else if (name.contains("history") || name.contains("political")) {
            return R.drawable.ic_history; // Clock for history
        } else if (name.contains("psychology") || name.contains("sociology")) {
            return R.drawable.ic_user; // Person for social sciences
        } else if (name.contains("humanities") || name.contains("liberal")) {
            return R.drawable.ic_school; // School for humanities
        } else if (name.contains("journalism") || name.contains("media")) {
            return R.drawable.ic_mic; // Mic for journalism
        } else if (name.contains("fine art") || name.contains("visual art")) {
            return R.drawable.ic_design; // Design for visual arts
        }

        // === ENGINEERING & TECHNOLOGY ===
        else if (name.contains("computer") || name.contains("cse") || name.contains("software")) {
            return R.drawable.ic_computer; // Computer for CS
        } else if (name.contains("mechanical") || name.contains("automobile")) {
            return R.drawable.ic_engineering; // Gear for mechanical
        } else if (name.contains("electrical") || name.contains("electronics")) {
            return R.drawable.ic_bulb; // Bulb for electrical
        } else if (name.contains("civil") || name.contains("construction")) {
            return R.drawable.ic_building; // Building for civil
        } else if (name.contains("information tech") || name.contains("it ")) {
            return R.drawable.ic_code; // Code for IT
        } else if (name.contains("engineering") || name.contains("b.tech") || name.contains("btech")) {
            return R.drawable.ic_engineering;
        } else if (name.contains("data science") || name.contains("ai") || name.contains("machine learning")) {
            return R.drawable.ic_ai_brain; // AI brain for data science
        }

        // === MEDICAL & HEALTH ===
        else if (name.contains("mbbs") || name.contains("medicine") || name.contains("doctor")) {
            return R.drawable.ic_medical; // Medical cross
        } else if (name.contains("nursing") || name.contains("healthcare")) {
            return R.drawable.ic_heart_filled; // Heart for nursing
        } else if (name.contains("pharmacy") || name.contains("pharma")) {
            return R.drawable.ic_science; // Flask for pharmacy
        } else if (name.contains("dental") || name.contains("bds")) {
            return R.drawable.ic_medical;
        } else if (name.contains("veterinary") || name.contains("animal")) {
            return R.drawable.ic_heart_outline;
        }

        // === LAW & LEGAL ===
        else if (name.contains("law") || name.contains("llb") || name.contains("legal")) {
            return R.drawable.ic_law; // Balance scale for law
        }

        // === DESIGN & CREATIVE ===
        else if (name.contains("design") || name.contains("graphic")) {
            return R.drawable.ic_design; // Design layers
        } else if (name.contains("architecture") || name.contains("interior")) {
            return R.drawable.ic_architecture; // Building design
        } else if (name.contains("fashion")) {
            return R.drawable.ic_star; // Star for fashion
        } else if (name.contains("animation") || name.contains("multimedia")) {
            return R.drawable.ic_camera; // Camera for multimedia
        }

        // === AVIATION & TRAVEL ===
        else if (name.contains("aviation") || name.contains("pilot") || name.contains("aerospace")) {
            return R.drawable.ic_aviation; // Plane for aviation
        } else if (name.contains("hotel") || name.contains("hospitality") || name.contains("tourism")) {
            return R.drawable.ic_building; // Building for hospitality
        }

        // === VOCATIONAL & DIPLOMA ===
        else if (name.contains("vocational") || name.contains("skill") || name.contains("iti")) {
            return R.drawable.ic_vocational; // Tools for vocational
        } else if (name.contains("diploma") || name.contains("polytechnic")) {
            return R.drawable.ic_diploma; // Certificate for diploma
        }

        // === GOVERNMENT & COMPETITIVE ===
        else if (name.contains("civil service") || name.contains("upsc") || name.contains("ias")) {
            return R.drawable.ic_govt; // Government building
        } else if (name.contains("bank") || name.contains("ssc")) {
            return R.drawable.ic_rupee; // Rupee for banking
        }

        // === RESEARCH & ACADEMIA ===
        else if (name.contains("research") || name.contains("phd") || name.contains("doctorate")) {
            return R.drawable.ic_research; // Research icon
        } else if (name.contains("teaching") || name.contains("education") || name.contains("b.ed")) {
            return R.drawable.ic_school; // School for education
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

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}

