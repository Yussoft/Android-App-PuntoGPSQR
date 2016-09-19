package com.npi.yus.puntogpsqr;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
    *Copyright (C) 2016  Jesús Sánchez de Castro
    *This program is free software: you can redistribute it and/or modify
    *it under the terms of the GNU General Public License as published by
    *the Free Software Foundation, either version 3 of the License, or
    *(at your option) any later version.
    *This program is distributed in the hope that it will be useful,
    *but WITHOUT ANY WARRANTY; without even the implied warranty of
    *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    *GNU General Public License for more details.
    *You should have received a copy of the GNU General Public License
    *along with this program.  If not, see <http://www.gnu.org/licenses/>
    *
    * @author Jesús Sánchez de Castro
    * @version 19.09.2016
    *
    Last Modification: 19/09/2016
    https://justyusblog.wordpress.com/
    https://github.com/Yussoft


    App idea: This app will read a QR code that gives geographical coordinates(Latitude,Altitude)
    them It will start a google maps activity and start a navigation from out current location
    to the given coordinates (QR).
    */
/*
    MainActivity class:

    Implements all the methods needed to used a QR scanner. It the device does not have the QR scanner
    that the app is using, it will start a PlayStore activity to download it. Once the QR code is
    scanned the google maps activity will be launched.

 */
public class MainActivity extends AppCompatActivity {

    private Button qrScan;
    private TextView qrResult;

    /**********************************************************************************************/
    /*
        Method called when the Activity is created. UI elements initialization and Button listener
        set.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        qrScan = (Button)findViewById(R.id.qrScanButton);
        qrResult = (TextView)findViewById(R.id.QRResultText);

        qrScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scanQRCode();
            }
        });

    }

    /**********************************************************************************************/
    /*
        Method that start the QR scanner activity. If the device does not have the app, it will be
        redirected to the PlayStore.

     */
    protected void scanQRCode(){
        try {

            //Create QR Scanner Intent
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            //If the QR scanner is not installed, open PlayStore.
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);

        }
    }

    /**********************************************************************************************/
    /*
        Method called when the QR Scanner Activity has finished.

     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Control whose result we are going to receive.
        if (requestCode == 0) {

            //If everything is ok
            if (resultCode == RESULT_OK) {
                //Get the data from the QR code.
                String contents = data.getStringExtra("SCAN_RESULT");
                qrResult.setText(contents);
                Intent intent = new Intent(this,NavigationActivity.class);
                //Put it in an intent.
                intent.putExtra("message",contents);
                //start next Activity.
                startActivity(intent);
            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }
}
