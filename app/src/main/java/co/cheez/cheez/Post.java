package co.cheez.cheez;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiho on 4/5/15.
 */
public class Post {
    public long id;
    public String title;
    public String subtitle;
    public String url;
    public String imageUrl;
    public boolean liked;

    private static Response.Listener listener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
        }
    };
    private static Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    };

    public static Post fromJsonString(String jsonString) throws JSONException {
        return fromJsonObject(new JSONObject(jsonString));
    }

    public static Post fromJsonObject(JSONObject jsonObject) throws JSONException {
        Post post = new Post();
        post.id = jsonObject.getLong("id");
        post.title = jsonObject.getString("title");
        if (jsonObject.has("subtitle")) {
            post.subtitle = jsonObject.getString("subtitle");
        }
        post.url = jsonObject.getString("url");
        post.imageUrl = jsonObject.getString("image_url");


        post.liked = jsonObject.getString("liked").equals("1");

        return post;
    }


    public void pass() {
        Request request = new StringRequest(
                Request.Method.POST,
                App.BASE_URL + "/pass/" + id,
                listener, errorListener
        );
        App.getRequestQueue().add(request);
    }

    public void linkClick() {
        Request request = new StringRequest(
                Request.Method.POST,
                App.BASE_URL + "/link-click",
                listener, errorListener
        ) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("post_id",""+id);

                return params;
            }
        };
        App.getRequestQueue().add(request);
    }

    public void like(final boolean isLike) {
        if (isLike) {
            this.liked = !this.liked;
        }
        Request request = new StringRequest(
                Request.Method.POST,
                App.BASE_URL + "/like",
                listener, errorListener
        ) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("post_id",""+id);
                params.put("is_liked", isLike?"1":"0");

                return params;
            }
        };
        App.getRequestQueue().add(request);
    }

    public void touch(final float windowWidth, final float windowHeight, final float x, final float y, final String action) {

        Request request = new StringRequest(
                Request.Method.POST,
                App.BASE_URL + "/app_touch_log",
                listener, errorListener
        ) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("post_id",""+id);
                params.put("x", "" + x);
                params.put("y", "" + y);
                params.put("device_width", ""+windowWidth);
                params.put("device_height", ""+windowHeight);
                params.put("action", action);

                return params;
            }
        };
        App.getRequestQueue().add(request);
    }
}
