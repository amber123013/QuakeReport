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

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private EarthquaAdapter mAdapter;

    private static final String USGS_REQUEST_URL =
            "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        // 启动 AsyncTask 以获取地震数据
        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);

    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {

        @Override
        protected List<Earthquake> doInBackground(String... params) {
            // 如果不存在任何 URL 或第一个 URL 为空，不执行执行。
            if (params.length < 1 || params[0] == null) {
                return null;
            }

            List<Earthquake> result = QueryUtils.fetchEarthquakeData(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {
            // 清除之前地震数据的适配器
            mAdapter.clear();

            // 如果存在 {@link Earthquake} 的有效列表，则将其添加到适配器的
            // 数据集。这将触发 ListView 执行更新。
            if (earthquakes != null && !earthquakes.isEmpty()) {
                mAdapter.addAll(earthquakes);
            }
        }
    }
}
