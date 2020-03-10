package com.example.shidan.ges;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shidan.ges.model.User;
import com.example.shidan.ges.shidan.InputTextBehaviour;
import com.example.shidan.ges.shidan.PageError;
import com.example.shidan.ges.shidan.Session;
import com.example.shidan.ges.shidan.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    User curUser;
    AlertDialog ui;
    private EditText et_username, et_password;
    private TextView tv_username, tv_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_password = (TextView) findViewById(R.id.tv_password);

        Button login = (Button) findViewById(R.id.btn_login);

        InputTextBehaviour username = new InputTextBehaviour("standard", et_username, tv_username);
        username.startBehaviour();
        InputTextBehaviour password = new InputTextBehaviour("standard", et_password, tv_password);
        password.startBehaviour();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (Session.getUser() != null) {
            Toast.makeText(Login.this, "Resume back dah penah login = " + Session.getUser().getFname(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, Session.getCurrentContext());
            startActivity(intent);
            finish();
        }
    }

    public void changePasswordState(View v) {

        Button lol = (Button) findViewById(v.getId());
        et_password.setInputType((et_password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        lol.setBackgroundResource((et_password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) ? R.drawable.icon_unhide : R.drawable.icon_hide);
        et_password.setSelection(et_password.length());
    }

    public void loginState(View v) {


        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        username = username.trim();
        password = password.trim();

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        } catch (Exception e) {

        }

        final HashMap<Object, Object> cubeString = new HashMap<Object, Object>();

        if (!username.isEmpty() && !password.isEmpty()) {
            ui = new AlertDialog.Builder(this).setView(R.layout.loading).show();
            cubeString.put("function", "fnLogin");
            cubeString.put("username", username);
            cubeString.put("pass", password);

            final WebService cube1 = new WebService(getApplicationContext());
            Thread loginThread = new Thread() {
                @Override
                public void run() {
                    cube1.post("", cubeString, new WebService.JSON() {
                        @Override
                        public void responseData(JSONObject Status, JSONObject data) {
                            try {
                                Log.d("shidan", Status.toString());
                                if (Status.getString("statusRequest").equals("success")) {
                                    if (data.getString("status").equals("valid")) {
                                        JSONObject data_temp = data.getJSONObject("data");
                                        curUser = new User(data_temp.optString("worker_id"), data_temp.optString("worker_type"), data_temp.optString("fname"), data_temp.optString("midname"), data_temp.optString("lname"), data_temp.optString("email"), data_temp.optString("phone"), data_temp.optString("address"), data_temp.getInt("postcode"), data_temp.optString("state"), data_temp.optString("country"));
                                        Session.setUser(curUser);
                                        Session.setCurrentContext(Admin.class);
                                        if (data_temp.getString("worker_type").equals("A") || data_temp.getString("worker_type").equals("a")) {
                                            Intent jumpToAdminPage = new Intent(Login.this, Admin.class);

                                            startActivity(jumpToAdminPage);
                                            ui.dismiss();
                                            finish();
                                        } else if (data_temp.getString("worker_type").equals("W") || data_temp.getString("worker_type").equals("w")) {
                                            Toast.makeText(getApplicationContext(), "Normal user login success", Toast.LENGTH_SHORT).show();
                                            ui.dismiss();

                                            Intent jumpToAdminPage = new Intent(Login.this, Admin.class);
                                            startActivity(jumpToAdminPage);
                                            finish();

                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Invalid Username and Password", Toast.LENGTH_SHORT).show();
                                        ui.dismiss();
                                    }
                                }
                                else if(Status.getString("statusRequest").equals("org.json.JSONException: Value [] of type org.json.JSONArray cannot be converted to JSONObject"))
                                {
                                    Toast.makeText(Login.this,"Invalid User",Toast.LENGTH_SHORT).show();
                                    ui.dismiss();
                                }
                                else
                                {
                                    Toast.makeText(Login.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                                    final PageError pageError = new PageError(Login.this, Login.class);
                                    setContentView(pageError.openWindow());
                                    pageError.openWindow().findViewById(R.id.refresh_btn).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            pageError.openLoading();
                                            final HashMap<Object, Object> params = new HashMap<Object, Object>();
                                            params.put("function", "checkConnection");
                                            Thread thread= new Thread() {
                                                @Override
                                                public void run() {
                                                    cube1.post("", params, new WebService.JSON() {
                                                        @Override
                                                        public void responseData(JSONObject status, JSONObject data) {
                                                            try {
                                                                Log.d("shidan", status.toString());
                                                                if (status.getString("statusRequest").equals("success")) {
                                                                    if (data.getString("status").equals("connected")) {
                                                                        Toast.makeText(Login.this, "connected", Toast.LENGTH_SHORT).show();
                                                                        new Handler().postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                pageError.closeLoading();
                                                                                Log.d("shidan", "coonnected");
                                                                                Intent loginIntent = new Intent(Login.this, Login.class);
                                                                                startActivity(loginIntent);
                                                                                finish();
                                                                            }
                                                                        }, 500);
                                                                    }
                                                                } else {
                                                                    Toast.makeText(Login.this, "No Network connected to server", Toast.LENGTH_SHORT).show();
                                                                    pageError.closeLoading();
                                                                }

                                                            } catch (JSONException e) {
                                                            }
                                                        }
                                                    });
                                                }
                                            };
                                            thread.start();
                                        }
                                    });

                                    ui.dismiss();
                                }

                            } catch (JSONException e) {
                                ui.dismiss();
//                                Toast.makeText(getApplicationContext(), "Invalid Username and Password4", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, "");
                }
            };
            loginThread.start();

        } else {

            if (username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "no username", Toast.LENGTH_SHORT).show();
            }
            if (password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "no password ", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
