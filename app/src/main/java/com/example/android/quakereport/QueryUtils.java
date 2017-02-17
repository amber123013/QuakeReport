package com.example.android.quakereport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    private QueryUtils() {
    }

    /**
     *解析服务器获取的JSON字符串，返回Earthquake数组
     */
    private static List<Earthquake> extractFeatureFromJson(String earthquakeJSON) {

        //存放Earthquake对象
        List<Earthquake> earthquakes = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(earthquakeJSON);
            // 提取与名为 "features" 的键关联的 JSONArray，
            // 该键表示特征（或地震）列表。
            JSONArray earthquakeArray = root.getJSONArray("features");
            // 针对 earthquakeArray 中的每个地震，创建 {@link Earthquake} 对象
            for(int i = 0; i < earthquakeArray.length(); i++) {
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");
                double magnitude = properties.getDouble("mag");
                String location = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");
                earthquakes.add(new Earthquake(magnitude,location,time,url));
            }
        } catch (JSONException e) {
            // 在 "try" 块中执行上述任一语句时若系统抛出错误，
            // 则在此处捕获异常，以便应用不会崩溃。在日志消息中打印
            // 来自异常的消息。
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return earthquakes;
    }
    /**
     * 从给定字符串 URL 返回新 URL 对象。
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }
    /**
     * 向给定 URL 进行 HTTP 请求，并返回字符串作为响应。
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        //如果url为空，则退出函数
        if(url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            url.openConnection().connect();

            //如果请求成功（code ==200）
            //取得输入流并解析取得字符串
            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch(IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inputStream != null) {
                // 关闭输入流可能会抛出 IOException，这就是
                //  makeHttpRequest(URL url) 方法签名指定可能抛出 IOException 的
                // 原因。
                inputStream.close();
            }
        }

        return jsonResponse;
    }
    /**
     * 将 {@link InputStream} 转换为包含
     * 来自服务器的整个 JSON 响应的字符串。
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            //使用BufferedReader封装，能够读取一行的数据
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    //该方法将所有步骤连接在一起，即创建 URL、发送请求、处理响应。
    // 这是 EarthquakeAsyncTask 需要交互的唯一 "public" QueryUtils 方法
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {
        //创建URL对象
        URL url = createUrl(requestUrl);
        // 执行 URL 的 HTTP 请求并接收返回的 JSON 响应
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // 从 JSON 响应提取相关域并创建 {@link Earthquake} 的列表
        List<Earthquake> earthquakes = extractFeatureFromJson(jsonResponse);

        return earthquakes;

    }
}