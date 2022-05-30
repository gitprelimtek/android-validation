package com.prelimtek.android.validation;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.prelimtek.android.validation.databinding.ExampleValidationBinding;

import java.math.BigDecimal;

public class ExampleValidationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.example_validation);
        ExampleModel dataModel = new ExampleModel();
        dataModel.setRentDue(new BigDecimal(100.00));
        ExampleValidationBinding binding = DataBindingUtil.setContentView(this,R.layout.example_validation);
        binding.setTenant(dataModel);
        View view = binding.getRoot();
    }
}
