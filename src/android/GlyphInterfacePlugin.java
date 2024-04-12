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

import java.util.List;
import java.util.ArrayList;

public class GlyphInterfacePlugin extends CordovaPlugin {

    private GlyphManager mGM = null;
    private GlyphManager.Callback mCallback = null;
    private Context context;

    private Map<String, GlyphFrame.Builder> builderMap = new HashMap<>();
    private Map<String, GlyphFrame> frameMap = new HashMap<>();
    private Map<String, List<String>> builderFrameMap = new HashMap<>();

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
            case "createBuilder":
                createBuilder(callbackContext);
                return true;
            case "addFrameToBuilder":
                addFrameToBuilder(args, callbackContext);
                return true;
            case "addFrameAnimatedToBuilder":
                addFrameAnimatedToBuilder(args, callbackContext);
                return true;
            case "builder":
                buildGlyphFrame(args, callbackContext);
                return true;
            case "turnOff":
                turnOffAllGlyphs(callbackContext);
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
            case "displayProgress":
                displayProgress(args, callbackContext);
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
            case "listFrameIds":
                listFrameIds(callbackContext);
                return true;
            case "clearFrames":
                clearFrames(callbackContext);
                return true;
            case "listBuilderFrames":
                listBuilderFrames(args, callbackContext);
                return true;
            case "clearBuilderFrames":
                clearBuilderFrames(callbackContext);
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

    private void createBuilder(CallbackContext callbackContext) {
        try {
            GlyphFrame.Builder builder = mGM.getGlyphFrameBuilder();
            String builderId = UUID.randomUUID().toString(); // Generate a new unique ID
            builderMap.put(builderId, builder);
            callbackContext.success(builderId);
        } catch (Exception e) {
            callbackContext.error("Error creating builder: " + e.getMessage());
        }
    }

    private void addFrameToBuilder(JSONArray args, CallbackContext callbackContext) {
        try {
            String builderId = args.getString(0);
            if (!builderMap.containsKey(builderId)) {
                callbackContext.error("Builder with ID not found");
                return;
            }
            GlyphFrame.Builder builder = builderMap.get(builderId);
            // Assuming channels are passed as an array of integers
            JSONArray channelsArray = args.getJSONArray(1);
            for (int i = 0; i < channelsArray.length(); i++) {
                int channel = channelsArray.getInt(i);
                builder.buildChannel(channel);
            }
            GlyphFrame frame = builder.build(); // Build the frame

            // Generate a UUID for the frame
            String frameId = UUID.randomUUID().toString();

            // Store the frame in the frameMap with its generated UUID
            frameMap.put(frameId, frame);

            // Store the information of the frames associated with the builder
            List<String> builderFrames = builderFrameMap.getOrDefault(builderId, new ArrayList<>());
            builderFrames.add(frameId);
            builderFrameMap.put(builderId, builderFrames);

            callbackContext.success(frameId);
        } catch (JSONException e) {
            callbackContext.error("Error processing arguments");
        } catch (Exception e) {
            callbackContext.error("Error adding frame to builder: " + e.getMessage());
        }
    }

    private void addFrameAnimatedToBuilder(JSONArray args, CallbackContext callbackContext) {
        try {
            String builderId = args.getString(0);
            if (!builderMap.containsKey(builderId)) {
                callbackContext.error("Builder with ID not found");
                return;
            }
    
            // Attempt to parse the second argument as a JSONObject
            JSONObject options;
            if (args.get(1) instanceof String) {
                options = new JSONObject((String) args.get(1));
            } else {
                options = args.getJSONObject(1);
            }
    
            int period = options.getInt("period");
            int cycles = options.getInt("cycles");
            int interval = options.getInt("interval");
            JSONArray channelsArray = options.getJSONArray("channels");
    
            GlyphFrame.Builder builder = builderMap.get(builderId);
    
            // Loop through the channels array and add each channel to the builder
            for (int i = 0; i < channelsArray.length(); i++) {
                int channel = channelsArray.getInt(i);
                int lightValue = options.getInt("lightValue"); // Assuming lightValue is common for all channels
                builder.buildChannel(channel, lightValue);
            }
    
            // Configuring the builder with animation parameters
            builder.buildPeriod(period)
                    .buildCycles(cycles)
                    .buildInterval(interval);
    
            // Building the frame
            GlyphFrame frame = builder.build();
    
            // Generating a UUID for the frame
            String frameId = UUID.randomUUID().toString();
    
            // Storing the frame in the frameMap with its generated UUID
            frameMap.put(frameId, frame);
    
            // Storing the information of the frames associated with the builder
            List<String> builderFrames = builderFrameMap.getOrDefault(builderId, new ArrayList<>());
            builderFrames.add(frameId);
            builderFrameMap.put(builderId, builderFrames);
    
            callbackContext.success(frameId);
        } catch (JSONException e) {
            callbackContext.error("Error processing arguments: " + e.getMessage());
        } catch (Exception e) {
            callbackContext.error("Error adding animated frame to builder: " + e.getMessage());
        }
    }
    

