package com.SIMATS.myapplication;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.SIMATS.myapplication.utils.SessionManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * ChangePasswordPage - Allows user to change their password via Firebase.
 * Features:
 * - Re-authentication with current password
 * - Updates Firebase password
 * - "Forgot Password" link (sends reset email)
 */
public class ChangePasswordPage extends AppCompatActivity {

    private static final String TAG = "ChangePasswordPage";

    // UI Components
    private ImageView backButton;
    private ProgressBar loadingProgress;
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private ImageView btnToggleCurrentPassword, btnToggleNewPassword, btnToggleConfirmPassword;
    private TextView btnForgotPassword;
    private Button btnSavePassword;

    private SessionManager sessionManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    // Password visibility states
    private boolean isCurrentPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        loadingProgress = findViewById(R.id.loadingProgress);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnToggleCurrentPassword = findViewById(R.id.btnToggleCurrentPassword);
        btnToggleNewPassword = findViewById(R.id.btnToggleNewPassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        btnSavePassword.setOnClickListener(v -> changePassword());

        btnForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());

        // Password visibility toggles
        btnToggleCurrentPassword.setOnClickListener(v -> {
            isCurrentPasswordVisible = !isCurrentPasswordVisible;
            togglePasswordVisibility(etCurrentPassword, btnToggleCurrentPassword, isCurrentPasswordVisible);
        });

        btnToggleNewPassword.setOnClickListener(v -> {
            isNewPasswordVisible = !isNewPasswordVisible;
            togglePasswordVisibility(etNewPassword, btnToggleNewPassword, isNewPasswordVisible);
        });

        btnToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            togglePasswordVisibility(etConfirmPassword, btnToggleConfirmPassword, isConfirmPasswordVisible);
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleButton, boolean isVisible) {
        if (isVisible) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_eye);
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_eye_off);
        }
        // Move cursor to end
        editText.setSelection(editText.getText().length());
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validation
        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(this, "Error: User not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentPassword.isEmpty()) {
            etCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Proceed to re-authenticate and update
        loadingProgress.setVisibility(View.VISIBLE);
        btnSavePassword.setEnabled(false);

        // 1. Re-authenticate user
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

        currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // 2. Update password
                currentUser.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                    loadingProgress.setVisibility(View.GONE);
                    btnSavePassword.setEnabled(true);
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(ChangePasswordPage.this, "Password updated successfully", Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordPage.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                loadingProgress.setVisibility(View.GONE);
                btnSavePassword.setEnabled(true);
                Toast.makeText(ChangePasswordPage.this, "Incorrect current password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Show forgot password dialog with email input.
     */
    private void showForgotPasswordDialog() {
        // Reuse email from session or current user
        String userEmail = "";
        if (currentUser != null && currentUser.getEmail() != null) {
            userEmail = currentUser.getEmail();
        }

        // Create dialog layout programmatically
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(60, 40, 60, 20);

        // Instructions text
        TextView instructionsText = new TextView(this);
        instructionsText.setText("Send password reset link to your email?");
        instructionsText.setTextSize(14);
        instructionsText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        dialogLayout.addView(instructionsText);

        // Email input (Read Only as it's the current user)
        EditText emailInput = new EditText(this);
        emailInput.setText(userEmail);
        emailInput.setEnabled(false); // Can't change email here, creates confusion
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 40, 0, 0);
        emailInput.setLayoutParams(params);
        dialogLayout.addView(emailInput);

        // Create and show dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Forgot Password")
                .setView(dialogLayout)
                .setPositiveButton("Send Link", (dialogInterface, which) -> {
                    if (currentUser != null && currentUser.getEmail() != null) {
                        sendForgotPasswordRequest(currentUser.getEmail());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void sendForgotPasswordRequest(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        new AlertDialog.Builder(ChangePasswordPage.this)
                                .setTitle("Email Sent!")
                                .setMessage("Please check your email inbox for the password reset link.")
                                .setPositiveButton("OK", null)
                                .show();
                    } else {
                        Toast.makeText(ChangePasswordPage.this, "Failed to send reset email.", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }
}
