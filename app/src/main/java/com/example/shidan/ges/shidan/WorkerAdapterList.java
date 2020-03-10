package com.example.shidan.ges.shidan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shidan.ges.R;
import com.example.shidan.ges.model.User;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

public class WorkerAdapterList extends ArrayAdapter<User> {

    private Context mContext;
    private int mResource;
    private ArrayList<User> workers;
    private int lastPosition = -1;
    private int img_width = 0;
    private int img_height = 0;
    private static ViewGroup parent;
    private Typeface tf1;
    private WebService ws;

    private ImageView worker_img_temp;
    //counter...

    int view_edit_count = 0;

    private ViewHolder holder;

    static class ViewHolder {
        ImageView worker_img_space;

        TextView worker_label_id;
        TextView worker_label_phone;
        TextView worker_label_name;
        TextView worker_label_email;
        TextView worker_label_address;
        TextView worker_label_postcode;
        TextView worker_label_state;
        TextView worker_label_country;

        TextView worker_id_space;
        TextView worker_phone_space;
        TextView worker_name_space;
        TextView worker_email_space;
        TextView worker_address_space;
        TextView worker_postcode_space;
        TextView worker_state_space;
        TextView worker_country_space;

        TextView delete_btn;
        TextView edit_btn;

    }

    public WorkerAdapterList(Context context, int resource, ArrayList<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        workers = objects;
        ws = new WebService(mContext);
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        this.parent = parent;
        final View result;


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.worker_img_space = (ImageView) convertView.findViewById(R.id.worker_img);

            holder.worker_label_id = (TextView) convertView.findViewById(R.id.worker_label_id);
            holder.worker_label_phone = (TextView) convertView.findViewById(R.id.worker_label_phone);
            holder.worker_label_email = (TextView) convertView.findViewById(R.id.worker_label_email);
            holder.worker_label_name = (TextView) convertView.findViewById(R.id.worker_label_name);
            holder.worker_label_address = (TextView) convertView.findViewById(R.id.worker_label_address);
            holder.worker_label_postcode = (TextView) convertView.findViewById(R.id.worker_label_postcode);
            holder.worker_label_state = (TextView) convertView.findViewById(R.id.worker_label_state);
            holder.worker_label_country = (TextView) convertView.findViewById(R.id.worker_label_country);

            holder.worker_id_space = (TextView) convertView.findViewById(R.id.worker_id_space);
            holder.worker_phone_space = (TextView) convertView.findViewById(R.id.worker_phone_space);
            holder.worker_email_space = (TextView) convertView.findViewById(R.id.worker_email_space);
            holder.worker_name_space = (TextView) convertView.findViewById(R.id.worker_name_space);
            holder.worker_address_space = (TextView) convertView.findViewById(R.id.worker_address_space);
            holder.worker_postcode_space = (TextView) convertView.findViewById(R.id.worker_postcode_space);
            holder.worker_state_space = (TextView) convertView.findViewById(R.id.worker_state_space);
            holder.worker_country_space = (TextView) convertView.findViewById(R.id.worker_country_space);
            holder.edit_btn = (TextView) convertView.findViewById(R.id.edit_btn);
            holder.delete_btn = (TextView) convertView.findViewById(R.id.delete_btn);

            holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"no error",Toast.LENGTH_SHORT).show();
                }
            });
            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.scroll_down_anim : R.anim.scroll_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

//        Toast.makeText(getContext(),getItem(position).getImage(),Toast.LENGTH_SHORT).show();
        if (!getItem(position).getImage().equals("null")) {
//            Picasso.get().invalidate("");
            Picasso.get().load("http://192.168.137.1/ges/UserImage/" + getItem(position).getImage() + ".png")
                    .resize(100, 100)
                    .centerCrop()
//                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(holder.worker_img_space);

        } else {
            Picasso.get().load("http://i.imgur.com/DvpvklR.png")
                    .resize(100, 100)
                    .centerCrop()
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(holder.worker_img_space);

        }
        holder.worker_id_space.setText(getItem(position).getWorker_id());
        holder.worker_phone_space.setText(getItem(position).getPhone());
        holder.worker_name_space.setText(getItem(position).getFname() + " " + getItem(position).getMidname() + " " + getItem(position).getLname());
        holder.worker_email_space.setText(getItem(position).getEmail());
        holder.worker_address_space.setText(getItem(position).getAddress());
        holder.worker_postcode_space.setText(String.valueOf(getItem(position).getPostcode()));
        holder.worker_state_space.setText(getItem(position).getState());
        holder.worker_country_space.setText(getItem(position).getCountry());


        tf1 = ResourcesCompat.getFont(mContext, R.font.aclonica);
        holder.worker_label_name.setTypeface(tf1);
        holder.worker_label_email.setTypeface(tf1);
        holder.worker_label_phone.setTypeface(tf1);
        holder.worker_label_id.setTypeface(tf1);
        holder.worker_label_address.setTypeface(tf1);
        holder.worker_label_postcode.setTypeface(tf1);
        holder.worker_label_state.setTypeface(tf1);
        holder.worker_label_country.setTypeface(tf1);

        holder.worker_email_space.setTypeface(tf1);
        holder.worker_id_space.setTypeface(tf1);
        holder.worker_phone_space.setTypeface(tf1);
        holder.worker_name_space.setTypeface(tf1);
        holder.worker_address_space.setTypeface(tf1);
        holder.worker_postcode_space.setTypeface(tf1);
        holder.worker_state_space.setTypeface(tf1);
        holder.worker_country_space.setTypeface(tf1);

        holder.edit_btn.setTypeface(tf1);

        return convertView;
    }

    public void setReset() {
        for (int i = 0; i < this.parent.getChildCount(); i++) {
            TextView edit_btn = (TextView) this.parent.getChildAt(i).findViewById(R.id.edit_btn);
            TextView delete_btn = (TextView) this.parent.getChildAt(i).findViewById(R.id.delete_btn);
            LinearLayout ll_1 = (LinearLayout) this.parent.getChildAt(i).findViewById(R.id.ll_address);
            LinearLayout ll_2 = (LinearLayout) this.parent.getChildAt(i).findViewById(R.id.ll_postcode_state);
            LinearLayout ll_3 = (LinearLayout) this.parent.getChildAt(i).findViewById(R.id.ll_country);
            ll_1.setVisibility(View.GONE);
            ll_2.setVisibility(View.GONE);
            ll_3.setVisibility(View.GONE);
            edit_btn.setVisibility(View.GONE);
            delete_btn.setVisibility(View.GONE);


        }
    }






}
