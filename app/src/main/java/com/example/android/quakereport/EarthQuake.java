package com.example.android.quakereport;

/**
 * Created by ASUS on 2017-02-12.
 */

public class Earthquake {
    /**地震 震级*/
    private String mMagnitude;
    /**地震 发生地点*/
    private String mLocation;
    /**地震 信息*/
    private String mDate;

    public Earthquake(String magnitude, String location, String date) {
        this.mMagnitude = magnitude;
        this.mLocation = location;
        this.mDate = date;
    }

    /**
     * 返回地震震级
     * @return 地震震级
     */
    public String getMagnitude() {
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
    public String getDate() {
        return mDate;
    }
}
