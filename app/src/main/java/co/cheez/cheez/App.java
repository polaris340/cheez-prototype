package co.cheez.cheez;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashSet;

import de.greenrobot.event.EventBus;

/**
 * Created by jiho on 4/5/15.
 */
public class App extends Application {
    private static RequestQueue mRequestQueue;
    private static Context context;
    private static HashSet<Long> postIdHashSet = new HashSet<>();
    private static ArrayList<Post> data = new ArrayList<>();
    private static KakaoLink mKakaoLink;

    private static final int MEMORY_CACHE_SIZE = 2 * 1024 * 1024;
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;
    private static final int DISK_CACHE_FILE_COUNT = 100;

    public static final String BASE_URL = "http://cheez.co";

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();


        context = this;

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        DisplayImageOptions.Builder mDefaultDisplayImageOptionBuilder = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(200));

        // AUIL Settings
        DisplayImageOptions mDefaultDisplayImageOptions = mDefaultDisplayImageOptionBuilder.build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(mDefaultDisplayImageOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(MEMORY_CACHE_SIZE))
                .diskCacheSize(DISK_CACHE_SIZE)
                .diskCacheFileCount(DISK_CACHE_FILE_COUNT)
                .build();
        ImageLoader.getInstance().init(config);

    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

    public static ArrayList<Post> getData() {
        return data;
    }

    public static void addPost(Post post) {
        if (post == null) {
            data.add(post);
            EventBus.getDefault().post(new DataUpdateEvent());
        } else if (!postIdHashSet.contains(post.id)) {
            postIdHashSet.add(post.id);
            data.add(post);
            EventBus.getDefault().post(new DataUpdateEvent());
        }
    }

    public static KakaoLink getKakaoLink() {
        if (mKakaoLink == null) {
            try {
                mKakaoLink = KakaoLink.getKakaoLink(context);
            } catch (KakaoParameterException e) {
                e.printStackTrace();
            }
        }
        return mKakaoLink;
    }

    public static Context getContext() {
        return context;
    }

    public static class DataUpdateEvent {

    }

    public synchronized Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.app_tracker);
        }
        return mTracker;
    }

}
