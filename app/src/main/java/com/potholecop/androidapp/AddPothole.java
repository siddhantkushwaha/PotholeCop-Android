package com.potholecop.androidapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

import butterknife.BindView;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import moe.feng.common.stepperview.VerticalStepperItemView;

public class AddPothole extends AppCompatActivity {
    private VerticalStepperItemView mSteppers[] = new VerticalStepperItemView[2];
    @BindView(R.id.street_name)
    TextInputEditText streetName;
    @BindView(R.id.upload)
    Button upload;
    @BindView(R.id.depth)
    TextInputEditText depth;
    public String severity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pothole);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    }
    public boolean validateStepOne() {
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
    public void submit() {}

}
