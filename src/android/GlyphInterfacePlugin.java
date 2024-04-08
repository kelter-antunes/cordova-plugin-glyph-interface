package com.kelter.glyphinterface;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.nothing.ketchum.Common;
import com.nothing.ketchum.GlyphException;
import com.nothing.ketchum.GlyphFrame;
import com.nothing.ketchum.GlyphManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GlyphInterfacePlugin extends CordovaPlugin {

    private GlyphManager mGM = null;
    private GlyphManager.Callback mCallback = null;
    private Context context;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.context = this.cordova.getActivity().getApplicationContext();
        init();
    }

    private void init() {
        if (!(Common.is20111() || Common.is22111() || Common.is23111())) {
            throw new RuntimeException("This device is not supported");
        }
        mCallback = new GlyphManager.Callback() {
            @Override
            public void onServiceConnected(ComponentName componentName) {
                if (Common.is20111()) {
                    mGM.register(Common.DEVICE_20111);
                }
                if (Common.is22111()) {
                    mGM.register(Common.DEVICE_22111);
                }
                if (Common.is23111()) {
                    mGM.register(Common.DEVICE_23111);
                }
                try {
                    mGM.openSession();
                } catch (GlyphException e) {
                    Log.d("GlyphIntegrationCordova", e.getMessage());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mGM.closeSession();
            }
        };

        mGM = GlyphManager.getInstance(context);
        mGM.init(mCallback);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "builder":
                // Implement builder logic here
                return true;
            case "getPlatform":
                // Implement getPlatform logic here
                return true;
            case "channel":
                // Implement channel logic here
                return true;
            case "build":
                // Implement build logic here
                return true;
            case "toggle":
                // Implement toggle logic here
                return true;
            case "setPeriod":
                // Implement setPeriod logic here
                return true;
            case "setCycles":
                // Implement setCycles logic here
                return true;
            case "setInterval":
                // Implement setInterval logic here
                return true;
            case "animate":
                // Implement animate logic here
                return true;
            case "getPlatformVersion":
                callbackContext.success("Android " + android.os.Build.VERSION.RELEASE);
                return true;
            default:
                callbackContext.error("Method not found");
                return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mGM.closeSession();
        } catch (GlyphException e) {
            Log.d("GlyphIntegrationCordova", e.getMessage());
        }
        mGM.unInit();
    }
}
