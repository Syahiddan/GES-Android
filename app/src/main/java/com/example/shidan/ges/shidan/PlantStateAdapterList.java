package com.example.shidan.ges.shidan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shidan.ges.R;
import com.example.shidan.ges.model.Plant;
import com.example.shidan.ges.model.PlantState;

import java.util.ArrayList;
import java.util.List;

public class PlantStateAdapterList extends ArrayAdapter<PlantState> {
    private Context context;
    private int resources;
    private ViewHolder holder;
    private ArrayList<PlantState> plantStates;
    private int lastPosition = 0;

    static class ViewHolder {
        TextView code,state_name,humidity,temperature;

    }
    public PlantStateAdapterList(Context context, int resource, ArrayList<PlantState> objects) {
        super(context,resource,objects);
        this.context = context;
        this.resources = resource;
        this.plantStates = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View result;

        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resources,parent,false);
            holder = new PlantStateAdapterList.ViewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.code);
            holder.state_name = (TextView) convertView.findViewById(R.id.state_name);
            holder.humidity = (TextView) convertView.findViewById(R.id.humidity);
            holder.temperature = (TextView)convertView.findViewById(R.id.temperature);

            result = convertView;
            convertView.setTag(holder);
        }
        else
        {
            holder = (PlantStateAdapterList.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.scroll_down_anim : R.anim.scroll_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.code.setText(getItem(position).getCode());
        holder.state_name.setText(getItem(position).getState_name());
        holder.humidity.setText(String.valueOf(getItem(position).getHumidity()));
        holder.temperature.setText(String.valueOf(getItem(position).getTemperature()));

        return convertView;
    }
}
