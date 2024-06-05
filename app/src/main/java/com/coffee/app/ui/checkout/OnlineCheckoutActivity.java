package com.coffee.app.ui.checkout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.coffee.app.MainActivity;
import com.coffee.app.R;
import com.coffee.app.shared.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class OnlineCheckoutActivity extends AppCompatActivity {
    WebView webView;
    TextView closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_checkout);

        String orderInfoJson = getIntent().getStringExtra("orderInfo");

        String orderItemsJson = getIntent().getStringExtra("orderItems");


        webView = findViewById(R.id.webView);


        // Encode order items to pass to the webview
        try {
            String encodedOrderItems = URLEncoder.encode(orderItemsJson, "UTF-8");
            String encodedOrderInfo = URLEncoder.encode(orderInfoJson, "UTF-8");

            // Open webview to show the payment page
            String url = Constants.WEB_URL + "/checkout/online?checkoutFrom=android&encodedOrderItems=" + encodedOrderItems + "&encodedOrderInfo=" + encodedOrderInfo;;

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    String currentURL = request.getUrl().toString();
                    final String successParam = "orderStatus=success";

                    if (currentURL.contains(successParam)) {
                        Toast.makeText(getApplicationContext(), "Thanh toán thành công ✅", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false; // Allow WebView to load the URL
                }
            });

            webView.loadUrl(url);
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Mở cổng thanh toán thất bại ❌", Toast.LENGTH_SHORT).show();
        }


        // Close the activity when the payment is successful
        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}