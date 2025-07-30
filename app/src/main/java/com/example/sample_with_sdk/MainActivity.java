package com.example.sample_with_sdk;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import android.graphics.Typeface;

public class MainActivity extends Activity {

    // NFC related variables
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private boolean mWriteMode = false;
    private Tag detectedTag;
    private int EPD_total_progress = 0;
    private int Size_Flag = 1; // 1 for 2.13 inch display
    private Bitmap bmp_send; // Your bitmap to send
    private TextView statusText;

    // Pattern cycling
    private int currentPattern = 0;
    private static final int TOTAL_PATTERNS = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create simple UI
        statusText = new TextView(this);
        statusText.setText("NFC E-Paper SDK Test App\nReady to write to display\nHold phone near e-Paper tag");
        statusText.setTextSize(16);
        statusText.setPadding(50, 50, 50, 50);
        setContentView(statusText);

        // Initialize NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC not supported on this device", Toast.LENGTH_LONG).show();
            setStatusBody("ERROR: NFC not supported on this device");
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC in settings", Toast.LENGTH_LONG).show();
            setStatusBody("ERROR: NFC is disabled. Please enable NFC in settings.");
            return;
        }

        // Create pending intent for NFC - fix the flag based on Android version
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }

        // Enable write mode
        mWriteMode = true;

        // Initialize bitmap - start with simple diagnostic pattern
//        bmp_send = createDiagnosticBitmap(currentPattern);
//        bmp_send = createTextBitmap("Hello World!", 30);
//        bmp_send = createMultiLineTextBitmap(new String[]{"Hello", "E-Paper", "Display"}, 24);

// Option 3: Mixed content with borders and graphics
//        bmp_send = createMixedContentBitmap();

