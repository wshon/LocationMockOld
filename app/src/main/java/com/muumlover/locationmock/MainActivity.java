package com.muumlover.locationmock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.model.LatLng;

//import android.support.v7.app.ActionBarActivity;


@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity {


    public class locationUpdatesCallback implements ICallback {
        @Override
        public void updateLocationInfo(Location loc, String info) {
            if (loc != null) {
                double _latitude = loc.getLatitude();
                double _longitude = loc.getLongitude();
                Log.i("Location", String.format("latitude: %s\nlongitude: %s", _latitude, _longitude));
                _tvNow.setText(String.format("latitude: %s\nlongitude: %s", _latitude, _longitude));
            } else {
                if (info != null) {
                    Log.i("has a info: ", info);
                    _tvNow.setText(info);
                }
            }
        }

        @Override
        public void updateLatLngInfo(LatLng loc, String info) {
        }

        @Override
        public void updateMockCount(int count) {
            _tvCount.setText(String.format("mock %d times", count));
        }

    }

    double _longitude;
    double _latitude;
    double _divider = 1000;

    //ui component
    Button _btnMock;
    Button _btnStop;
    Button _btnGet;
    TextView _tvNow;
    TextView _tvCount;
    EditText _etLongitude;
    EditText _etLatitude;
    EditText _etDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //btnMock click
        _btnMock = findViewById(R.id.btnMock);
        _btnGet = findViewById(R.id.btnGetGPS);
        _btnStop = findViewById(R.id.btnStopMock);
        _etLongitude = findViewById(R.id.etLongitude);
        _etLatitude = findViewById(R.id.etLatitude);
        _etDivider = findViewById(R.id.etDivider);
        _divider = Integer.parseInt(_etDivider.getText().toString());
        _tvNow = findViewById(R.id.tvNowGPS);
        _tvCount = findViewById(R.id.tvCount);

        final MyMockGps myMockGps = new MyMockGps(this, new locationUpdatesCallback());

        _btnMock.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (isAllowMockLocation()) {
                    _longitude = Double.parseDouble(_etLongitude.getText().toString());
                    _latitude = Double.parseDouble(_etLatitude.getText().toString());
                    _divider = Double.parseDouble(_etDivider.getText().toString());
                    myMockGps.startMock(_longitude, _latitude, _divider);
                } else {
                    new locationUpdatesCallback().updateLocationInfo(null, "System do not allow mock location!");
                }
            }
        });

        _btnGet.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	/*
            	Location loc = myMockGps.getLocation();
            	if(loc != null){
            		new locationUpdatesCallback().updateLocationInfo(loc, "get it");
            	}
            	*/
                Intent it = new Intent(MainActivity.this, BaiduMapActivity.class);
                //bind data
                Bundle data = new Bundle();
                _longitude = Double.parseDouble(_etLongitude.getText().toString());
                _latitude = Double.parseDouble(_etLatitude.getText().toString());
                data.putDouble("Lat", _latitude);
                data.putDouble("Lng", _longitude);
                it.putExtras(data);
                //ask result to start activity
                startActivityForResult(it, 1);

            }
        });

        _btnStop.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                myMockGps.stopMock();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("sys call", " main onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        Bundle result = data.getExtras();
        _latitude = result.getDouble("Lat");
        _longitude = result.getDouble("Lng");

        _etLatitude.setText(Double.toString(_latitude));
        _etLongitude.setText(Double.toString(_longitude));
    }


    private boolean isAllowMockLocation() {
        return Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
    }

}
