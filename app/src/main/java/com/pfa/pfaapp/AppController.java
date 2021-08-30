package com.pfa.pfaapp;

import android.content.res.Configuration;

import androidx.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.LruBitmapCache;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import java.util.Locale;

import static com.pfa.pfaapp.utils.AppConst.SP_APP_LANG;

public class AppController extends MultiDexApplication {

    public static final String TAG = AppController.class
            .getSimpleName();

    private static AppController mInstance;
    private RequestQueue mRequestQueue;

    private ImageLoader mImageLoader;
    private Locale locale = null;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Fresco.initialize(this);

        updateLocale();
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    public void updateLocale() {
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(getBaseContext());
        String lang = sharedPrefUtils.getSharedPrefValue(SP_APP_LANG, "");

        if (lang == null || sharedPrefUtils.isEnglishLang()) {
            lang = String.valueOf(AppUtils.APP_LANGUAGE.en);
        }

        Configuration config = getBaseContext().getResources().getConfiguration();

        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }

}