// Option 4: Dynamic content (time/date)
        bmp_send = createCustomTextBitmap();
        setStatusBody("Setup complete. NFC enabled.\nPattern " + currentPattern + " ready.\nHold phone near NFC tag to start.");

        // Add logging
        android.util.Log.d("NFCDebug", "onCreate completed successfully");
    }

    @Override
    protected void onResume() {
        super.onResume();
        android.util.Log.d("NFCDebug", "onResume called");

        // Add null checks to prevent crashes
        if (mNfcAdapter != null && mPendingIntent != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            android.util.Log.d("NFCDebug", "NFC foreground dispatch enabled");
            setStatusBody("App ready. NFC listening enabled.\nPattern " + currentPattern + " ready.\nBring phone close to e-Paper display.");
        } else {
            android.util.Log.e("NFCDebug", "NFC adapter or PendingIntent is null");
            setStatusBody("Error: NFC initialization failed");
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
        setIntent(intent);

        android.util.Log.d("NFCDebug", "onNewIntent called with action: " + intent.getAction());

        setStatusBody("NFC tag detected! Processing...");

        // Your original NFC handling code
        if (mWriteMode && (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()))) {
            android.util.Log.d("NFCDebug", "NFC action matched: " + intent.getAction());
            detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (detectedTag == null) {
                setStatusBody("Error: No tag data received");
                android.util.Log.e("NFCDebug", "detectedTag is null");
                return;
            }

            String[] tech = detectedTag.getTechList();
            android.util.Log.d("NFCDebug", "Tag technologies: " + java.util.Arrays.toString(tech));
            setStatusBody("Tag tech: " + tech[0] + "\nStarting transfer...");

            if(tech[0].equals("android.nfc.tech.NfcA")) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Boolean success = false;
                        NfcA tntag = null;
                        WaveshareNFCWriter nfcWriter = null;

                        try {
                            setStatusBody("Creating NFC writer instance...");

                            // Create the Waveshare NFC writer instance
                            nfcWriter = new WaveshareNFCWriter();
                            nfcWriter.a(); // Initialize

                            setStatusBody("NFC writer initialized. Getting tag...");

                            tntag = NfcA.get(detectedTag);
                            if (tntag == null) {
                                setStatusBody("Error: Could not get NfcA tag");
                                return;
                            }

                            setStatusBody("Got NfcA tag. Starting image transfer...");

                            // Send the bitmap using the SDK
                            int whether_succeed = nfcWriter.a(tntag, Size_Flag, bmp_send);

                            // Monitor progress in separate thread
                            final WaveshareNFCWriter finalWriter = nfcWriter;
                            Thread progressThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    int lastProgress = -1;
                                    while (EPD_total_progress != -1 && EPD_total_progress < 100) {
                                        try {
                                            EPD_total_progress = finalWriter.b();
                                            if (EPD_total_progress != lastProgress) {
                                                setStatusBody("Progress: " + EPD_total_progress + "%");
                                                lastProgress = EPD_total_progress;
                                            }
                                            SystemClock.sleep(100);
                                        } catch (Exception e) {
                                            setStatusBody("Progress error: " + e.getMessage());
                                            break;
                                        }
                                    }
                                }
                            });
                            progressThread.start();

                            setStatusBody("Starting transfer to display...");

                            if(whether_succeed == 1){
                                success = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setStatusBody("Transfer successful! Pattern " + currentPattern + " sent.\nCycling to next pattern...");

                                        // Cycle to next pattern
                                        currentPattern = (currentPattern + 1) % TOTAL_PATTERNS;
                                        bmp_send = createDiagnosticBitmap(currentPattern);

                                        // Wait a moment then show ready message
                                        new android.os.Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                setStatusBody("Ready for pattern " + currentPattern + "\nBring phone close to e-Paper display.");
                                            }
                                        }, 2000);
                                    }
                                });
                            }else{
                                setStatusBody("Transfer failed. Error code: " + whether_succeed);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            setStatusBody("Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                        } finally {
                            try {
                                if (success == false) {
                                    setStatusBody("Transfer failed");
                                }
                                if (tntag != null) {
                                    tntag.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
            } else {
                setStatusBody("Wrong tag type: " + tech[0] + ". Expected NfcA.");
                android.util.Log.e("NFCDebug", "Wrong tag type: " + tech[0]);
            }
        } else {
            android.util.Log.d("NFCDebug", "Not in write mode or wrong action. WriteMode: " + mWriteMode + ", Action: " + intent.getAction());
            setStatusBody("NFC detected but not processing. Action: " + intent.getAction());
        }
    }

    // Helper method to update status text
    private void setStatusBody(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (statusText != null) {
                    statusText.setText(message);
                }
            }
        });
    }

    // Create diagnostic patterns to figure out the display format
    private Bitmap createDiagnosticBitmap(int testMode) {
        // Try creating bitmap at the actual display size (no rotation)
        int width = 250;
        int height = 122;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Start with white background
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setFilterBitmap(false);
        paint.setColor(Color.BLACK);

        android.util.Log.d("NFCDebug", "Creating diagnostic pattern " + testMode);

        switch(testMode) {
            case 0:
                // Test 0: Four corners - single pixels
                android.util.Log.d("NFCDebug", "Pattern: Four corner pixels");
                canvas.drawPoint(0, 0, paint);           // Top-left
                canvas.drawPoint(249, 0, paint);         // Top-right
                canvas.drawPoint(0, 121, paint);         // Bottom-left
                canvas.drawPoint(249, 121, paint);       // Bottom-right
                // Also draw small squares to make them more visible
                canvas.drawRect(0, 0, 5, 5, paint);
                canvas.drawRect(245, 0, 250, 5, paint);
                canvas.drawRect(0, 117, 5, 122, paint);
                canvas.drawRect(245, 117, 250, 122, paint);
                break;

            case 1:
                // Test 1: Border
                android.util.Log.d("NFCDebug", "Pattern: Border");
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                canvas.drawRect(1, 1, 249, 121, paint);
                break;

            case 2:
                // Test 2: Horizontal lines
                android.util.Log.d("NFCDebug", "Pattern: Horizontal lines");
                for (int y = 0; y < height; y += 10) {
                    canvas.drawLine(0, y, width, y, paint);
                }
                break;

            case 3:
                // Test 3: Vertical lines
                android.util.Log.d("NFCDebug", "Pattern: Vertical lines");
                for (int x = 0; x < width; x += 10) {
                    canvas.drawLine(x, 0, x, height, paint);
                }
                break;

            case 4:
                // Test 4: Solid black
                android.util.Log.d("NFCDebug", "Pattern: Solid black");
                canvas.drawColor(Color.BLACK);
                break;

            case 5:
                // Test 5: Half black, half white (vertical split)
                android.util.Log.d("NFCDebug", "Pattern: Half black vertical");
                canvas.drawRect(0, 0, 125, height, paint);
                break;

            case 6:
                // Test 6: Half black, half white (horizontal split)
                android.util.Log.d("NFCDebug", "Pattern: Half black horizontal");
                canvas.drawRect(0, 0, width, 61, paint);
                break;
        }

        // Force pixels to pure black/white
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
            pixels[i] = gray > 127 ? 0xFFFFFFFF : 0xFF000000;
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }
    // Add these methods to your MainActivity.java

    // Create a bitmap with text
    private Bitmap createTextBitmap(String text, int fontSize) {
        // Create bitmap at display size
        int width = 250;
        int height = 122;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // White background
        canvas.drawColor(Color.WHITE);

        // Setup paint for text
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true); // Enable for smooth text
        paint.setTextSize(fontSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        // Measure text to center it
        Paint.FontMetrics fm = paint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float textWidth = paint.measureText(text);

        // Calculate position to center text
        float x = (width - textWidth) / 2;
        float y = (height - textHeight) / 2 - fm.ascent;

        // Draw the text
        canvas.drawText(text, x, y, paint);

        // Convert to pure black and white
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
            pixels[i] = gray > 127 ? 0xFFFFFFFF : 0xFF000000;
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

    // Create a bitmap with multiple lines of text
    private Bitmap createMultiLineTextBitmap(String[] lines, int fontSize) {
        int width = 250;
        int height = 122;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // White background
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(fontSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        Paint.FontMetrics fm = paint.getFontMetrics();
        float lineHeight = fm.descent - fm.ascent;
        float totalHeight = lineHeight * lines.length;

        // Start position for first line
        float y = (height - totalHeight) / 2 - fm.ascent;

        // Draw each line
        for (String line : lines) {
            float textWidth = paint.measureText(line);
            float x = (width - textWidth) / 2;
            canvas.drawText(line, x, y, paint);
            y += lineHeight;
        }

        // Convert to pure black and white
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
            pixels[i] = gray > 127 ? 0xFFFFFFFF : 0xFF000000;
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

    // Create a bitmap with text and graphics
    private Bitmap createMixedContentBitmap() {
        int width = 250;
        int height = 122;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // White background
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

        // Draw a border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(2, 2, width-2, height-2, paint);

        // Draw title text
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        String title = "E-Paper Display";
        float titleWidth = paint.measureText(title);
        canvas.drawText(title, (width - titleWidth) / 2, 30, paint);

        // Draw smaller text
        paint.setTextSize(14);
        paint.setTypeface(Typeface.DEFAULT);
        String line1 = "NFC Powered";
        String line2 = "2.13\" Display";
        float line1Width = paint.measureText(line1);
        float line2Width = paint.measureText(line2);
        canvas.drawText(line1, (width - line1Width) / 2, 60, paint);
        canvas.drawText(line2, (width - line2Width) / 2, 80, paint);

        // Draw a simple icon/shape
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width/2, 100, 10, paint);

        // Convert to pure black and white
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
            pixels[i] = gray > 127 ? 0xFFFFFFFF : 0xFF000000;
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

    // Method to display custom text
    private Bitmap createCustomTextBitmap() {
        // Example: Display current time
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new java.util.Date());

        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM dd, yyyy");
        String date = dateFormat.format(new java.util.Date());

        return createMultiLineTextBitmap(new String[]{date, time}, 24);
    }
}