package com.rockcity.game;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Thin WebView shell. Loads the live Rockcity website so web updates
 * appear in the app without a new Play Store upload.
 */
public class MainActivity extends AppCompatActivity {

    public static final String LIVE_URL = "https://nnamdinwali.github.io/rockcity/";
    private static final String SITE_HOST = "nnamdinwali.github.io";

    private WebView webView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl() != null ? request.getUrl().toString() : "";
                if (isBarePortfolioRoot(url)) {
                    view.loadUrl(LIVE_URL);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                if (isBarePortfolioRoot(url)) {
                    view.stopLoading();
                    view.loadUrl(LIVE_URL);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                CookieManager.getInstance().flush();
                if (isBarePortfolioRoot(url)) {
                    view.loadUrl(LIVE_URL);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress >= 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        if (savedInstanceState == null) {
            webView.loadUrl(LIVE_URL);
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    /**
     * After Google/Clerk login, Clerk sometimes redirects to the GitHub Pages
     * root (portfolio site) instead of /rockcity/. Catch that and force Rockcity.
     */
    private boolean isBarePortfolioRoot(String url) {
        if (url == null || url.isEmpty()) return false;
        String u = url.toLowerCase();
        // Allow rockcity paths
        if (u.contains("/rockcity")) return false;
        // Block plain root of this github pages host
        if (u.equals("https://" + SITE_HOST)
                || u.equals("https://" + SITE_HOST + "/")
                || u.equals("http://" + SITE_HOST)
                || u.equals("http://" + SITE_HOST + "/")) {
            return true;
        }
        // Also catch root with only query/hash (oauth leftovers)
        if (u.startsWith("https://" + SITE_HOST + "/?")
                || u.startsWith("https://" + SITE_HOST + "/#")
                || u.startsWith("https://" + SITE_HOST + "?")) {
            return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null) {
            webView.saveState(outState);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
