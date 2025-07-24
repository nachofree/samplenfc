package com.example.sample_with_sdk;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class MainActivity extends Activity {

    // NFC related variables
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private boolean mWriteMode = false;
    private Tag detectedTag;
    private int EPD_total_progress = 0;
    private int Size_Flag = 1; // Adjust based on your display size
    private Bitmap bmp_send; // Your bitmap to send
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create simple UI
        statusText = new TextView(this);
        statusText.setText("NFC E-Paper SDK Test App\nReady to write to display");
        statusText.setTextSize(16);
        statusText.setPadding(50, 50, 50, 50);
        setContentView(statusText);

        // Initialize NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC not supported on this device", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Create pending intent for NFC
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE);

        // Enable write mode
        mWriteMode = true;

        // TODO: Initialize your bitmap here
        // bmp_send = createTestBitmap(); // You'll need to create this method
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Your original NFC handling code
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] tech = detectedTag.getTechList();

            if(tech[0].equals("android.nfc.tech.NfcA")) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Boolean success = false;
                        NfcA tntag;

//                        final a a = new a(); // Waveshare SDK class
//                        a.a(); // Initialize send function

                        final WaveshareNFCWriter a = new WaveshareNFCWriter();
                        a.a();


                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                EPD_total_progress = 0;
                                while(EPD_total_progress != -1){
                                    EPD_total_progress = a.b();
                                    if(EPD_total_progress == -1){
                                        break;
                                    }
                                    setStatusBody("Transmitting: " + EPD_total_progress +"%");
                                    if(EPD_total_progress == 100 ){
                                        break;
                                    }
                                    SystemClock.sleep(10);
                                }
                            }
                        });
                        thread.start();

                        tntag = NfcA.get(detectedTag);
                        try {
                            int whether_succeed = a.a(tntag, Size_Flag, bmp_send);
                            if(whether_succeed == 1){
                                success = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setStatusBody("Transfer successful!");
                                    }
                                });
                                // Success_Sound_Effects(); // Uncomment if you have this method
                            }else{
                                setStatusBody("Transfer failed");
                            }
                        } finally {
                            try {
                                if (success == false) {
                                    setStatusBody("Transfer failed");
                                }
                                tntag.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
            }
        }
    }

    // Helper method to update status text
    private void setStatusBody(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(message);
            }
        });
    }

    // TODO: Create a test bitmap for your display
    private Bitmap createTestBitmap() {
        // Create a simple test bitmap
        // You'll need to implement this based on your display size
        // For 2.13 inch: 122 x 250 pixels
        return null; // Replace with actual bitmap creation
    }
}