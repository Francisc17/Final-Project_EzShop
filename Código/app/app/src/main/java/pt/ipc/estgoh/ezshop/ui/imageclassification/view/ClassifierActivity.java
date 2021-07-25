package pt.ipc.estgoh.ezshop.ui.imageclassification.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseApp;
import com.google.mlkit.common.model.CustomRemoteModel;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.linkfirebase.FirebaseModelSource;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import pt.ipc.estgoh.ezshop.ui.camera.CameraHandlerActivity;
import pt.ipc.estgoh.ezshop.ui.imageclassification.viewmodel.ResultsViewModel;

public class ClassifierActivity extends CameraHandlerActivity implements DialogInterface.OnDismissListener {

    private ResultsViewModel viewModel;
    private long listId;
    final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(ClassifierActivity.this);

        this.listId = getIntent().getLongExtra("listId", -1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel = new ViewModelProvider(this).get(ResultsViewModel.class);
    }

    @Override
    protected ImageCapture.OnImageCapturedCallback onImageCapturedCallback() {
        return new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                handleCaptureSuccess(image);
                cameraProvider.unbind(preview);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                // TODO: 30/05/2021 Handle error
                Log.e(this.getClass().getSimpleName(), exception.getMessage());
            }
        };
    }

    private void handleCaptureSuccess(ImageProxy image) {
        FirebaseModelSource firebaseModelSource =
                new FirebaseModelSource.Builder("EzShop_modeller").build();

        CustomRemoteModel remoteModel =
                new CustomRemoteModel.Builder(firebaseModelSource).build();

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        RemoteModelManager.getInstance().download(remoteModel, downloadConditions)
                .addOnSuccessListener(ignored -> {

                    final CustomImageLabelerOptions.Builder optionsBuilder;

                    optionsBuilder = new CustomImageLabelerOptions.Builder(remoteModel);

                    final CustomImageLabelerOptions options = optionsBuilder.setConfidenceThreshold(0.15f).build();

                    final ImageLabeler labeler = ImageLabeling.getClient(options);

                    final Bitmap convertedImage = this.convertImageProxyToBitmap(image);

                    final InputImage inputImage = InputImage.fromBitmap(convertedImage, image.getImageInfo().getRotationDegrees());
                    labeler.process(inputImage)
                            .addOnSuccessListener(imageLabels -> {
                               try {
                                   viewModel.setResults(this.listId, imageLabels, this.convertBitmapToFile(inputImage.getBitmapInternal(), inputImage.getRotationDegrees()));
                                   final ResultsListBottomSheetDialogFragment frag = new ResultsListBottomSheetDialogFragment();
                                   frag.setStartForResult(this.startForResult);
                                   if (!frag.isAdded())
                                       frag.show(getSupportFragmentManager(), frag.getTag());
                               } catch (final NullPointerException npe) {
                                   // If users goes back after pressing the shutter and before the result bottom sheet appers
                                   // It will crash because this activity has finished
                                   // It needs to catch NullPointerException to prevent the crash
                               }
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show())
                .addOnCompleteListener(task -> image.close()); // Close image
    }

    private Bitmap convertImageProxyToBitmap(ImageProxy aImage) {
        ImageProxy.PlaneProxy planeProxy = aImage.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private File convertBitmapToFile(Bitmap aBitmap, final int aRotation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(aRotation);
        aBitmap = Bitmap.createBitmap(aBitmap, 0, 0, aBitmap.getWidth(), aBitmap.getHeight(), matrix, true);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        aBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] bytes = baos.toByteArray();

        File file = null;
        try {
            file = File.createTempFile("image", ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel = null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        bindPreview();
    }
}
