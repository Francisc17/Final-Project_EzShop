package pt.ipc.estgoh.ezshop.ui.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import pt.ipc.estgoh.ezshop.databinding.ActivityCameraBinding;

public abstract class CameraHandlerActivity extends AppCompatActivity {

    protected ActivityCameraBinding binding;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    this.loadCamera();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    finish();
                }
            });

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    protected ProcessCameraProvider cameraProvider;
    protected Preview preview;

    protected abstract ImageCapture.OnImageCapturedCallback onImageCapturedCallback();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.setStatusBarColor(Color.TRANSPARENT);
        // this lines ensure only the status-bar to become transparent without affecting the nav-bar
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        this.checkPermissions();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            this.loadCamera();
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void loadCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview();
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    protected void bindPreview() {
        // Get window width and height
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        final WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);


        // Preview
        if (preview == null)
            preview = new Preview.Builder()
                    // Set phone resolution
                    .setTargetResolution(new Size(displayMetrics.widthPixels, displayMetrics.heightPixels))
                    .build();

        // Lens selector
        final CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();


        // Set up the capture use case to allow users to take photos
        final ImageCapture imageCapture = new ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();


        if (this.binding != null) {
            preview.setSurfaceProvider(this.binding.cameraPv.getSurfaceProvider());

            cameraProvider.unbindAll();

            final androidx.camera.core.Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);

            binding.captureFab.setOnClickListener(v -> imageCapture.takePicture(ContextCompat.getMainExecutor(this), this.onImageCapturedCallback()));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding = null;
        cameraProviderFuture.cancel(true);
        cameraProviderFuture = null;
    }
}
