package com.example.shidan.ges.shidan;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class InputTextBehaviour {
    private EditText editText;
    private String behaviour;
    private TextView textView;

    public InputTextBehaviour(String behaviour, EditText editText) {
        this.editText = editText;
        this.behaviour = behaviour;


    }
    public InputTextBehaviour(String behaviour, EditText editText, TextView textView) {
        this.behaviour = behaviour;
        this.editText = editText;
        this.textView = textView;
    }

    public void startBehaviour(){
        if(this.behaviour == "standard")
        {
            this.textView.setVisibility(View.INVISIBLE);

            this.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length() > 0)
                    {
                        textView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        textView.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }
}
