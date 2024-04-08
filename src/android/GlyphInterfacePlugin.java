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


    private Map<String, GlyphFrame.Builder> builderMap = new HashMap<>();
    private Map<String, GlyphFrame> frameMap = new HashMap<>();

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
                buildFrame(args, callbackContext);
                return true;
            case "toggle":
                toggleFrame(args, callbackContext);
                return true;
            case "setPeriod":
                setPeriod(args, callbackContext);
                return true;
            case "setCycles":
                setCycles(args, callbackContext);
                return true;
            case "setInterval":
                setInterval(args, callbackContext);
                return true;
            case "animate":
                animate(args, callbackContext);
                return true;
            case "getPlatformVersion":
                callbackContext.success("Android " + android.os.Build.VERSION.RELEASE);
                return true;
            case "listBuilderIds":
                listBuilderIds(callbackContext);
                return true;
            case "clearBuilders":
                clearBuilders(callbackContext);
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

    // Method to list existing builder IDs
    private void listBuilderIds(CallbackContext callbackContext) {
        try {
            JSONArray ids = new JSONArray(builderMap.keySet()); // Convert the keySet to JSONArray
            callbackContext.success(ids);
        } catch (Exception e) {
            callbackContext.error("Error listing builder IDs: " + e.getMessage());
        }
    }

    // Method to clear all entries in builderMap
    private void clearBuilders(CallbackContext callbackContext) {
        try {
            builderMap.clear(); // Clear the map
            callbackContext.success("All builders cleared.");
        } catch (Exception e) {
            callbackContext.error("Error clearing builders: " + e.getMessage());
        }
    }


    private void buildFrame(JSONArray args, CallbackContext callbackContext) {
        try {
            String id = args.optString(0);
            if (id.isEmpty()) {
                callbackContext.error("ID is required for building a frame.");
                return;
            }
            if (!builderMap.containsKey(id)) {
                callbackContext.error("Builder ID not found: " + id);
                return;
            }
            GlyphFrame frame = builderMap.get(id).build(); // Assuming build() can't return null
            frameMap.put(id, frame);
            callbackContext.success("Frame built successfully for ID: " + id);
        } catch (Exception e) {
            callbackContext.error("Error building frame: " + e.getMessage());
        }
    }

    private void toggleFrame(JSONArray args, CallbackContext callbackContext) {
        try {
            String id = args.optString(0);
            if (id.isEmpty()) {
                callbackContext.error("ID is required for toggling a frame.");
                return;
            }
            if (!frameMap.containsKey(id)) {
                callbackContext.error("Frame ID not found: " + id);
                return;
            }
            GlyphFrame frame = frameMap.get(id); // Assuming non-null
            // Assuming mGM is your GlyphManager instance and it has a toggle method
            mGM.toggle(frame);
            callbackContext.success("Frame toggled successfully for ID: " + id);
        } catch (Exception e) {
            callbackContext.error("Error toggling frame: " + e.getMessage());
        }
    }

    private void setPeriod(JSONArray args, CallbackContext callbackContext) {
        try {
            String id = args.getString(0);
            int period = args.getInt(1);
            GlyphBuilder builder = builderMap.get(id);
            if (builder != null) {
                builderMap.put(id, builder.buildPeriod(period));
                callbackContext.success(true);
            } else {
                throw new Exception("Builder not found for ID: " + id);
            }
        } catch (Exception e) {
            callbackContext.error("Error setting period: " + e.getMessage());
        }
    }

    private void setCycles(JSONArray args, CallbackContext callbackContext) {
        try {
            String id = args.getString(0);
            int cycles = args.getInt(1);
            GlyphBuilder builder = builderMap.get(id);
            if (builder != null) {
                builderMap.put(id, builder.buildCycles(cycles));
                callbackContext.success(true);
            } else {
                throw new Exception("Builder not found for ID: " + id);
            }
        } catch (Exception e) {
            callbackContext.error("Error setting cycles: " + e.getMessage());
        }
    }

    private void setInterval(JSONArray args, CallbackContext callbackContext) {
        try {
            String id = args.getString(0);
            int interval = args.getInt(1);
            GlyphBuilder builder = builderMap.get(id);
            if (builder != null) {
                builderMap.put(id, builder.buildInterval(interval));
                callbackContext.success(true);
            } else {
                throw new Exception("Builder not found for ID: " + id);
            }
        } catch (Exception e) {
            callbackContext.error("Error setting interval: " + e.getMessage());
        }
    }

    private void animate(JSONArray args, CallbackContext callbackContext) {
        try {
            String id = args.getString(0);
            GlyphFrame frame = frameMap.get(id);
            if (frame != null) {
                mGM.animate(frame);
                callbackContext.success(true);
            } else {
                throw new Exception("Frame not found for ID: " + id);
            }
        } catch (Exception e) {
            callbackContext.error("Error animating frame: " + e.getMessage());
        }
    }
    
}
