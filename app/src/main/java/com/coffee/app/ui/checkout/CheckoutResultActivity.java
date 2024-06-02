package com.coffee.app.ui.checkout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.coffee.app.MainActivity;
import com.coffee.app.R;

public class CheckoutResultActivity extends AppCompatActivity {

    Button btnHome, btnTrackOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_result);

        btnHome = findViewById(R.id.btnHome);
        btnTrackOrder = findViewById(R.id.btnTrackOrder);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutResultActivity.this, MainActivity.class);
                intent.putExtra("open_fragment", "HomeFragment");
                startActivity(intent);
                finish();
            }
        });

        btnTrackOrder.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutResultActivity.this, MainActivity.class);
                intent.putExtra("open_fragment", "OrderFragment");
                startActivity(intent);
                finish();
            }
        });

    }
}