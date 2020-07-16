package com.youngwu.location;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private GoogleLocationProvider provider;
    private TextView tv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_info = findViewById(R.id.tv_info);

        if (provider == null) {
            provider = new GoogleLocationProvider();
        }

        provider.setLocationListener(new XLocationListener() {
            @Override
            public void onLocation(Location location) {
                if (location == null) {
                    return;
                }
                if (provider != null) {
                    provider.stopGetLocation();
                }
                if (TextUtils.isEmpty(tv_info.getText())) {
                    tv_info.setText("lat:" + location.getLatitude() + ",lng:" + location.getLongitude());
                } else {
                    tv_info.append("\nlat:" + location.getLatitude() + ",lng:" + location.getLongitude());
                }
            }

            @Override
            public void onFail(String error) {
                tv_info.setText("error:" + error);
            }
        });
        try {
            provider.startGetLocation(getApplicationContext(), 10000);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (provider != null) {
            provider.removeLocationListener(null);
            provider.stopGetLocation();
            provider = null;
        }
    }
}