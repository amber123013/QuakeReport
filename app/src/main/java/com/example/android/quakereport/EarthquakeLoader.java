package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by ASUS on 2017-02-18.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    /** 日志消息标签 */
    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    /** 查询 URL */
    private String mUrl;

    /**必须执行此方法才能实际触发 loadInBackground() 方法的执行*/
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * 此方法相当于AsyncTask的doInBackground
     * 此方法运行在后台线程
     */
    @Override
    public List<Earthquake> loadInBackground() {
        if (mUrl == null || mUrl == "") {
            return null;
        }
        // 执行网络请求、解析响应和提取地震列表。
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        Log.v(LOG_TAG,"请求了一次信息");
        return earthquakes;
    }
}
