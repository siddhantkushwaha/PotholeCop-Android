package com.potholecop.androidapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import moe.feng.common.stepperview.VerticalStepperItemView;


public class AddPothole extends AppCompatActivity {
    private VerticalStepperItemView mSteppers[] = new VerticalStepperItemView[2];
    @BindView(R.id.street_name)
    TextInputEditText streetName;
    @BindView(R.id.depth)
    TextInputEditText depth;
    public String severity;
    public Button upload;

    private Uri mCropImageUri;
    private Bitmap mSelectedImage;
    public boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pothole);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        form();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void form(){
        mSteppers[0] = findViewById(R.id.stepper_0);
        mSteppers[1] = findViewById(R.id.stepper_1);
        VerticalStepperItemView.bindSteppers(mSteppers);
        Button next_0 = findViewById(R.id.button_next_0);
        next_0.setOnClickListener(view -> {
            if(validateStepOne()) {
                mSteppers[0].nextStep();
                mSteppers[0].setErrorText(null);
            } else {
                mSteppers[0].setErrorText("Image not uploaded!");
            }
        });
        Button prev = findViewById(R.id.button_prev_1);
        prev.setOnClickListener(view -> mSteppers[1].prevStep());
        Button next_1 = findViewById(R.id.button_next_1);
        next_1.setOnClickListener(view -> {
            if(validateStepTwo()) {
                submit();
                mSteppers[1].setErrorText(null);
            } else {
                mSteppers[1].setErrorText("All Fields are Mandatory!");
            }
        });
        RadioRealButtonGroup group = findViewById(R.id.group);
        severity = "Low";
        group.setOnClickedButtonListener((button, position) -> {
            severity = button.getText();
        });
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(view -> CropImage.startPickImageActivity(AddPothole.this));
    }
    public boolean validateStepOne() {
        if (!flag){
            return false;
        }
        return true;
    }
    public boolean validateStepTwo() {
        TextInputEditText diameter = findViewById(R.id.diameter);
        if(diameter.getText().toString().length() <= 1) {
            return false;
        }
        TextInputEditText depth = findViewById(R.id.depth);
        if(depth.getText().toString().length() <= 1) {
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //imvDisplayImage.setImageURI(result.getUri());
                try {
                    mSelectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                upload.setText("Uploaded");
                flag = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }


    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }
    public void submit() {}

}
