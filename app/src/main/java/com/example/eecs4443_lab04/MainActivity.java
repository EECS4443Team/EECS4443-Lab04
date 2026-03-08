package com.example.eecs4443_lab04;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.os.BundleCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Activity Result Launchers for modern intent handling
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> getContentLauncher;

    // UI and Data Components
    private Uri photoUri;
    private ImageView ivProfile;
    private Button btnCamera;
    private Button btnGallery;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Standard system bar padding handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components from layout
        ivProfile = findViewById(R.id.iv_profile);
        btnCamera = findViewById(R.id.btn_camera);
        btnGallery = findViewById(R.id.btn_gallery);
        tvStatus = findViewById(R.id.tv_status);

        // Click listeners for user actions
        btnCamera.setOnClickListener(v -> onCameraButtonClick());
        btnGallery.setOnClickListener(v -> onGalleryButtonClick());

        // Registry for Gallery picker result
        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        photoUri = uri;
                        ivProfile.setImageURI(uri);
                        tvStatus.setText("Status: Image loaded from Gallery");
                    } else {
                        tvStatus.setText("Status: Gallery selection canceled");
                    }
                }
        );

        // Registry for Camera capture result
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        ivProfile.setImageURI(photoUri);
                        tvStatus.setText("Status: Photo captured successfully");
                    } else {
                        tvStatus.setText("Status: Camera canceled");
                    }
                }
        );

        // Registry for Camera runtime permission request
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startCameraProcess();
                    } else {
                        Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                        tvStatus.setText("Status: Permission Denied");
                    }
                }
        );
    }

    // Handles camera button click and permission check
    private void onCameraButtonClick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraProcess();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // Handles gallery button click
    private void onGalleryButtonClick() {
        getContentLauncher.launch("image/*");
    }

    // Creates file and launches camera intent
    private void startCameraProcess() {
        try {
            File photoFile = createImageFile();
            // Generate secure URI using FileProvider
            photoUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(photoUri);
        } catch (Exception e) {
            e.printStackTrace();
            tvStatus.setText("Error: Camera failed to start");
        }
    }

    // Generates a unique image file in external storage
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    // Save image state for orientation changes
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photoUri != null) {
            outState.putParcelable("photo_uri", photoUri);
        }
    }

    // Restore image state after recreation
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        photoUri = BundleCompat.getParcelable(savedInstanceState, "photo_uri", Uri.class);
        if (photoUri != null) {
            ivProfile.setImageURI(photoUri);
        }
    }
}