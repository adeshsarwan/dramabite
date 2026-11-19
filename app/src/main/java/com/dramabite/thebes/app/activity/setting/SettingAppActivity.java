package com.dramabite.thebes.app.activity.setting;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dramabite.thebes.app.R;
import com.dramabite.thebes.app.activity.main.base.BaseOtherActivity;

public class SettingAppActivity extends BaseOtherActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_app);
    }
}