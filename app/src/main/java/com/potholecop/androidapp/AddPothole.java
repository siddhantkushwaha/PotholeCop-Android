package com.potholecop.androidapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPothole extends AppCompatActivity {

    @BindView(R.id.addPhoto)
    Button addPhoto;
    @BindView(R.id.submitData)
    Button submitData;

    private Uri photoUri;

    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_STORAGE = 1;
    private static final int PICK_IMAGE = 1;
    private int MAX_SIZE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pothole);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.addPhoto)
    public void onAddPhotoClicked() {
        getCameraAccessPermission();
    }

    @OnClick(R.id.submitData)
    public void onSubmitDataClicked() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(AddPothole.class.toString(), "onActivityResult");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE:

                    if (photoUri != null)
                        startCrop(photoUri);

                    break;
                case UCrop.REQUEST_CROP:
                    Uri croppedUri = UCrop.getOutput(data);
                    handleCropResult(croppedUri);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(AddPothole.class.toString(), "CAMERA_PERMISSION_GRANTED");
                    getWriteStoragePermission();
                }
                break;
            case REQUEST_CODE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(AddPothole.class.toString(), "STORAGE_PERMISSION_GRANTED");
                    openImageIntent();
                }
        }
    }

    private void getCameraAccessPermission() {

        if (ContextCompat.checkSelfPermission(AddPothole.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            getWriteStoragePermission();
        } else {
            try {
                ActivityCompat.requestPermissions(AddPothole.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            } catch (Exception e) {
                Log.e(AddPothole.class.toString(), e.toString());
            }
        }

    }

    private void getWriteStoragePermission() {

        if (ContextCompat.checkSelfPermission(AddPothole.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openImageIntent();
        } else {
            try {
                ActivityCompat.requestPermissions(AddPothole.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
            } catch (Exception e) {
                Log.e(AddPothole.class.toString(), e.toString());
            }
        }
    }

    private void openImageIntent() {


        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(getPackageManager()) != null) {

            File file = null;
            try {
                file = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (file != null) {

                photoUri = FileProvider.getUriForFile(this, "com.potholecop.androidapp.fileprovider", file);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(captureIntent, PICK_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void startCrop(@NonNull Uri uri) {

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), "display_pic_square.png")));
        uCrop.withMaxResultSize(MAX_SIZE, MAX_SIZE);
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setActiveWidgetColor(getResources().getColor(R.color.colorAccent));
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        uCrop.withOptions(options);
        uCrop.start(this);
    }

    private void handleCropResult(Uri uri) {

        Log.i(AddPothole.class.toString(), String.valueOf(uri));
    }
}
