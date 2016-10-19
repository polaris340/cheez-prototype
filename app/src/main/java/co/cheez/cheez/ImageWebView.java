package co.cheez.cheez;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jiho on 4/5/15.
 */
public class ImageWebView extends WebView {
    public ImageWebView(Context context) {
        super(context);
    }

    public ImageWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageUrl(String url) throws UnsupportedEncodingException {
        loadData("<html style=\"height:100%;\"><head><style>* {margin:0;padding:0;ovrflow:hidden;}</style></head><body style=\"100%;background-image:url('"+ URLEncoder.encode(url, "utf-8")+"');background-position:center; background-size: cover; background-repeat:no-repeat;\"></body></html>", "text/html","UTF-8");
        //loadData("<html style=\"height:100%;\"><head><style>* {margin:0;padding:0;ovrflow:hidden;}</style></head><body style=\"100%;background-image:url('http://cheez.co/static/res/img/loading.gif');background-position:center; background-repeat:no-repeat;\"><video autoplay name=\"media\" style=\"width:100%;height:100%;object-fit: cover;\"><source src=\"" + URLEncoder.encode(url, "utf-8") + "\" type=\"video/mp4\"></video></body></html>", "text/html","UTF-8");



    }
}
