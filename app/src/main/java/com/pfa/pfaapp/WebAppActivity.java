package com.pfa.pfaapp;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class WebAppActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_app);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        webView = findViewById(R.id.webView);

        final String currentUrl = "https://cell.pfa.gop.pk/dev/staff_login";//sharedPrefUtils.getSharedPrefValue(MTO_WEB_URL);

        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        WebSettings websettings = webView.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setDomStorageEnabled(true);
        websettings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.1.2; C1905 Build/15.1.C.2.8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36");

        webView.loadUrl(currentUrl);
        webView.setWebViewClient(new MyWebViewClient());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl(currentUrl);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            sharedPrefUtils.showExitDialog();
        }
    }


//    public void onClickLogoutBtn(View view) {
//        sharedPrefUtils.logoutFromApp(httpService);
//    }

    public class MyWebViewClient extends WebViewClient {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            String temp  = request.getUrl().toString();
            sharedPrefUtils.printLog("shouldOverrideUrlLoading==>", ""+ temp);
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            swipeRefreshLayout.setRefreshing(false);
//            findViewById(R.id.appLogoutImgBtn).setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }

}
