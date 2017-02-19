/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private EarthquaAdapter mAdapter;

    private static final String USGS_REQUEST_URL =
            "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=9";
    /**
     * 地震 loader ID 的常量值。我们可选择任意整数。
     * 仅当使用多个 loader 时该设置才起作用。
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    /** 列表为空时显示的 TextView */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG,"重新创建一个Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // 创建新适配器，将空地震列表作为输入
        mAdapter = new EarthquaAdapter(this, new ArrayList<Earthquake>());
        // 在 {@link ListView} 上设置适配器
        // 以便可以在用户界面中填充列表
        earthquakeListView.setAdapter(mAdapter);

        // 在 ListView 上设置项目单击监听器，该监听器会向 Web 浏览器发送 intent，
        // 打开包含有关所选地震详细信息的网站。
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //点击的 当前地震
                Earthquake currentEarthquake = mAdapter.getItem(position);
                // 将字符串 URL 转换为 URI 对象（以传递至 Intent 中 constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());
                //创建查看详细信息的intent
                Intent webIntent = new Intent(Intent.ACTION_VIEW,earthquakeUri);
                //启动
                startActivity(webIntent);
            }
        });



        // 初始化 loader。传递上面定义的整数 ID 常量并为为捆绑
        // 传递 null。为 LoaderCallbacks 参数（由于
        // 此活动实现了 LoaderCallbacks 接口而有效）传递此活动。
        /**
         * 此方法新建了一个loader(如果已存在loader_id ==1则直接使用)
         * 执行onCreateLoader()方法
         * 之后再后台线程执行EarthquakeLoader.loadInBackground()返回一个地震列表到
         * onLoadFinished()之后填充到mAdapter实现视图的更新
         */

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        /**
         * 获取网络连接状态
         */
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // 引用 LoaderManager，以便与 loader 进行交互。
            LoaderManager loaderManager = getLoaderManager();
            /**
             * 存在网络连接，则新建Loader
             */
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            /**
             * 无网络连接 时
             * 隐藏加载指示符
             * 提示无网络信息
             */
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }


    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        return new EarthquakeLoader(this,USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {
        // 因数据已加载，隐藏加载指示符
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // 清除之前地震数据的适配器
        mAdapter.clear();

        // 如果存在 {@link Earthquake} 的有效列表，则将其添加到适配器的
        // 数据集。这将触发 ListView 执行更新。
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
        /**
         * 为避免首次启动应用时屏幕中闪现“未发现地震。(No earthquakes found.)”消息
         * 将空状态 TextView 留空， 直至完成第一次加载
         */
        mEmptyStateTextView.setText(R.string.no_earthquakes);
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
       // Log.v(LOG_TAG,"重置了Loader");
        // 重置 Loader，以便能够清除现有数据。
       mAdapter.clear();
    }
    //添加Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //选中的是action_settings
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
