package co.cheez.cheez;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


public class URLViewActivity extends ActionBarActivity {
    WebView mWebView;
    ProgressBar mProgressBar;

    public static Intent getIntent(Context context, String url) {
        Intent intent = new Intent(context, URLViewActivity.class);
        intent.putExtra("url", url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_main);



        String url = "http://cheez.co";

        Intent intent = getIntent();
        String passedUrl = intent.getStringExtra("url");
        if (passedUrl != null) {
            url = passedUrl;
        }

        getSupportActionBar().hide();

        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.setMax(100);

        mWebView = (WebView) findViewById(R.id.webview);



        // 웹뷰에서 자바스크립트실행가능
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 구글홈페이지 지정
        mWebView.loadUrl(url);
        // WebViewClient 지정
        mWebView.setWebViewClient(new WebViewClientClass());
        mWebView.setWebChromeClient(new WebChromeClientClass());
    }


    @Override
    protected void onPause() {
        mWebView.onPause();
        mWebView.pauseTimers();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return true;
        }
    }

    private class WebChromeClientClass extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

}