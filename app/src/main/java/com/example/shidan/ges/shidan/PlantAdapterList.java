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

import java.util.ArrayList;

public class PlantAdapterList extends ArrayAdapter<Plant> {
    int lastPosition = 0;
    private Context mContext;
    private int mResource;
    private ArrayList<Plant> plants;
    private ViewHolder holder;

    public PlantAdapterList(Context context, int resource, ArrayList<Plant> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        plants = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new PlantAdapterList.ViewHolder();
            holder.plant_image = (ImageView) convertView.findViewById(R.id.plant_image);
            holder.plant_name = (TextView) convertView.findViewById(R.id.plant_name);
            holder.plant_scientific_name = (TextView) convertView.findViewById(R.id.plant_scientific_name);
            holder.plant_description = (TextView) convertView.findViewById(R.id.plant_description);
            holder.id_set = (TextView) convertView.findViewById(R.id.id_set);

            result = convertView;
            convertView.setTag(holder);

        } else {
            holder = (PlantAdapterList.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.scroll_down_anim : R.anim.scroll_up_anim);
        result.startAnimation(animation);
        lastPosition = position;
        holder.plant_name.setText(getItem(position).getName());
        holder.plant_scientific_name.setText(getItem(position).getScientific_name());
        holder.plant_description.setText(getItem(position).getDescription());
        holder.id_set.setText(String.valueOf(getItem(position).getId()));

        return convertView;
    }

    public ArrayList<Plant> getAllData() {
        return plants;
    }

    static class ViewHolder {
        ImageView plant_image;
        TextView plant_name, plant_scientific_name, plant_description, id_set;

    }
}
