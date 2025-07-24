package com.example.sample_with_sdk;

import android.graphics.Bitmap;

public class b {
    private Bitmap input;

    public b(Bitmap bitmap) {
        this.input = bitmap;
    }

    public Bitmap a() {
        // Convert input to black and white
        Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                int pixel = input.getPixel(x, y);
                int gray = (int) (0.3 * ((pixel >> 16) & 0xFF) +
                        0.59 * ((pixel >> 8) & 0xFF) +
                        0.11 * (pixel & 0xFF));
                int bw = gray < 128 ? 0xFF000000 : 0xFFFFFFFF; // threshold to black or white
                output.setPixel(x, y, bw);
            }
        }
        return output;
    }
}
