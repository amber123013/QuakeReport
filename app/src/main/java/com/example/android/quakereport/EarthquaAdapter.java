package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ASUS on 2017-02-12.
 */

public class EarthquaAdapter extends ArrayAdapter<Earthquake>{

//    分离地址
    private static final String LOCATION_SEPARATOR = " of ";

    public EarthquaAdapter(Context context, List<Earthquake> objects) {
        super(context, 0 , objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String primaryLocation;
        String locationOffset;
        // 检查是否已经有可以重用的列表项视图（称为 convertView），
        // 否则，如果 convertView 为 null，则 inflate 一个新列表项布局。
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.earthquake_list_item, parent, false);
        }

        // 在地震列表中的给定位置找到地震
        Earthquake currentEarthquake = getItem(position);

        // 找到视图 ID 为 magnitude 的 TextView
        TextView magnitudeView = (TextView) listItemView.findViewById(R.id.magnitude);
        // 在该 TextView 中显示目前地震的震级
        magnitudeView.setText(formatMagnitude(currentEarthquake.getMagnitude()));

        // 为震级圆圈设置正确的背景颜色。
        // 从 TextView 获取背景，该背景是一个 GradientDrawable。
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();

        // 根据当前的地震震级获取相应的背景颜色
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());

        // 设置震级圆圈的颜色
        magnitudeCircle.setColor(magnitudeColor);

        //地震位置("88km N of Yelizovo, Russia")
        String originalLocation = currentEarthquake.getLocation();
        //判断是否有位置的详细描述
        if(originalLocation.contains(LOCATION_SEPARATOR)) {
            //有，分离字符串
            String[] parts = originalLocation.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        } else {
            locationOffset = getContext().getString(R.string.near_the);
            primaryLocation = originalLocation;
        }
        // 找到视图 ID 为 primary_location 的 TextView
        TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.primary_location);
        primaryLocationView.setText(primaryLocation);
        // 找到视图 ID 为 location_Offset 的 TextView
        TextView locationOffsetView = (TextView) listItemView.findViewById(R.id.location_offset);
        locationOffsetView.setText(locationOffset);

        // 根据地震时间（以毫秒为单位）创建一个新的 Date 对象
        Date dateObject = new Date(currentEarthquake.getDate());

        // 找到视图 ID 为 date 的 TextView
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        // 设置日期字符串的格式（即 "Mar 3, 1984"）
        String formattedDate = formatDate(dateObject);
        // 在该 TextView 中显示目前地震的日期
        dateView.setText(formattedDate);

        // 找到视图 ID 为 time 的 TextView
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        // 设置时间字符串的格式（即 "4:30PM"）
        String formattedTime = formatTime(dateObject);
        // 在该 TextView 中显示目前地震的时间
        timeView.setText(formattedTime);

        // 返回目前显示适当数据的列表项视图
        return listItemView;
    }

    /**
     *根据传递过来的震级，返回对应的颜色
     */
    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        //向下取整
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        //颜色资源 ID 仅指向我们定义的资源，而非颜色的 值
        //调用 ContextCompat getColor() 以将颜色资源 ID 转换为 实际整数颜色值
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
    /**
     * 从 Date 对象返回格式化的日期字符串（即 "Mar 3, 1984"）。
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * 从 Date 对象返回格式化的时间字符串（即 "4:30 PM"）。
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
    /**
     * 从十进制震级值返回格式化后的仅显示一位小数的震级字符串
     * （如“3.2”）。
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }
}
