package com.ueniweb.techsuperficial.totalityeditor.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Flash;
import com.ueniweb.techsuperficial.totalityeditor.BuildConfig;
import com.ueniweb.techsuperficial.totalityeditor.R;
import com.ueniweb.techsuperficial.totalityeditor.util.EditHandler;
import com.ueniweb.techsuperficial.totalityeditor.util.PermissionsUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.widget.ImageView.ScaleType.FIT_CENTER;

public class HomeActivity extends AppCompatActivity implements BitmapCallback {
    Context mcontext;
    @BindView(R.id.add_photo_iv)
    ImageView addphotoiv;
    @BindView(R.id.camera_iv)
    ImageView camera_iv;
    @BindView(R.id.cameraview)
    CameraView cameraview;
    @BindView(R.id.camerall)
    RelativeLayout camerall;
    @BindView(R.id.camera_click_iv)
    ImageView camera_click_iv;
    @BindView(R.id.change_camera_iv)
    ImageView change_camera_iv;
    @BindView(R.id.flash_on_iv)
    ImageView flash_on_iv;
    @BindView(R.id.flash_off_iv)
    ImageView flash_off_iv;
    Boolean isFlront = false;
    Boolean isFlashOn = false;
    private int requestMode = BuildConfig.RequestMode;
    @BindView(R.id.image_iv)
    ImageView image_iv;
    @BindView(R.id.welcomell)
    LinearLayout welcomell;
    String uristr;
    Uri saveduri;
    Bitmap bitmapfromedit;
    private View parent_view;
    EditHandler editHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initVariable();
        getIntentData();
        cameraResultListener();
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                uristr = super.getIntent().getExtras().getString("uri");
                saveduri = Uri.parse(uristr);
                bitmapfromedit = MediaStore.Images.Media.getBitmap(this.getContentResolver(), saveduri);
                image_iv.setImageBitmap(bitmapfromedit);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), saveduri);
                save(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            image_iv.setImageResource(R.drawable.homeimage);
            image_iv.setScaleType(FIT_CENTER);
            image_iv.setPadding(200, 200, 200, 200);
        }
    }

    private void save(Bitmap bitmap) {
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Toatality", "totatlityfiles");
        mcontext = HomeActivity.this;
        showSaveSnackBar();
    }

    public void showSaveSnackBar() {
        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_LONG);
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_saved, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);
        (custom_view.findViewById(R.id.tv_undo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }

    private void cameraResultListener() {
        cameraview.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
                result.toBitmap(1000, 1000, HomeActivity.this::onBitmapReady);
            }

            @Override
            public void onVideoTaken(@NonNull VideoResult result) {
                super.onVideoTaken(result);
            }
        });
    }

    private void initVariable() {
        mcontext = HomeActivity.this;
        editHandler = new EditHandler(mcontext, this);
        cameraview.setLifecycleOwner(this);
        parent_view = findViewById(android.R.id.content);
    }

    @OnClick(R.id.add_photo_iv)
    public void addPhotoClicked() {
        if (PermissionsUtils.checkCameraPermission(HomeActivity.this) &&
                PermissionsUtils.checkWriteStoragePermission(HomeActivity.this)) {
            pickFromGallery();
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }

        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), requestMode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == requestMode && data != null) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    EditorActivity.startWithUri(HomeActivity.this, selectedUri);
                } else {
                    Toast.makeText(mcontext, "Error getting image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @OnClick(R.id.camera_iv)
    public void CameraClicked() {
        if (PermissionsUtils.checkCameraPermission(HomeActivity.this) &&
                PermissionsUtils.checkWriteStoragePermission(HomeActivity.this)) {
            camerall.setVisibility(View.VISIBLE);
            welcomell.setVisibility(View.GONE);

        }
    }


    @OnClick(R.id.camera_click_iv)
    public void cameraClicked() {
        cameraview.takePicture();
    }

    @OnClick(R.id.change_camera_iv)
    public void camerachanged() {
        if (!isFlront) {
            cameraview.setFacing(Facing.FRONT);
            isFlront = true;
        } else {
            cameraview.setFacing(Facing.BACK);
            isFlront = false;
        }
    }

    @OnClick(R.id.flash_on_iv)
    public void flashOnClicked() {
        if (isFlashOn) {
            flash_off_iv.setVisibility(View.VISIBLE);
            flash_on_iv.setVisibility(View.GONE);
            cameraview.setFlash(Flash.OFF);
            isFlashOn = false;
        }
    }

    @OnClick(R.id.flash_off_iv)
    public void flashOffClicked() {
        if (!isFlashOn) {
            cameraview.setFlash(Flash.ON);
            flash_on_iv.setVisibility(View.VISIBLE);
            flash_off_iv.setVisibility(View.GONE);
            isFlashOn = true;
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (iArr.length > 0 && iArr[0] == 0) {
            if ((i == 1 || i == 3) && PermissionsUtils.checkWriteStoragePermission(HomeActivity.this)
                    && PermissionsUtils.checkCameraPermission(HomeActivity.this)) {
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraview.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraview.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraview.destroy();
    }

    @Override
    public void onBitmapReady(@Nullable Bitmap bitmap) {
        final Uri selectedUri;
        try {
            selectedUri = editHandler.getUri(bitmap);
            if (selectedUri != null) {
                Bitmap bitmapcrop = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedUri);
                Uri afterflipuri = editHandler.getUri(editHandler.flip(bitmapcrop));
                EditorActivity.startWithUri(HomeActivity.this, afterflipuri);
            } else {
                Toast.makeText(mcontext, "Error getting image", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (camerall.getVisibility() == View.VISIBLE) {
            camerall.setVisibility(View.GONE);
            welcomell.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();

        }
    }
}