package com.SIMATS.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.SIMATS.myapplication.network.ApiConfig;
import com.SIMATS.myapplication.network.VolleyMultipartRequest;
import com.SIMATS.myapplication.utils.SessionManager;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * EditProfilePage - Allows user to edit their profile
 * Updated:
 * - Loads existing profile image
 * - Robust upload with resize and timeout policy
 */
public class EditProfilePage extends AppCompatActivity {

    private static final String TAG = "EditProfilePage";

    // UI Components
    private ImageView backButton;
    private TextView btnSave;
    private ProgressBar loadingProgress;
    private ScrollView contentScrollView;
    private ImageView profileImage;
    private View btnCamera; // FrameLayout
    private TextView btnChangePhoto;
    private EditText etFullName, etEmail, etPhone, etDateOfBirth;
    private EditText etEducationLevel, etSchool, etBoard, etAspiringCareer;
    private LinearLayout cardChangePassword;
    private Button btnSaveChanges;

    private SessionManager sessionManager;
    private int userId;

    // Image Picking
    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupImagePickers();
        initViews();
        setupClickListeners();
        fetchProfileData();
    }

    private void setupImagePickers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        processImage(bitmap);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            processImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void processImage(Bitmap bitmap) {
        // Show immediately locally
        profileImage.setImageBitmap(bitmap);
        // Upload
        uploadProfileImage(bitmap);
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        btnSave = findViewById(R.id.btnSave);
        loadingProgress = findViewById(R.id.loadingProgress);
        contentScrollView = findViewById(R.id.contentScrollView);
        profileImage = findViewById(R.id.profileImage);
        btnCamera = findViewById(R.id.btnCamera);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etEducationLevel = findViewById(R.id.etEducationLevel);
        etSchool = findViewById(R.id.etSchool);
        etBoard = findViewById(R.id.etBoard);
        etAspiringCareer = findViewById(R.id.etAspiringCareer);

        cardChangePassword = findViewById(R.id.cardChangePassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveProfile());
        btnSaveChanges.setOnClickListener(v -> saveProfile());

        cardChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfilePage.this, ChangePasswordPage.class);
            startActivity(intent);
        });

        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        View.OnClickListener photoListener = v -> showImageSourceDialog();
        btnCamera.setOnClickListener(photoListener);
        btnChangePhoto.setOnClickListener(photoListener);
        profileImage.setOnClickListener(photoListener);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 15; // Default to 15 years ago
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedYear + "-" + String.format("%02d", selectedMonth + 1)
                            + "-" + String.format("%02d", selectedDay);
                    etDateOfBirth.setText(date);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showImageSourceDialog() {
        String[] options = { "Take Photo", "Choose from Gallery", "Remove Photo" };
        new AlertDialog.Builder(this)
                .setTitle("Change Profile Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndLaunch();
                    } else if (which == 1) {
                        galleryLauncher.launch("image/*");
                    } else {
                        confirmRemoveProfileImage();
                    }
                })
                .show();
    }

    private void confirmRemoveProfileImage() {
        new AlertDialog.Builder(this)
                .setTitle("Remove Profile Photo")
                .setMessage("Are you sure you want to remove your profile photo?")
                .setPositiveButton("Remove", (dialog, which) -> removeProfileImage())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeProfileImage() {
        loadingProgress.setVisibility(View.VISIBLE);
        String url = ApiConfig.getBaseUrl() + "remove_profile_image.php";

        com.android.volley.toolbox.StringRequest request = new com.android.volley.toolbox.StringRequest(
                Request.Method.POST, url,
                response -> {
                    loadingProgress.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean status = jsonObject.optBoolean("status", false);
                        if (status) {
                            Toast.makeText(this, "Profile picture removed", Toast.LENGTH_SHORT).show();
                            profileImage.setImageResource(R.drawable.ic_avatar);
                        } else {
                            Toast.makeText(this, "Failed to remove picture", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingProgress.setVisibility(View.GONE);
                    Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void checkCameraPermissionAndLaunch() {
        if (checkSelfPermission(
                android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(null);
        } else {
            requestPermissions(new String[] { android.Manifest.permission.CAMERA }, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(null);
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadProfileImage(Bitmap bitmap) {
        // Show loading but don't block user from editing other fields
        // Or show a toast saying "Uploading..."
        Toast.makeText(this, "Uploading profile picture...", Toast.LENGTH_SHORT).show();

        String url = ApiConfig.getBaseUrl() + "upload_profile_image.php";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> {
                    try {
                        String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        Log.d(TAG, "Upload Response: " + jsonString);
                        JSONObject jsonObject = new JSONObject(jsonString);
                        boolean status = jsonObject.optBoolean("status", false);
                        String message = jsonObject.optString("message", "Upload failed");

                        if (status) {
                            Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                            // If we received a new URL, we could log it
                            String imageUrl = jsonObject.optString("image_url");
                            Log.d(TAG, "New Image URL: " + imageUrl);
                        } else {
                            Toast.makeText(this, "Upload failed: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing upload response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    String errorMsg = "Network Error";
                    if (error.networkResponse != null) {
                        errorMsg += " (" + error.networkResponse.statusCode + ")";
                    }
                    Toast.makeText(this, errorMsg + ". Try a smaller image.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // Resize image before upload to avoid timeouts with large files
                byte[] imageData = getFileDataFromDrawable(bitmap);
                params.put("image", new DataPart("profile_" + System.currentTimeMillis() + ".jpg",
                        imageData, "image/jpeg"));
                return params;
            }
        };

        // Set explicit retry policy for slower connections or large files
        // 30 seconds timeout, 1 retry
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(multipartRequest);
    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        // Resize bitmap if too large (max 1024x1024)
        int maxHeight = 1024;
        int maxWidth = 1024;
        float scale = Math.min(((float) maxHeight / bitmap.getWidth()), ((float) maxWidth / bitmap.getHeight()));

        Bitmap finalBitmap = bitmap;
        if (scale < 1) {
            int width = Math.round(bitmap.getWidth() * scale);
            int height = Math.round(bitmap.getHeight() * scale);
            finalBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress to JPEG 80%
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void fetchProfileData() {
        loadingProgress.setVisibility(View.VISIBLE);
        contentScrollView.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "get_profile.php?user_id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    loadingProgress.setVisibility(View.GONE);
                    contentScrollView.setVisibility(View.VISIBLE);

                    try {
                        boolean status = response.optBoolean("status", false);
                        if (status) {
                            JSONObject data = response.getJSONObject("data");
                            populateForm(data);
                        } else {
                            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                    }
                },
                error -> {
                    loadingProgress.setVisibility(View.GONE);
                    contentScrollView.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Network error", error);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void populateForm(JSONObject data) {
        try {
            etFullName.setText(data.optString("full_name", ""));
            etEmail.setText(data.optString("email", ""));
            etPhone.setText(data.optString("phone", ""));

            String dob = data.optString("date_of_birth", "");
            if (!dob.isEmpty() && !dob.equals("null")) {
                etDateOfBirth.setText(dob);
            }

            etEducationLevel.setText(data.optString("education_level", ""));
            etSchool.setText(data.optString("current_school", ""));
            etBoard.setText(data.optString("board", ""));
            etAspiringCareer.setText(data.optString("aspiring_career", ""));

            // Load existing profile image
            String imagePath = data.optString("profile_image", "");
            if (!imagePath.isEmpty()) {
                // If path is relative (uploads/...), prepend base url
                String fullUrl = imagePath.startsWith("http") ? imagePath : ApiConfig.getBaseUrl() + imagePath;
                loadProfileImage(fullUrl);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error populating form", e);
        }
    }

    private void loadProfileImage(String url) {
        ImageRequest imageRequest = new ImageRequest(url,
                bitmap -> profileImage.setImageBitmap(bitmap),
                0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                error -> {
                    Log.e(TAG, "Error loading image: " + error.getMessage());
                });
        Volley.newRequestQueue(this).add(imageRequest);
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDateOfBirth.getText().toString().trim();
        String educationLevel = etEducationLevel.getText().toString().trim();
        String school = etSchool.getText().toString().trim();
        String board = etBoard.getText().toString().trim();
        String aspiringCareer = etAspiringCareer.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Name is required");
            etFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        loadingProgress.setVisibility(View.VISIBLE);

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("user_id", userId);
            requestBody.put("full_name", fullName);
            requestBody.put("email", email);
            requestBody.put("phone", phone);
            requestBody.put("date_of_birth", dob);
            requestBody.put("education_level", educationLevel);
            requestBody.put("current_school", school);
            requestBody.put("board", board);
            requestBody.put("aspiring_career", aspiringCareer);

            String url = ApiConfig.getBaseUrl() + "update_profile.php";
            Log.d(TAG, "Saving profile to: " + url);

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                    response -> {
                        loadingProgress.setVisibility(View.GONE);
                        Log.d(TAG, "Response: " + response.toString());

                        try {
                            boolean status = response.optBoolean("status", false);
                            if (status) {
                                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                String message = response.optString("message", "Failed to update profile");
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response", e);
                            Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        loadingProgress.setVisibility(View.GONE);
                        Log.e(TAG, "Network error", error);
                        Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    });

            queue.add(request);

        } catch (Exception e) {
            loadingProgress.setVisibility(View.GONE);
            Log.e(TAG, "Error creating request", e);
            Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
        }
    }
}
