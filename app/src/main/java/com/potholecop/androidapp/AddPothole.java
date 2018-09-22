package com.potholecop.androidapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.potholecop.androidapp.pojo.Location;
import com.potholecop.androidapp.pojo.PotholeData;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import moe.feng.common.stepperview.VerticalStepperItemView;


public class AddPothole extends AppCompatActivity {
    @BindView(R.id.upload)
    Button upload;
    @BindView(R.id.button_next_0)
    Button buttonNext0;
    @BindView(R.id.stepper_0)
    VerticalStepperItemView stepper0;
    @BindView(R.id.diameter)
    TextInputEditText diameter;
    @BindView(R.id.low)
    RadioRealButton low;
    @BindView(R.id.medium)
    RadioRealButton medium;
    @BindView(R.id.high)
    RadioRealButton high;
    @BindView(R.id.group)
    RadioRealButtonGroup group;
    @BindView(R.id.button_next_1)
    Button buttonNext1;
    @BindView(R.id.button_prev_1)
    Button buttonPrev1;
    @BindView(R.id.stepper_1)
    VerticalStepperItemView stepper1;
    private VerticalStepperItemView mSteppers[] = new VerticalStepperItemView[2];
    @BindView(R.id.street_name)
    TextInputEditText streetName;
    @BindView(R.id.depth)
    TextInputEditText depth;
    public String severity;

    private Uri mCropImageUri;
    private Bitmap mSelectedImage;
    public boolean flag = false;

    private Boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private PotholeData potholeData;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pothole);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        form();

        potholeData = new PotholeData();

        mLocationPermissionGranted = false;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (mLocationPermissionGranted)
            getDeviceLocation();
        else
            getLocationPermission();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void form() {
        mSteppers[0] = findViewById(R.id.stepper_0);
        mSteppers[1] = findViewById(R.id.stepper_1);
        VerticalStepperItemView.bindSteppers(mSteppers);
        Button next_0 = findViewById(R.id.button_next_0);
        next_0.setOnClickListener(view -> {
            if (validateStepOne()) {
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
            if (validateStepTwo()) {
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
        upload.setOnClickListener(v -> CropImage.startPickImageActivity(AddPothole.this));
    }

    public boolean validateStepOne() {
        if (!flag) {
            return false;
        }
        return true;
    }

    public boolean validateStepTwo() {
        if (diameter.getText().toString().length() < 1) {
            return false;
        }
        if (depth.getText().toString().length() < 1) {
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


        if (requestCode == 0) {

            mLocationPermissionGranted = false;
            switch (requestCode) {
                case 0: {

                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = true;
                    }
                }
                break;

            }

            if (mLocationPermissionGranted)
                getDeviceLocation();
        } else {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    public void submit() {

        potholeData.setTimestamp(DateTime.now().toString());
        potholeData.setDepth(depth.getText().toString());
        potholeData.setDiameter(diameter.getText().toString());
        potholeData.setFixed(false);
        potholeData.setSeverity(severity);
        potholeData.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        key = FirebaseDb.getInstance().getReference("potholes").push().getKey();
        FirebaseDb.getInstance().getReference("potholes/" + key).setValue(potholeData);

//        Log.i(AddPothole.class.toString(), mCropImageUri.toString());


//        StorageReference ref = FirebaseStorage.getInstance().getReference("potholeImage/" + key + ".png");
//        UploadTask uploadTask = ref.putFile(mCropImageUri);
//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
//                task.addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//
//                        if (task.isSuccessful()) {
//
//                            String downloadUrl = task.getResult().toString();
//
//                            potholeData.setPhoto(downloadUrl);
//                            FirebaseDb.getInstance().getReference("potholes/" + key).setValue(potholeData);
//
//                        } else if (task.getException() != null) {
//                            Toast.makeText(AddPothole.this, "Failed to add.", Toast.LENGTH_LONG).show();
//                            FirebaseDb.getInstance().getReference("potholes/" + key).setValue(null);
//                        }
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//                Toast.makeText(AddPothole.this, "Failed to add.", Toast.LENGTH_LONG).show();
//                FirebaseDb.getInstance().getReference("potholes/" + key).setValue(null);
//            }
//        });
    }

    private void getDeviceLocation() {

        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        android.location.Location mLastKnownLocation = (android.location.Location) task.getResult();
                        if (mLastKnownLocation == null)
                            return;
                        LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                        if(potholeData != null) {
                            potholeData.setLocation(new Location(latLng.latitude, latLng.longitude));
                            FirebaseDb.getInstance().getReference("potholes/" + key).setValue(potholeData);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
    }


}
