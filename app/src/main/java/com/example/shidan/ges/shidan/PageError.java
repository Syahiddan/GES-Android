package com.example.shidan.ges.shidan;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shidan.ges.R;

import java.util.Timer;
import java.util.TimerTask;

public class PageError {

    private Context context;
    private Class curClass;
    private View view;
    private WebService ws;
    private Button refresh_btn;
    private RelativeLayout rl1, rl2;
    private TextView tv_fetching;

    public PageError(Context context, Class curClass) {
        this.context = context;
        this.curClass = curClass;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_network_error, null);
        refresh_btn = (Button) view.findViewById(R.id.refresh_btn);
        rl1 = (RelativeLayout) view.findViewById(R.id.img_relative_layout);
        rl2 = (RelativeLayout) view.findViewById(R.id.text_relative_layout);
        tv_fetching = (TextView) view.findViewById(R.id.tv_fetching);

        rl1.setVisibility(View.GONE);
        rl2.setVisibility(View.GONE);

        ws = new WebService(context);
    }

    public View openWindow() {

        return view;
    }

    public void openLoading() {
        rl1.setVisibility(View.VISIBLE);
        rl2.setVisibility(View.VISIBLE);

    }
    public void closeLoading() {
        rl1.setVisibility(View.GONE);
        rl2.setVisibility(View.GONE);

    }


}
