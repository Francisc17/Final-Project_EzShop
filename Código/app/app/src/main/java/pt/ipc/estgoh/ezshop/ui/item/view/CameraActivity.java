package pt.ipc.estgoh.ezshop.ui.item.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import pt.ipc.estgoh.ezshop.ui.camera.CameraHandlerActivity;

public class CameraActivity extends CameraHandlerActivity {
    @Override
    protected ImageCapture.OnImageCapturedCallback onImageCapturedCallback() {
        return new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                cameraProvider.unbind(preview);
                final Intent intent = new Intent();
                intent.putExtra("image", convertBitmapToFile(convertImageProxyToBitmap(image), image.getImageInfo().getRotationDegrees()));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                // TODO: 30/05/2021 Handle error
                Log.e(this.getClass().getSimpleName(), exception.getMessage());
            }
        };
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
}
