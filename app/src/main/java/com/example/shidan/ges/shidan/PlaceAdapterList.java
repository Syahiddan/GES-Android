package com.example.shidan.ges.shidan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shidan.ges.R;
import com.example.shidan.ges.model.Device;
import com.example.shidan.ges.model.Place;

import java.util.ArrayList;

public class PlaceAdapterList extends ArrayAdapter<Place>
{
    private Context mContext;
    private int mResource;
    private ArrayList<Place> places;
    private ViewHolder holder;
    int lastPosition = 0;

    static class ViewHolder {
        TextView place_name,place_guardian,place_id,place_sec_name,plant_id,plant_name,plant_state,place_sec_guardian;
    }

    public PlaceAdapterList(Context context, int resource, ArrayList<Place> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        places = objects;
    }

    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        final View result;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new PlaceAdapterList.ViewHolder();
            holder.place_name = (TextView) convertView.findViewById(R.id.place_name);
            holder.place_guardian = (TextView) convertView.findViewById(R.id.place_guardian);
            holder.place_id = (TextView) convertView.findViewById(R.id.place_id);
            holder.place_sec_name = (TextView) convertView.findViewById(R.id.place_sec_name);
            holder.plant_id = (TextView) convertView.findViewById(R.id.plant_id);
            holder.plant_name = (TextView)convertView.findViewById(R.id.plant_name);
            holder.plant_state = (TextView) convertView.findViewById(R.id.plant_state);
            holder.place_sec_guardian = (TextView) convertView.findViewById(R.id.place_sec_guardian);


            result = convertView;
            convertView.setTag(holder);

        } else {
            holder = (PlaceAdapterList.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.scroll_down_anim : R.anim.scroll_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.place_name.setText(getItem(position).getPlace_name());
        holder.place_guardian.setText((getItem(position).getMonitor().getFname()!= "" || getItem(position).getMonitor().getFname() != null)? getItem(position).getMonitor().getFname(): "NOT EXIST");
        holder.place_id.setText(getItem(position).getPlace_id());
        holder.place_sec_name.setText(getItem(position).getPlace_name());
        holder.plant_id.setText(String.valueOf(getItem(position).getPlant().getId()));
        holder.plant_name.setText(getItem(position).getPlant().getName());
        holder.plant_state.setText(getItem(position).getPlantState().getState_name());
        holder.place_sec_guardian.setText((getItem(position).getMonitor().getFname()!= "" || getItem(position).getMonitor().getFname() != null)? getItem(position).getMonitor().getFname() + " " + getItem(position).getMonitor().getMidname() +" " + getItem(position).getMonitor().getLname():"NOT EXIST" );

        return convertView;
    }
    public  ArrayList<Place> getAllData(){
        return places;
    }
}
