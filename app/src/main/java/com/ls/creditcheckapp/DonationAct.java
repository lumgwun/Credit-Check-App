package com.ls.creditcheckapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static com.ls.creditcheckapp.Constant.PAYSTACK_FIX;
import static com.ls.creditcheckapp.Constant.PAYSTACK_SUB;

public class DonationAct extends AppCompatActivity {
    WebView mywebView;
    private AppCompatButton btnSub,btnOntTimeD;
    private String weblink;
    private String fluterOneTime="https://business.quickteller.com/link/pay/CreditcheckA/";
    private String flutterSub="https://business.quickteller.com/link/pay/CreditReport";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_donation);
        btnSub = findViewById(R.id.btnSub);
        btnOntTimeD = findViewById(R.id.btn_OneTime);
        btnSub.setOnClickListener(this::ChooseDonation);
        btnOntTimeD.setOnClickListener(this::oneTimeDonation);
        btnOntTimeD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weblink=fluterOneTime;

            }
        });
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weblink=flutterSub;

            }
        });
        mywebView = (WebView) findViewById(R.id.webView_donation);
        mywebView.setWebViewClient(new WebViewClient());
        WebSettings webViewSettings = mywebView.getSettings();
        webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webViewSettings.setJavaScriptEnabled(true);
        mywebView.loadUrl(weblink);
        mywebView.setWebViewClient(new WebViewClient());
    }
    @Override
    public void onBackPressed() {
        if(mywebView.canGoBack())
        {
            mywebView.goBack();
        }

        else
        {
            super.onBackPressed();
        }

    }

    public void ChooseDonation(View view) {
        weblink=PAYSTACK_SUB;
    }

    public void oneTimeDonation(View view) {
        weblink=PAYSTACK_FIX;
    }
}