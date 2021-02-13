package com.ueniweb.techsuperficial.totalityeditor.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.ueniweb.techsuperficial.totalityeditor.views.EditorActivity.READ_WRITE_STORAGE;

public class EditHandler {
    Activity activity;
    Context mcontext;

    public EditHandler(Context mcontext, Activity activity) {
        this.mcontext = mcontext;
        this.activity = activity;
    }

    public Uri getUri(Bitmap bitmap) throws IOException {
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
        tempDir.mkdir();
        File tempFile = File.createTempFile("framten", ".png", tempDir);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        byte[] bitmapData = bytes.toByteArray();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        return Uri.fromFile(tempFile);
    }

    public boolean requestPermission(String permission) {
        boolean isGranted = ContextCompat.checkSelfPermission(mcontext, permission) == PackageManager.PERMISSION_GRANTED;
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{permission},
                    READ_WRITE_STORAGE);
        }
        return isGranted;
    }

    public Bitmap flip(Bitmap src) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap flippedbitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        flippedbitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return (flippedbitmap);
    }
}
