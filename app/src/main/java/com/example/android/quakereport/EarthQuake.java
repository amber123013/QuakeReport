package com.example.android.quakereport;

/**
 * Created by ASUS on 2017-02-12.
 */

public class Earthquake {
    /**地震 震级*/
    private double mMagnitude;
    /**地震 发生地点*/
    private String mLocation;
    /**地震 信息*/
    private long mDate;
    /**地震的详细网址*/
    private String mUrl;
    public Earthquake(double magnitude, String location, long date,String url) {
        this.mMagnitude = magnitude;
        this.mLocation = location;
        this.mDate = date;
        this.mUrl = url;
    }

    /**
     * 返回地震震级
     * @return 地震震级
     */
    public double getMagnitude() {
        return mMagnitude;
    }

    /**
     * 返回地震发生地点
     * @return 地点
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * 返回地震发生时间
     * @return 时间
     */
    public long getDate() {
        return mDate;
    }
    /**
     * 返回地震详细的url
     * @return url
     */
    public String getUrl() {
        return mUrl;
    }
}
