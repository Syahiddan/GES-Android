package com.example.shidan.ges.shidan;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shidan.ges.R;

public class CustomProgressBarShidan {

    private View full_layout;
    private ProgressBar progressBar;
    private TextView progressBarText;
    private ObjectAnimator progressAnimator;
    private int currPercentage;
    private Context context;
    private Activity activity;

    public CustomProgressBarShidan(Context context, Activity activity) {
        this.activity = activity;
        this.context = context;
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        full_layout = li.inflate(R.layout.custom_progress_bar, null);
        progressBar = full_layout.findViewById(R.id.progressBar);
        progressBarText = full_layout.findViewById(R.id.loading_text);
        currPercentage = 0;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public TextView getProgressBarText() {
        return progressBarText;
    }

    public void setProgressBarText(String str) {
        this.progressBarText.setText(str);
    }

    public View getProgressBarLayout() {
        return full_layout;
    }

    public void animateTo(final int level) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", currPercentage, level);
                progressAnimator.setDuration(5000);
                progressAnimator.start();
                currPercentage = level;
//                Toast.makeText(context,String.valueOf(level),Toast.LENGTH_SHORT).show();

            }
        });
    }
    public  void setProgressLoading(int level)
    {
        this.progressBar.setProgress(level);
    }

}
