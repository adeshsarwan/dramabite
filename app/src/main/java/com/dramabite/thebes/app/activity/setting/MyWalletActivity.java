package com.dramabite.thebes.app.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.dramabite.thebes.app.R;
import com.dramabite.thebes.app.activity.main.base.BaseOtherActivity;
import com.dramabite.thebes.app.activity.premium.ConsumptionRewardScreen;
import com.dramabite.thebes.app.activity.premium.TopUpActivity;
import com.dramabite.thebes.app.databinding.ActivityMyWalletBinding;
import com.dramabite.thebes.app.utils.coins.CoinsManager;

public class MyWalletActivity extends BaseOtherActivity {
    ActivityMyWalletBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            onBackPressed();
        });
        binding.btnTopupCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyWalletActivity.this, TopUpActivity.class);
                startActivity(intent);
            }
        });
        binding.imageTransactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Purchases go through Google Play; show the user's Play order history.
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://play.google.com/store/account/orderhistory"));
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ignored) {
                }

            }
        });
        binding.buttonConsumptionRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyWalletActivity.this, ConsumptionRewardScreen.class);
                startActivity(intent);

            }
        });
        final String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            ListenerRegistration[] reg = new ListenerRegistration[1];
            reg[0] = CoinsManager.listenCoins(uid, new CoinsManager.CoinsListener() {
                @Override
                public void onCoinsChanged(long coins) {
                    if (binding.tvBalanceValue != null) {
                        binding.tvBalanceValue.setText(String.valueOf(coins));
                    }
                }

                @Override
                public void onError(@NonNull Exception e) {
                    Toast.makeText(MyWalletActivity.this,
                            "Failed to load coins", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (binding.tvBalanceValue != null) {
                binding.tvBalanceValue.setText("0");
            }
            Toast.makeText(this, "Please login to see coins", Toast.LENGTH_SHORT).show();
        }





    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}