package com.mumoji;

import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.utils.L;
import org.apache.http.StatusLine;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.loopj.android.http.*;

public class MainActivity extends Activity {
  private BeaconManager _beaconManager;
  private Beacon _currentBeacon = null;

  private static final int REQUEST_ENABLE_BT = 1234;

  private static final String HOST = "http://192.168.1.3:3000";
  private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
  private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.main);

    // Configure verbose debug logging.
    L.enableDebugLogging(true);

    // Configure BeaconManager.
    _beaconManager = new BeaconManager(this);
    _beaconManager.setRangingListener(new BeaconManager.RangingListener() {
      @Override public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
        // Larger values are physically closer.
        int closest = Integer.MIN_VALUE;
        Beacon closestBeacon = null;
        for (Beacon beacon : beacons) {
          int rssi = beacon.getRssi();
          // Empirically-determined threshold (~4 inches)
          if ((rssi > closest) && (rssi > -50)) {
            closest = rssi;
            closestBeacon = beacon;
          }
        }

        if ((closestBeacon != null) && (!closestBeacon.equals(_currentBeacon))) {
          Log.d("mumoji", "Closest beacon --> " + closestBeacon.getMajor()); 
          _currentBeacon = closestBeacon;
          loadContent(_currentBeacon);
        }
      }
    });
  }

  void loadContent(Beacon beacon) {
    String url = HOST + "/estimote";

    RequestParams params = new RequestParams();
    params.put("uuid", beacon.getProximityUUID());
    params.put("major", Integer.toString(beacon.getMajor()));
    params.put("minor", Integer.toString(beacon.getMinor()));

    AsyncHttpClient client = new AsyncHttpClient();
    client.get(url, params, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(String response) {
        Log.d("mumoji", "Response --> " + response);
        WebView view = (WebView)findViewById(R.id.webView);
        view.loadUrl(response);
      }
    });
  }
  
  @Override
  protected void onStart() {
    super.onStart();

    // Check if device supports Bluetooth Low Energy.
    if (!_beaconManager.hasBluetooth()) {
      Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
      return;
    }

    // If Bluetooth is not enabled, let user enable it.
    if (!_beaconManager.isBluetoothEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    } else {
      //connectToService();
    }

    _beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override public void onServiceReady() {
        try {
          _beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
        } catch (RemoteException e) {
          Log.e("mumoji", "Cannot start ranging", e);
        }
      }
    });

    WebView view = (WebView)findViewById(R.id.webView);
    view.setWebViewClient(new WebViewClient());
    view.getSettings().setJavaScriptEnabled(true);
    view.getSettings().setRenderPriority(RenderPriority.HIGH);
    view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    if (Build.VERSION.SDK_INT >= 11){
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    view.loadUrl(HOST);
  }

  @Override
  protected void onStop() {
    try {
      _beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
    } catch (RemoteException e) {
      Log.e("mumoji", "Cannot stop but it does not matter now", e);
    }
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    _beaconManager.disconnect();
    super.onDestroy();
  }
}
