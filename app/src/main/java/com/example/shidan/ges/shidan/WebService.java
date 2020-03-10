package com.example.shidan.ges.shidan;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class WebService {
    HashMap<String, String> responseStatus = new HashMap<String, String>();
    private RequestQueue RQ;
    private static String URL;
    private Context con;

    public WebService(Context con) {
        this.con = con;
        RQ = Volley.newRequestQueue(con);
        URL = "http://192.168.137.1/GES/mobileGes.php";
    }

    public void post(String URL) {
        if (URL != "") {
            this.URL = URL;
            StringRequest sr = new StringRequest(Request.Method.POST, this.URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

    }

    public void post(String URL, final StringResponse respond) {
        if (URL != "") {
            this.URL = URL;
            StringRequest sr = new StringRequest(Request.Method.POST, this.URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    respond.responseData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    respond.responseData(error.getLocalizedMessage());
                }
            });
        } else {
        }

    }

    public void post(String URL, HashMap<Object, Object> parameter) {
        if (URL != "") {
            this.URL = URL;
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, this.URL, new JSONObject(parameter), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    public void post(String URL, HashMap<Object, Object> parameter, final JSON respond) {
        if (URL != "") {
            this.URL = URL;
        }
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, this.URL, new JSONObject(parameter), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("shidanTest",response.toString());
                if(response instanceof JSONObject)
                {
                    responseStatus.put("statusRequest", "success");
                }
                else
                {
                    responseStatus.put("statusRequest", "unsuccess");
                }

                respond.responseData(new JSONObject(responseStatus), response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("shidanTest",error.getLocalizedMessage());
                responseStatus.put("info",error.getLocalizedMessage());
                responseStatus.put("statusRequest", "no connection");
                respond.responseData(new JSONObject(responseStatus), new JSONObject(responseStatus));
            }
        });
        RQ.add(jor);


    }

    public void post(String URL, HashMap<Object, Object> parameter, final WebService.JSON callback, String returnForm) {
//        Toast.makeText(con,"sini",Toast.LENGTH_SHORT).show();
        if (URL != "") {
            this.URL = URL;
        }
//        Toast.makeText(con,this.URL,Toast.LENGTH_SHORT).show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, this.URL, new JSONObject(parameter), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                responseStatus.put("statusRequest", "success");

                callback.responseData(new JSONObject(responseStatus), response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                responseStatus.put("statusRequest", error.getMessage());
                callback.responseData(new JSONObject(responseStatus), new JSONObject(responseStatus));
            }
        });
        RQ.add(jsonObjectRequest);
    }

//    public  void get(String URL, HashMap<Object,Object> parameter,final  JSON respond)
//    {
//        HashMap<Object,Object> params = new HashMap<Object, Object>();
//        params.put("function","fnGetNewID");
//        params.put("worker_type","w");
//        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, this.URL, new JSONObject(params), new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                responseStatus.put("statusRequest", "success");
//                Log.d("test",response.toString());
//                respond.responseData(new JSONObject(responseStatus), response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                responseStatus.put("statusRequest", error.getLocalizedMessage());
//                responseStatus.put("statusRequest", error.getLocalizedMessage());
//                respond.responseData(new JSONObject(responseStatus), new JSONObject(responseStatus));
//            }
//        });
//        RQ.add(jor);
//    }
    public interface JSON {
        void responseData(JSONObject status, JSONObject data);
    }

    public abstract class StringResponse {
        public abstract void responseData(String uini);
    }
}
