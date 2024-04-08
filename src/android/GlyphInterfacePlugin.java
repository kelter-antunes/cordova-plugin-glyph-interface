package com.kelter.glyphinterface;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    // Declare the builderMap
    private Map<String, GlyphFrame.Builder> builderMap = new HashMap<>();

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
                try {
                    mGM.closeSession();
                } catch (GlyphException e) {
                    Log.e("GlyphIntegrationCordova", "Error closing Glyph session: " + e.getMessage());
                }
            }
            
        };

        mGM = GlyphManager.getInstance(context);
        mGM.init(mCallback);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "builder":
                buildGlyphFrame(args, callbackContext);
                return true;
            case "getPlatform":
                getPlatform(callbackContext);
                return true;
            case "channel":
                setChannel(args, callbackContext);
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
        if (mGM != null) {
            try {
                mGM.closeSession();
            } catch (GlyphException e) {
                Log.e("GlyphIntegrationCordova", "Error closing Glyph session: " + e.getMessage());
            }
            mGM.unInit();
        }
        // Clear the builderMap
        builderMap.clear();
    }


    private void buildGlyphFrame(JSONArray args, CallbackContext callbackContext) {
        try {
            JSONObject options = args.getJSONObject(0);
            String builderId = options.optString("builderId"); // Use optString to handle null values
            if (builderId == null || builderId.isEmpty()) {
                builderId = UUID.randomUUID().toString(); // Generate a new unique ID if none provided
            }
            int period = options.optInt("period", -1); // Use default value if not provided
            int cycles = options.optInt("cycles", -1);
            int interval = options.optInt("interval", -1);
            int channel = options.optInt("channel", -1); // Adjust according to how channels are identified

            // Check if a builder for this ID already exists, otherwise create a new one
            GlyphFrame.Builder builder = builderMap.get(builderId);
            if (builder == null) {
                builder = mGM.getGlyphFrameBuilder(); // Initialize a new Builder if not found
                // Assuming the builder allows setting these values directly
                builderMap.put(builderId, builder); // Store the new builder with the generated/found ID
            }

            // Configure the builder based on the parameters. This part might need adjustment
            // based on your actual builder methods and capabilities
            builder.buildPeriod(period)
                .buildCycles(cycles)
                .buildInterval(interval)
                .buildChannel(channel); // Adjust this part based on actual implementation

            // Construct the JSON object to return
            JSONObject builderDetails = new JSONObject();
            builderDetails.put("builderId", builderId);
            builderDetails.put("period", period);
            builderDetails.put("cycles", cycles);
            builderDetails.put("interval", interval);
            builderDetails.put("channel", channel);

            // Convert the JSON object to string and return it
            callbackContext.success(builderDetails.toString());

        } catch (JSONException e) {
            callbackContext.error("Error processing arguments");
        } catch (Exception e) {
            callbackContext.error("Error building GlyphFrame: " + e.getMessage());
        }
    }


    private void getPlatform(CallbackContext callbackContext) {
        if (Common.is20111()) {
            callbackContext.success("20111");
        } else if (Common.is22111()) {
            callbackContext.success("22111");
        } else {
            callbackContext.success("23111"); // Assuming 23111 is the default or fallback version
        }
    }

    private void setChannel(JSONArray args, CallbackContext callbackContext) {
        try {
            JSONObject options = args.getJSONObject(0);
            String id = options.getString("id");
            int channel = options.getInt("channel");
            if (!builderMap.containsKey(id)) {
                callbackContext.error("Builder with ID not found");
                return;
            }
            // Check if lightValue is provided
            if (options.has("light")) {
                int lightValue = options.getInt("light");
                builderMap.get(id).buildChannel(channel, lightValue);
            } else {
                builderMap.get(id).buildChannel(channel);
            }
            callbackContext.success("Channel set successfully");
        } catch (JSONException e) {
            callbackContext.error("Error processing channel operation");
        }
    }
    
}
