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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * SystemGenerateP2Page - Step 2: Select Stream
 */
public class SystemGenerateP2Page extends AppCompatActivity {

    private static final String TAG = "SystemGenerateP2";
    // Using centralized ApiConfig.getBaseUrl()

    private ImageView backButton;
    private Button nextButton;
    private ProgressBar loadingProgress;
    private ScrollView contentScroll;
    private LinearLayout streamsContainer;
    private TextView subtitleText;

    // Data from P1
    private int educationLevelId = -1;
    private String educationLevelName = "";

    // Selected stream
    private int selectedStreamId = -1;
    private String selectedStreamName = "";
    private String selectedStreamDescription = "";
    private View currentSelectedCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_system_generate_p2_page);

        // Get data from P1
        educationLevelId = getIntent().getIntExtra("education_level_id", -1);
        educationLevelName = getIntent().getStringExtra("education_level_name");
        if (educationLevelName == null)
            educationLevelName = "";

        Log.d(TAG, "Received education_level_id: " + educationLevelId);
        Log.d(TAG, "Received education_level_name: " + educationLevelName);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        fetchStreams();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        loadingProgress = findViewById(R.id.loadingProgress);
        contentScroll = findViewById(R.id.contentScroll);
        streamsContainer = findViewById(R.id.streamsContainer);
        subtitleText = findViewById(R.id.subtitleText);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        nextButton.setOnClickListener(v -> {
            if (selectedStreamId != -1) {
                Intent intent = new Intent(this, SystemGenerateP3Page.class);
                intent.putExtra("education_level_id", educationLevelId);
                intent.putExtra("education_level_name", educationLevelName);
                intent.putExtra("stream_id", selectedStreamId);
                intent.putExtra("stream_name", selectedStreamName);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a stream", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStreams() {
        loadingProgress.setVisibility(View.VISIBLE);
        contentScroll.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "system_streams.php?education_level_id=" + educationLevelId;
        Log.d(TAG, "Fetching streams from: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    loadingProgress.setVisibility(View.GONE);
                    contentScroll.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Response: " + response.toString());

                    try {
                        boolean success = response.optBoolean("success", false);
                        if (success) {
                            JSONArray streams = response.optJSONArray("data");
                            if (streams != null && streams.length() > 0) {
                                populateStreams(streams);
                            } else {
                                Toast.makeText(this, "No streams found for this level", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String message = response.optString("message", "Unknown error");
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "API error: " + message);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Parse error: " + e.getMessage(), e);
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingProgress.setVisibility(View.GONE);
                    contentScroll.setVisibility(View.VISIBLE);
                    String errorMsg = error.getMessage() != null ? error.getMessage() : "Network error";
                    Log.e(TAG, "Network error: " + errorMsg, error);
                    Toast.makeText(this, "Network error: " + errorMsg, Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void populateStreams(JSONArray streams) {
        streamsContainer.removeAllViews();

        int[] icons = { R.drawable.ic_science, R.drawable.ic_microscope, R.drawable.ic_commerce,
                R.drawable.ic_arts, R.drawable.ic_vocational };

        try {
            for (int i = 0; i < streams.length(); i++) {
                JSONObject stream = streams.getJSONObject(i);
                int streamId = stream.optInt("stream_id", 0);
                String streamName = stream.optString("stream_name", "Unknown");
                String description = stream.optString("description", "");

                Log.d(TAG, "Adding stream: " + streamId + " - " + streamName);

                View card = createStreamCard(streamId, streamName, description, icons[i % icons.length]);
                streamsContainer.addView(card);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating streams: " + e.getMessage(), e);
            Toast.makeText(this, "Error displaying streams", Toast.LENGTH_SHORT).show();
        }
    }

    private View createStreamCard(int streamId, String name, String description, int iconRes) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setBackgroundResource(R.drawable.bg_card_selectable);

        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        card.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = (int) (12 * getResources().getDisplayMetrics().density);
        card.setLayoutParams(params);

        // Icon
        ImageView icon = new ImageView(this);
        int iconSize = (int) (44 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        icon.setLayoutParams(iconParams);
        icon.setImageResource(iconRes);
        icon.setBackgroundResource(R.drawable.bg_icon_circle_light);
        icon.setColorFilter(0xFF2563EB);
        int iconPadding = (int) (10 * getResources().getDisplayMetrics().density);
        icon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        card.addView(icon);

        // Text container
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,
                1);
        textParams.leftMargin = (int) (16 * getResources().getDisplayMetrics().density);
        textContainer.setLayoutParams(textParams);

        TextView nameText = new TextView(this);
        nameText.setText(name);
        nameText.setTextSize(16);
        nameText.setTextColor(0xFF111827);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);
        textContainer.addView(nameText);

        if (description != null && !description.isEmpty()) {
            TextView descText = new TextView(this);
            descText.setText(description);
            descText.setTextSize(13);
            descText.setTextColor(0xFF6B7280);
            descText.setMaxLines(2);
            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            descParams.topMargin = (int) (4 * getResources().getDisplayMetrics().density);
            descText.setLayoutParams(descParams);
            textContainer.addView(descText);
        }

        card.addView(textContainer);

        // Checkmark for selected state
        ImageView checkmark = new ImageView(this);
        int checkSize = (int) (24 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(checkSize, checkSize);
        checkmark.setLayoutParams(checkParams);
        checkmark.setImageResource(R.drawable.ic_check);
        checkmark.setColorFilter(0xFF2563EB);
        checkmark.setVisibility(View.GONE);
        card.addView(checkmark);

        final ImageView finalCheckmark = checkmark;
        card.setOnClickListener(v -> {
            // Deselect previous
            if (currentSelectedCard != null) {
                currentSelectedCard.setSelected(false);
                View prevCheck = ((LinearLayout) currentSelectedCard).getChildAt(2);
                if (prevCheck != null)
                    prevCheck.setVisibility(View.GONE);
            }

            // Select new
            card.setSelected(true);
            finalCheckmark.setVisibility(View.VISIBLE);
            currentSelectedCard = card;
            selectedStreamId = streamId;
            selectedStreamName = name;
            selectedStreamDescription = description;

            Log.d(TAG, "Selected stream: " + streamId + " - " + name);
        });

        return card;
    }
}