    private void listBuilderFrames(JSONArray args, CallbackContext callbackContext) {
        try {
            String builderId = args.getString(0);
            List<String> builderFrames = builderFrameMap.get(builderId);
            if (builderFrames != null) {
                JSONArray frameIdsArray = new JSONArray(builderFrames);
                callbackContext.success(frameIdsArray);
            } else {
                callbackContext.error("No frames found for the given builder ID: " + builderId);
            }
        } catch (JSONException e) {
            callbackContext.error("Error processing arguments");
        }
    }

    private void clearBuilderFrames(CallbackContext callbackContext) {
        try {
            builderMap.clear();
            builderFrameMap.clear();
            callbackContext.success("Builders cleared successfully");
        } catch (Exception e) {
            callbackContext.error("Error clearing builders: " + e.getMessage());
        }
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

            // Configure the builder based on the parameters. This part might need
            // adjustment
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

    private void turnOffAllGlyphs(CallbackContext callbackContext) {
        try {

            mGM.turnOff();

            // Convert the JSON object to string and return it
            callbackContext.success();

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
                callbackContext.error("Builder ID is required for building a frame.");
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

    private void buildFrameAnimated(JSONArray args, CallbackContext callbackContext) {
        try {
            String id = args.optString(0);
            if (id.isEmpty()) {
                callbackContext.error("Builder ID is required for building a frame.");
                return;
            }
            if (!builderMap.containsKey(id)) {
                callbackContext.error("Builder ID not found: " + id);
                return;
            }

            // Extracting other parameters from the JSONArray
            int period = args.getInt(1);
            int cycles = args.getInt(2);
            int interval = args.getInt(3);
            int channel = args.getInt(4);
            int lightValue = args.getInt(5);

            // Reusing the existing builder or creating a new one if not found
            GlyphFrame.Builder builder = builderMap.getOrDefault(id, mGM.getGlyphFrameBuilder());

            // Configuring the builder with animation parameters
            builder.buildPeriod(period)
                    .buildCycles(cycles)
                    .buildInterval(interval)
                    .buildChannel(channel, lightValue);

            // Building the frame
            GlyphFrame frame = builder.build();

            // Generating a UUID for the frame
            String frameId = UUID.randomUUID().toString();

            // Storing the frame in the frameMap with its generated UUID
            frameMap.put(frameId, frame);

            callbackContext.success(frameId);
        } catch (JSONException e) {
            callbackContext.error("Error processing arguments");
        } catch (Exception e) {
            callbackContext.error("Error building animated frame: " + e.getMessage());
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
            GlyphFrame.Builder builder = builderMap.get(id);
            if (builder != null) {
                GlyphFrame frame = builder.buildPeriod(period).build(); // Assuming build() method returns GlyphFrame
                frameMap.put(id, frame);
                callbackContext.success();
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
            GlyphFrame.Builder builder = builderMap.get(id);
            if (builder != null) {
                builderMap.put(id, builder.buildCycles(cycles));
                callbackContext.success();
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
            GlyphFrame.Builder builder = builderMap.get(id);
            if (builder != null) {
                builderMap.put(id, builder.buildInterval(interval));
                callbackContext.success();
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
                callbackContext.success();
            } else {
                throw new Exception("Frame not found for ID: " + id);
            }
        } catch (Exception e) {
            callbackContext.error("Error animating frame: " + e.getMessage());
        }
    }

    private void displayProgress(JSONArray args, CallbackContext callbackContext) {
        try {
            String id = args.getString(0);
            int progress = args.getInt(1);
            GlyphFrame frame = frameMap.get(id);
            if (frame != null) {
                mGM.displayProgress(frame, progress);
                callbackContext.success();
            } else {
                throw new Exception("Frame not found for ID: " + id);
            }
        } catch (Exception e) {
            callbackContext.error("Error displaying progress on frame: " + e.getMessage());
        }
    }

    private void listFrameIds(CallbackContext callbackContext) {
        try {
            JSONArray ids = new JSONArray(frameMap.keySet()); // Convert the keySet to JSONArray
            callbackContext.success(ids);
        } catch (Exception e) {
            callbackContext.error("Error listing frame IDs: " + e.getMessage());
        }
    }

    // Method to clear all entries in frameMap
    private void clearFrames(CallbackContext callbackContext) {
        try {
            frameMap.clear(); // Clear the map
            callbackContext.success("All frames cleared.");
        } catch (Exception e) {
            callbackContext.error("Error clearing frames: " + e.getMessage());
        }
    }

}
