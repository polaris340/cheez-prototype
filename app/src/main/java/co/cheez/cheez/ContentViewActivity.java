package co.cheez.cheez;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.cheez.cheez.fragment.ContentViewFragment;
import co.cheez.cheez.util.MessageUtil;
import de.greenrobot.event.EventBus;


public class ContentViewActivity extends ActionBarActivity {
    public static final int MAX_CONTENT_COUNT = 20;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ContentViewActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_view);


        getSupportActionBar().hide();



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //mViewPager.setPageTransformer(false, new DepthPageTransformer());

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mSectionsPagerAdapter.getCount() > MAX_CONTENT_COUNT) {
                    return;
                }
                if (position == mSectionsPagerAdapter.getCount() - 1) {

                    final ProgressDialog progressDialog = new ProgressDialog(ContentViewActivity.this);
                    progressDialog.show();
                    Request request = new JsonObjectRequest(
                            "http://cheez.co/posts/1",
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progressDialog.hide();
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

                                        if (App.getData().size() >= MAX_CONTENT_COUNT) {
                                            App.addPost(null);
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

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            EventBus.getDefault().register(this);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return ContentViewFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return App.getData().size();
        }

        public void onEvent(App.DataUpdateEvent event) {
            notifyDataSetChanged();
        }
    }


}
