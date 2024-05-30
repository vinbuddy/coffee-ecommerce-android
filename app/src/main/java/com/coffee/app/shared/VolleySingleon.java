package com.coffee.app.shared;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleon {
    RequestQueue requestQueue;
    private static VolleySingleon mInstance;

    private VolleySingleon(Context context)
    {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }
    public static VolleySingleon getmInstance(Context context)
    {
        if(mInstance == null){
            mInstance = new VolleySingleon(context);
        }
        return  mInstance;
    }
    public  RequestQueue getRequestQueue()
    {
        return  requestQueue;
    }
}
