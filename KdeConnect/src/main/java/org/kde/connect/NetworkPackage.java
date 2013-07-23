package org.kde.connect;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkPackage {

    private final static int CURRENT_PACKAGE_VERSION = 1;

    public final static String PACKAGE_TYPE_IDENTITY = "kdeconnect.identity";
    public final static String PACKAGE_TYPE_PING = "kdeconnect.ping";
    public final static String PACKAGE_TYPE_NOTIFICATION = "kdeconnect.notification";
    public final static String PACKAGE_TYPE_CALL = "kdeconnect.call";

    private long mId;
    private String mType;
    private JSONObject mBody;
    private int mVersion;

    private NetworkPackage() {
    }

    public NetworkPackage(String type) {
        mId = System.currentTimeMillis();
        mType = type;
        mBody = new JSONObject();
        mVersion = CURRENT_PACKAGE_VERSION;
    }

    public String getType() {
        return mType;
    }

    public int getVersion() {
        return mVersion;
    }

    //Most commons getters and setters defined for convenience
    public String getString(String key) { return mBody.optString(key,""); }
    public String getString(String key, String defaultValue) { return mBody.optString(key,defaultValue); }
    public void set(String key, String value) { try { mBody.put(key,value); } catch(Exception e) { } }
    public int getInt(String key) { return mBody.optInt(key,-1); }
    public int getInt(String key, int defaultValue) { return mBody.optInt(key,defaultValue); }
    public void set(String key, int value) { try { mBody.put(key,value); } catch(Exception e) { } }
    public boolean getBoolean(String key) { return mBody.optBoolean(key,false); }
    public boolean getBoolean(String key, boolean defaultValue) { return mBody.optBoolean(key,defaultValue); }
    public void set(String key, boolean value) { try { mBody.put(key,value); } catch(Exception e) { } }

    public String serialize() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("id",mId);
            jo.put("type",mType);
            jo.put("body",mBody);
            jo.put("version",mVersion);
        } catch(Exception e) {
        }
        Log.e("NetworkPackage.serialize",jo.toString());
        return jo.toString();
    }

    static public NetworkPackage unserialize(String s) {
        Log.e("NetworkPackage.unserialize", s);
        NetworkPackage np = new NetworkPackage();
        try {
            JSONObject jo = new JSONObject(s);
            np.mId = jo.getLong("id");
            np.mType = jo.getString("type");
            np.mBody = jo.getJSONObject("body");
            np.mVersion = jo.getInt("version");
        } catch (Exception e) {
            return null;
        }
        if (np.mVersion > CURRENT_PACKAGE_VERSION) {
            Log.e("NetworkPackage.unserialize","Version "+np.mVersion+" greater than supported version "+CURRENT_PACKAGE_VERSION);
        }
        return np;
    }

    static public NetworkPackage createIdentityPackage(Context context) {

        NetworkPackage np = new NetworkPackage(NetworkPackage.PACKAGE_TYPE_IDENTITY);

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            np.mBody.put("deviceId",tm.getDeviceId());
            np.mBody.put("deviceName", Build.BRAND + " " + Build.MODEL);
        } catch (JSONException e) {
        }

        return np;

    }


}