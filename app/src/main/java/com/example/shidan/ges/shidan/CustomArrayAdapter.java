package com.example.shidan.ges.shidan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.shidan.ges.R;
import com.example.shidan.ges.model.Plant;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter {

    private ViewHolder holder;
    private int mResource;
    private Context mContext;
    private ArrayList<Plant> plants;
    private int lastPosition = -1;

    static class ViewHolder {
        TextView plant_id;
        TextView plant_name;

    }
    public CustomArrayAdapter(Context context, int resource, ArrayList<Plant> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        plants = objects;
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        final View result;
        Plant plant_temp = plants.get(position);
//        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new CustomArrayAdapter.ViewHolder();
            holder.plant_id = convertView.findViewById(R.id.plant_id);
            holder.plant_name = convertView.findViewById(R.id.plant_name);

            result = convertView;
//            convertView.setTag(holder);

//        } else {
//            holder = (CustomArrayAdapter.ViewHolder) convertView.getTag();
//            result = convertView;
//        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.scroll_down_anim : R.anim.scroll_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.plant_id.setText(String.valueOf(plant_temp.getId()));
        holder.plant_name.setText(plant_temp.getName());
        return convertView;

    }
}
