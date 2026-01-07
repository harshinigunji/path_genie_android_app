package com.SIMATS.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.SIMATS.myapplication.network.ApiConfig;
import com.SIMATS.myapplication.network.VolleySingleton;
import com.SIMATS.myapplication.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * LoginPage Activity handles user authentication.
 * Features:
 * - Email/password login via REST API
 * - Password visibility toggle
 * - Input validation
 * - Session management
 * - Navigation to Dashboard on success
 */
public class LoginPage extends AppCompatActivity {

    // UI Components
    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageView passwordToggle;
    private Button loginButton;
    private TextView forgotPassword;
    private TextView signupText;

    // State
    private boolean isPasswordVisible = false;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToHomePage();
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
    }

    /**
     * Initialize all view references.
     */
    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordToggle = findViewById(R.id.passwordToggle);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        signupText = findViewById(R.id.signupText);
    }

    /**
     * Setup click listeners for all interactive elements.
     */
    private void setupClickListeners() {
        // Login button
        loginButton.setOnClickListener(v -> attemptLogin());

        // Password visibility toggle
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());

        // Google sign-in (placeholder)

        // Forgot password
        forgotPassword.setOnClickListener(v -> showForgotPasswordDialog());

        // Sign up navigation
        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, RegisterPage.class);
            startActivity(intent);
        });
    }

    /**
     * Toggle password field visibility.
     */
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_eye_off);
        } else {
            // Show password
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_eye_on);
        }
        isPasswordVisible = !isPasswordVisible;

        // Keep cursor at end
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    /**
     * Validate inputs and attempt login API call.
     */
    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        // Validate email
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        // Validate password
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Make API call
        loginUser(email, password);
    }

    /**
     * Login using Firebase Auth first, then fetch user data from PHP.
     *
     * @param email    User's email
     * @param password User's password
     */
    private void loginUser(String email, String password) {
        // First, authenticate with Firebase (this has the latest password after reset)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Firebase auth successful, now get user data from PHP
                        fetchUserDataFromPhp(email, password);
                    } else {
                        setLoadingState(false);
                        String errorMessage = "Login failed.";
                        if (task.getException() != null) {
                            String exceptionMessage = task.getException().getMessage();
                            if (exceptionMessage != null) {
                                if (exceptionMessage.contains("no user record") ||
                                        exceptionMessage.contains("INVALID_LOGIN_CREDENTIALS")) {
                                    errorMessage = "Invalid email or password.";
                                } else if (exceptionMessage.contains("password is invalid")) {
                                    errorMessage = "Invalid password.";
                                } else if (exceptionMessage.contains("badly formatted")) {
                                    errorMessage = "Please enter a valid email address.";
                                } else if (exceptionMessage.contains("blocked")) {
                                    errorMessage = "Too many failed attempts. Try again later.";
                                } else {
                                    errorMessage = exceptionMessage;
                                }
                            }
                        }
                        Toast.makeText(LoginPage.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Fetch user data from PHP backend after Firebase auth success.
     *
     * @param email    User's email
     * @param password User's password (not used, kept for signature compatibility)
     */
    private void fetchUserDataFromPhp(String email, String password) {
        // Create JSON request body - only email needed since Firebase verified password
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
            setLoadingState(false);
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create request - use LOGIN_FIREBASE which doesn't verify password
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.LOGIN_FIREBASE,
                requestBody,
                response -> {
                    setLoadingState(false);
                    handleLoginResponse(response, email);
                },
                error -> {
                    setLoadingState(false);
                    handleLoginError(error);
                });

        // Add to request queue
        VolleySingleton.getInstance(this).addToRequestQueue(request, "login");
    }

    /**
     * Handle successful login response.
     *
     * @param response JSON response from server
     * @param email    User's email for session
     */
    private void handleLoginResponse(JSONObject response, String email) {
        try {
            boolean status = response.getBoolean("status");

            if (status) {
                // Login successful
                JSONObject data = response.getJSONObject("data");
                int userId = data.getInt("user_id");
                String token = data.getString("token");
                String fullName = data.optString("name", "");

                // Save session with full name
                sessionManager.createLoginSession(userId, token, email, fullName);

                // Save FCM token for push notifications
                saveFcmToken(userId);

                // Show success message
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                // Navigate to Home Page
                navigateToHomePage();

            } else {
                // Login failed
                String message = response.optString("message", "Login failed");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get FCM token and save to server for push notifications.
     *
     * @param userId User's ID
     */
    private void saveFcmToken(int userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }

                    String fcmToken = task.getResult();
                    if (fcmToken == null || fcmToken.isEmpty()) {
                        return;
                    }

                    // Send token to server
                    JSONObject requestBody = new JSONObject();
                    try {
                        requestBody.put("user_id", userId);
                        requestBody.put("fcm_token", fcmToken);
                    } catch (JSONException e) {
                        return;
                    }

                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            ApiConfig.SAVE_FCM_TOKEN,
                            requestBody,
                            response -> {
                                // Token saved successfully (silent)
                            },
                            error -> {
                                // Token save failed (silent)
                            });

                    VolleySingleton.getInstance(this).addToRequestQueue(request, "fcm_token");
                });
    }

    /**
     * Handle login error.
     *
     * @param error Volley error object
     */
    private void handleLoginError(com.android.volley.VolleyError error) {
        String message = "Network error. Please try again.";

        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            if (statusCode == 401) {
                message = "Invalid email or password";
            } else if (statusCode == 500) {
                message = "Server error. Please try later.";
            }

            // Try to parse error response
            try {
                String responseBody = new String(error.networkResponse.data, "UTF-8");
                JSONObject errorJson = new JSONObject(responseBody);
                if (errorJson.has("message")) {
                    message = errorJson.getString("message");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (error instanceof com.android.volley.NoConnectionError) {
            message = "No internet connection";
        } else if (error instanceof com.android.volley.TimeoutError) {
            message = "Connection timed out";
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Enable/disable loading state.
     *
     * @param isLoading true to show loading state
     */
    private void setLoadingState(boolean isLoading) {
        loginButton.setEnabled(!isLoading);
        loginButton.setText(isLoading ? "Logging in..." : "Log In");
        emailEditText.setEnabled(!isLoading);
        passwordEditText.setEnabled(!isLoading);
    }

    /**
     * Navigate to Home Page activity.
     */
    private void navigateToHomePage() {
        Intent intent = new Intent(LoginPage.this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Show forgot password dialog with email input.
     */
    private void showForgotPasswordDialog() {
        // Create dialog layout programmatically
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(60, 40, 60, 20);

        // Instructions text
        TextView instructionsText = new TextView(this);
        instructionsText
                .setText("Enter your registered email address and we'll send you a link to reset your password.");
        instructionsText.setTextSize(14);
        instructionsText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        dialogLayout.addView(instructionsText);

        // Email input
        EditText emailInput = new EditText(this);
        emailInput.setHint("Email address");
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setSingleLine(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 40, 0, 0);
        emailInput.setLayoutParams(params);
        dialogLayout.addView(emailInput);

        // Pre-fill with entered email if exists
        String enteredEmail = emailEditText.getText().toString().trim();
        if (!enteredEmail.isEmpty()) {
            emailInput.setText(enteredEmail);
        }

        // Create and show dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Forgot Password")
                .setView(dialogLayout)
                .setPositiveButton("Send Reset Link", null) // Set null to handle click manually
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String email = emailInput.getText().toString().trim();

                // Validate email
                if (email.isEmpty()) {
                    emailInput.setError("Email is required");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInput.setError("Please enter a valid email");
                    return;
                }

                // Disable button and show loading
                positiveButton.setEnabled(false);
                positiveButton.setText("Sending...");

                // Make API call
                sendForgotPasswordRequest(email, dialog, positiveButton);
            });
        });

        dialog.show();
    }

    /**
     * Send forgot password request using Firebase Auth.
     *
     * @param email          User's email
     * @param dialog         Dialog to dismiss on success
     * @param positiveButton Button to re-enable on failure
     */
    private void sendForgotPasswordRequest(String email, AlertDialog dialog, Button positiveButton) {
        // Use Firebase Auth to send password reset email
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Success - dismiss dialog and show message
                        dialog.dismiss();
                        showSuccessDialog("Password reset link has been sent to your email.");
                    } else {
                        // Error
                        positiveButton.setEnabled(true);
                        positiveButton.setText("Send Reset Link");

                        String errorMessage = "Failed to send reset email.";
                        if (task.getException() != null) {
                            String exceptionMessage = task.getException().getMessage();
                            if (exceptionMessage != null) {
                                if (exceptionMessage.contains("no user record")) {
                                    errorMessage = "No account found with this email address.";
                                } else if (exceptionMessage.contains("badly formatted")) {
                                    errorMessage = "Please enter a valid email address.";
                                } else {
                                    errorMessage = exceptionMessage;
                                }
                            }
                        }
                        Toast.makeText(LoginPage.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Show success dialog after sending reset email.
     */
    private void showSuccessDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Email Sent!")
                .setMessage(
                        message + "\n\nPlease check your email inbox (and spam folder) for the password reset link.")
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_email)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel any pending requests
        VolleySingleton.getInstance(this).cancelRequests("login");
        VolleySingleton.getInstance(this).cancelRequests("forgot_password");
    }
}
