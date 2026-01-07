package com.SIMATS.myapplication;

import com.SIMATS.myapplication.network.ApiConfig;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.SIMATS.myapplication.utils.SessionManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * ForumAskQuestionPage - Post a new question to the forum.
 */
public class ForumAskQuestionPage extends AppCompatActivity {

    private static final String TAG = "ForumAskQuestion";
    // Using centralized ApiConfig.getBaseUrl()

    private ImageView backIcon;
    private EditText inputTitle, inputDescription;
    private Spinner spinnerLevel;
    private Button btnPostQuestion;

    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    private String[] educationLevels = { "Select Level", "After 10th", "After 12th", "Diploma", "Undergraduate",
            "Postgraduate" };
    private int[] levelIds = { 0, 1, 2, 3, 4, 5 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum_ask_question_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupSpinner();
        setupClickListeners();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        spinnerLevel = findViewById(R.id.spinnerLevel);
        btnPostQuestion = findViewById(R.id.btnPostQuestion);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, educationLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapter);
    }

    private void setupClickListeners() {
        backIcon.setOnClickListener(v -> finish());

        btnPostQuestion.setOnClickListener(v -> postQuestion());
    }

    private void postQuestion() {
        String title = inputTitle.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();
        int selectedPosition = spinnerLevel.getSelectedItemPosition();

        if (title.isEmpty()) {
            inputTitle.setError("Please enter a title");
            return;
        }

        if (description.isEmpty()) {
            inputDescription.setError("Please enter a description");
            return;
        }

        if (selectedPosition == 0) {
            Toast.makeText(this, "Please select an education level", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        int educationLevelId = levelIds[selectedPosition];

        btnPostQuestion.setEnabled(false);
        btnPostQuestion.setText("Posting...");

        try {
            JSONObject payload = new JSONObject();
            payload.put("user_id", userId);
            payload.put("title", title);
            payload.put("description", description);
            payload.put("education_level_id", educationLevelId);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.getBaseUrl() + "post_question.php",
                    payload,
                    response -> {
                        if (response.optBoolean("status", false)) {
                            Toast.makeText(this, "Question posted successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String error = response.optString("message", "Failed to post question");
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                            resetButton();
                        }
                    },
                    error -> {
                        Log.e(TAG, "Error posting question", error);
                        Toast.makeText(this, "Failed to post question", Toast.LENGTH_SHORT).show();
                        resetButton();
                    });

            requestQueue.add(request);

        } catch (Exception e) {
            Log.e(TAG, "Error creating request", e);
            resetButton();
        }
    }

    private void resetButton() {
        btnPostQuestion.setEnabled(true);
        btnPostQuestion.setText("Post Question  ➤");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}


