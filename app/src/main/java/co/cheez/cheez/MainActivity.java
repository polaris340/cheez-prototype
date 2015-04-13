package co.cheez.cheez;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.cheez.cheez.util.MessageUtil;
import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {
    WebView mWebView;
    ProgressBar mProgressBar;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());

//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        getSupportActionBar().hide();

        App.getData().clear();
        EventBus.getDefault().post(new App.DataUpdateEvent());
        Request request = new JsonObjectRequest(
                "http://cheez.co/posts/1",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        progressDialog.hide();
                        try {
                            JSONArray data = response.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    Post post = Post.fromJsonObject(data.getJSONObject(i));
                                    App.addPost(post);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (App.getData().size() == 0) {
                                findViewById(R.id.iv_last_page).setVisibility(View.VISIBLE);
                            } else {
                                startActivity(ContentViewActivity.getIntent(MainActivity.this));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MessageUtil.showDefaultErrorMessage();
                    }
                }
        );
        App.getRequestQueue().add(request);
    }






}