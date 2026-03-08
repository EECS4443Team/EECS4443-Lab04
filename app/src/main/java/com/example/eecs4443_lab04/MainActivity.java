package com.example.eecs4443_lab04;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Persistent storage constants
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_BG_COLOR = "bg_color";
    private static final String PROFILE_IMAGE_NAME = "profile_image.jpg";

    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> getContentLauncher;

    private Uri tempPhotoUri; // Temporary URI for camera capture
    private ImageView ivProfile;
    private Button btnCamera;
    private Button btnGallery;
    private TextView tvStatus;
    private ProgressBar pbLoading;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        scrollView = findViewById(R.id.scroll_view);
        ivProfile = findViewById(R.id.iv_profile);
        btnCamera = findViewById(R.id.btn_camera);
        btnGallery = findViewById(R.id.btn_gallery);
        tvStatus = findViewById(R.id.tv_status);
        pbLoading = findViewById(R.id.pb_loading);

        // Standard system bar padding handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Load saved preferences and profile image
        loadPreferences();
        setupColorCustomization();

        btnCamera.setOnClickListener(v -> onCameraButtonClick());
        btnGallery.setOnClickListener(v -> onGalleryButtonClick());

        // Handle image selection from gallery
        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    pbLoading.setVisibility(View.GONE);
                    if (uri != null) {
                        saveImageToInternalStorage(uri); // Persist image to internal storage
                        displayProfileImage();
                        tvStatus.setText("Status: Image loaded from Gallery");
                    } else {
                        tvStatus.setText("Status: Gallery selection canceled");
                    }
                }
        );

        // Handle photo capture from camera
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    pbLoading.setVisibility(View.GONE);
                    if (success && tempPhotoUri != null) {
                        saveImageToInternalStorage(tempPhotoUri); // Persist captured photo
                        displayProfileImage();
                        tvStatus.setText("Status: Photo captured successfully");
                    } else {
                        tvStatus.setText("Status: Camera canceled");
                    }
                }
        );

        // Handle camera permission request
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startCameraProcess();
                    } else {
                        pbLoading.setVisibility(View.GONE);
                        Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                        tvStatus.setText("Status: Permission Denied");
                    }
                }
        );
    }

    private void setupColorCustomization() {
        findViewById(R.id.btn_color_gray).setOnClickListener(v -> changeBackgroundColor(R.color.bg_light_gray));
        findViewById(R.id.btn_color_blue).setOnClickListener(v -> changeBackgroundColor(R.color.bg_light_blue));
        findViewById(R.id.btn_color_green).setOnClickListener(v -> changeBackgroundColor(R.color.bg_light_green));
        findViewById(R.id.btn_color_yellow).setOnClickListener(v -> changeBackgroundColor(R.color.bg_light_yellow));
        findViewById(R.id.btn_color_pink).setOnClickListener(v -> changeBackgroundColor(R.color.bg_light_pink));
    }

    private void changeBackgroundColor(int colorResId) {
        scrollView.setBackgroundColor(ContextCompat.getColor(this, colorResId));
        // Save the background color preference
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_BG_COLOR, colorResId).apply();
    }

    /**
     * Copies the image from the given URI to the app's internal storage.
     * This avoids SecurityExceptions when reloading the app as gallery URIs are temporary.
     */
    private void saveImageToInternalStorage(Uri uri) {
        try (InputStream is = getContentResolver().openInputStream(uri);
             FileOutputStream fos = openFileOutput(PROFILE_IMAGE_NAME, Context.MODE_PRIVATE)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the profile image from internal storage and displays it in the ImageView.
     */
    private void displayProfileImage() {
        File file = new File(getFilesDir(), PROFILE_IMAGE_NAME);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ivProfile.setImageBitmap(bitmap);
        }
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Restore background color
        int savedColor = prefs.getInt(KEY_BG_COLOR, R.color.bg_light_gray);
        scrollView.setBackgroundColor(ContextCompat.getColor(this, savedColor));

        // Restore profile image from internal storage
        displayProfileImage();
    }

    private void onCameraButtonClick() {
        pbLoading.setVisibility(View.VISIBLE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraProcess();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void onGalleryButtonClick() {
        pbLoading.setVisibility(View.VISIBLE);
        getContentLauncher.launch("image/*");
    }

    private void startCameraProcess() {
        try {
            // Create a temporary file for the camera intent
            File photoFile = File.createTempFile(
                    "TEMP_IMG_", ".jpg", getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES));
            tempPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            takePictureLauncher.launch(tempPhotoUri);
        } catch (IOException e) {
            e.printStackTrace();
            pbLoading.setVisibility(View.GONE);
            tvStatus.setText("Error: Camera failed to start");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save temp URI to handle configuration changes during camera capture
        if (tempPhotoUri != null) {
            outState.putParcelable("temp_photo_uri", tempPhotoUri);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore temp URI after configuration change
        tempPhotoUri = savedInstanceState.getParcelable("temp_photo_uri");
    }
}
