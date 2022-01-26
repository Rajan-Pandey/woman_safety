package com.rajan.woman_safety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static java.lang.System.exit;


public class BgService extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textLAtLong;
    private ProgressBar progressBar;
    String latitude;
    String longitude;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgservice);

        textLAtLong = findViewById(R.id.textLatLong);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.getCurrentLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            BgService.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                }
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(),Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            BgService.this, new String[] { Manifest.permission.SEND_SMS},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                }
                else
                    getCurrentLocation();

                }
        });
    }

    public void onRequestPermissionsResult(int requestCode , @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length >0 )
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
            else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {

        //locationEnabled();
        progressBar.setVisibility(View.VISIBLE);

        LocationManager lm = (LocationManager)getSystemService(Context. LOCATION_SERVICE ) ;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(BgService.this)
                    .setMessage("GPS Enable")
                    .setPositiveButton("Settings", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            toast();
                            Intent i_back = new Intent(BgService.this, Display.class);
                            startActivity(i_back);
                            finish();

                        }
                    })
                    .show();
        }
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(BgService.this).
                requestLocationUpdates(locationRequest, new LocationCallback() {
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(BgService.this).removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude =
                                    String.valueOf(locationResult.getLocations().get(latestLocationIndex).getLatitude());
                            longitude =
                                    String.valueOf(locationResult.getLocations().get(latestLocationIndex).getLongitude());
                            textLAtLong.setText(
                                    String.format("Latitude: %s\nLongitude: %s", latitude, longitude) );
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }, Looper.getMainLooper());


            sendSms();

    }


    public void sendSms() {

            String message = "Please help me. I need help immediately. This is where i am now:" + " https://www.google.com/maps/place/" + latitude + "," + longitude;
        if(gps_enabled) {
            SQLiteDatabase db;
            try {
                db = openOrCreateDatabase("NumDB", Context.MODE_PRIVATE, null);
                Cursor c = db.rawQuery("SELECT * FROM details", null);
                Cursor c1 = db.rawQuery("SELECT * FROM SOURCE", null);
                while (c.moveToNext() && c1.moveToFirst()) {
                    String source_number = c1.getString(0);
                    String name = c.getString(0);
                    String target_ph_number = c.getString(1);
                    try {
                        Toast.makeText(this, "Sending Message to " + name, Toast.LENGTH_SHORT).show();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(target_ph_number, source_number, message, null, null);
                        Toast.makeText(this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        // flag = false;
                        Toast.makeText(this, "Sending SMS failed , Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this,"Please Turn ON Location and Press Button to Send SMS",Toast.LENGTH_SHORT).show();
        }
    }

        private void toast()
        {
        Toast.makeText(this,"Location not Enabled",Toast.LENGTH_SHORT).show();
            }
}


