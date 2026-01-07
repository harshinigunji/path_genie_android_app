package com.SIMATS.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.SIMATS.myapplication.network.ApiConfig;
import com.SIMATS.myapplication.network.VolleyMultipartRequest;
import com.SIMATS.myapplication.network.VolleyMultipartRequest.DataPart;
import com.SIMATS.myapplication.utils.SessionManager;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class ProfilePage extends AppCompatActivity {

    private static final String TAG = "ProfilePage";

    // UI Components
    private ImageView backButton;
    private ProgressBar loadingProgress;
    private ScrollView contentScrollView;
    private ImageView profileImage;
    private View btnCamera;
    private TextView textName, textEducation, textAspiringCareer;
    private TextView textEmail, textPhone, textDob;
    private TextView textSchool, textBoard;
    private TextView textRoadmapsCount;
    private Button btnEditProfile, btnLogout;
    private LinearLayout cardViewRoadmaps;
    private LinearLayout navHome, navCommunity, navSaved, navProfile;

    private SessionManager sessionManager;

    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private String currentImageUrl = ""; // To store current image URL for full screen view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);

        setupImagePickers();
        initViews();
        setupClickListeners();
    }

    private void setupImagePickers() {
        cameraLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        uploadProfileImage(bitmap);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(),
                                    uri);
                            uploadProfileImage(bitmap);
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProfileData();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        loadingProgress = findViewById(R.id.loadingProgress);
        contentScrollView = findViewById(R.id.contentScrollView);
        profileImage = findViewById(R.id.profileImage);
        // btnCamera refers to the old camera button, we now use btnEditIcon
        btnCamera = findViewById(R.id.btnEditIcon);
        textName = findViewById(R.id.textName);
        textEducation = findViewById(R.id.textEducation);
        textAspiringCareer = findViewById(R.id.textAspiringCareer);
        textEmail = findViewById(R.id.textEmail);
        textPhone = findViewById(R.id.textPhone);
        textDob = findViewById(R.id.textDob);
        textSchool = findViewById(R.id.textSchool);
        textBoard = findViewById(R.id.textBoard);
        textRoadmapsCount = findViewById(R.id.textRoadmapsCount);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        cardViewRoadmaps = findViewById(R.id.cardViewRoadmaps);

        navHome = findViewById(R.id.navHome);
        navCommunity = findViewById(R.id.navCommunity);
        navSaved = findViewById(R.id.navSaved);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePage.this, EditProfilePage.class);
            startActivity(intent);
        });

        // Edit Icon -> Show Camera/Gallery Dialog
        if (btnCamera != null) {
            btnCamera.setOnClickListener(v -> showImageSourceDialog());
        }

        // Profile Image -> Full Screen View
        profileImage.setOnClickListener(v -> showFullScreenImage());

        cardViewRoadmaps.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePage.this, SavedRoadmapListPage.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomePage.class));
            finish();
        });
        navCommunity.setOnClickListener(v -> {
            startActivity(new Intent(this, ForumHomePage.class));
            finish();
        });
        navSaved.setOnClickListener(v -> {
            startActivity(new Intent(this, SavedRoadmapListPage.class));
            finish();
        });

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
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
                            currentImageUrl = ""; // clear stored url
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
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("user_id", String.valueOf(sessionManager.getUserId()));
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
        Toast.makeText(this, "Uploading profile picture...", Toast.LENGTH_SHORT).show();
        String url = ApiConfig.getBaseUrl() + "upload_profile_image.php";

        com.SIMATS.myapplication.network.VolleyMultipartRequest multipartRequest = new com.SIMATS.myapplication.network.VolleyMultipartRequest(
                Request.Method.POST, url,
                response -> {
                    try {
                        String jsonString = new String(response.data,
                                com.android.volley.toolbox.HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonObject = new JSONObject(jsonString);
                        boolean status = jsonObject.optBoolean("status", false);
                        if (status) {
                            Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                            fetchProfileData(); // Refresh to show new image
                        } else {
                            String message = jsonObject.optString("message", "Upload failed");
                            Toast.makeText(this, "Upload failed: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing upload response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Network Error. Try a smaller image.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("user_id", String.valueOf(sessionManager.getUserId()));
                return params;
            }

            @Override
            protected java.util.Map<String, DataPart> getByteData() {
                java.util.Map<String, DataPart> params = new java.util.HashMap<>();
                byte[] imageData = getFileDataFromDrawable(bitmap);
                params.put("image",
                        new DataPart("profile_" + System.currentTimeMillis() + ".jpg", imageData, "image/jpeg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(30000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(multipartRequest);
    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        int maxHeight = 1024;
        int maxWidth = 1024;
        float scale = Math.min(((float) maxHeight / bitmap.getWidth()), ((float) maxWidth / bitmap.getHeight()));
        Bitmap finalBitmap = bitmap;
        if (scale < 1) {
            int width = Math.round(bitmap.getWidth() * scale);
            int height = Math.round(bitmap.getHeight() * scale);
            finalBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
        java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void showFullScreenImage() {
        if (currentImageUrl == null || currentImageUrl.isEmpty())
            return;

        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        ImageView fullScreenImageView = new ImageView(this);
        fullScreenImageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        fullScreenImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        fullScreenImageView.setBackgroundColor(android.graphics.Color.BLACK);

        // Load image
        ImageRequest imageRequest = new ImageRequest(currentImageUrl,
                bitmap -> fullScreenImageView.setImageBitmap(bitmap),
                0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565,
                error -> {
                });
        Volley.newRequestQueue(this).add(imageRequest);

        dialog.setContentView(fullScreenImageView);
        fullScreenImageView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void fetchProfileData() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
                            populateProfile(data);
                        } else {
                            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
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

    private void populateProfile(JSONObject data) {
        try {
            String fullName = data.optString("full_name", "User");
            textName.setText(fullName);

            String imagePath = data.optString("profile_image", "");
            if (!imagePath.isEmpty()) {
                String fullUrl = imagePath.startsWith("http") ? imagePath : ApiConfig.getBaseUrl() + imagePath;
                currentImageUrl = fullUrl; // Save for full screen
                loadProfileImage(fullUrl);
            } else {
                profileImage.setImageResource(R.drawable.ic_avatar);
                currentImageUrl = "";
            }

            String education = data.optString("education_level", "");
            if (!education.isEmpty()) {
                textEducation.setText(education + " Student");
                textEducation.setVisibility(View.VISIBLE);
            } else {
                textEducation.setVisibility(View.GONE);
            }

            String aspiringCareer = data.optString("aspiring_career", "");
            if (!aspiringCareer.isEmpty()) {
                textAspiringCareer.setText("Aspiring " + aspiringCareer);
                textAspiringCareer.setVisibility(View.VISIBLE);
            } else {
                textAspiringCareer.setVisibility(View.GONE);
            }

            String email = data.optString("email", "Not set");
            textEmail.setText(email.isEmpty() ? "Not set" : email);

            String phone = data.optString("phone", "Not set");
            textPhone.setText(phone.isEmpty() ? "Not set" : phone);

            String dob = data.optString("date_of_birth", "Not set");
            textDob.setText(dob.isEmpty() || dob.equals("null") ? "Not set" : dob);

            String school = data.optString("current_school", "Not set");
            textSchool.setText(school.isEmpty() ? "Not set" : school);

            String board = data.optString("board", "Not set");
            textBoard.setText(board.isEmpty() ? "Not set" : board);

            int roadmapsCount = data.optInt("saved_roadmaps_count", 0);
            if (roadmapsCount > 0) {
                textRoadmapsCount.setText(roadmapsCount + " saved career paths");
            } else {
                textRoadmapsCount.setText("Access and manage all your saved career paths");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error populating profile", e);
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

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(ProfilePage.this, LoginPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
