package com.SIMATS.myapplication;

import com.SIMATS.myapplication.network.ApiConfig;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.SIMATS.myapplication.network.VolleySingleton;
import com.SIMATS.myapplication.utils.SessionManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AiAssistantPage extends AppCompatActivity {

    private static final String TAG = "AiAssistantPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private TextView chipStreams, chipExams, chipCareers;

    // Chat Data
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private SessionManager sessionManager;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_assistant_page);

        // Setup edge-to-edge
        try {
            android.view.View mainView = findViewById(R.id.main);
            if (mainView != null) {
                ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "EdgeToEdge configuration failed", e);
        }

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        initViews();
        setupRecyclerView();
        setupClickListeners();

        // Start Animated Greeting
        animateGreeting(
                "Hello I'm your Path Genie.\nI can help you choose the right stream, find entrance exams, or explore career paths.");
    }

    private void animateGreeting(String fullMessage) {
        addBotMessage(""); // Add empty message first
        final int messageIndex = chatMessages.size() - 1;
        final long typingDelay = 50; // Delay per character in ms

        new android.os.Handler(android.os.Looper.getMainLooper()).post(new Runnable() {
            int charIndex = 0;
            StringBuilder currentText = new StringBuilder();

            @Override
            public void run() {
                if (charIndex < fullMessage.length()) {
                    currentText.append(fullMessage.charAt(charIndex));
                    chatMessages.get(messageIndex).setMessage(currentText.toString());
                    chatAdapter.notifyItemChanged(messageIndex);
                    charIndex++;
                    scrollToBottom();
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this, typingDelay);
                }
            }
        });
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chipStreams = findViewById(R.id.chipStreams);
        chipExams = findViewById(R.id.chipExams);
        chipCareers = findViewById(R.id.chipCareers);
    }

    private void setupRecyclerView() {
        if (chatRecyclerView != null) {
            chatMessages = new ArrayList<>();
            chatAdapter = new ChatAdapter(chatMessages);
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            chatRecyclerView.setAdapter(chatAdapter);
        }
    }

    private void setupClickListeners() {
        if (backButton != null)
            backButton.setOnClickListener(v -> finish());

        if (sendButton != null) {
            sendButton.setOnClickListener(v -> {
                if (messageInput != null) {
                    String message = messageInput.getText().toString().trim();
                    if (!message.isEmpty()) {
                        sendMessage(message);
                        messageInput.setText("");
                    }
                }
            });
        }

        // Chip Quick Actions
        if (chipStreams != null)
            chipStreams.setOnClickListener(v -> sendMessage("Best Streams after 10th?"));
        if (chipExams != null)
            chipExams.setOnClickListener(v -> sendMessage("List of Entrance Exams?"));
        if (chipCareers != null)
            chipCareers.setOnClickListener(v -> sendMessage("Trending Career Paths?"));
    }

    private void sendMessage(String message) {
        // Add User Message
        addUserMessage(message);

        // Show Typing Indicator
        addBotMessage("Typing...");
        final int typingMessageIndex = chatMessages.size() - 1;

        // API Call
        // Using chatbot.php as the bridge to the Python backend
        String url = ApiConfig.getBaseUrl() + "chatbot.php";

        JSONObject requestBody = new JSONObject();
        try {
            // STRICT CONTRACT: Only "message" field. No user_id.
            requestBody.put("message", message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    // Remove Typing Indicator
                    removeMessage(typingMessageIndex);

                    try {
                        boolean status = response.getBoolean("status");
                        if (status) {
                            // Contract: {"status": true, "reply": "response text"}
                            String reply = response.getString("reply");
                            // animateResponse(reply); // TODO: Future enhancement
                            addBotMessage(reply);
                        } else {
                            addBotMessage("I'm having trouble understanding. Please try again.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        addBotMessage("Something went wrong. Please try again.");
                    }
                },
                error -> {
                    // Remove Typing Indicator
                    removeMessage(typingMessageIndex);

                    Log.e(TAG, "Error sending message: " + error.toString());
                    addBotMessage("I'm having trouble connecting right now. Please check your internet connection.");
                });

        // Set longer timeout for AI processing
        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                30000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void removeMessage(int index) {
        if (chatMessages != null && chatAdapter != null && index >= 0 && index < chatMessages.size()) {
            chatMessages.remove(index);
            chatAdapter.notifyItemRemoved(index);
        }
    }

    private void addUserMessage(String message) {
        if (chatMessages != null && chatAdapter != null) {
            chatMessages.add(new ChatMessage(message, true, getCurrentTime()));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            scrollToBottom();
        }
    }

    private void addBotMessage(String message) {
        if (chatMessages != null && chatAdapter != null) {
            chatMessages.add(new ChatMessage(message, false, getCurrentTime()));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        if (chatMessages != null && !chatMessages.isEmpty() && chatRecyclerView != null) {
            chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
        }
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }
}
