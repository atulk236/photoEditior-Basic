package com.ueniweb.techsuperficial.totalityeditor.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fenchtose.nocropper.BitmapResult;
import com.fenchtose.nocropper.CropperImageView;
import com.fenchtose.nocropper.CropperView;
import com.ueniweb.techsuperficial.totalityeditor.R;
import com.ueniweb.techsuperficial.totalityeditor.util.EditHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class EditorActivity extends AppCompatActivity {
    Context mcontext;
    private static final String TAG = "EditorActivity";
    @BindView(R.id.image_view)
    ImageView image_view;
    @BindView(R.id.imgtick)
    AppCompatImageView imgtick;
    @BindView(R.id.rotate_iv)
    AppCompatImageView rotateIv;
    @BindView(R.id.crop_iv)
    AppCompatImageView cropIv;
    @BindView(R.id.undo_iv)
    AppCompatImageView undoIv;
    @BindView(R.id.bottom_ll)
    LinearLayout bottomLl;
    @BindView(R.id.cropper_iv)
    CropperView cropper_iv;
    CropperImageView cropperImageView;
    public static final int READ_WRITE_STORAGE = 52;
    Bitmap bitmapcrop, bitmapcropsaved;
    Uri uri;
    @BindView(R.id.cropper_iv_ll)
    LinearLayout cropper_iv_ll;
    @BindView(R.id.imgtickcrop)
    ImageView imgtickcrop;
    BitmapResult bitmapResult;
    @BindView(R.id.imgcrosscrop)
    ImageView imgcrosscrop;
    Bitmap rotated;
    EditHandler editHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);
        init();
    }

    public static void startWithUri(@NonNull Context context, @NonNull Uri uri) {
        Intent intent = new Intent(context, EditorActivity.class);
        intent.setData(uri);
        context.startActivity(intent);
    }

    private void init() {
        initVariable();
        getIntentData();
    }

    private void initVariable() {
        mcontext = EditorActivity.this;
        cropper_iv.fitToCenter();
        cropperImageView = new CropperImageView(mcontext);
        editHandler = new EditHandler(mcontext, this);
    }

    private void getIntentData() {
        uri = getIntent().getData();
        if (uri != null) {
            try {
                bitmapcrop = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                image_view.setImageURI(uri);
                cropper_iv.setImageBitmap(bitmapcrop);
            } catch (Exception e) {
                Log.e(TAG, "setImageUri", e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @OnClick({R.id.image_view, R.id.imgtick, R.id.rotate_iv, R.id.crop_iv, R.id.undo_iv, R.id.imgtickcrop, R.id.imgcrosscrop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_view:
                break;
            case R.id.imgtick:
                if (imgtickcrop.getVisibility() == View.VISIBLE) {
                    Toast.makeText(mcontext, "Please crop ur  image", Toast.LENGTH_SHORT).show();
                } else {
                    saveImage();
                }
                break;
            case R.id.rotate_iv:
                rotateClicked();
                break;
            case R.id.crop_iv:
                imgtickcrop.setVisibility(View.VISIBLE);
                cropper_iv_ll.setVisibility(View.VISIBLE);
                imgcrosscrop.setVisibility(View.VISIBLE);
                image_view.setVisibility(View.GONE);
                break;
            case R.id.undo_iv:
                undoClicked();
                break;
            case R.id.imgtickcrop:
                cropper_iv_ll.setVisibility(View.GONE);
                image_view.setVisibility(View.VISIBLE);
                imgtickcrop.setVisibility(View.GONE);
                imgcrosscrop.setVisibility(View.GONE);
                bitmapResult = cropper_iv.getCroppedBitmap();
                bitmapcropsaved = bitmapResult.getBitmap();
                image_view.setImageBitmap(bitmapcropsaved);
                break;

            case R.id.imgcrosscrop:
                cropper_iv_ll.setVisibility(View.GONE);
                image_view.setVisibility(View.VISIBLE);
                imgtickcrop.setVisibility(View.GONE);
                imgcrosscrop.setVisibility(View.GONE);
                break;
        }
    }

    private void rotateClicked() {
        if (rotated == null) {
            BitmapDrawable drawable = (BitmapDrawable) image_view.getDrawable();
            Bitmap myImg = drawable.getBitmap();
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
                    matrix, true);
            image_view.setImageBitmap(rotated);
        } else {
            Bitmap myImg = rotated;
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
                    matrix, true);
            image_view.setImageBitmap(rotated);
        }
    }

    private void undoClicked() {
        image_view.setImageURI(uri);
    }

    private void saveImage() {
        if (editHandler.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            BitmapDrawable drawable = (BitmapDrawable) image_view.getDrawable();
            Bitmap editedbitmap = drawable.getBitmap();
            try {
                Uri uri = editHandler.getUri(editedbitmap);
                Intent intent = new Intent(EditorActivity.this, HomeActivity.class);
                String struri;
                struri = String.valueOf(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("uri", struri);
                startActivity(intent);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}