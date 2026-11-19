package com.dramabite.thebes.app.activity.setting;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.dramabite.thebes.app.R;
import com.dramabite.thebes.app.activity.main.base.BaseOtherActivity;

public class GiftsActivity extends BaseOtherActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gifts);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        finish();
                    }
                });
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });
    }


}