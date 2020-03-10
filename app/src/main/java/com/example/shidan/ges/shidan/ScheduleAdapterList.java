package com.example.shidan.ges.shidan;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shidan.ges.AdminDevice;
import com.example.shidan.ges.R;
import com.example.shidan.ges.model.Device;
import com.example.shidan.ges.model.DeviceComp;
import com.example.shidan.ges.model.Schedule;

import java.util.ArrayList;

public class ScheduleAdapterList extends ArrayAdapter<Schedule> {
    private Context mContext;
    private int mResource;



    private ArrayList<Schedule> schedules;
    private ViewHolder holder;
    private int lastPosition = -1;

    static class ViewHolder {
        TextView schedule_hour;
        Button schedule_btn_edit;

    }

    public ScheduleAdapterList(Context context, int resource, ArrayList<Schedule> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        schedules = objects;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.schedule_hour = (TextView) convertView.findViewById(R.id.schedule_hour);

            result = convertView;
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.scroll_down_anim : R.anim.scroll_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.schedule_hour.setText(getItem(position).getTime());

        return convertView;
    }


}