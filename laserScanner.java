package cordova.plugin.barcode;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * broadcaster receive
 */
public class laserScanner extends CordovaPlugin {
    
    private String SCANNER_ACTION = "android.intent.action.SCANRESULT";//idata 95/90等
    // private String SCANNER_ACTION = "df.scanservice.result";//研华手持

    private String RESULT_TAG = "value";//idata 95/90等
    // private String RESULT_TAG = "result";//研华手持

    private static final String LOG_TAG = "laserScanner";
    private CallbackContext callbackContext = null;
    private BroadcastReceiver receiver = null;

    /**
     * get activity
     * @return Activity
     */
    private Activity getActivity(){
        return cordova.getActivity();
    }

    /**
     * cordova initialize
     * @param cordova
     * @param webView
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        register();
    }

    void register(){
        /**
         * register scanner
         */
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SCANNER_ACTION);

        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(SCANNER_ACTION)) {
                        String data = intent.getStringExtra(RESULT_TAG);
                        sendUpdate(data, true);
                    }
                }
            };
        }

        /**
         * register receiver
         */
        getActivity().registerReceiver(receiver, iFilter);
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        if (action.equals("receive")) {
            return true;
         }

        return false;
    }

    /**
     * stop scan and unregister receiver when destroy
     */
    @Override
    public void onDestroy() {
        removeLaserScannerListener();
        super.onDestroy();
    }

    @Override
    public void onReset() {
        removeLaserScannerListener();
        super.onReset();
    }

    private void sendUpdate(final String data, boolean keepCallback) {
        if (callbackContext != null) {
            getActivity().runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, data);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                }
            });
        }
    }    

     /**
     * Stop the receiver and set it to null.
     */
    private void removeLaserScannerListener() {
        if (this.receiver != null) {
            try {
                getActivity().unregisterReceiver(this.receiver);
                this.receiver = null;
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error unregistering LaserReceiver receiver: " + e.getMessage(), e);
            }
        }
    }
}

