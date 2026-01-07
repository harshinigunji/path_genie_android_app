package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.SIMATS.myapplication.network.VolleySingleton;

import org.json.JSONObject;

/**
 * StreamDetailsPage Activity - Displays detailed information about a stream.
 * Fetches data from stream_details.php API based on stream_id.
 */
public class StreamDetailsPage extends AppCompatActivity {

    private static final String TAG = "StreamDetailsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private ProgressBar progressBar;
    private ScrollView contentScroll;
    private TextView streamName;
    private ImageView streamIcon;
    private TextView streamDescription;
    private TextView subjectsText;
    private TextView difficultyLevel;
    private TextView durationText;
    private LinearLayout whoShouldChooseContainer;
    private GridLayout futureScopeGrid;
    private Button viewEntranceExamButton;

    // Data
    private int streamId = -1;
    private int educationLevelId = -1;
    private String currentStreamName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stream_details_page);

        // Get data from intent
        streamId = getIntent().getIntExtra("stream_id", -1);
        educationLevelId = getIntent().getIntExtra("education_level_id", 1);

        if (streamId == -1) {
            Toast.makeText(this, "Invalid stream", Toast.LENGTH_SHORT).show();
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

        // Setup click listeners
        setupClickListeners();

        // Fetch stream details from API
        fetchStreamDetails();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        contentScroll = findViewById(R.id.contentScroll);
        streamName = findViewById(R.id.streamName);
        streamIcon = findViewById(R.id.streamIcon);
        streamDescription = findViewById(R.id.streamDescription);
        subjectsText = findViewById(R.id.subjectsText);
        difficultyLevel = findViewById(R.id.difficultyLevel);
        durationText = findViewById(R.id.durationText);
        whoShouldChooseContainer = findViewById(R.id.whoShouldChooseContainer);
        futureScopeGrid = findViewById(R.id.futureScopeGrid);
        viewEntranceExamButton = findViewById(R.id.viewEntranceExamButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        viewEntranceExamButton.setOnClickListener(v -> {
            // Navigate to exams page for this stream
            Intent intent = new Intent(this, StreamExamsPage.class);
            intent.putExtra("stream_id", streamId);
            intent.putExtra("education_level_id", educationLevelId);
            intent.putExtra("stream_name", currentStreamName);
            startActivity(intent);
        });
    }

    /**
     * Fetch stream details from API.
     */
    private void fetchStreamDetails() {
        progressBar.setVisibility(View.VISIBLE);
        contentScroll.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "stream_details.php?stream_id=" + streamId;

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
                            JSONObject stream = response.getJSONObject("data");
                            displayStreamDetails(stream);
                        } else {
                            String message = response.optString("message", "Failed to fetch stream details");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(this, "Error loading stream details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching stream details: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Display stream details from API response.
     */
    private void displayStreamDetails(JSONObject stream) {
        try {
            // Stream name
            currentStreamName = stream.getString("stream_name");
            streamName.setText(currentStreamName);

            // Set stream icon
            streamIcon.setImageResource(getStreamIcon(currentStreamName));
            streamIcon.setColorFilter(android.graphics.Color.parseColor("#2563EB"), android.graphics.PorterDuff.Mode.SRC_IN);

            // Description
            String description = stream.optString("description", "");
            streamDescription.setText(description);

            // Subjects (What is this stream?)
            String subjects = stream.optString("subjects", "");
            if (!subjects.isEmpty()) {
                subjectsText.setText(subjects);
            }

            // Duration
            String duration = stream.optString("duration", "2 Years");
            durationText.setText(duration);

            // Difficulty level
            String difficulty = stream.optString("difficulty_level", "Higher Secondary");
            difficultyLevel.setText(difficulty);

            // Who should choose
            String whoShouldChoose = stream.optString("who_should_choose", "");
            displayWhoShouldChoose(whoShouldChoose);

            // Career scope / Future scope
            String careerScope = stream.optString("career_scope", "");
            displayFutureScope(careerScope);

            // Update button text
            viewEntranceExamButton.setText("View entrance exam for " + getShortStreamName(currentStreamName) + "  →");

        } catch (Exception e) {
            Log.e(TAG, "Error displaying stream: " + e.getMessage());
        }
    }

    /**
     * Display who should choose bullet points.
     */
    private void displayWhoShouldChoose(String whoShouldChoose) {
        whoShouldChooseContainer.removeAllViews();

        if (whoShouldChoose.isEmpty()) {
            // Default content
            addBulletPoint("Students interested in this field.");
            return;
        }

        // Split by newlines or commas
        String[] points = whoShouldChoose.split("[\\n,;]+");
        for (String point : points) {
            String trimmed = point.trim();
            if (!trimmed.isEmpty()) {
                addBulletPoint(trimmed);
            }
        }
    }

    /**
     * Add a bullet point to who should choose container.
     */
    private void addBulletPoint(String text) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = 8;
        row.setLayoutParams(rowParams);

        ImageView check = new ImageView(this);
        check.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(20), dpToPx(20)));
        check.setImageResource(R.drawable.ic_check_green);

        TextView textView = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.leftMargin = dpToPx(8);
        textView.setLayoutParams(textParams);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setTextColor(getColor(R.color.black));

        row.addView(check);
        row.addView(textView);
        whoShouldChooseContainer.addView(row);
    }

    /**
     * Display future scope grid.
     */
    private void displayFutureScope(String careerScope) {
        futureScopeGrid.removeAllViews();

        if (careerScope.isEmpty()) {
            // Default careers
            addFutureScopeItem("Engineering");
            addFutureScopeItem("Research");
            return;
        }

        // Split by newlines or commas
        String[] careers = careerScope.split("[\\n,;]+");
        for (String career : careers) {
            String trimmed = career.trim();
            if (!trimmed.isEmpty()) {
                addFutureScopeItem(trimmed);
            }
        }
    }

    /**
     * Add a future scope item to the grid.
     */
    private void addFutureScopeItem(String text) {
        TextView item = new TextView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dpToPx(60);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
        item.setLayoutParams(params);
        item.setText(text);
        item.setGravity(Gravity.CENTER);
        item.setTextSize(14);
        item.setTextColor(getColor(R.color.black));
        item.setBackgroundResource(R.drawable.bg_card_light);
        item.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));

        futureScopeGrid.addView(item);
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
        } else if (lower.contains("chemistry")) {
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
        } else if (lower.contains("humanities") || lower.contains("liberal")) {
            return R.drawable.ic_school;
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

    /**
     * Get short stream name for button.
     */
    private String getShortStreamName(String name) {
        if (name.contains("(") && name.contains(")")) {
            int start = name.indexOf("(");
            int end = name.indexOf(")");
            return name.substring(start + 1, end);
        }
        return name.split(" ")[0];
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}

